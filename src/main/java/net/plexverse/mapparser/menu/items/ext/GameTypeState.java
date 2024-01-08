package net.plexverse.mapparser.menu.items.ext;

import lombok.Data;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.plexverse.mapparser.enums.GameType;
import net.plexverse.mapparser.objects.Team;
import org.bukkit.Material;

@Data
public class GameTypeState implements State {

    private final GameType gameType;

    public GameTypeState(GameType team) {
        this.gameType = team;
    }

    @Override
    public Material getMaterial() {
        return gameType.getMaterial();
    }

    @Override
    public Component getName() {
        return Component.join(JoinConfiguration.builder().build(), Component.text("Game: "), MiniMessage.miniMessage().deserialize(gameType.getDisplayName()));
    }
}
