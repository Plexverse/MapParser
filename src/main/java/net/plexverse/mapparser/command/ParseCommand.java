package net.plexverse.mapparser.command;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.plexverse.mapparser.MapParser;
import net.plexverse.mapparser.parser.WorldParser;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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

        if (args.length != 4) {
            player.sendMessage(this.miniMessage.deserialize("<red>Usage: <white>/parse <game> <mapName> <legacy> <builderUuids> <radiusInBlocks>"));
            player.sendMessage(this.miniMessage.deserialize("<red>Example: <white>/parse SKYWARS CookieTown true efa1e2d8-871d-4ede-93e5-3c82102cbd42,fd837352-6d75-4e44-a135-da277cb4c36b 300"));
            return true;
        }

        final String gameName = args[0];
        final String mapName = args[1];
        final boolean legacy;
        try {
            legacy = Boolean.parseBoolean(args[2]);
        } catch (NumberFormatException exception) {
            player.sendMessage(this.miniMessage.deserialize("<red>Invalid legacy status. (true/false)"));
            return true;
        }

        final String builder = args[3];

        final String[] uuids = builder.split(",");
        for(String uuid : uuids) {
            if(!uuid.equals("null")) {
                try {
                    final UUID parsedUuid = UUID.fromString(uuid);
                } catch (Exception e) {
                    player.sendMessage(this.miniMessage.deserialize("<red>Invalid builder uuid. (e.g. fd837352-6d75-4e44-a135-da277cb4c36b or null)"));
                    return true;
                }
            }
        }

        final int radius;
        try {
            radius = Integer.parseInt(args[4]);
        } catch (NumberFormatException exception) {
            player.sendMessage(this.miniMessage.deserialize("<red>Invalid radius. (E.g. 50, 100, 150)"));
            return true;
        }

        player.sendMessage(MiniMessage.miniMessage().deserialize("<dark_purple><b>(0/8)</b> <white>Parsing map for datapoints"));
        new WorldParser(this.plugin, player, gameName, mapName, builder, radius, legacy)
                .parse(() -> player.sendMessage(MiniMessage.miniMessage().deserialize("<dark_purple><b>(8/8)</b> <white>Parsing map " + mapName + " has been completed and saved in the /templates/ directory.")));
        return true;
    }
}
