package net.plexverse.mapparser.objects;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Color;

import java.util.*;

public class Team {
    private final String id;
    private final String displayName;
    private final Color helmetColor;

    private static final Map<String, Team> PREDEFINED_TEAMS = new HashMap<>();
    public static final Team AQUA = new Team("AQUA", "<blue>Aqua", Color.AQUA);
    public static final Team RED = new Team("RED", "<red>Red", Color.RED);
    public static final Team GREEN = new Team("GREEN", "<green>Green", Color.GREEN);
    public static final Team YELLOW = new Team("YELLOW", "<yellow>Yellow", Color.YELLOW);
    public static final List<Team> VALUES = Arrays.asList(AQUA, RED, GREEN, YELLOW);

    public Team(String id, String displayName, Color helmetColor) {
        this.id = id.toUpperCase(Locale.ROOT);
        this.displayName = displayName;
        this.helmetColor = helmetColor;
        PREDEFINED_TEAMS.put(this.id, this);
    }

    public String getId() {
        return this.id;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public Color getHelmetColor() {
        return this.helmetColor;
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

    public static Team getNextTeam(Team team) {
        final int index = VALUES.indexOf(team);
        return index == -1 ? VALUES.get(0) : (index >= VALUES.size() - 1 ? null : VALUES.get(index + 1));
    }

    public static Team getExistingOrCreate(String teamId) {
        final Team existing = PREDEFINED_TEAMS.get(teamId.toUpperCase(Locale.ROOT));
        return existing == null ? new Team(teamId, StringUtils.capitalize(teamId), Color.WHITE) : existing;
    }
}
