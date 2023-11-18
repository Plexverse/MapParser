package net.plexverse.mapparser;

import net.plexverse.mapparser.command.ParseCommand;
import net.plexverse.mapparser.listener.ArmorStandListener;
import net.plexverse.mapparser.saving.SavingStrategy;
import net.plexverse.mapparser.saving.zip.ZipSavingStrategy;
import net.plexverse.mapparser.util.menu.MenuListeners;
import org.bukkit.plugin.java.JavaPlugin;

public class MapParser extends JavaPlugin {
    private SavingStrategy savingStrategy;

    @Override
    public void onEnable() {
        this.savingStrategy = new ZipSavingStrategy();

        this.initCommands();
        this.initListeners();
    }

    public SavingStrategy getSavingStrategy() {
        return this.savingStrategy;
    }

    private void initCommands() {
        this.getCommand("parse").setExecutor(new ParseCommand(this));
    }

    private void initListeners() {
        new MenuListeners();
        new ArmorStandListener();
    }
}
