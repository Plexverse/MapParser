package net.plexverse.mapparser.util.message.replacer.entry;

import net.kyori.adventure.text.Component;
import net.plexverse.mapparser.util.message.ComponentHelper;
import net.plexverse.mapparser.util.message.Replacer;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

public class ReplacerEntry {
    public final String[] keys;
    private final long cacheForMS;
    private final Supplier<Component> content;
    public boolean ignoreCase;
    private long lastSupplied = -1;
    private Component cache;

    public ReplacerEntry(Builder builder) {
        this.keys = builder.keys.toArray(new String[0]);
        this.content = builder.contentSupplier;
        this.cacheForMS = builder.cacheForMs;
        this.ignoreCase = builder.ignoreCase;
    }

    public Component supplyContent() {
        final Supplier<Component> supply = () -> {
            final Component result = this.content.get();

            if (this.isCacheEnabled()) {
                this.lastSupplied = System.currentTimeMillis();
                this.cache = result;
            }

            return result;
        };

        if (this.isCacheEnabled()) {
            if (this.cache == null) {
                return supply.get();
            }

            if ((System.currentTimeMillis() - this.lastSupplied) >= this.cacheForMS) {
                return supply.get();
            }

            return this.cache;
        }

        return supply.get();
    }

    private boolean isCacheEnabled() {
        return this.cacheForMS != -1;
    }

    public static class Builder {
        private Collection<String> keys = new LinkedList<>();
        private Supplier<Component> contentSupplier;
        private boolean ignoreCase = false;
        private long cacheForMs = -1;

        public Builder keys(Collection<String> keys) {
            this.keys = new LinkedList<>(keys);
            return this;
        }

        public Builder key(String... keys) {
            this.keys.addAll(Arrays.asList(keys));
            return this;
        }

        public Builder contentPlain(String content) {
            return this.contentPlain(() -> List.of(content));
        }

        public Builder contentPlain(Supplier<List<String>> supplier) {
            this.contentSupplier = () -> ComponentHelper.listedTextOf(this.markString(supplier.get()));
            return this;
        }

        private List<String> markString(List<String> value) {
            final List<String> marked = new LinkedList<>();
            for (final String toMark : value) {
                marked.add(toMark + Replacer.STRING_IDENTIFIER);
            }

            return marked;
        }

        public Builder contentComponent(Supplier<Component> supplier) {
            this.contentSupplier = supplier;
            return this;
        }

        public Builder ignoreCase(boolean ignoreCase) {
            this.ignoreCase = ignoreCase;
            return this;
        }

        public Builder cacheFor(long timeMS) {
            this.cacheForMs = timeMS;
            return this;
        }

        public ReplacerEntry build() {
            return new ReplacerEntry(this);
        }
    }
}