package net.plexverse.mapparser.parser;

import net.plexverse.mapparser.constant.Keys;
import net.plexverse.mapparser.enums.DataPointType;
import net.plexverse.mapparser.objects.Cuboid;
import net.plexverse.mapparser.objects.DataPointInfo;
import net.plexverse.mapparser.objects.Team;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class DatapointUtil {

    public static void parseDataPoints(Entity[] entities, DataPointInfo dataPointInfo) {
        for (final Entity entity : entities) {
            final PersistentDataContainer persistentDataContainer = entity.getPersistentDataContainer();
            if (entity.getType() != EntityType.ARMOR_STAND || !persistentDataContainer.has(Keys.DATAPOINT_KEY)) {
                continue;
            }

            final String dataPointName = persistentDataContainer.get(Keys.DATAPOINT_KEY, PersistentDataType.STRING);
            final DataPointType dataPointType = DataPointType.valueOf(dataPointName.toUpperCase(Locale.ROOT));
            parse(dataPointType, dataPointInfo, entity, persistentDataContainer);
            entity.remove();
        }
    }

    public static void parse(DataPointType dataPointType, DataPointInfo dataPointInfo, Entity entity, PersistentDataContainer dataContainer) {
        final String teamName = dataContainer.get(Keys.TEAM_KEY, PersistentDataType.STRING);
        Team team = teamName != null ? Team.getExistingOrCreate(teamName) : null;
        dataPointInfo.addDataType(dataPointType, team, entity.getX(), entity.getY(), entity.getZ(), entity.getYaw(), entity.getPitch());
    }


    public static List<ArmorStand> getMinibuildDatapoints(final Location startpoint, final int limit) {
        final Collection<Entity> entities = startpoint.getNearbyEntities(limit * 7, 20, limit * 7);
        return entities.stream()
                .filter(entity -> entity.getPersistentDataContainer().has(Keys.DATAPOINT_KEY))
                .filter(entity -> entity.getPersistentDataContainer().get(Keys.DATAPOINT_KEY, PersistentDataType.STRING).equalsIgnoreCase(DataPointType.MINIBUILD.name()))
                .filter(entity -> entity instanceof ArmorStand)
                .map(entity -> (ArmorStand) entity)
                .sorted(Comparator.comparingDouble(entity -> entity.getLocation().distanceSquared(startpoint)))
                .limit(limit)
                .toList();
    }

    public static List<ArmorStand> getMobDatapoints(final Cuboid cuboid) {
        final Collection<Entity> entities = cuboid.getCenter().clone().getNearbyEntities(6, 6, 6);
        return entities.stream()
                .filter(entity -> entity.getPersistentDataContainer().has(Keys.DATAPOINT_KEY))
                .filter(entity -> entity.getPersistentDataContainer().get(Keys.DATAPOINT_KEY, PersistentDataType.STRING).equalsIgnoreCase(DataPointType.MOB.name()))
                .filter(entity -> entity instanceof ArmorStand)
                .map(entity -> (ArmorStand) entity)
                .toList();
    }

}
