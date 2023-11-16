package net.plexverse.mapparser.util.menu.configurable;

import com.google.common.collect.Lists;
import com.oop.memorystore.api.Store;
import com.oop.memorystore.implementation.memory.MemoryStore;

import java.util.List;

public class ConfigurableMenu {
    private final String menuTitle;
    private final List<String> menuDesign;
    private final List<ConfigurableMenuButton> items;
    private final transient Store<ConfigurableMenuButton> cache = new MemoryStore<>();

    public ConfigurableMenu(Builder builder) {
        this.menuTitle = builder.menuTitle;
        this.menuDesign = builder.menuDesign;
        this.items = builder.items;

        this.index();
    }

    public void index() {
        this.cache.index("symbol", ConfigurableMenuButton::getKey);
        this.cache.index("identifier", ConfigurableMenuButton::getName);

        this.cache.addAll(this.items);
        this.cache.reindex();
    }

    public String getMenuTitle() {
        return this.menuTitle;
    }

    public List<String> getMenuDesign() {
        return this.menuDesign;
    }

    public List<ConfigurableMenuButton> getItems() {
        return this.items;
    }

    public Store<ConfigurableMenuButton> getCache() {
        return this.cache;
    }

    public static class Builder {
        private final List<String> menuDesign = Lists.newLinkedList();
        private final List<ConfigurableMenuButton> items = Lists.newArrayList();
        private String menuTitle;

        public Builder menuTitle(String menuTitle) {
            this.menuTitle = menuTitle;
            return this;
        }

        public Builder menuDesign(String row) {
            this.menuDesign.add(row);
            return this;
        }

        public Builder menuDesign(String... rows) {
            this.menuDesign.addAll(Lists.newArrayList(rows));
            return this;
        }

        public Builder menuDesign(List<String> row) {
            this.menuDesign.addAll(row);
            return this;
        }

        public Builder item(ConfigurableMenuButton item) {
            this.items.add(item);
            return this;
        }

        public Builder items(List<ConfigurableMenuButton> item) {
            this.items.addAll(item);
            return this;
        }

        public ConfigurableMenu build() {
            return new ConfigurableMenu(this);
        }
    }
}
