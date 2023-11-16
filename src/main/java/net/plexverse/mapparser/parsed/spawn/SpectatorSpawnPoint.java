package net.plexverse.mapparser.parsed.spawn;

import lombok.Data;

@Data
public class SpectatorSpawnPoint {
    private final double x;
    private final double y;
    private final double z;
    private float yaw;
    private float pitch;
}
