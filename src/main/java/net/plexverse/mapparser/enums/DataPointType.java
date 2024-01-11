package net.plexverse.mapparser.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.plexverse.mapparser.constant.Keys;
import net.plexverse.mapparser.objects.Team;
import net.plexverse.mapparser.parsed.DataPointInfo;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

@RequiredArgsConstructor
@AllArgsConstructor
@Getter
public enum DataPointType {
    SPAWNPOINT("Spawnpoint", 0, true, true, Material.RED_BED),
    CHEST("Chest", true, true, Material.CHEST),
    CHEST_MID("Mid Chest", false, true, Material.ENDER_CHEST),
    HOLOGRAM("Hologram", true, false, Material.OAK_SIGN),
    WALLPOINT("Wall Base", true, false, Material.GLASS),
    SPECTATOR_SPAWNPOINT("Spectator Spawnpoint", false, true, Material.GREEN_BED),
    ISLAND_BORDER("Island Border", true, true, Material.BARRIER),
    CENTER("Center", false, false, Material.TOTEM_OF_UNDYING),
    BORDER("Border", false, false, Material.BARRIER),
    ISLAND_BUILD_BORDER("Island Build Border", false, false, Material.DIAMOND_PICKAXE),
    MINIBUILD("Mini Build", false, false, Material.EMERALD_BLOCK),
    MOB("Mob", false, true, Material.ZOMBIE_SPAWN_EGG),
    GAME_NPC("Game (NPC)", true, true, Material.VILLAGER_SPAWN_EGG),
    STORE_NPC("Store (NPC)", true, true, Material.EMERALD),
    INTERACT_NPC("Interaction (NPC)", true, true, Material.WITHER_SKELETON_SKULL),
    INTERACTION("Interaction", true, true, Material.WITHER_SKELETON_SKULL),
    EVENT_BORDER("Event Border", true, false, Material.LAVA_BUCKET),
    GAME_AREA("Game Area Border", true, false, Material.BEACON),;

    private final String menuName;
    private int yDiff = 0;
    private final boolean hasTeam;
    private final boolean changeYawPitch;
    private final Material material;

    public void parse(DataPointInfo dataPointInfo, Entity entity, PersistentDataContainer dataContainer) {
        final String teamName = dataContainer.get(Keys.TEAM_KEY, PersistentDataType.STRING);
        Team team = teamName != null ? Team.getExistingOrCreate(teamName) : null;
        dataPointInfo.addDataType(this, team, entity.getX(), entity.getY() + this.yDiff, entity.getZ(), entity.getYaw(), entity.getPitch());
    }
}
