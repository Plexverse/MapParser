package net.plexverse.mapparser.util.menu.event;

import net.plexverse.mapparser.util.menu.button.BuiltButton;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class MenuButtonClickEvent extends InventoryClickEvent {
    private final BuiltButton button;

    public MenuButtonClickEvent(InventoryClickEvent event, BuiltButton button) {
        super(event.getView(), event.getSlotType(), event.getRawSlot(), event.getClick(), event.getAction());
        this.button = button;
    }

    public Player getPlayer() {
        return (Player) super.getWhoClicked();
    }

    public BuiltButton getButton() {
        return this.button;
    }
}
