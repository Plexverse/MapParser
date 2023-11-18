package net.plexverse.mapparser.util.menu;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.oop.memorystore.api.Store;
import com.oop.memorystore.implementation.memory.MemoryStore;
import com.oop.memorystore.implementation.query.Query;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.plexverse.mapparser.MapParser;
import net.plexverse.mapparser.util.item.ItemBuilder;
import net.plexverse.mapparser.util.menu.button.BuiltButton;
import net.plexverse.mapparser.util.menu.button.ButtonState;
import net.plexverse.mapparser.util.menu.configurable.ConfigurableMenu;
import net.plexverse.mapparser.util.menu.configurable.ConfigurableMenuButton;
import net.plexverse.mapparser.util.menu.event.MenuButtonClickEvent;
import net.plexverse.mapparser.util.menu.inventory.BuiltInventory;
import net.plexverse.mapparser.util.menu.state.MenuAction;
import net.plexverse.mapparser.util.menu.state.MenuState;
import net.plexverse.mapparser.util.message.Replacer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.io.Closeable;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class Menu implements InventoryHolder, Closeable {
    protected static final Map<UUID, MenuAction> ACTION_MAP = Maps.newConcurrentMap();
    protected final Player player;
    protected final ConfigurableMenu configurableMenu;
    protected final Map<String, Consumer<MenuButtonClickEvent>> clickHandlerMap = Maps.newHashMap();
    protected final Map<String, Function<ConfigurableMenuButton, ButtonState>> stateHandler = new HashMap<>();
    protected final Replacer replacer = new Replacer();
    protected final Map<String, ItemStack> OVERRIDDEN_STATES = Maps.newHashMap();
    protected MenuState menuState = MenuState.NOT_OPEN_YET;
    protected BuiltInventory builtInventory;
    protected Menu previousMenu;
    protected boolean forceMoveBack = true;
    protected Menu movingTo = null;
    protected MenuAction currentAction = null;

    public Menu(Player player, ConfigurableMenu configurableMenu) {
        this.player = player;
        this.configurableMenu = configurableMenu;
    }

    public void modifyMenu() {
    }

    @Override
    public Inventory getInventory() {
        this.buildInventory();
        return this.builtInventory.inventory();
    }

    public void onClick(String buttonIdentifier, Consumer<MenuButtonClickEvent> clickHandler) {
        this.clickHandlerMap.put(buttonIdentifier, clickHandler);
    }

    public void handleOpen(InventoryOpenEvent event) {
        this.menuState = MenuState.OPEN;
    }

    public void handleClose() {
        this.menuState = MenuState.CLOSED;

        if (this.movingTo != null) {
            return;
        }

        if (!this.forceMoveBack || this.previousMenu == null) {
            this.close();
            return;
        }

        this.returnToPreviousMenu();
    }

    public void returnToPreviousMenu() {
        this.executeAction(MenuAction.RETURN, null);
    }

    public void handleDrag(InventoryDragEvent event) {
        event.setCancelled(true);
    }

    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
        if (event.getCurrentItem() == null) {
            return;
        }

        final BuiltButton builtButton =
            this.builtInventory.buttons().getFirst(Query.where("slot", event.getRawSlot()));
        if (builtButton == null) {
            return;
        }

        final Consumer<MenuButtonClickEvent> clickHandler = this.clickHandlerMap.get(builtButton.template().getName());
        if (clickHandler == null) {
            return;
        }

        clickHandler.accept(new MenuButtonClickEvent(event, builtButton));
    }

    public void applyBuiltButton(BuiltButton builtButton) {
        this.builtInventory.buttons().remove(Query.where("slot", builtButton.slot()));
        this.builtInventory.buttons().add(builtButton);
        this.builtInventory.inventory().setItem(builtButton.slot(), builtButton.item());
    }

    public void applyItemStack(ItemStack stack, int slot) {
        this.builtInventory.buttons().remove(Query.where("slot", slot));
        this.builtInventory.buttons().add(new BuiltButton(stack, slot, "default", new ConfigurableMenuButton.Builder().build()));
        this.builtInventory.inventory().setItem(slot, stack);
    }

    public void applyButtonState(ButtonState button, String state) {
        final Map<Integer, ConfigurableMenuButton> slots = Maps.newHashMap();

        for (BuiltButton template : this.builtInventory.buttons().get(Query.where("template", button.getName()))) {
            slots.put(template.slot(), template.template());
        }

        for (final Map.Entry<Integer, ConfigurableMenuButton> entry : slots.entrySet()) {
            final ItemStack stack = button.getItem();
            final int slot = entry.getKey();
            final ConfigurableMenuButton configurableMenuButton = entry.getValue();

            this.builtInventory.buttons().remove(Query.where("slot", slot));
            this.builtInventory.buttons().add(new BuiltButton(stack, slot, state, configurableMenuButton));
            this.builtInventory.inventory().setItem(slot, ItemBuilder.create(stack.clone()).replace(this.replacer).build());
        }
    }

    public void overrideButtonState(ItemStack stack, String state) {
        OVERRIDDEN_STATES.put(state, stack);
    }

    @Nullable
    protected ConfigurableMenuButton findButtonTemplate(Query query) {
        return this.configurableMenu.getCache().getFirst(query);
    }

    public void handleBottomClick(InventoryClickEvent event) {
        event.setCancelled(true);
    }

    protected void buildInventory() {
        final Inventory bukkitInventory = Bukkit.createInventory(this, this.configurableMenu.getMenuDesign().size() * 9, this.buildTitle());
        final Store<BuiltButton> builtButtonStore = new MemoryStore<BuiltButton>().synchronizedStore();
        final List<String> layout = this.configurableMenu.getMenuDesign();

        builtButtonStore.index("slot", BuiltButton::slot);
        builtButtonStore.index("state", BuiltButton::state);
        builtButtonStore.index("symbol", (button) -> button.template().getKey());
        builtButtonStore.index("template", button -> button.template().getName());
        this.builtInventory = new BuiltInventory(bukkitInventory, builtButtonStore);

        for (int row = 0; row < layout.size(); row++) {
            final String rowLayout = layout.get(row);
            int slot = row * 9;

            for (int column = 0; column < rowLayout.length(); column++) {
                final String symbol = rowLayout.charAt(column) + "";
                if (symbol.trim().isEmpty()) {
                    continue;
                }

                final ConfigurableMenuButton menuButtonTemplate = this.findButtonTemplate(Query.where("symbol", symbol));

                if (menuButtonTemplate == null) {
                    slot++;
                    continue;
                }
                final BuiltButton builtButton = this.createButton(slot, menuButtonTemplate);
                final ItemStack stack = OVERRIDDEN_STATES.get(builtButton.template().getName() + "-" + builtButton.state());

                if (Objects.nonNull(stack)) {
                    this.applyItemStack(stack, builtButton.slot());
                } else {
                    this.applyBuiltButton(builtButton);
                }

                slot++;
            }
        }

        this.modifyMenu();
    }

    public BuiltButton createButton(int slot, ConfigurableMenuButton buttonTemplate) {
        ButtonState usingState = buttonTemplate.getStates().get("default");
        if (usingState == null) {
            // If we don't have default state, we then use state Requester
            final Function<ConfigurableMenuButton, ButtonState> stateProvider = this.stateHandler.get(buttonTemplate.getName());

            if (stateProvider == null) {
                return new BuiltButton(new ItemStack(Material.BARRIER), slot, "default", buttonTemplate);
            }

            usingState = stateProvider.apply(buttonTemplate);
            if (usingState == null) {
                return new BuiltButton(new ItemStack(Material.BARRIER), slot, "default", buttonTemplate);
            }
        }

        final ItemStack itemStack = usingState.getItem().clone();
        return new BuiltButton(this.buildMenuItem(buttonTemplate.getName(), usingState, itemStack), slot, usingState.getName(), buttonTemplate);
    }

    protected ItemStack buildMenuItem(String identifier, ButtonState state, ItemStack itemStack) {
        if (Objects.isNull(this.replacer)) {
            return itemStack;
        }

        return ItemBuilder.create(itemStack).replace(this.replacer).build();
    }

    protected Component buildTitle() {
        return this.replacer.accept(MiniMessage.miniMessage().deserialize(this.configurableMenu.getMenuTitle()));
    }

    protected void executeAction(MenuAction action, Runnable callback) {
        if (this.currentAction != null) {
            return;
        }

        ACTION_MAP.put(this.player.getUniqueId(), action);

        final Runnable finalCallback = () -> {
            this.currentAction = null;
            ACTION_MAP.remove(this.player.getUniqueId(), action);
            if (callback != null) {
                callback.run();
            }
        };

        this.currentAction = action;
        switch (action) {
            case REFRESH, OPEN -> this.open(this, finalCallback);
            case MOVE -> this.open(this.movingTo, () -> {
                this.movingTo = null;
                finalCallback.run();
            });
            case CLOSE -> Bukkit.getScheduler().runTask(MapParser.getPlugin(MapParser.class), () -> {
                this.player.closeInventory();
                finalCallback.run();
            });
            case RETURN -> this.open(this.previousMenu, finalCallback);
        }
    }

    public void move(Menu menu) {
        this.movingTo = menu;
        menu.previousMenu = this;
        this.executeAction(MenuAction.MOVE, null);
    }

    public void open() {
        this.executeAction(MenuAction.OPEN, null);
    }

    public void open(Menu menu, Runnable callback) {
        Bukkit.getScheduler().runTaskAsynchronously(MapParser.getPlugin(MapParser.class), () -> {
            final Inventory inventory = menu.getInventory();

            Bukkit.getScheduler().runTask(MapParser.getPlugin(MapParser.class), () -> {
                this.player.openInventory(inventory);
                if (callback != null) {
                    callback.run();
                }
            });
        });
    }

    public List<Integer> getEmptySlots() {
        if (this.builtInventory == null) {
            return Lists.newLinkedList();
        }

        final List<Integer> emptySlots = Lists.newLinkedList();
        for (int slot = 0; slot < this.builtInventory.inventory().getSize(); slot++) {
            if (this.builtInventory.buttons().findFirst(Query.where("slot", slot)).isPresent()) {
                continue;
            }

            emptySlots.add(slot);
        }

        return emptySlots;
    }

    @Override
    public void close() {
        if (this.menuState != MenuState.OPEN) {
            return;
        }

        this.player.closeInventory();
    }

    public void forceClose() {
        this.forceMoveBack = false;
        this.close();
    }
}
