package net.plexverse.mapparser.constant;

import net.plexverse.mapparser.util.item.ItemBuilder;
import net.plexverse.mapparser.util.menu.configurable.ConfigurableMenu;
import net.plexverse.mapparser.util.menu.configurable.ConfigurableMenuButton;
import org.bukkit.Material;

public class Menus {
    public static final ConfigurableMenu DATA_POINT_MENU = new ConfigurableMenu.Builder()
        .menuTitle("<red>Data Point")
        .menuDesign(
            "$$$$$$$$$",
            "$$S$H$B$$",
            "$$$$$$$$$",
            "$$$P$W$$$",
            "$$$$$$$$$"
        ).item(new ConfigurableMenuButton.Builder()
            .name("bg")
            .key('$')
            .defaultItem(ItemBuilder.create(Material.PURPLE_STAINED_GLASS_PANE).build())
            .build()
        ).item(new ConfigurableMenuButton.Builder()
            .name("spawnpoint")
            .key('S')
            .defaultItem(ItemBuilder.create(Material.RED_BED)
                .name("<light_purple><b>Spawn Point</b>")
                .lore(
                    "",
                    "<gray><i>(( Click to define as a (Team) Spawn Point ))"
                ).build()
            ).build()
        ).item(new ConfigurableMenuButton.Builder()
            .name("hologram")
            .key('H')
            .defaultItem(ItemBuilder.create(Material.OAK_SIGN)
                .name("<light_purple><b>Hologram</b>")
                .lore(
                    "",
                    "<gray><i>(( Click to define as a (Team) Hologram ))"
                ).build()
            ).build()
        ).item(new ConfigurableMenuButton.Builder()
            .name("border")
            .key('B')
            .defaultItem(ItemBuilder.create(Material.BARRIER)
                .name("<light_purple><b>Border</b>")
                .lore(
                    "",
                    "<gray><i>(( Click to define as a Border Point ))",
                    "<gray><i>(( Be sure to place one at the other corner! ))"
                ).build()
            ).build()
        ).item(new ConfigurableMenuButton.Builder()
            .name("spectator-spawnpoint")
            .key('P')
            .defaultItem(ItemBuilder.create(Material.PLAYER_HEAD)
                .name("<light_purple><b>Spectator Spawn Point</b>")
                .lore(
                    "",
                    "<gray><i>(( Click to define as a Spectator Spawn Point ))"
                ).build()
            ).build()
        ).item(new ConfigurableMenuButton.Builder()
            .name("wall-base")
            .key('W')
            .defaultItem(ItemBuilder.create(Material.PLAYER_HEAD)
                .name("<light_purple><b>Wall Base</b>")
                .lore(
                    "",
                    "<gray><i>(( Click to define as a (Team) Wall Base ))"
                ).build()
            ).build()
        ).build();
}
