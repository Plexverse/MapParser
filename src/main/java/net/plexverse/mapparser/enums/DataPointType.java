package net.plexverse.mapparser.enums;

import net.plexverse.mapparser.constant.Keys;
import net.plexverse.mapparser.objects.Team;
import net.plexverse.mapparser.parsed.DataPointInfo;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public enum DataPointType {
    SPAWNPOINT(0, true, true),
    CHEST(0, true, true),
    CHEST_MID(0, false, true),
    HOLOGRAM(0, true, false),
    WALLPOINT(-1, true, false),
    SPECTATOR_SPAWNPOINT(0, false, true),
    ISLAND_BORDER(0, true, true),
    BORDER(0, false, false);

    private final int yDiff;
    private final boolean hasTeam;
    private final boolean changeYawPitch;

    DataPointType(int yDiff, boolean hasTeam, boolean changeYawPitch) {
        this.yDiff = yDiff;
        this.hasTeam = hasTeam;
        this.changeYawPitch = changeYawPitch;
    }

    public boolean canHaveTeam() {
        return this.hasTeam;
    }

    public boolean canChangeYawPitch() {
        return this.changeYawPitch;
    }

    public void parse(Player player, DataPointInfo dataPointInfo, Entity entity, PersistentDataContainer dataContainer) {
        final String teamName = dataContainer.get(Keys.TEAM_KEY, PersistentDataType.STRING);
        Team team = teamName != null ? Team.getExistingOrCreate(teamName) : null;
        dataPointInfo.addDataType(this, team, entity.getX(), entity.getY() + this.yDiff, entity.getZ(), entity.getYaw(), entity.getPitch());
    }
}
