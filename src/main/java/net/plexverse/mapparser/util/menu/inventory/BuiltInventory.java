package net.plexverse.mapparser.util.menu.inventory;

import com.oop.memorystore.api.Store;
import com.oop.memorystore.implementation.query.Query;
import net.plexverse.mapparser.util.menu.Menu;
import net.plexverse.mapparser.util.menu.button.BuiltButton;
import org.bukkit.inventory.Inventory;

import java.util.List;
import java.util.Objects;

public final class BuiltInventory {
    private final Inventory inventory;
    private final Store<BuiltButton> buttons;

    public BuiltInventory(Inventory inventory, Store<BuiltButton> buttons) {
        this.inventory = inventory;
        this.buttons = buttons;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.inventory);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }

        final BuiltInventory that = (BuiltInventory) obj;
        return Objects.equals(this.inventory, that.inventory);
    }

    public Inventory inventory() {
        return this.inventory;
    }

    public Store<BuiltButton> buttons() {
        return this.buttons;
    }

    public void rebuildButton(Query query) {
        if (this.inventory == null) {
            return;
        }

        final List<BuiltButton> builtButtons = this.buttons.get(query);
        if (this.inventory.getHolder() instanceof Menu menu) {
            for (final BuiltButton builtButton : builtButtons) {
                final BuiltButton button = menu.createButton(builtButton.slot(), builtButton.template());
                menu.applyBuiltButton(button);
            }
        }
    }

    public void rebuildButton(int slot) {
        this.rebuildButton(Query.where("slot", slot));
    }

    public void rebuildButton(String identifier) {
        this.rebuildButton(Query.where("template", identifier));
    }

    public void rebuildButton(char symbol) {
        this.rebuildButton(Query.where("symbol", String.valueOf(symbol)));
    }

    public void refreshTitle() {

    }
}
