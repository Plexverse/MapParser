package net.plexverse.mapparser.util.menu;

import net.plexverse.mapparser.util.event.Events;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class MenuListeners {
    public MenuListeners() {
        Events.hook(InventoryClickEvent.class, event -> {
            if (event.getWhoClicked().getOpenInventory().getTopInventory() == null) {
                return;
            }

            if (event.getClickedInventory() == null) {
                return;
            }

            if (event.getSlot() == -1) {
                return;
            }

            if (!(event.getWhoClicked().getOpenInventory().getTopInventory().getHolder() instanceof final Menu menu)) {
                return;
            }

            if (!(event.getClickedInventory().getHolder() instanceof Menu)) {
                menu.handleBottomClick(event);
                return;
            }

            menu.handleClick(event);
        });

        Events.hook(InventoryDragEvent.class, event -> {
            if (!(event.getInventory().getHolder() instanceof Menu menu)) {
                return;
            }

            menu.handleDrag(event);
        });

        Events.hook(InventoryOpenEvent.class, event -> {
            if (!(event.getInventory().getHolder() instanceof Menu menu)) {
                return;
            }

            menu.handleOpen(event);
        });

        Events.hook(InventoryCloseEvent.class, event -> {
            if (!(event.getInventory().getHolder() instanceof Menu menu)) {
                return;
            }

            menu.handleClose();
        });
    }
}
