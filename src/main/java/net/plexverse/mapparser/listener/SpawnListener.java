package net.plexverse.mapparser.listener;

import net.plexverse.mapparser.util.event.Events;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class SpawnListener {

    public SpawnListener() {
        Events.hook(CreatureSpawnEvent.class, (event) -> {
            if (event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.NATURAL) {
                return;
            }

            event.setCancelled(true);
        });
    }
}
