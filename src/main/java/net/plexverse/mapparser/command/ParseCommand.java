package net.plexverse.mapparser.command;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.plexverse.mapparser.MapParser;
import net.plexverse.mapparser.mapsettings.MapSettingsManager;
import net.plexverse.mapparser.mapsettings.objects.MapMeta;
import net.plexverse.mapparser.parser.WorldParser;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.UUID;

public class ParseCommand implements CommandExecutor {
    private final MapParser plugin;
    private final MiniMessage miniMessage;

    public ParseCommand(MapParser plugin) {
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

        try {
            if(MapSettingsManager.getMapSettings(player.getWorld()).isEmpty()) {
                player.sendMessage(this.miniMessage.deserialize("<red>This map has not been setup (/mapsettings)"));
                return true;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (args.length != 1) {
            player.sendMessage(this.miniMessage.deserialize("<red>Usage: <white>/parse <radiusInBlocks>"));
            player.sendMessage(this.miniMessage.deserialize("<red>Example: <white>/parse 300"));
            return true;
        }


        final int radius;
        try {
            radius = Integer.parseInt(args[0]);
        } catch (NumberFormatException exception) {
            player.sendMessage(this.miniMessage.deserialize("<red>Invalid radius. (E.g. 50, 100, 150)"));
            return true;
        }
        final MapMeta mapMeta;
        try {
            mapMeta = MapSettingsManager.getMapSettings(player.getWorld()).get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        player.sendMessage(MiniMessage.miniMessage().deserialize("<dark_purple><b>(0/8)</b> <white>Parsing map for datapoints"));
        new WorldParser(this.plugin, player, mapMeta.getGameType(), mapMeta.getMapName(), mapMeta.getAuthor(), radius, mapMeta.isLegacy())
                .parse(() -> player.sendMessage(MiniMessage.miniMessage().deserialize("<dark_purple><b>(8/8)</b> <white>Parsing map " + mapMeta.getMapName() + " has been completed and saved in the /templates/ directory.")));
        return true;
    }
}
