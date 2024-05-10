package net.plexverse.mapparser.objects;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Color;
import org.bukkit.Material;

import java.util.*;

@Data
public class Team {
    private static final Map<String, Team> PREDEFINED_TEAMS = getPredefinedTeams();
    public static final List<Team> VALUES = PREDEFINED_TEAMS.values().stream().toList();
    private final String id;
    private final String displayName;
    private final Color helmetColor;
    private final Material material;

    public Team(String id, String displayName, Color helmetColor, Material material) {
        this.id = id.toUpperCase(Locale.ROOT);
        this.displayName = displayName;
        this.helmetColor = helmetColor;
        this.material = material;
    }

    public static Map<String, Team> getPredefinedTeams() {
        final Map<String, Team> result = new HashMap<>();
        result.put("AQUA", new Team("AQUA", "<blue>Aqua", Color.AQUA, Material.BLUE_BED));
        result.put("RED", new Team("RED", "<red>Red", Color.RED, Material.RED_BED));
        result.put("GREEN", new Team("GREEN", "<green>Green", Color.GREEN, Material.GREEN_BED));
        result.put("YELLOW", new Team("YELLOW", "<yellow>Yellow", Color.YELLOW, Material.YELLOW_BED));
        return result;
    }

    public static Team getNextTeam(Team team) {
        final int index = VALUES.indexOf(team);
        return index == -1 ? VALUES.get(0) : (index >= VALUES.size() - 1 ? null : VALUES.get(index + 1));
    }

    public static Team getExistingOrCreate(String teamId) {
        final Team existing = PREDEFINED_TEAMS.get(teamId.toUpperCase(Locale.ROOT));
        return existing == null ? new Team(teamId, StringUtils.capitalize(teamId), Color.WHITE, Material.WHITE_BED) : existing;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Team)) {
            return false;
        }

        return other == this || ((Team) other).getId().equals(this.id);
    }
}
