package net.plexverse.mapparser.constant;

import net.plexverse.mapparser.MapParser;
import org.bukkit.NamespacedKey;

public class Keys {
    public static final NamespacedKey DATAPOINT_KEY = key("data-point");
    public static final NamespacedKey TEAM_KEY = key("team");
    public static final NamespacedKey MINIBUILD_NAME_KEY = key("minibuild-name");
    public static final NamespacedKey MINIBUILD_UUID_KEY = key("minibuild-uuid");
    public static final NamespacedKey MINIBUILD_DIFFICULTY_KEY = key("minibuild-difficulty");
    public static final NamespacedKey MINIBUILD_CATEGORY_KEY = key("minibuild-category");

    private static NamespacedKey key(String name) {
        return new NamespacedKey(MapParser.getPlugin(MapParser.class), name);
    }
}
