package net.plexverse.mapparser.objects.minibuild;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;

@RequiredArgsConstructor
@Getter
public enum MinibuildDifficulty {
    EASY(Material.GREEN_WOOL),
    MEDIUM(Material.ORANGE_WOOL),
    HARD(Material.RED_WOOL),
    DEMON(Material.PURPLE_WOOL);

    private final Material material;
}
