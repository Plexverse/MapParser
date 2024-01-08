package net.plexverse.mapparser.mapsettings;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.plexverse.mapparser.mapsettings.objects.MapMeta;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MapSettingsManager {

    private static final Map<World, MapMeta> worldSettingsMap = new HashMap<>();

    public static Optional<MapMeta> getMapSettings(final World world) throws IOException {
        if(worldSettingsMap.containsKey(world)) return Optional.ofNullable(worldSettingsMap.get(world));
        fetchMapSettingsFromFile(world).ifPresent(worldSettings -> worldSettingsMap.put(world, worldSettings));
        return fetchMapSettingsFromFile(world);
    }

    public static void saveMapSettings(final World world, MapMeta mapSettings) throws IOException {
        final File file = new File(String.valueOf(Bukkit.getWorldContainer().toPath()), world.getName());
        final File jsonFile = new File(file, "mapMeta.json");
        if(!jsonFile.exists()) jsonFile.createNewFile();
        new ObjectMapper().writeValue(jsonFile, mapSettings);
    }

    private static Optional<MapMeta> fetchMapSettingsFromFile(final World world) throws IOException {
        final File file = new File(String.valueOf(Bukkit.getWorldContainer().toPath()), world.getName());
        final File mapSettings = new File(file, "mapMeta.json");
        if(!mapSettings.exists()) return Optional.empty();
        else return Optional.ofNullable(new ObjectMapper().readValue(mapSettings, MapMeta.class));
    }

}
