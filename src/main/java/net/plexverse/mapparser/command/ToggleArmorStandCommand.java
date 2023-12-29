package net.plexverse.mapparser.command;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.plexverse.mapparser.MapParser;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ToggleArmorStandCommand implements CommandExecutor, Listener {
    private final MapParser plugin;
    private final List<UUID> toggled = new ArrayList();

    public ToggleArmorStandCommand(MapParser plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            return true;
        }

        final World world = player.getWorld();
        final Location location = player.getLocation();
        final int playerChunkX = location.getBlockX() >> 4;
        final int playerChunkZ = location.getBlockZ() >> 4;
        final boolean toggled = this.toggled.contains(player.getUniqueId());

        for (int chunkX = -8; chunkX <= 8; ++chunkX) {
            for (int chunkZ = -8; chunkZ <= 8; ++chunkZ) {
                final int realChunkX = playerChunkX + chunkX;
                final int realChunkZ = playerChunkZ + chunkZ;
                final Chunk chunk = world.getChunkAt(realChunkX, realChunkZ);
                Arrays.stream(chunk.getEntities()).filter((entity) -> entity instanceof ArmorStand).forEach((entity) -> {
                    if (toggled) {
                        player.showEntity(this.plugin, entity);
                        return;
                    }

                    player.hideEntity(this.plugin, entity);
                });
            }
        }

        if (this.toggled.remove(player.getUniqueId())) {
            player.sendMessage(MiniMessage.miniMessage().deserialize("<green>You have shown all armorstands in a 8 chunk radius around you."));
            return true;
        }

        this.toggled.add(player.getUniqueId());
        player.sendMessage(MiniMessage.miniMessage().deserialize("<red>You have hidden all armorstands in a 8 chunk radius around you."));
        return true;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.toggled.remove(event.getPlayer().getUniqueId());
    }
}