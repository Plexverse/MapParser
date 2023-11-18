package net.plexverse.mapparser.command;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.plexverse.mapparser.MapParser;
import net.plexverse.mapparser.parser.WorldParser;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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

        if (args.length != 4) {
            player.sendMessage(this.miniMessage.deserialize("<red>Usage: <white>/parse <game> <mapName> <builder> <radiusInBlocks>"));
            return true;
        }

        final String gameName = args[0];
        final String mapName = args[1];
        final String builder = args[2];
        final int radius;
        try {
            radius = Integer.parseInt(args[3]);
        } catch (NumberFormatException exception) {
            player.sendMessage(this.miniMessage.deserialize("<red>Invalid radius. (E.g. 50, 100, 150)"));
            return true;
        }

        player.sendMessage(MiniMessage.miniMessage().deserialize("<dark_purple><b>(0/8)</b> <white>Parsing map for datapoints"));
        new WorldParser(this.plugin, player, gameName, mapName, builder, radius).parse(() -> {
            player.sendMessage(MiniMessage.miniMessage().deserialize("<dark_purple><b>(8/8)</b> <white>Parsing map " + mapName + " has been completed and saved in the /templates/ directory."));
        });
        return true;
    }
}
