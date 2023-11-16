package net.plexverse.mapparser.util.message;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class ComponentHelper {

    public static final Map<Character, NamedTextColor> BUKKIT_COLOR_TO_ADVENTURE = new HashMap<>();
    public static final Map<Character, TextDecoration> BUKKIT_DECORATION_TO_ADVENTURE = new HashMap<>();
    public static LegacyComponentSerializer SERIALIZER_WITH_URL = LegacyComponentSerializer.builder()
        .extractUrls()
        .character('§')
        .build();

    public static LegacyComponentSerializer SERIALIZER_WITHOUT_URL = LegacyComponentSerializer.builder()
        .character('§')
        .build();

    static {
        ComponentHelper.BUKKIT_COLOR_TO_ADVENTURE.put('0', NamedTextColor.BLACK);
        ComponentHelper.BUKKIT_COLOR_TO_ADVENTURE.put('1', NamedTextColor.DARK_BLUE);
        ComponentHelper.BUKKIT_COLOR_TO_ADVENTURE.put('2', NamedTextColor.DARK_GREEN);
        ComponentHelper.BUKKIT_COLOR_TO_ADVENTURE.put('3', NamedTextColor.DARK_AQUA);
        ComponentHelper.BUKKIT_COLOR_TO_ADVENTURE.put('4', NamedTextColor.DARK_RED);
        ComponentHelper.BUKKIT_COLOR_TO_ADVENTURE.put('5', NamedTextColor.DARK_PURPLE);
        ComponentHelper.BUKKIT_COLOR_TO_ADVENTURE.put('6', NamedTextColor.GOLD);
        ComponentHelper.BUKKIT_COLOR_TO_ADVENTURE.put('7', NamedTextColor.GRAY);
        ComponentHelper.BUKKIT_COLOR_TO_ADVENTURE.put('8', NamedTextColor.DARK_GRAY);
        ComponentHelper.BUKKIT_COLOR_TO_ADVENTURE.put('9', NamedTextColor.BLUE);
        ComponentHelper.BUKKIT_COLOR_TO_ADVENTURE.put('a', NamedTextColor.GREEN);
        ComponentHelper.BUKKIT_COLOR_TO_ADVENTURE.put('b', NamedTextColor.AQUA);
        ComponentHelper.BUKKIT_COLOR_TO_ADVENTURE.put('c', NamedTextColor.RED);
        ComponentHelper.BUKKIT_COLOR_TO_ADVENTURE.put('d', NamedTextColor.LIGHT_PURPLE);
        ComponentHelper.BUKKIT_COLOR_TO_ADVENTURE.put('e', NamedTextColor.YELLOW);
        ComponentHelper.BUKKIT_COLOR_TO_ADVENTURE.put('f', NamedTextColor.WHITE);
        ComponentHelper.BUKKIT_DECORATION_TO_ADVENTURE.put('k', TextDecoration.OBFUSCATED);
        ComponentHelper.BUKKIT_DECORATION_TO_ADVENTURE.put('l', TextDecoration.BOLD);
        ComponentHelper.BUKKIT_DECORATION_TO_ADVENTURE.put('m', TextDecoration.STRIKETHROUGH);
        ComponentHelper.BUKKIT_DECORATION_TO_ADVENTURE.put('n', TextDecoration.UNDERLINED);
        ComponentHelper.BUKKIT_DECORATION_TO_ADVENTURE.put('o', TextDecoration.ITALIC);
    }

    public static Component listedTextOf(String... lines) {
        return listedTextOf(List.of(lines));
    }

    public static Component listedTextOf(List<String> lines) {
        TextComponent component = lines.size() == 1 ? Component.empty() : Component.empty().append(Component.newline());
        final Iterator<String> iterator = lines.iterator();

        while (iterator.hasNext()) {
            final String next = iterator.next();
            component = component.append(ComponentHelper.componentFromString(SERIALIZER_WITHOUT_URL, next));
            if (iterator.hasNext()) {
                component = component.append(Component.newline());
            }
        }

        return component;
    }

    public static Component componentFromString(String content) {
        return componentFromString(SERIALIZER_WITH_URL, content);
    }

    public static Component componentFromString(LegacyComponentSerializer serializer, String content) {
        return serializer.deserialize(Text.colorize(content));
    }

    public static Component replace(Replacer replacer, Component component) {
        return ComponentHelper.componentFromString(replacer.accept(ComponentHelper.contentFromComponent(component)));
    }

    public static String contentFromComponent(Component component) {
        return SERIALIZER_WITH_URL.serialize(component);
    }

    public static String miniMessageFromComponent(Component component) {
        return MiniMessage.miniMessage().serialize(component);
    }

    public static List<String> contentFromComponent(List<Component> components) {
        return components.stream().map(ComponentHelper::contentFromComponent).collect(Collectors.toList());
    }

    public static NamedTextColor matchColor(String rawContent) {
        if (rawContent.length() != 2) {
            return null;
        }

        final char colorChar = rawContent.toCharArray()[0];
        final char color = rawContent.toCharArray()[1];

        if (!(colorChar == Text.COLOR_CHAR || colorChar == '&')) {
            return null;
        }

        return BUKKIT_COLOR_TO_ADVENTURE.get(color);
    }
}

