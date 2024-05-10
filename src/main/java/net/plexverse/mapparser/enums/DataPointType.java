package net.plexverse.mapparser.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;

@RequiredArgsConstructor
@Getter
public enum DataPointType {
    SPAWNPOINT("Spawnpoint", true, true, Material.RED_BED),
    CHEST("Chest", true, true, Material.CHEST),
    CHEST_MID("Mid Chest", false, true, Material.ENDER_CHEST),
    HOLOGRAM("Hologram", true, false, Material.OAK_SIGN),
    WALLPOINT("Wall Base", true, false, Material.GLASS),
    SPECTATOR_SPAWNPOINT("Spectator Spawnpoint", false, true, Material.GREEN_BED),
    ISLAND_BORDER("Island Border", true, true, Material.BARRIER),
    CENTER("Center", false, false, Material.TOTEM_OF_UNDYING),
    BORDER("Border", false, false, Material.BARRIER),
    ISLAND_BUILD_BORDER("Island Build Border", true, false, Material.DIAMOND_PICKAXE),
    MINIBUILD("Mini Build", false, false, Material.EMERALD_BLOCK),
    MOB("Mob", false, true, Material.ZOMBIE_SPAWN_EGG),
    GAME_NPC("Game (NPC)", true, true, Material.VILLAGER_SPAWN_EGG),
    STORE_NPC("Store (NPC)", true, true, Material.EMERALD),
    INTERACT_NPC("Interaction (NPC)", true, true, Material.WITHER_SKELETON_SKULL),
    INTERACTION("Interaction", true, true, Material.WITHER_SKELETON_SKULL),
    EVENT_BORDER("Event Border", true, false, Material.LAVA_BUCKET),
    GAME_AREA("Game Area Border", true, false, Material.BEACON),
    BRIDGE_POINT("Bridge Point", true, false, Material.OAK_LOG),
    BRIDGE_TYPE("Bridge Type", true, false, Material.WATER_BUCKET);

    private final String menuName;
    private final boolean hasTeam;
    private final boolean changeYawPitch;
    private final Material material;

}
