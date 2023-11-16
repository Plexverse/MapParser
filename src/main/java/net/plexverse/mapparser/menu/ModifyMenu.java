package net.plexverse.mapparser.menu;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.plexverse.mapparser.constant.Keys;
import net.plexverse.mapparser.constant.Menus;
import net.plexverse.mapparser.enums.DataPointType;
import net.plexverse.mapparser.enums.Team;
import net.plexverse.mapparser.util.asker.InputAsker;
import net.plexverse.mapparser.util.menu.Menu;
import net.plexverse.mapparser.util.menu.state.MenuAction;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

public class ModifyMenu extends Menu {
    private final Entity armorStandEntity;
    private final PersistentDataContainer dataContainer;
    private final DataPointType dataPointType;
    private final MiniMessage miniMessage;
    private static final InputAsker YAW_ASKER = new InputAsker("<light_purple>Please enter the desired yaw. (E.g. -90, -45, 0, 45, 90, 180)");
    private static final InputAsker PITCH_ASKER = new InputAsker("<light_purple>Please enter the desired yaw. (E.g. -90, -45, 0, 45, 90)");

    public ModifyMenu(Player player, Entity armorStandEntity) {
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
    }

    private void onTeamClick() {
        this.onClick("team", (event) -> {
            if (!this.dataPointType.canHaveTeam()) {
                this.player.sendMessage(this.miniMessage.deserialize("<red>This data point cannot have its Team changed."));
                this.close();
                return;
            }

            final int maxTeam = Team.VALUES.length;
            final Team currentTeam = this.getCurrentTeam();
            final Team targetTeam = currentTeam == null ? Team.BLUE : (currentTeam.ordinal() + 1 >= maxTeam ? null : Team.VALUES[currentTeam.ordinal() + 1]);
            if (targetTeam == null) {
                this.dataContainer.remove(Keys.TEAM_KEY);
                this.executeAction(MenuAction.REFRESH, null);
                return;
            }

            this.dataContainer.set(Keys.TEAM_KEY, PersistentDataType.STRING, targetTeam.name());
            this.executeAction(MenuAction.REFRESH, null);
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

    private Team getCurrentTeam() {
        return this.dataContainer.has(Keys.TEAM_KEY) ? Team.valueOf(this.dataContainer.get(Keys.TEAM_KEY, PersistentDataType.STRING)) : null;
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
