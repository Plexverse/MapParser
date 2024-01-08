package net.plexverse.mapparser.listener;

import net.plexverse.mapparser.util.event.Events;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class SpawnListener implements Listener {

    @EventHandler
    public void spawnListener(CreatureSpawnEvent event) {
            if (event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.NATURAL) {
                return;
            }

            event.setCancelled(true);
    }
}
