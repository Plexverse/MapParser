package net.plexverse.mapparser.util.message;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.plexverse.mapparser.util.message.replacer.IReplacerSkeleton;
import net.plexverse.mapparser.util.message.replacer.entry.ReplacerEntry;
import net.plexverse.mapparser.util.message.replacer.replacer.CollectionReplacer;
import net.plexverse.mapparser.util.message.replacer.replacer.ComponentReplacer;
import net.plexverse.mapparser.util.message.replacer.replacer.StringReplacer;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@SuppressWarnings("rawtypes")
public class Replacer {
    public static final Replacer GLOBAL_REPLACER = new Replacer(false);
    public static final String STRING_IDENTIFIER = "-s-";
    private static final Set<IReplacerSkeleton> REGISTERED_REPLACERS = new HashSet<>();

    static {
        REGISTERED_REPLACERS.add(new StringReplacer());
        REGISTERED_REPLACERS.add(new CollectionReplacer());
        REGISTERED_REPLACERS.add(new ComponentReplacer());
    }

    public final Set<ReplacerEntry> replacers = Collections.synchronizedSet(new LinkedHashSet<>());

    public Replacer() {
        this(true);
    }

    protected Replacer(boolean merge) {
        if (merge) {
            this.merge(GLOBAL_REPLACER);
        }
    }

    public static Replacer of(Object... placeholders) {
        if (placeholders.length % 2 != 0) {
            throw new IllegalStateException("Size should be dividable by 2");
        }

        final Replacer replacer = new Replacer();
        for (int i = 0; i < placeholders.length; i++) {
            final String key = placeholders[i].toString();
            final Object value = placeholders[i + 1];

            replacer.replaceLiteral(key, value == null ? "null" : value.toString());
            i++;
        }

        return replacer;
    }

    public static String replaceText(String input, String replacement, boolean ignoreCase, String... keys) {
        String replaced = input;
        for (final String key : keys) {
            replaced = replaceText(input, key, replacement, ignoreCase);
        }

        return replaced;
    }

    private static String replaceText(String input, String key, String replacement, boolean ignoreCase) {
        return ignoreCase ? Replacer.replaceIgnoreCase(input, key, replacement) : StringUtils.replace(input, key, replacement);
    }

    private static String replaceIgnoreCase(String input, String key, String replacement) {
        final StringBuilder sbSource = new StringBuilder(input);
        final StringBuilder sbSourceLower = new StringBuilder(input.toLowerCase());
        final String searchString = key.toLowerCase();

        int idx = 0;
        while ((idx = sbSourceLower.indexOf(searchString, idx)) != -1) {
            sbSource.replace(idx, idx + searchString.length(), replacement);
            sbSourceLower.replace(idx, idx + searchString.length(), replacement);
            idx += replacement.length();
        }

        sbSourceLower.setLength(0);
        sbSourceLower.trimToSize();

        return sbSource.toString();
    }

    public static <T> IReplacerSkeleton findReplacer(Class<T> clazz) {
        for (final IReplacerSkeleton<?> registeredReplacer : REGISTERED_REPLACERS) {
            if (registeredReplacer.accepts(clazz)) {
                return registeredReplacer;
            }
        }

        return null;
    }

    public static Component replaceColor(Component component, String match, NamedTextColor color) {
        if (!(component instanceof TextComponent textComponent)) {
            return component;
        }

        final Function<Component, Component> wrap = (toWrap) -> toWrap
            .color(color)
            .decorations(new IdentityHashMap<>())
            .children(textComponent.children()
                .stream().map((c) -> replaceColor(c, match, color))
                .collect(Collectors.toList())
            );

        final String content = textComponent.content();
        if (content.contains(match)) {
            final String[] split = content.split(Pattern.quote(match));

            if (split.length == 0) {
                return wrap.apply(Component.text(""));
            }

            if (split.length == 1) {
                return wrap.apply(Component.text(split[0]));
            }

            if (split.length == 2) {
                final String part1 = split[0];
                final String part2 = split[1];

                if (part1.isEmpty()) {
                    return wrap.apply(Component.text(part2));
                }

                if (part2.isEmpty()) {
                    return wrap.apply(Component.text(part1));
                }
                return Component.text(part1).mergeStyle(textComponent).children(
                    List.of(wrap.apply(Component.text(part2)))
                );
            }
        }

        return component.children(textComponent.children()
            .stream().map((c) -> replaceColor(c, match, color))
            .collect(Collectors.toList())
        );
    }

    public static <T> T accept(T object, Replacer replacer) {
        for (final IReplacerSkeleton registeredReplacer : REGISTERED_REPLACERS) {
            if (!registeredReplacer.accepts(object.getClass())) {
                continue;
            }

            return (T) registeredReplacer.accept(object, replacer);
        }

        throw new IllegalStateException(String.format("No acceptable replacer found for %s (loader: %s)", object.getClass(),
            object.getClass().getClassLoader()
        ));
    }

    public Replacer replaceLiteral(String key, String value) {
        return this.replaceLiteral(key, value, false);
    }

    public Replacer merge(Replacer other) {
        this.replacers.addAll(other.replacers);
        return this;
    }

    public Replacer replace(UnaryOperator<ReplacerEntry.Builder> builder) {
        final ReplacerEntry replacerEntry = builder.apply(new ReplacerEntry.Builder()).build();
        this.removeIfFound(replacerEntry);
        this.replacers.add(replacerEntry);
        return this;
    }

    private void removeIfFound(ReplacerEntry replacerEntry) {
        this.replacers.removeIf((value) -> Arrays.equals(value.keys, replacerEntry.keys));
    }

    public Replacer replaceComponent(String key, Component with) {
        return this.replaceComponent(key, with, false);
    }

    public Replacer replaceComponent(String key, Component with, boolean ignoreCase) {
        return this.replace((builder) -> builder.contentComponent(() -> with).key(key).ignoreCase(ignoreCase));
    }

    public Replacer replaceWithSupplier(String key, boolean ignoreCase, Supplier<String> supplier) {
        return this.replace((builder) -> builder.key(key).ignoreCase(ignoreCase).contentPlain(() -> List.of(supplier.get())));
    }

    public Replacer replaceWithSupplier(String key, Supplier<Component> supplier) {
        return this.replace((builder) -> builder.key(key).contentComponent(supplier));
    }

    public <T> T accept(T object) {
        return Replacer.accept(object, this);
    }

    public Replacer replaceFromMap(Map<String, String> map) {
        return this.replaceFromMap(map, false);
    }

    public Replacer replaceFromMap(Map<String, String> map, boolean ignoreCase) {
        for (final Entry<String, String> entry : map.entrySet()) {
            this.replaceLiteral(entry.getKey(), entry.getValue(), ignoreCase);
        }

        return this;
    }

    public Replacer replaceLiteral(String key, String value, boolean ignoreCase) {
        return this.replace((builder) -> builder.key(key).contentPlain(value).ignoreCase(ignoreCase));
    }
}
