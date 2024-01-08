package net.plexverse.mapparser.menu.items.ext;

import lombok.Data;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.plexverse.mapparser.objects.Team;
import org.bukkit.Material;

@Data
public class TeamState implements State {

    private final Team team;

    public TeamState(Team team) {
        this.team = team;
    }

    @Override
    public Material getMaterial() {
        if(team == null) return Material.BLACK_BED;
        return team.getMaterial();
    }

    @Override
    public Component getName() {
        if(team == null) return Component.text("Team: None");
        return Component.join(JoinConfiguration.builder().build(), Component.text("Team: "), MiniMessage.miniMessage().deserialize(team.getDisplayName()));
    }
}
