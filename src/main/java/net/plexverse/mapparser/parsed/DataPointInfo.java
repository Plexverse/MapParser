package net.plexverse.mapparser.parsed;

import lombok.Data;
import net.plexverse.mapparser.enums.DataPointType;
import net.plexverse.mapparser.enums.Team;
import net.plexverse.mapparser.parsed.hologram.TeamHologramPoint;
import net.plexverse.mapparser.parsed.spawn.SpectatorSpawnPoint;
import net.plexverse.mapparser.parsed.spawn.TeamSpawnPoint;
import net.plexverse.mapparser.parsed.wall.Border;
import net.plexverse.mapparser.parsed.wall.WallPoint;

import java.util.*;

public class DataPointInfo {
    private final Map<String, Set<WorldLocation>> dataPoints;

    public DataPointInfo() {
        this.dataPoints = new HashMap<>();
    }

    public Map<String, Set<WorldLocation>> getDataPoints() {
        return this.dataPoints;
    }

    public void setBorderPosition(double x, double y, double z, float yaw, float pitch) {
        final Set<WorldLocation> locations = this.dataPoints.computeIfAbsent("BORDER", ($) -> new HashSet<>());
        if (locations.size() >= 2) {
            return;
        }

        locations.add(new WorldLocation(x, y, z, yaw, pitch));
    }

    public void addDataType(DataPointType dataPointType, Team team, double x, double y, double z, float yaw, float pitch) {
        this.dataPoints.computeIfAbsent(dataPointType.name() + (team != null ? "_" + team.name() : ""), ($) -> new HashSet<>()).add(new WorldLocation(x, y, z, yaw, pitch));
    }

    @Data
    private class WorldLocation {
        private final double x;
        private final double y;
        private final double z;
        private final float yaw;
        private final float pitch;
    }
}
