package net.plexverse.mapparser.parsed;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Data;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.plexverse.mapparser.enums.DataPointType;
import net.plexverse.mapparser.objects.Team;
import org.bukkit.entity.Player;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DataPointInfo {
    private final Map<String, String> mapMeta;
    private final Map<String, Set<WorldLocation>> dataPoints;
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().setLenient().enableComplexMapKeySerialization().create();

    public DataPointInfo() {
        this.mapMeta = new HashMap<>();
        this.dataPoints = new HashMap<>();
    }

    public void addMapMeta(String key, String value) {
        this.mapMeta.put(key, value);
    }

    public void addDataType(DataPointType dataPointType, Team team, double x, double y, double z, float yaw, float pitch) {
        this.dataPoints.computeIfAbsent(dataPointType.name() + (team != null ? "_" + team.getId() : ""), ($) -> new HashSet<>()).add(new WorldLocation(x, y, z, yaw, pitch));
    }

    public void export(Player player, File targetFile) {
        player.sendMessage(MiniMessage.miniMessage().deserialize("<dark_purple><b>(7/8)</b> <white>Saving info..."));
        try (final Writer writer = new OutputStreamWriter(new FileOutputStream(new File(targetFile, "mapMeta.json")), StandardCharsets.UTF_8)) {
            GSON.toJson(this.mapMeta, writer);
        } catch (IOException e) {
            throw new RuntimeException("Saving mapMeta.json", e);
        }

        try (final Writer writer = new OutputStreamWriter(new FileOutputStream(new File(targetFile, "dataPoints.json")), StandardCharsets.UTF_8)) {
            GSON.toJson(this.dataPoints, writer);
        } catch (IOException e) {
            throw new RuntimeException("Saving dataPoints.json", e);
        }
    }

    @Data
    private class WorldLocation {
        private final double x;
        private final double y;
        private final double z;
        private final float yaw;
        private final float pitch;
    }
}
