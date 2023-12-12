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
            "$$$P$W$$$",
            "$$C$I$M$$",
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
            .defaultItem(ItemBuilder.create(Material.GLASS)
                .name("<light_purple><b>Wall Base</b>")
                .lore(
                    "",
                    "<gray><i>(( Click to define as a (Team) Wall Base ))"
                ).build()
            ).build()
        ).item(new ConfigurableMenuButton.Builder()
            .name("chest")
            .key('C')
            .defaultItem(ItemBuilder.create(Material.CHEST)
                .name("<light_purple><b>Chest</b>")
                .lore(
                    "",
                    "<gray><i>(( Click to define as a (Team) Chest ))"
                ).build()
            ).build()
        ).item(new ConfigurableMenuButton.Builder()
            .name("island-border")
            .key('I')
            .defaultItem(ItemBuilder.create(Material.BARRIER)
                .name("<light_purple><b>Island Border</b>")
                .lore(
                    "",
                    "<gray><i>(( Click to define as a (Team) Island Border ))"
                ).build()
            ).build()
        ).item(new ConfigurableMenuButton.Builder()
            .name("mid-chest")
            .key('M')
            .defaultItem(ItemBuilder.create(Material.ENDER_CHEST)
                .name("<light_purple><b>Mid Chest</b>")
                .lore(
                    "",
                    "<gray><i>(( Click to define as a Mid Chest ))"
                ).build()
            ).build()
        ).build();

    public static final ConfigurableMenu MODIFY_MENU = new ConfigurableMenu.Builder()
        .menuTitle("<red>Modify %data_point% Data Point")
        .menuDesign(
            "$$$$$$$$$",
            "$$T$Y$P$$",
            "$$$$$$$$$",
            "$$C$$$A$$",
            "$$$$$$$$$"
        ).item(new ConfigurableMenuButton.Builder()
            .name("bg")
            .key('$')
            .defaultItem(ItemBuilder.create(Material.PURPLE_STAINED_GLASS_PANE).build())
            .build()
        ).item(new ConfigurableMenuButton.Builder()
            .name("team")
            .key('T')
            .defaultItem(ItemBuilder.create(Material.RED_BED)
                .name("<light_purple><b>Change Team</b>")
                .lore(
                    "  <dark_purple><b>Current Team:</b> <white>%team%",
                    "<gray><i>(( Click to change the Team ))"
                ).build()
            ).build()
        ).item(new ConfigurableMenuButton.Builder()
            .name("yaw")
            .key('Y')
            .defaultItem(ItemBuilder.create(Material.OAK_SIGN)
                .name("<light_purple><b>Change Yaw</b>")
                .lore(
                    "",
                    "<gray><i>(( Click to change the Yaw ))"
                ).build()
            ).build()
        ).item(new ConfigurableMenuButton.Builder()
            .name("pitch")
            .key('P')
            .defaultItem(ItemBuilder.create(Material.BARRIER)
                .name("<light_purple><b>Chance Pitch</b>")
                .lore(
                    "",
                    "<gray><i>(( Click to change the Pitch ))"
                ).build()
            ).build()
        ).item(new ConfigurableMenuButton.Builder()
            .name("custom-team")
            .key('C')
            .defaultItem(ItemBuilder.create(Material.RED_BED)
                .name("<light_purple><b>Change to Custom Team</b>")
                .lore(
                    "  <dark_purple><b>Current Team:</b> <white>%team%",
                    "<gray><i>(( Click to change the Team by specifying the name in Chat ))"
                ).build()
            ).build()
        ).item(new ConfigurableMenuButton.Builder()
            .name("view")
            .key('V')
            .defaultItem(ItemBuilder.create(Material.DISPENSER)
                .name("<light_purple><b>First Person View</b>")
                .lore(
                    "",
                    "<gray><i>(( Click to obtain this Datapoint in your Inventory ))"
                ).build()
            ).build()
        ).item(new ConfigurableMenuButton.Builder()
            .name("clone")
            .key('A')
            .defaultItem(ItemBuilder.create(Material.STICKY_PISTON)
                .name("<light_purple><b>Clone</b>")
                .lore(
                    "",
                    "<gray><i>(( Click to clone to an item ))"
                ).build()
            ).build()
        ).build();


    public static final ConfigurableMenu KIT_CREATOR = new ConfigurableMenu.Builder()
        .menuTitle("<red>Kit Creator")
        .menuDesign(
            "$$$$$$$$$",
            "$$N$G$C$$",
            "$$$$$$$$$"
        ).item(new ConfigurableMenuButton.Builder()
            .name("bg")
            .key('$')
            .defaultItem(ItemBuilder.create(Material.PURPLE_STAINED_GLASS_PANE).build())
            .build()
        ).item(new ConfigurableMenuButton.Builder()
            .name("kit-name")
            .key('N')
            .defaultItem(ItemBuilder.create(Material.NAME_TAG)
                .name("<light_purple><b>Kit Name</b>")
                .lore(
                    "",
                    "<light_purple><b>Current Name:</b> <white>%kit_name%",
                    "",
                    "<gray><i>(( Click to define the Kit's Name ))"
                ).build()
            ).build()
        ).item(new ConfigurableMenuButton.Builder()
            .name("game-type")
            .key('G')
            .defaultItem(ItemBuilder.create(Material.GRASS_BLOCK)
                .name("<light_purple><b>Game Type</b>")
                .lore(
                    "",
                    "<light_purple><b>Game:</b> <white>%game%",
                    "",
                    "<gray><i>(( Click to define the Game ))"
                ).build()
            ).build()
        ).item(new ConfigurableMenuButton.Builder()
            .name("create")
            .key('C')
            .defaultItem(ItemBuilder.create(Material.BARRIER)
                .name("<light_purple><b>Create Kit</b>")
                .lore(
                    "",
                    "<gray><i>(( Click to create the kit using your inventory contents ))"
                ).build()
            ).build()
        ).build();
}
