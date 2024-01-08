package net.plexverse.mapparser.menu.items;

import it.unimi.dsi.fastutil.Function;
import net.plexverse.mapparser.menu.items.ext.State;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.impl.AbstractItem;
import xyz.xenondevs.invui.item.impl.SimpleItem;

import java.util.List;

public class StateItem extends AbstractItem {

    private final List<? extends State> states;
    private int currentIndex = 0;
    private final Function<State, Void> stateChangeFunction;

    public StateItem(final List<? extends State> states, final int startIndex, Function<State, Void> stateChangeFunction) {
        this.states = states;
        this.currentIndex = startIndex;
        this.stateChangeFunction = stateChangeFunction;
    }

    @Override
    public ItemProvider getItemProvider() {
        final State state = states.get(currentIndex);
        final ItemStack itemStack = new ItemStack(state.getMaterial());
        itemStack.editMeta(ItemMeta.class, itemMeta -> itemMeta.displayName(state.getName()));
        return new SimpleItem(itemStack).getItemProvider();
    }

    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        if(currentIndex >= states.size() - 1) {
            currentIndex = -1;
        }
        currentIndex = currentIndex + 1;
        final State state = states.get(currentIndex);
        stateChangeFunction.apply(state);
        notifyWindows();
    }
}
