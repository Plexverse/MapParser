package net.plexverse.mapparser;

import lombok.Getter;
import net.plexverse.mapparser.command.MapSettingsCommand;
import net.plexverse.mapparser.command.ParseCommand;
import net.plexverse.mapparser.command.ToggleArmorStandCommand;
import net.plexverse.mapparser.listener.ArmorStandListener;
import net.plexverse.mapparser.listener.SpawnListener;
import net.plexverse.mapparser.saving.SavingStrategy;
import net.plexverse.mapparser.saving.zip.ZipSavingStrategy;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class MapParser extends JavaPlugin {
    private SavingStrategy savingStrategy;
    private PluginManager pluginManager;

    @Getter
    private static MapParser mapParser;

    @Override
    public void onEnable() {
        System.out.println("Enabling...");
        this.savingStrategy = new ZipSavingStrategy();
        this.pluginManager = this.getServer().getPluginManager();
        mapParser = this;

        System.out.println("Registering commands");
        this.initCommands();

        System.out.println("Registering listeners");
        this.initListeners();
    }

    private void initCommands() {
        this.getCommand("parse").setExecutor(new ParseCommand(this));
        this.getCommand("togglearmorstand").setExecutor(new ToggleArmorStandCommand(this));
        this.getCommand("mapsettings").setExecutor(new MapSettingsCommand(this));
    }

    private void initListeners() {
        this.pluginManager.registerEvents(new SpawnListener(), this);
        this.pluginManager.registerEvents(new ArmorStandListener(), this);
    }
}
