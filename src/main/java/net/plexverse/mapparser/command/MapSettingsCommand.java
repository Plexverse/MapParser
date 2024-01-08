package net.plexverse.mapparser.command;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.plexverse.mapparser.MapParser;
import net.plexverse.mapparser.mapsettings.MapSettingsManager;
import net.plexverse.mapparser.mapsettings.objects.MapMeta;
import net.plexverse.mapparser.menu.MapSettingsMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.xenondevs.invui.window.Window;

import java.io.IOException;
import java.util.Optional;

public class MapSettingsCommand implements CommandExecutor {
    private final MapParser plugin;
    private final MiniMessage miniMessage;

    public MapSettingsCommand(MapParser plugin) {
        this.plugin = plugin;
        this.miniMessage = MiniMessage.miniMessage();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(this.miniMessage.deserialize("<red>You need to be a player to execute this command!"));
            return true;
        }

        final Player player = (Player) sender;
        if (!player.hasPermission("command.parse.use")) {
            player.sendMessage(this.miniMessage.deserialize("<red>You do not have permission to parse the world!"));
            return true;
        }
        final Optional<MapMeta> mapSettingsOptional;
        try {
            mapSettingsOptional = MapSettingsManager.getMapSettings(player.getWorld());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        final MapMeta mapSettings = mapSettingsOptional.orElseGet(MapMeta::new);
        Window.single().setGui(new MapSettingsMenu(player.getWorld(), mapSettings)).setTitle("Settings: " + player.getWorld().getName()).build(player).open();

        return true;
    }
}
