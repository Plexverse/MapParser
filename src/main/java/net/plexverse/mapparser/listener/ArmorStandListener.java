package net.plexverse.mapparser.listener;

import net.plexverse.mapparser.menu.DataPointMenu;
import net.plexverse.mapparser.util.event.Events;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

public class ArmorStandListener {

    public ArmorStandListener() {
        this.onArmorStandPlace();
        this.onArmorStandClick();
    }

    private void onArmorStandPlace() {
        Events.hook(EntitySpawnEvent.class, (event) -> {
            if (!(event.getEntity() instanceof ArmorStand)) {
                return;
            }

            event.getEntity().setGravity(false);
        });
    }

    private void onArmorStandClick() {
        Events.hook(PlayerInteractAtEntityEvent.class, (event) -> {
            final Player player = event.getPlayer();
            final Entity clickedEntity = event.getRightClicked();
            if (!(clickedEntity instanceof ArmorStand)) {
                return;
            }

            new DataPointMenu(player, clickedEntity).open();
        });
    }
}
