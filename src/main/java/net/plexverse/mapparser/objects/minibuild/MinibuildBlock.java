package net.plexverse.mapparser.objects.minibuild;

import lombok.Data;
import net.plexverse.mapparser.objects.Cuboid;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;

@Data
public class MinibuildBlock {
    private final String blockData;
    private final int relativeX, relativeY, relativeZ;

    public static MinibuildBlock from(final Block block, final Location bottomLeft) {
        final int x = block.getLocation().getBlockX() - bottomLeft.getBlockX();
        final int y = block.getLocation().getBlockY() - bottomLeft.getBlockY();
        final int z = block.getLocation().getBlockZ() - bottomLeft.getBlockZ();
        return new MinibuildBlock(block.getBlockData().getAsString(), x, y, z);
    }

    public static List<MinibuildBlock> from(final Cuboid cuboid) {
        final List<MinibuildBlock> minibuildBlocks = new ArrayList<>();
        cuboid.blockList().forEachRemaining(block -> {
            if (!block.getType().isAir())
                minibuildBlocks.add(from(block, cuboid.getPoint1()));
        });
        return minibuildBlocks;
    }

    public static List<MinibuildBlock> from(final Cuboid cuboid, final Location point1) {
        final List<MinibuildBlock> minibuildBlocks = new ArrayList<>();
        cuboid.blockList().forEachRemaining(block -> {
            if (!block.getType().isAir())
                minibuildBlocks.add(from(block, point1));
        });
        return minibuildBlocks;
    }

}
