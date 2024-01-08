package net.plexverse.mapparser.menu.items.ext;

import lombok.Data;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.plexverse.mapparser.enums.GameType;
import org.bukkit.Material;

@Data
public class BooleanState implements State {

    private final boolean value;
    private final String label;

    public BooleanState(Boolean team, String label) {
        this.value = team;
        this.label = label;
    }

    @Override
    public Material getMaterial() {
        return value ? Material.GREEN_DYE : Material.RED_DYE;
    }

    @Override
    public Component getName() {
        return Component.join(JoinConfiguration.builder().build(), Component.text(label + ": " + value));
    }
}
