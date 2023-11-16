package net.plexverse.mapparser.util.message;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import java.util.List;
import java.util.regex.Pattern;

public class Text {
    public static final char COLOR_CHAR = '\u00A7';
    private static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)(%s|&)[0-9A-FK-OR]".formatted(Text.COLOR_CHAR));

    public static <T extends List<String>> T colorize(T collection) {
        for (int i = 0; i < collection.size(); i++) {
            collection.set(i, Text.colorize(collection.get(i)));
        }

        return collection;
    }

    public static String asPlain(Component component) {
        return PlainTextComponentSerializer.plainText().serialize(component);
    }

    public static String colorize(String string) {
        return Text.convertColors('&', Text.COLOR_CHAR, string);
    }

    public static String convertColors(char currentColorChar, char newColorChar, String textToTranslate) {
        final char[] b = textToTranslate.toCharArray();
        for (int i = 0; i < b.length - 1; i++) {
            if (b[i] == currentColorChar && "0123456789AaBbCcDdEeFfKkLlMmNnOoRrXx".indexOf(b[i + 1]) > -1) {
                b[i] = newColorChar;
                b[i + 1] = Character.toLowerCase(b[i + 1]);
            }
        }
        return new String(b);
    }

    public static String stripColors(String text) {
        if (text == null) {
            return null;
        }

        return Text.STRIP_COLOR_PATTERN.matcher(text).replaceAll("");
    }
}
