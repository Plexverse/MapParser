package net.plexverse.mapparser.objects.minibuild;

import lombok.Data;

import java.util.List;

@Data
public class Minibuild {
    private final String name;
    private final String identifier;
    private final MinibuildCategory category;
    private final MinibuildDifficulty difficulty;
    private final List<MinibuildBlock> blockList;
    private final List<MinibuildBlock> floorList;
    private final List<MinibuildMob> mobList;
}
