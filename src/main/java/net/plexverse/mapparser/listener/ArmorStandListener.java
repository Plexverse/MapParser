package net.plexverse.mapparser.listener;

import net.plexverse.mapparser.constant.Keys;
import net.plexverse.mapparser.menu.DataPointMenu;
import net.plexverse.mapparser.menu.ModifyMenu;
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

            if (clickedEntity.getPersistentDataContainer().has(Keys.DATAPOINT_KEY)) {
                new ModifyMenu(player, clickedEntity).open();
                return;
            }

            new DataPointMenu(player, clickedEntity).open();
        });
    }
}
