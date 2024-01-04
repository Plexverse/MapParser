package net.plexverse.mapparser.enums;

import lombok.Getter;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.plexverse.mapparser.enums.DataPointType.*;

@Getter
public enum GameType {

    MICRO_BATTLES(
            microBattlesRequirements(),
            "Micro Battles"
    ),
    SPEED_BUILDERS(
            speedBuildersRequirements(),
            "Speed Builders"
    ),
    LOBBY(
            Map.of(SPAWNPOINT.name(), 4, HOLOGRAM.name(), 1, BORDER.name(), 2),
            "Lobby"
    ),
    SKYWARS(
            skywarsRequirements(),
            "Skywars"
    );

    private static Map<String, Integer> microBattlesRequirements() {
        final Map<String, Integer> result = new HashMap<>();
        result.put("SPAWNPOINT_RED", 4);
        result.put("SPAWNPOINT_GREEN", 4);
        result.put("SPAWNPOINT_AQUA", 4);
        result.put("SPAWNPOINT_YELLOW", 4);
        result.put("HOLOGRAM_RED", 1);
        result.put("HOLOGRAM_GREEN", 1);
        result.put("HOLOGRAM_AQUA", 1);
        result.put("HOLOGRAM_YELLOW", 1);
        result.put("WALLPOINT_RED", 10);
        result.put("WALLPOINT_GREEN", 10);
        result.put("WALLPOINT_AQUA", 10);
        result.put("WALLPOINT_YELLOW", 10);
        result.put("SPECTATOR_SPAWNPOINT", 1);
        result.put("BORDER", 2);
        return result;
    }

    private static Map<String, Integer> speedBuildersRequirements() {
        final Map<String, Integer> result = new HashMap<>();
        for(int i = 1; i < 8; i++) {
            result.put("SPAWNPOINT_" + i, 2);
            result.put("ISLAND_BORDER_" + i, 2);
        }
        result.put("CENTER", 2);
        result.put("BORDERS", 2);
        result.put("SPECTATOR_SPAWNPOINT", 1);
        return result;
    }

    private static Map<String, Integer> skywarsRequirements() {
        final Map<String, Integer> result = new HashMap<>();
        for(int i = 1; i < 13; i++) {
            result.put("SPAWNPOINT_" + i, 2);
            result.put("HOLOGRAM_" + i, 1);
            result.put("CHEST_" + i, 3);
            result.put("ISLAND_BORDER_" + i, 2);
        }
        result.put("CHEST_MID", 6);
        result.put("BORDER", 2);
        result.put("SPECTATOR_SPAWNPOINT", 1);
        return result;
    }

    private final Map<String, Integer> requirements;
    @Getter
    private final String displayName;

    private static final GameType[] VALUES = values();

    GameType(Map<String, Integer> requirements, String displayName) {
        this.requirements = requirements;
        this.displayName = displayName;
    }

    public static GameType getNextGameType(GameType gameType) {
        return gameType == null ? GameType.MICRO_BATTLES : (gameType.ordinal() + 1 >= VALUES.length ? null : VALUES[gameType.ordinal() + 1]);
    }
}
