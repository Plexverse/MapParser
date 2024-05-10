package net.plexverse.mapparser.menu.items.ext;

import lombok.Data;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.plexverse.mapparser.objects.minibuild.MinibuildDifficulty;
import org.bukkit.Material;

@Data
public class DifficultyState implements State {

    private final MinibuildDifficulty difficulty;

    public DifficultyState(MinibuildDifficulty diff) {
        this.difficulty = diff;
    }

    @Override
    public Material getMaterial() {
        if (difficulty == null) return Material.BEDROCK;
        return difficulty.getMaterial();
    }

    @Override
    public Component getName() {
        if (difficulty == null) return Component.text("Difficulty: None");
        return Component.join(JoinConfiguration.builder().build(), Component.text("Difficulty: "), MiniMessage.miniMessage().deserialize(difficulty.name()));
    }
}
