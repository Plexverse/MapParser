package net.plexverse.mapparser.menu.items.ext;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;

public interface State {
    Material getMaterial();
    Component getName();
}
