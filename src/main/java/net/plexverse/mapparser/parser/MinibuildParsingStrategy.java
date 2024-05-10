package net.plexverse.mapparser.parser;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.plexverse.mapparser.MapParser;
import net.plexverse.mapparser.constant.Keys;
import net.plexverse.mapparser.objects.Cuboid;
import net.plexverse.mapparser.objects.minibuild.*;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static net.plexverse.mapparser.parser.DatapointUtil.getMinibuildDatapoints;

@RequiredArgsConstructor
public class MinibuildParsingStrategy implements ParsingStrategy {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().setLenient().enableComplexMapKeySerialization().create();
    private final Location startpoint;
    private final int limit;
    private final Player player;

    @SneakyThrows
    @Override
    public void parse(Runnable onComplete) {
        final List<ArmorStand> nearbyBuilds = getMinibuildDatapoints(startpoint, limit);
        player.sendMessage(MiniMessage.miniMessage().deserialize("<dark_purple><b>(!)</b> <white>Found " + nearbyBuilds.size() + " minibuilds to parse..."));

        for (final ArmorStand armorStand : nearbyBuilds) {

            final String buildName = armorStand.getPersistentDataContainer().get(Keys.MINIBUILD_NAME_KEY, PersistentDataType.STRING);
            final String categoryString = armorStand.getPersistentDataContainer().get(Keys.MINIBUILD_CATEGORY_KEY, PersistentDataType.STRING);
            String difficultyString = armorStand.getPersistentDataContainer().get(Keys.MINIBUILD_DIFFICULTY_KEY, PersistentDataType.STRING);
            String uuid = armorStand.getPersistentDataContainer().get(Keys.MINIBUILD_UUID_KEY, PersistentDataType.STRING);
            if (uuid == null) {
                final UUID randomUUID = UUID.randomUUID();
                uuid = randomUUID.toString();
                armorStand.getPersistentDataContainer().set(Keys.MINIBUILD_UUID_KEY, PersistentDataType.STRING, uuid);
            }
            if (buildName == null) {
                player.sendMessage(MiniMessage.miniMessage().deserialize("<red><b>(!)</b> <white>Cannot parse minibuild at " + armorStand.getLocation().x() + ", " + armorStand.getLocation().z() + " [no name]"));
                return;
            }
            if (categoryString == null) {
                player.sendMessage(MiniMessage.miniMessage().deserialize("<red><b>(!)</b> <white>Cannot parse minibuild at " + armorStand.getLocation().x() + ", " + armorStand.getLocation().z() + " [no category]"));
                return;
            }
            if (difficultyString == null) {
                difficultyString = "EASY";
            }

            final MinibuildCategory category = MinibuildCategory.valueOf(categoryString.toUpperCase());
            final MinibuildDifficulty difficulty = MinibuildDifficulty.valueOf(difficultyString.toUpperCase());
            final Minibuild minibuild = new Minibuild(buildName, uuid, category, difficulty, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

            final Location point1 = armorStand.getLocation().clone().add(0, -1, 0);
            final Location point2 = point1.getBlock().getRelative(8, 8, 8).getLocation().clone();
            final Cuboid cuboid = new Cuboid(point1, point2);

            final int index = nearbyBuilds.indexOf(armorStand);
            player.sendMessage(MiniMessage.miniMessage().deserialize("<dark_purple><b>[" + index + "]</b> <white>Parsing minibuild " + buildName + " (" + minibuild.getDifficulty().name() + ")"));

            enrichBlocks(cuboid, minibuild);
            enrichMobs(cuboid, minibuild);
            enrichFloor(cuboid, minibuild);

            saveFile(minibuild, minibuild.getName().toUpperCase().replace(" ", "_"));
            player.sendMessage(MiniMessage.miniMessage().deserialize("<dark_purple><b>[" + index + "]</b> <white>Saved minibuild " + buildName + " (" + minibuild.getDifficulty().name() + ")"));

        }
        player.sendMessage(MiniMessage.miniMessage().deserialize("<dark_purple><b>[]</b> <white>Completed minibuild saving, saved " + nearbyBuilds.size() + " builds!"));
    }

    private void enrichFloor(final Cuboid cuboid, final Minibuild minibuild) {
        final Cuboid snippedCuboid = new Cuboid(cuboid.getPoint1().clone().add(0, 0, 0), cuboid.getPoint2().clone().add(0, -8, 0));
        minibuild.getFloorList().addAll(MinibuildBlock.from(snippedCuboid, cuboid.getPoint1().clone().add(0, 1, 0)));
    }

    private void enrichBlocks(final Cuboid cuboid, final Minibuild minibuild) {
        final Cuboid snippedCuboid = new Cuboid(cuboid.getPoint1().clone().add(1, 1, 1), cuboid.getPoint2().clone().add(-1, 0, -1));
        minibuild.getBlockList().addAll(MinibuildBlock.from(snippedCuboid, cuboid.getPoint1().clone().add(0, 1, 0)));
    }

    private void enrichMobs(final Cuboid cuboid, final Minibuild minibuild) {
        final Cuboid snippedCuboid = new Cuboid(cuboid.getPoint1().clone().add(1, 1, 1), cuboid.getPoint2().clone().add(-1, 0, -1));
        final List<MinibuildMob> minibuildMobs = new ArrayList<>();
        for (ArmorStand armorStand : DatapointUtil.getMobDatapoints(snippedCuboid)) {
            final String teamName = armorStand.getPersistentDataContainer().get(Keys.TEAM_KEY, PersistentDataType.STRING);
            if (teamName == null) {
                player.sendMessage(MiniMessage.miniMessage().deserialize("<red><b>[!]</b> <white>Unable to save mobs on " + minibuild.getName() + ": a datapoint has got an null mob type!"));
                return;
            }
            final EntityType entityType = EntityType.valueOf(teamName.toUpperCase());
            minibuildMobs.add(MinibuildMob.from(armorStand.getLocation(), cuboid.getPoint1().clone().add(0, 1, 0), entityType));
        }
        minibuild.getMobList().addAll(minibuildMobs);
    }

    private void saveFile(final Minibuild minibuild, final String fileName) {
        try (final Writer writer = new OutputStreamWriter(new FileOutputStream(new File(new File(MapParser.getMapParser().getDataFolder(), "minibuilds"), fileName + ".json")), StandardCharsets.UTF_8)) {
            GSON.toJson(minibuild, writer);
        } catch (IOException e) {
            throw new RuntimeException("Saving " + fileName + ".json", e);
        }
    }


}
