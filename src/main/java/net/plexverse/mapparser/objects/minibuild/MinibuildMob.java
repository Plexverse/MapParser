package net.plexverse.mapparser.objects.minibuild;

import lombok.Data;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

@Data
public class MinibuildMob {
    private final EntityType entityType;
    private final double relativeX, relativeY, relativeZ;

    public static MinibuildMob from(final Location entityLocation, final Location bottomLeft, final EntityType entityType) {
        final int x = entityLocation.getBlockX() - bottomLeft.getBlockX();
        final int y = entityLocation.getBlockY() - bottomLeft.getBlockY();
        final int z = entityLocation.getBlockZ() - bottomLeft.getBlockZ();
        return new MinibuildMob(entityType, x, y, z);
    }

}
