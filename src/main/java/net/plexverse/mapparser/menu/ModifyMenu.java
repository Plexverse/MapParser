package net.plexverse.mapparser.menu;

import lombok.Getter;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.plexverse.mapparser.constant.Keys;
import net.plexverse.mapparser.enums.DataPointType;
import net.plexverse.mapparser.menu.items.ClickableItem;
import net.plexverse.mapparser.menu.items.StateItem;
import net.plexverse.mapparser.menu.items.ext.TeamState;
import net.plexverse.mapparser.objects.Team;
import net.plexverse.mapparser.util.asker.InputAsker;
import net.plexverse.mapparser.util.item.ItemBuilder;
import net.plexverse.mapparser.util.message.Replacer;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.impl.SimpleItem;

import java.util.ArrayList;
import java.util.List;

import java.util.function.BiConsumer;

@Getter
public class ModifyMenu extends PagedMenu {
    private final LivingEntity armorStandEntity;
    MiniMessage miniMessage = MiniMessage.miniMessage();

    private static final InputAsker CUSTOM_TEAM_ASKER = new InputAsker("<light_purple>Please enter the desired team id. (E.g. 1, 2, 3, magenta, black)");
    private static final InputAsker YAW_ASKER = new InputAsker("<light_purple>Please enter the desired yaw. (E.g. -90, -45, 0, 45, 90, 180)");
    private static final InputAsker PITCH_ASKER = new InputAsker("<light_purple>Please enter the desired pitch. (E.g. -90, -45, 0, 45, 90)");
    private static final InputAsker MOB_ASKER = new InputAsker("<light_purple>Please enter the entity id. (E.g. COW, HORSE, CREEPER)");


    private Team getCurrentTeam() {
        try {
            final String teamName = getDataContainer().get(Keys.TEAM_KEY, PersistentDataType.STRING);
            if (teamName == null) {
                return null;
            }
            return Team.getExistingOrCreate(teamName);
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }

    public PersistentDataContainer getDataContainer() {
        return armorStandEntity.getPersistentDataContainer();
    }

    public List<Item> getItems(final DataPointType dataPointType) {
        final List<Item> itemList = new ArrayList<>();
        if(dataPointType.isHasTeam()) {
            itemList.add(getTeamStateItem(getCurrentTeam(), dataPointType));
            itemList.add(getCustomTeamItem(dataPointType));
        }

        if(dataPointType.isChangeYawPitch()) {
            itemList.add(getYawItem());
            itemList.add(getPitchItem());
        }

        if(dataPointType == DataPointType.MOB) {
            itemList.add(getMobItem(dataPointType));
        }

        itemList.add(getCloneItem(dataPointType));

        return itemList;
    }

    public Item getCloneItem(final DataPointType dataPointType) {
        final ItemStack piston = new ItemStack(Material.STICKY_PISTON);
        piston.editMeta(ItemMeta.class, itemMeta -> itemMeta.setDisplayName("Clone Datapoint"));
        final ClickableItem clickableItem = new ClickableItem(new SimpleItem(piston).getItemProvider(), player -> {
            final ItemStack itemStack = ItemBuilder.create(Material.ARMOR_STAND)
                    .persistentData(Keys.DATAPOINT_KEY, dataPointType.name())
                    .persistentData(Keys.TEAM_KEY, getDataContainer().get(Keys.TEAM_KEY, PersistentDataType.STRING))
                    .build();
            itemStack.editMeta(ItemMeta.class, itemMeta -> itemMeta.setDisplayName(dataPointType.getMenuName()));
            player.getInventory().addItem(itemStack);
            return null;
        });
        return clickableItem;
    }

    public Item getYawItem() {
        final ItemStack itemStack = new ItemStack(Material.ENDER_PEARL);
        itemStack.editMeta(ItemMeta.class, itemMeta -> itemMeta.setDisplayName("Set Yaw"));
        final ClickableItem clickableItem = new ClickableItem(new SimpleItem(itemStack).getItemProvider(), player -> {
            YAW_ASKER.ask(player, (response) -> changeDirection(response, (location, direction) -> {
                location.setYaw(direction);
                player.sendMessage(this.miniMessage.deserialize("<green>You have set the Yaw to " + direction));
            }));
            return null;
        });
        return clickableItem;
    }

    public Item getPitchItem() {
        final ItemStack itemStack = new ItemStack(Material.ARROW);
        itemStack.editMeta(ItemMeta.class, itemMeta -> itemMeta.setDisplayName("Set Pitch"));
        final ClickableItem clickableItem = new ClickableItem(new SimpleItem(itemStack).getItemProvider(), player -> {
            PITCH_ASKER.ask(player, (response) -> changeDirection(response, (location, direction) -> {
                location.setPitch(direction);
                player.sendMessage(this.miniMessage.deserialize("<green>You have set the pitch to " + direction));
            }));
            return null;
        });
        return clickableItem;
    }

    public Item getMobItem(final DataPointType dataPointType) {
        final ItemStack itemStack = new ItemStack(Material.OCELOT_SPAWN_EGG);
        itemStack.editMeta(ItemMeta.class, itemMeta -> itemMeta.setDisplayName("Mob: " + getCurrentTeam()));
        itemStack.editMeta(ItemMeta.class, itemMeta -> itemMeta.setLore(List.of(ChatColor.GRAY + "Click to change!")));
        final ClickableItem clickableItem = new ClickableItem(new SimpleItem(itemStack).getItemProvider(), player -> {
            MOB_ASKER.ask(player, (response) -> {
                player.getInventory().close();
                getDataContainer().set(Keys.TEAM_KEY, PersistentDataType.STRING, response);
                final Team team = this.getCurrentTeam();
                if (team == null) {
                    return;
                }
                this.armorStandEntity.customName(MiniMessage.miniMessage().deserialize(team.getDisplayName() + " <red>" + dataPointType.name()));
                this.armorStandEntity.getEquipment().setHelmet(ItemBuilder.create(Material.ZOMBIE_HEAD).build());
            });
            return null;
        });
        return clickableItem;
    }

    public Item getCustomTeamItem(final DataPointType dataPointType) {
        final ItemStack itemStack = new ItemStack(Material.BLACK_BED);
        itemStack.editMeta(ItemMeta.class, itemMeta -> itemMeta.setDisplayName("Set Custom Team"));
        final ClickableItem clickableItem = new ClickableItem(new SimpleItem(itemStack).getItemProvider(), player -> {
            CUSTOM_TEAM_ASKER.ask(player, (response) -> {
                player.getInventory().close();
                getDataContainer().set(Keys.TEAM_KEY, PersistentDataType.STRING, response);
                final Team team = this.getCurrentTeam();
                if (team == null) {
                    return;
                }
                this.armorStandEntity.customName(MiniMessage.miniMessage().deserialize(team.getDisplayName() + " <red>" + dataPointType.name()));
                this.armorStandEntity.getEquipment().setHelmet(ItemBuilder.create(Material.LEATHER_HELMET).color(team.getHelmetColor()).build());
            });
            return null;
        });
        return clickableItem;
    }

    public StateItem getTeamStateItem(final Team currentTeam, final DataPointType dataPointType) {
        final List<TeamState> teamStates = new ArrayList<>();
        int currentIndex = 0;
        for(Team team : Team.VALUES) {
            if(currentTeam == team) currentIndex = Team.VALUES.indexOf(team);
            teamStates.add(new TeamState(team));
        }
        teamStates.add(new TeamState(null));

        final StateItem clickableItem = new StateItem(teamStates, currentIndex, state -> {
            final Team targetTeam = ((TeamState)state).getTeam();
            if (targetTeam == null) {
                getDataContainer().remove(Keys.TEAM_KEY);
                getArmorStandEntity().getEquipment().setHelmet(null);
                this.armorStandEntity.customName(MiniMessage.miniMessage().deserialize("<red>" + dataPointType.name()));
                return null;
            }
            getDataContainer().set(Keys.TEAM_KEY, PersistentDataType.STRING, targetTeam.getId());
            this.armorStandEntity.customName(MiniMessage.miniMessage().deserialize(targetTeam.getDisplayName().toUpperCase() + " <red>" + dataPointType.name()));
            this.armorStandEntity.getEquipment().setHelmet(ItemBuilder.create(Material.LEATHER_HELMET).color(targetTeam.getHelmetColor()).build());
            return null;
        });
        return clickableItem;
    }

    public ModifyMenu(LivingEntity armorStandEntity, final DataPointType dataPointType) {
        super(new ArrayList<>());
        this.armorStandEntity = armorStandEntity;

        setContent(getItems(dataPointType));

        Replacer replacer = new Replacer();
        replacer.replaceLiteral("%data_point%", dataPointType.name()).replaceWithSupplier("%team%", () -> {
            final Team currentTeam = this.getCurrentTeam();
            return this.miniMessage.deserialize(currentTeam == null ? "N/A" : currentTeam.getDisplayName());
        });

        //this.onDirectionClick();
        //this.onCloneClick();
    }

    private void changeDirection(String response, BiConsumer<Location, Float> directionModifier) {
        final float direction;
        try {
            direction = Float.parseFloat(response);
        } catch (NumberFormatException exception) {
            return;
        }

        final Location armorStandLoc = getArmorStandEntity().getLocation();
        directionModifier.accept(armorStandLoc, direction);
        this.armorStandEntity.teleport(armorStandLoc);
    }

}
