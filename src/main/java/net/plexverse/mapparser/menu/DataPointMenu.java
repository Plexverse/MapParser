package net.plexverse.mapparser.menu;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.plexverse.mapparser.constant.Keys;
import net.plexverse.mapparser.constant.Menus;
import net.plexverse.mapparser.enums.DataPointType;
import net.plexverse.mapparser.util.menu.Menu;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

public class DataPointMenu extends Menu {
    private final Entity armorStandEntity;

    public DataPointMenu(Player player, Entity armorStandEntity) {
        super(player, Menus.DATA_POINT_MENU);
        this.armorStandEntity = armorStandEntity;

        this.onClick();
    }

    private void onClick() {
        this.onClick("spawnpoint", (event) -> this.defineEntity(DataPointType.SPAWNPOINT));
        this.onClick("hologram", (event) -> this.defineEntity(DataPointType.HOLOGRAM));
        this.onClick("border", (event) -> this.defineEntity(DataPointType.BORDER));
        this.onClick("spectator-spawnpoint", (event) -> this.defineEntity(DataPointType.SPECTATOR_SPAWNPOINT));
        this.onClick("wall-base", (event) -> this.defineEntity(DataPointType.WALLPOINT));
    }

    private void defineEntity(DataPointType dataPointType) {
        this.armorStandEntity.setPersistent(true);
        this.armorStandEntity.getPersistentDataContainer().set(Keys.DATAPOINT_KEY, PersistentDataType.STRING, dataPointType.name());
        this.player.sendMessage(MiniMessage.miniMessage().deserialize("<red>You have defined this armorstand as a " + dataPointType.name()));
        this.close();
    }
}
