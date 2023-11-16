package net.plexverse.mapparser.util.message.replacer.replacer;

import net.plexverse.mapparser.util.message.ComponentHelper;
import net.plexverse.mapparser.util.message.Replacer;
import net.plexverse.mapparser.util.message.Text;
import net.plexverse.mapparser.util.message.replacer.IReplacerSkeleton;
import net.plexverse.mapparser.util.message.replacer.entry.ReplacerEntry;

public class StringReplacer implements IReplacerSkeleton<String> {
    @Override
    public boolean accepts(Class<?> aClass) {
        return String.class.isAssignableFrom(aClass);
    }

    @Override
    public String accept(String object, Replacer replacer) {
        String replaced = object;

        for (final ReplacerEntry replacerEntry : replacer.replacers) {
            String replaceWith = ComponentHelper.miniMessageFromComponent(replacerEntry.supplyContent());
            if (replaceWith.endsWith(Replacer.STRING_IDENTIFIER)) {
                replaceWith = replaceWith.replace(Replacer.STRING_IDENTIFIER, "");
            }

            replaced = Replacer.replaceText(replaced, Text.colorize(replaceWith), replacerEntry.ignoreCase, replacerEntry.keys);
        }

        return replaced.replace("\\<", "<");
    }
}