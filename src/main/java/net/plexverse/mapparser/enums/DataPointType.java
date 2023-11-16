package net.plexverse.mapparser.enums;

import net.plexverse.mapparser.constant.Keys;
import net.plexverse.mapparser.parsed.DataPointInfo;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public enum DataPointType {
    SPAWNPOINT(),
    HOLOGRAM(),
    WALLPOINT(),
    SPECTATOR_SPAWNPOINT(),
    BORDER();

    public void parse(DataPointInfo dataPointInfo, Entity entity, PersistentDataContainer dataContainer) {
        final Team team = dataContainer.has(Keys.TEAM_KEY) ? Team.valueOf(dataContainer.get(Keys.TEAM_KEY, PersistentDataType.STRING)) : null;
        dataPointInfo.addDataType(this, team, entity.getX(), entity.getY(), entity.getZ(), entity.getYaw(), entity.getPitch());
    }
}
