package net.plexverse.mapparser.parser;

import lombok.SneakyThrows;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.plexverse.mapparser.MapParser;
import net.plexverse.mapparser.constant.Keys;
import net.plexverse.mapparser.enums.DataPointType;
import net.plexverse.mapparser.enums.GameType;
import net.plexverse.mapparser.parsed.DataPointInfo;
import org.apache.commons.io.FileUtils;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.util.*;

public class WorldParser {
    private final MapParser plugin;
    private final Player player;
    private final Location centerLocation;
    private final World world;
    private final String mapName;
    private final GameType gameName;
    private final int radius;
    private final boolean legacy;

    private final DataPointInfo dataPointInfo;

    public static final File TEMPLATES_FOLDER = new File(Bukkit.getWorldContainer(), "templates");
    private static final List<String> DO_NOT_COPY = Arrays.asList(
        "session.lock",
        "uid.dat",
        "advancements",
        "playerdata",
        "stats"
    );

    public WorldParser(MapParser plugin, Player player, GameType gameName, String mapName, String builder, int radius, boolean legacy) {
        this.plugin = plugin;
        this.player = player;
        this.centerLocation = player.getLocation();
        this.world = player.getWorld();
        this.gameName = gameName;
        this.mapName = mapName;
        this.radius = radius;
        this.legacy = legacy;

        this.dataPointInfo = new DataPointInfo();
        this.dataPointInfo.addMapMeta("mapName", mapName);
        this.dataPointInfo.addMapMeta("gameType", gameName.name());
        this.dataPointInfo.addMapMeta("author", builder);
        this.dataPointInfo.addMapMeta("legacy", String.valueOf(legacy));
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

        this.player.sendMessage(MiniMessage.miniMessage().deserialize("<dark_purple><b>(4/8)</b> <white>Checking datapoint requirements are met..."));
        final Map<String, Integer> datapointAmount = new HashMap<>();
        dataPointInfo.getDataPoints().keySet().forEach(datapoint -> {
            datapointAmount.put(datapoint, dataPointInfo.getDataPoints().get(datapoint).size());
            System.out.println(datapoint);
            System.out.println(dataPointInfo.getDataPoints().get(datapoint).size());
        });

        final Map<String, Integer> requirements = gameName.getRequirements();
        for (String dataPointType : requirements.keySet()) {
            if(!datapointAmount.containsKey(dataPointType)) {
                this.player.sendMessage(MiniMessage.miniMessage().deserialize("<red><b>(!!)</b> <white>No " + dataPointType + " datapoint(s) found..."));
                Bukkit.unloadWorld(clonedWorld, true);
                return;
            }
            if(datapointAmount.get(dataPointType) < requirements.get(dataPointType)) {
                this.player.sendMessage(MiniMessage.miniMessage().deserialize("<red><b>(!!)</b> <white>Not enough " + dataPointType + " datapoint(s) found..."));
                this.player.sendMessage(MiniMessage.miniMessage().deserialize("<red><b>(!!)</b> <white>"+requirements.get(dataPointType)+ " are required, only " + datapointAmount.get(dataPointType) + " found!"));
                Bukkit.unloadWorld(clonedWorld, true);
                return;
            }
        }


        this.player.sendMessage(MiniMessage.miniMessage().deserialize("<dark_purple><b>(5/8)</b> <white>Parsing complete..."));
        this.player.sendMessage(MiniMessage.miniMessage().deserialize("<dark_purple><b>(6/8)</b> <white>Unloading world..."));
        Bukkit.unloadWorld(clonedWorld, true);

        this.dataPointInfo.export(this.player, targetFile);
        this.plugin.getSavingStrategy().save(targetFile);

        onComplete.run();
    }

    private File clonedFile() {
        return new File(Bukkit.getWorldContainer(), this.gameName.name() + "-" + this.mapName.toUpperCase(Locale.ROOT));
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
        return this.loadNewWorld(targetFile.getName());
    }

    private World loadNewWorld(String worldName) {
        final WorldCreator worldCreator = WorldCreator.name(worldName);
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
