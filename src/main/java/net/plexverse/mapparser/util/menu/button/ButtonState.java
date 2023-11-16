package net.plexverse.mapparser.util.menu.button;

import net.plexverse.mapparser.util.item.ItemBuilder;
import net.plexverse.mapparser.util.message.Replacer;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.function.UnaryOperator;

public class ButtonState {
    private String name;
    private ItemStack item;

    public ButtonState(@Nullable String name, ItemStack item) {
        this.name = name;
        this.item = item;
    }

    public static ButtonStateBuilder builder() {
        return new ButtonStateBuilder();
    }

    public ButtonState copy() {
        return new ButtonState(this.name, this.item.clone());
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ItemStack getItem() {
        return this.item;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public ButtonState replace(Replacer replacer) {
        this.setItem(ItemBuilder.create(this.item).replace(replacer).build());
        return this;
    }

    public static class ButtonStateBuilder {
        private String name;
        private ItemStack item;

        ButtonStateBuilder() {
        }

        public String toString() {
            return "ButtonState.ButtonStateBuilder(name=" + this.name + ", item=" + this.item + ")";
        }

        public ButtonStateBuilder name(String name) {
            this.name = name;
            return this;
        }

        public ButtonStateBuilder item(UnaryOperator<ItemBuilder> builder) {
            return this.item(builder.apply(ItemBuilder.create()).build());
        }

        public ButtonStateBuilder item(ItemStack item) {
            this.item = item;
            return this;
        }

        public ButtonState build() {
            return new ButtonState(this.name, this.item);
        }
    }
}
