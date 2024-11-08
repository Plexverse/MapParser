package net.plexverse.mapparser.enums;

import lombok.Getter;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.plexverse.mapparser.enums.DataPointType.*;

@Getter
public enum GameType {

    MICRO_BATTLES(
            microBattlesRequirements(),
            "Micro Battles",
            List.of(SPAWNPOINT, HOLOGRAM, SPECTATOR_SPAWNPOINT, BORDER, WALLPOINT),
            Material.GLASS
    ),
    SPEED_BUILDERS(
            speedBuildersRequirements(),
            "Speed Builders",
            List.of(SPAWNPOINT, HOLOGRAM, ISLAND_BORDER, ISLAND_BUILD_BORDER, CENTER, BORDER, SPECTATOR_SPAWNPOINT),
            Material.GUARDIAN_SPAWN_EGG
    ),
    SPEED_BUILDERS_MINIBUILDS(
            Map.of("MINIBUILD", 1, "MOB", 1),
            "SB Minibuilds",
            List.of(MOB, MINIBUILD),
            Material.DIAMOND_PICKAXE
    ),
    LOBBY(
            Map.of("SPAWNPOINT_LOBBY", 2, BORDER.name(), 2),
            "Lobby",
            List.of(SPAWNPOINT, HOLOGRAM, BORDER, INTERACT_NPC, INTERACTION, GAME_NPC, STORE_NPC, GAME_AREA, EVENT_BORDER),
            Material.BEACON
    ),
    SKYWARS(
            skywarsRequirements(),
            "Skywars",
            List.of(SPAWNPOINT, CHEST, HOLOGRAM, CHEST_MID, ISLAND_BORDER, BORDER, SPECTATOR_SPAWNPOINT),
            Material.FEATHER
    ),
    WOODCHUCK(
            woodChuckRequirements(),
            "WoodChuck",
            List.of(SPAWNPOINT, TREE, BORDER, TEAM_GOAL, SPECTATOR_SPAWNPOINT),
            Material.OAK_SAPLING
    );

    private static final GameType[] VALUES = values();
    private final Map<String, Integer> requirements;
    private final String displayName;
    private final List<DataPointType> dataPointTypeList;
    private final Material material;

    GameType(Map<String, Integer> requirements, String displayName, List<DataPointType> dataPointTypeList, Material material) {
        this.requirements = requirements;
        this.displayName = displayName;
        this.dataPointTypeList = dataPointTypeList;
        this.material = material;
    }

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
        for (int i = 1; i <= 8; i++) {
            result.put("SPAWNPOINT_" + i, 2);
            result.put("ISLAND_BORDER_" + i, 2);
            result.put("ISLAND_BUILD_BORDER_" + i, 2);
            result.put("HOLOGRAM_" + i, 1);
        }
        result.put("CENTER", 1);
        result.put("BORDER", 2);
        result.put("SPECTATOR_SPAWNPOINT", 1);
        return result;
    }

    private static Map<String, Integer> skywarsRequirements() {
        final Map<String, Integer> result = new HashMap<>();
        for (int i = 1; i <= 12; i++) {
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

    private static Map<String, Integer> woodChuckRequirements() {
        final Map<String, Integer> result = new HashMap<>();
        result.put("SPAWNPOINT_RED", 6);
        result.put("SPAWNPOINT_BLUE", 6);
        result.put("TREE", 1);
        result.put("SPECTATOR_SPAWNPOINT", 1);
        result.put("BORDER", 2);

        return result;
    }

    public static GameType getNextGameType(GameType gameType) {
        return gameType == null ? GameType.MICRO_BATTLES : (gameType.ordinal() + 1 >= VALUES.length ? null : VALUES[gameType.ordinal() + 1]);
    }
}
