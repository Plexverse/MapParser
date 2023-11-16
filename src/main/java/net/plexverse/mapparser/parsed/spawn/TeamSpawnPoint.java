package net.plexverse.mapparser.parsed.spawn;

import lombok.Data;
import net.plexverse.mapparser.enums.Team;

@Data
public class TeamSpawnPoint {
    private final double x;
    private final double y;
    private final double z;
    private Team team;
    private float yaw;
    private float pitch;
}
