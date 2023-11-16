package net.plexverse.mapparser.util.message.replacer.replacer;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.plexverse.mapparser.util.message.ComponentHelper;
import net.plexverse.mapparser.util.message.Replacer;
import net.plexverse.mapparser.util.message.replacer.IReplacerSkeleton;
import net.plexverse.mapparser.util.message.replacer.entry.ReplacerEntry;

import java.util.regex.Pattern;

public class ComponentReplacer implements IReplacerSkeleton<Component> {
    @Override
    public boolean accepts(Class<?> aClass) {
        return Component.class.isAssignableFrom(aClass);
    }

    @Override
    public Component accept(Component object, Replacer replacer) {
        Component replaced = object;

        for (final ReplacerEntry replacerEntry : replacer.replacers) {
            Component replaceWith = replacerEntry.supplyContent();
            String rawContent = ComponentHelper.contentFromComponent(replaceWith);

            if (rawContent.endsWith(Replacer.STRING_IDENTIFIER)) {
                rawContent = rawContent.replace(Replacer.STRING_IDENTIFIER, "");
                final NamedTextColor namedTextColor = ComponentHelper.matchColor(rawContent);

                if (namedTextColor != null) {
                    for (final String key : replacerEntry.keys) {
                        replaced = Replacer.replaceColor(replaced, key, namedTextColor);
                    }
                    continue;
                }

                replaceWith = ComponentHelper.componentFromString(rawContent);
            }

            final Component finalReplaceWith = replaceWith;
            for (final String key : replacerEntry.keys) {
                replaced = replaced.replaceText((builder) -> {
                    int flag = Pattern.LITERAL;

                    if (replacerEntry.ignoreCase) {
                        flag = flag | Pattern.CASE_INSENSITIVE;
                    }

                    builder.match(Pattern.compile(key, flag));
                    builder.replacement(finalReplaceWith);
                });
            }
        }

        return replaced;
    }
}