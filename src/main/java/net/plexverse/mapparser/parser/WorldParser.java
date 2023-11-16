package net.plexverse.mapparser.parser;

import com.google.gson.ExclusionStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.SneakyThrows;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.plexverse.mapparser.constant.Keys;
import net.plexverse.mapparser.enums.DataPointType;
import net.plexverse.mapparser.parsed.DataPointInfo;
import org.apache.commons.io.FileUtils;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class WorldParser {
    private final Player player;
    private final Location centerLocation;
    private final World world;
    private final String mapName;
    private final String builder;
    private final int radius;

    private final DataPointInfo dataPointInfo;

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().setLenient().enableComplexMapKeySerialization().create();
    private static final File TEMPLATES_FOLDER = new File(Bukkit.getWorldContainer(), "templates");
    private static final List<String> DO_NOT_COPY = Arrays.asList(
        "session.lock",
        "uid.dat",
        "advancements",
        "playerdata",
        "stats"
    );

    public WorldParser(Player player, String mapName, String builder, int radius) {
        this.player = player;
        this.centerLocation = player.getLocation();
        this.world = player.getWorld();
        this.mapName = mapName;
        this.builder = builder;
        this.radius = radius;

        this.dataPointInfo = new DataPointInfo();
    }

    @SneakyThrows
    public void parse(Runnable onComplete) {
        if (!TEMPLATES_FOLDER.exists()) {
            TEMPLATES_FOLDER.mkdirs();
        }

        final File targetFile = this.clonedFile();
        final World clonedWorld = this.cloneWorld(targetFile);
        final int minChunkX = (this.centerLocation.getBlockX() - this.radius) >> 4;
        final int minChunkZ = (this.centerLocation.getBlockZ() - this.radius) >> 4;
        final int maxChunkX = (this.centerLocation.getBlockX() + this.radius) >> 4;
        final int maxChunkZ = (this.centerLocation.getBlockZ() + this.radius) >> 4;
        this.player.sendMessage(MiniMessage.miniMessage().deserialize("<dark_purple><b>(4/8)</b> <white>Parsing chunks..."));

        int chunkCount = 0;
        final int totalChunks = ((maxChunkX + 1) - minChunkX) * ((maxChunkZ + 1) - minChunkZ);
        for (int chunkX = minChunkX; chunkX <= maxChunkX; chunkX++) {
            for (int chunkZ = minChunkZ; chunkZ <= maxChunkZ; chunkZ++) {
                final Chunk chunk = clonedWorld.getChunkAt(chunkX, chunkZ);
                this.parseDataPoints(chunk.getEntities());

                chunkCount++;
                this.player.sendActionBar(MiniMessage.miniMessage().deserialize("<dark_purple><b>Parsing Chunks:</b> <white>" + chunkCount + "<light_purple>/<white>" + totalChunks));
            }
        }

        this.player.sendMessage(MiniMessage.miniMessage().deserialize("<dark_purple><b>(5/8)</b> <white>Parsing complete..."));
        this.player.sendMessage(MiniMessage.miniMessage().deserialize("<dark_purple><b>(6/8)</b> <white>Unloading world..."));
        Bukkit.unloadWorld(clonedWorld, true);
        new File(targetFile, "session.lock").delete();
        new File(targetFile, "uid.dat").delete();

        try (final Writer writer = new OutputStreamWriter(new FileOutputStream(new File(Bukkit.getWorldContainer() + "/" + this.mapName, "dataPoints.json")), StandardCharsets.UTF_8)) {
            this.player.sendMessage(MiniMessage.miniMessage().deserialize("<dark_purple><b>(7/8)</b> <white>Saving dataPoint.json..."));
            gson.toJson(this.dataPointInfo.getDataPoints(), writer);
        } catch (IOException e) {
            throw new RuntimeException("Saving dataPoints.json", e);
        }

        final File templatesTarget = new File(TEMPLATES_FOLDER, this.mapName);
        FileUtils.deleteDirectory(templatesTarget);
        FileUtils.moveDirectory(targetFile, templatesTarget);

        onComplete.run();
    }

    private File clonedFile() {
        return new File(Bukkit.getWorldContainer(), this.mapName);
    }

    @SneakyThrows
    private World cloneWorld(File targetFile) {
        this.player.sendMessage(MiniMessage.miniMessage().deserialize("<dark_purple><b>(1/8)</b> <white>Saving world..."));
        this.world.save();

        final File worldFile = this.world.getWorldFolder();

        this.player.sendMessage(MiniMessage.miniMessage().deserialize("<dark_purple><b>(2/8)</b> <white>Copying world files..."));
        FileUtils.copyDirectory(worldFile, targetFile, (file) -> !DO_NOT_COPY.contains(file.getName()));
        if (!targetFile.exists()) {
            this.player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Failure when copying world."));
            return null;
        }

        this.player.sendMessage(MiniMessage.miniMessage().deserialize("<dark_purple><b>(3/8)</b> <white>Loading new world into server..."));
        return this.loadNewWorld();
    }

    private World loadNewWorld() {
        final WorldCreator worldCreator = WorldCreator.name(this.mapName);
        worldCreator.environment(this.world.getEnvironment())
            .seed(this.world.getSeed());

        if (this.world.getGenerator() != null) {
            worldCreator.generator(this.world.getGenerator());
        }

        return worldCreator.createWorld();
    }

    private void parseDataPoints(Entity[] entities) {
        for (final Entity entity : entities) {
            final PersistentDataContainer persistentDataContainer = entity.getPersistentDataContainer();
            if (entity.getType() != EntityType.ARMOR_STAND || !persistentDataContainer.has(Keys.DATAPOINT_KEY)) {
                continue;
            }

            final String dataPointName = persistentDataContainer.get(Keys.DATAPOINT_KEY, PersistentDataType.STRING);
            final DataPointType dataPointType = DataPointType.valueOf(dataPointName.toUpperCase(Locale.ROOT));
            dataPointType.parse(this.dataPointInfo, entity, persistentDataContainer);
            entity.remove();
        }
    }
}