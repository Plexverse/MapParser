package net.plexverse.mapparser.constant;

import net.plexverse.mapparser.MapParser;
import org.bukkit.NamespacedKey;

public class Keys {
    public static final NamespacedKey DATAPOINT_KEY = key("data-point");
    public static final NamespacedKey TEAM_KEY = key("team");

    private static NamespacedKey key(String name) {
        return new NamespacedKey(MapParser.getPlugin(MapParser.class), name);
    }
}
