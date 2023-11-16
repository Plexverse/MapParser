package net.plexverse.mapparser;

import net.plexverse.mapparser.command.ParseCommand;
import net.plexverse.mapparser.listener.ArmorStandListener;
import net.plexverse.mapparser.util.menu.MenuListeners;
import org.bukkit.plugin.java.JavaPlugin;

public class MapParser extends JavaPlugin {

    @Override
    public void onEnable() {
        this.initCommands();
        this.initListeners();
    }

    private void initCommands() {
        this.getCommand("parse").setExecutor(new ParseCommand(this));
    }

    private void initListeners() {
        new MenuListeners();
        new ArmorStandListener();
    }
}
