package net.plexverse.mapparser.command;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.plexverse.mapparser.menu.KitCreatorMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class KitCreatorCommand implements CommandExecutor {
    private final MiniMessage miniMessage;

    public KitCreatorCommand() {
        this.miniMessage = MiniMessage.miniMessage();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(this.miniMessage.deserialize("<red>You need to be a player to execute this command!"));
            return true;
        }

        final Player player = (Player) sender;
        if (!player.hasPermission("command.kitcreator.use")) {
            player.sendMessage(this.miniMessage.deserialize("<red>You do not have permission to create a kit!"));
            return true;
        }

        new KitCreatorMenu(player).open();
        return true;
    }
}
