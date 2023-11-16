package net.plexverse.mapparser.enums;

import net.plexverse.mapparser.constant.Keys;
import net.plexverse.mapparser.parsed.DataPointInfo;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public enum DataPointType {
    SPAWNPOINT(true, true),
    HOLOGRAM(true, false),
    WALLPOINT(true, false),
    SPECTATOR_SPAWNPOINT(false, true),
    BORDER(false, false);

    private final boolean hasTeam;
    private final boolean changeYawPitch;

    DataPointType(boolean hasTeam, boolean changeYawPitch) {
        this.hasTeam = hasTeam;
        this.changeYawPitch = changeYawPitch;
    }

    public boolean canHaveTeam() {
        return this.hasTeam;
    }

    public boolean canChangeYawPitch() {
        return this.changeYawPitch;
    }

    public void parse(DataPointInfo dataPointInfo, Entity entity, PersistentDataContainer dataContainer) {
        final Team team = dataContainer.has(Keys.TEAM_KEY) ? Team.valueOf(dataContainer.get(Keys.TEAM_KEY, PersistentDataType.STRING)) : null;
        dataPointInfo.addDataType(this, team, entity.getX(), entity.getY(), entity.getZ(), entity.getYaw(), entity.getPitch());
    }
}
