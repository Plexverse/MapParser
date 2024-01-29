package net.plexverse.mapparser.menu;

import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import net.plexverse.mapparser.enums.GameType;
import net.plexverse.mapparser.mapsettings.MapSettingsManager;
import net.plexverse.mapparser.mapsettings.objects.MapMeta;
import net.plexverse.mapparser.menu.items.ClickableItem;
import net.plexverse.mapparser.menu.items.StateItem;
import net.plexverse.mapparser.menu.items.ext.BooleanState;
import net.plexverse.mapparser.menu.items.ext.GameTypeState;
import net.plexverse.mapparser.util.asker.InputAsker;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.impl.SimpleItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Getter
public class MapSettingsMenu extends PagedMenu {
    private final MapMeta mapSettings;
    private final World world;

    private static final InputAsker NAME_ASKER = new InputAsker("<light_purple>Please enter the desired name");
    private static final InputAsker AUTHOR_ASKER = new InputAsker("<light_purple>Please enter the desired author(s) (UUIDS). (Seperate author uuids with a ',')");

    public MapSettingsMenu(World world, MapMeta mapSettings) {
        super(new ArrayList<>());
        this.mapSettings = mapSettings;
        this.world = world;

        final List<Item> itemList = new ArrayList<>();

        // game state
        final List<GameTypeState> states = Arrays.stream(GameType.values()).map(GameTypeState::new).toList();
        final StateItem stateItem = new StateItem(states, Arrays.stream(GameType.values()).toList().indexOf(mapSettings.getGameType()), state -> {
            mapSettings.setGameType(((GameTypeState) state).getGameType());
            try {
                MapSettingsManager.saveMapSettings(world, mapSettings);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return null;
        });
        itemList.add(stateItem);

        // legacy
        final List<BooleanState> legacy = Stream.of(true, false).map(v -> new BooleanState(v, "Legacy")).toList();
        final StateItem legacyItem = new StateItem(legacy, List.of(true, false).indexOf(mapSettings.isLegacy()), state -> {
            mapSettings.setLegacy(((BooleanState) state).isValue());
            try {
                MapSettingsManager.saveMapSettings(world, mapSettings);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return null;
        });
        itemList.add(legacyItem);

        itemList.add(getAuthorsItem());
        itemList.add(getMapNameItem());

        setContent(itemList);
    }

    public Item getAuthorsItem() {
        final ItemStack itemStack = new ItemStack(Material.WRITTEN_BOOK);
        itemStack.editMeta(ItemMeta.class, itemMeta -> itemMeta.setDisplayName("Author(s)"));
        itemStack.editMeta(ItemMeta.class, itemMeta -> itemMeta.setLore(mapSettings.parseAuthorUuids().stream().map(uuid -> {
            final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
            if(!offlinePlayer.hasPlayedBefore()) return ChatColor.YELLOW + uuid.toString();
            return ChatColor.YELLOW + offlinePlayer.getName();
        }).toList()));
        final ClickableItem clickableItem = new ClickableItem(new SimpleItem(itemStack).getItemProvider(), player -> {
            AUTHOR_ASKER.ask(player, (response) -> {
                player.getInventory().close();

                if(!response.equals("null")) {
                    try {
                        final String[] authors = response.split(",");
                        for (final String author : authors) UUID.fromString(author);
                    } catch (Exception e) {
                        player.sendMessage(ChatColor.RED + "Read the instructions carefully, uuids separated by dashes...");
                        return;
                    }
                }

                mapSettings.setAuthor(response);
                try {
                    MapSettingsManager.saveMapSettings(world, mapSettings);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                player.sendMessage("You set authors to " + response);
            });
            return null;
        });
        return clickableItem;
    }

    public Item getMapNameItem() {
        final ItemStack itemStack = new ItemStack(Material.NAME_TAG);
        itemStack.editMeta(ItemMeta.class, itemMeta -> itemMeta.setDisplayName("Name: " + mapSettings.getMapName()));
        final ClickableItem clickableItem = new ClickableItem(new SimpleItem(itemStack).getItemProvider(), player -> {
            NAME_ASKER.ask(player, (response) -> {
                player.getInventory().close();
                mapSettings.setMapName(response);
                try {
                    MapSettingsManager.saveMapSettings(world, mapSettings);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                player.sendMessage("You set map name to " + response);
            });
            return null;
        });
        return clickableItem;
    }


}
