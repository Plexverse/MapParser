package net.plexverse.mapparser.util.menu.configurable;

import com.google.common.collect.Maps;
import net.plexverse.mapparser.util.menu.button.ButtonState;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.Map.Entry;

public class ConfigurableMenuButton {
    private final String key;
    private final String name;
    private final Map<String, ItemStack> items;
    private final transient Map<String, ButtonState> states = Maps.newHashMap();

    public ConfigurableMenuButton(Builder builder) {
        this.key = Character.toString(builder.key);
        this.name = builder.name;
        this.items = builder.items;

        this.index();
    }

    public void index() {
        for (final Entry<String, ItemStack> entry : this.items.entrySet()) {
            this.states.put(entry.getKey(), ButtonState.builder().name(entry.getKey()).item(entry.getValue()).build());
        }
    }

    public String getKey() {
        return this.key;
    }

    public String getName() {
        return this.name;
    }

    public Map<String, ItemStack> getItems() {
        return this.items;
    }

    public Map<String, ButtonState> getStates() {
        return this.states;
    }

    public static class Builder {
        private final Map<String, ItemStack> items = Maps.newHashMap();
        private char key;
        private String name;

        public Builder key(char key) {
            this.key = key;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder defaultItem(ItemStack item) {
            this.items.put("default", item);
            return this;
        }

        public Builder item(String state, ItemStack item) {
            this.items.put(state, item);
            return this;
        }

        public Builder items(Map<String, ItemStack> items) {
            this.items.putAll(items);
            return this;
        }

        public ConfigurableMenuButton build() {
            return new ConfigurableMenuButton(this);
        }
    }
}
