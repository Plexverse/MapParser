package net.plexverse.mapparser.menu;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.plexverse.mapparser.constant.Keys;
import net.plexverse.mapparser.constant.Menus;
import net.plexverse.mapparser.enums.DataPointType;
import net.plexverse.mapparser.objects.Team;
import net.plexverse.mapparser.util.asker.InputAsker;
import net.plexverse.mapparser.util.item.ItemBuilder;
import net.plexverse.mapparser.util.menu.Menu;
import net.plexverse.mapparser.util.menu.state.MenuAction;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.function.BiConsumer;

public class ModifyMenu extends Menu {
    private final LivingEntity armorStandEntity;
    private final PersistentDataContainer dataContainer;
    private final DataPointType dataPointType;
    private final MiniMessage miniMessage;

    private static final InputAsker CUSTOM_TEAM_ASKER = new InputAsker("<light_purple>Please enter the desired team id. (E.g. 1, 2, 3, magenta, black)");
    private static final InputAsker YAW_ASKER = new InputAsker("<light_purple>Please enter the desired yaw. (E.g. -90, -45, 0, 45, 90, 180)");
    private static final InputAsker PITCH_ASKER = new InputAsker("<light_purple>Please enter the desired yaw. (E.g. -90, -45, 0, 45, 90)");

    public ModifyMenu(Player player, LivingEntity armorStandEntity) {
        super(player, Menus.MODIFY_MENU);
        this.armorStandEntity = armorStandEntity;
        this.dataContainer = this.armorStandEntity.getPersistentDataContainer();
        this.dataPointType = DataPointType.valueOf(this.dataContainer.get(Keys.DATAPOINT_KEY, PersistentDataType.STRING));
        this.miniMessage = MiniMessage.miniMessage();

        this.replacer.replaceLiteral("%data_point%", this.dataPointType.name()).replaceWithSupplier("%team%", () -> {
            final Team currentTeam = this.getCurrentTeam();
            return this.miniMessage.deserialize(currentTeam == null ? "N/A" : currentTeam.getDisplayName());
        });

        this.onTeamClick();
        this.onDirectionClick();
        this.onCloneClick();
    }

    private void onTeamClick() {
        this.onClick("team", (event) -> {
            if (!this.dataPointType.canHaveTeam()) {
                this.player.sendMessage(this.miniMessage.deserialize("<red>This data point cannot have its Team modified."));
                this.close();
                return;
            }

            final Team currentTeam = this.getCurrentTeam();
            final Team targetTeam = Team.getNextTeam(currentTeam);
            if (targetTeam == null) {
                this.dataContainer.remove(Keys.TEAM_KEY);
                this.armorStandEntity.getEquipment().setHelmet(null);
                this.executeAction(MenuAction.REFRESH, null);
                return;
            }

            this.dataContainer.set(Keys.TEAM_KEY, PersistentDataType.STRING, targetTeam.getId());
            this.armorStandEntity.customName(MiniMessage.miniMessage().deserialize(targetTeam.getDisplayName() + " <red>" + this.dataPointType.name()));
            this.armorStandEntity.getEquipment().setHelmet(ItemBuilder.create(Material.LEATHER_HELMET).color(targetTeam.getHelmetColor()).build());
            this.executeAction(MenuAction.REFRESH, null);
        });

        this.onClick("custom-team", (event) -> {
            if (!this.dataPointType.canHaveTeam()) {
                this.close();
                this.player.sendMessage(this.miniMessage.deserialize("<red>This data point cannot have its Team modified."));
                return;
            }

            this.close();
            CUSTOM_TEAM_ASKER.ask(this.player, (response) -> {
                this.dataContainer.set(Keys.TEAM_KEY, PersistentDataType.STRING, response);

                final Team team = this.getCurrentTeam();
                if (team == null) {
                    return;
                }

                this.armorStandEntity.customName(MiniMessage.miniMessage().deserialize(team.getDisplayName() + " <red>" + this.dataPointType.name()));
                this.armorStandEntity.getEquipment().setHelmet(ItemBuilder.create(Material.LEATHER_HELMET).color(team.getHelmetColor()).build());
                this.executeAction(MenuAction.REFRESH, null);
            });
        });
    }

    private void onDirectionClick() {
        this.onClick("yaw", (event) -> {
            this.close();
            if (!this.dataPointType.canChangeYawPitch()) {
                this.player.sendMessage(this.miniMessage.deserialize("<red>This data point cannot have its Yaw changed."));
                return;
            }

            YAW_ASKER.ask(this.player, (response) -> {
                this.changeDirection(response, (location, direction) -> {
                    location.setYaw(direction);
                    this.player.sendMessage(this.miniMessage.deserialize("<green>You have set the Yaw to " + direction));
                });
            });
        });

        this.onClick("pitch", (event) -> {
            this.close();
            if (!this.dataPointType.canChangeYawPitch()) {
                this.player.sendMessage(this.miniMessage.deserialize("<red>This data point cannot have its Pitch changed."));
                return;
            }

            PITCH_ASKER.ask(this.player, (response) -> {
                this.changeDirection(response, (location, direction) -> {
                    location.setPitch(direction);
                    this.player.sendMessage(this.miniMessage.deserialize("<green>You have set the Pitch to " + direction));
                });
            });
        });
    }

    private void onCloneClick() {
        this.onClick("clone", (event) -> {
            final ItemStack itemStack = ItemBuilder.create(Material.ARMOR_STAND)
                .persistentData(Keys.DATAPOINT_KEY, this.dataPointType.name())
                .persistentData(Keys.TEAM_KEY, this.dataContainer.get(Keys.TEAM_KEY, PersistentDataType.STRING))
                .build();

            this.player.getInventory().addItem(itemStack);
        });
    }

    private Team getCurrentTeam() {
        try {
            final String teamName = this.dataContainer.get(Keys.TEAM_KEY, PersistentDataType.STRING);
            if (teamName == null) {
                return null;
            }

            return Team.getExistingOrCreate(teamName);
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }

    private void changeDirection(String response, BiConsumer<Location, Float> directionModifier) {
        if (this.armorStandEntity.isDead()) {
            this.player.sendMessage(this.miniMessage.deserialize("<red>That data point doesn't exist anymore!"));
            return;
        }

        final float direction;
        try {
            direction = Float.parseFloat(response);
        } catch (NumberFormatException exception) {
            this.player.sendMessage(this.miniMessage.deserialize("<red>The input provided is not a valid number!"));
            return;
        }

        final Location armorStandLoc = this.armorStandEntity.getLocation();
        directionModifier.accept(armorStandLoc, direction);
        this.armorStandEntity.teleport(armorStandLoc);
    }
}
