package net.plexverse.mapparser.parsed.hologram;

import lombok.Data;
import net.plexverse.mapparser.enums.Team;

@Data
public class TeamHologramPoint {
    private final double x;
    private final double y;
    private final double z;
    private Team team;
}
