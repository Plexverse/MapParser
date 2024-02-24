package net.plexverse.mapparser.listener;

import io.papermc.paper.event.player.AsyncChatEvent;
import io.papermc.paper.event.player.ChatEvent;
import lombok.SneakyThrows;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.plexverse.mapparser.mapsettings.MapSettingsManager;
import net.plexverse.mapparser.mapsettings.objects.MapMeta;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Optional;

public class ChatListener implements Listener {

    @SneakyThrows
    @EventHandler
    public void handleChat(final AsyncChatEvent event) {
        event.setCancelled(true);
        final Component username = event.getPlayer().displayName();

        final Optional<MapMeta> mapMeta = MapSettingsManager.getMapSettings(event.getPlayer().getWorld());
        final Component map = mapMeta.map(meta ->
                Component.text(ChatColor.GRAY + "[" + ChatColor.LIGHT_PURPLE + meta.getMapName() + ChatColor.GRAY + "] ")
                        .hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, Component.text(ChatColor.WHITE + "Game: " + ChatColor.LIGHT_PURPLE + mapMeta.get().getGameType().getDisplayName() + "\n" + ChatColor.WHITE + "Name: " + ChatColor.LIGHT_PURPLE + mapMeta.get().getMapName() + ChatColor.YELLOW + "\n\nClick to teleport")))
            ).orElseGet(() -> Component.text(ChatColor.GRAY + "[] "));

        final Component component = Component.join(JoinConfiguration.noSeparators(),
                map,
                username,
                Component.text(" "),
                event.message())
                .clickEvent(ClickEvent.runCommand("/tp " + event.getPlayer().getName()));

        event.setCancelled(true);
        Bukkit.broadcast(component);
    }

}
