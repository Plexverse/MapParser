package net.plexverse.mapparser.util.menu.button;

import net.plexverse.mapparser.util.menu.configurable.ConfigurableMenuButton;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public final class BuiltButton {
    private final ItemStack item;
    private final int slot;
    private final String state;
    private final ConfigurableMenuButton template;

    public BuiltButton(ItemStack item, int slot, String state, ConfigurableMenuButton template) {
        this.item = item;
        this.slot = slot;
        this.state = state;
        this.template = template;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        final BuiltButton that = (BuiltButton) obj;
        return Objects.equals(this.item, that.item) &&
            this.slot == that.slot &&
            Objects.equals(this.state, that.state) &&
            Objects.equals(this.template, that.template);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.item, this.slot, this.state, this.template);
    }

    @Override
    public String toString() {
        return "BuiltButton[" +
            "item=" + this.item + ", " +
            "slot=" + this.slot + ", " +
            "state=" + this.state + ", " +
            "template=" + this.template + ']';
    }

    public ItemStack item() {
        return this.item;
    }

    public int slot() {
        return this.slot;
    }

    public String state() {
        return this.state;
    }

    public ConfigurableMenuButton template() {
        return this.template;
    }
}
