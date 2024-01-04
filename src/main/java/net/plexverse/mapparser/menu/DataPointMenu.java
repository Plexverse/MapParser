package net.plexverse.mapparser.menu;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.plexverse.mapparser.constant.Keys;
import net.plexverse.mapparser.constant.Menus;
import net.plexverse.mapparser.enums.DataPointType;
import net.plexverse.mapparser.util.menu.Menu;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

public class DataPointMenu extends Menu {
    private final LivingEntity armorStandEntity;

    public DataPointMenu(Player player, LivingEntity armorStandEntity) {
        super(player, Menus.DATA_POINT_MENU);
        this.armorStandEntity = armorStandEntity;

        this.onClick();
    }

    private void onClick() {
        this.onClick("spawnpoint", (event) -> this.defineEntity(DataPointType.SPAWNPOINT));
        this.onClick("chest", (event) -> this.defineEntity(DataPointType.CHEST));
        this.onClick("mid-chest", (event) -> this.defineEntity(DataPointType.CHEST_MID));
        this.onClick("hologram", (event) -> this.defineEntity(DataPointType.HOLOGRAM));
        this.onClick("island-border", (event) -> this.defineEntity(DataPointType.ISLAND_BORDER));
        this.onClick("border", (event) -> this.defineEntity(DataPointType.BORDER));
        this.onClick("spectator-spawnpoint", (event) -> this.defineEntity(DataPointType.SPECTATOR_SPAWNPOINT));
        this.onClick("wall-base", (event) -> this.defineEntity(DataPointType.WALLPOINT));
        this.onClick("center", (event) -> this.defineEntity(DataPointType.CENTER));
    }

    private void defineEntity(DataPointType dataPointType) {
        this.armorStandEntity.setPersistent(true);
        this.armorStandEntity.getPersistentDataContainer().set(Keys.DATAPOINT_KEY, PersistentDataType.STRING, dataPointType.name());
        this.armorStandEntity.setCustomNameVisible(true);
        this.armorStandEntity.customName(MiniMessage.miniMessage().deserialize("<red>" + dataPointType.name()));
        this.player.sendMessage(MiniMessage.miniMessage().deserialize("<red>You have defined this armorstand as a " + dataPointType.name()));
        this.close();
        new ModifyMenu(this.player, this.armorStandEntity).open();
    }
}
