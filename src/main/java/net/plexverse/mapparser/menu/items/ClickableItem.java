package net.plexverse.mapparser.menu.items;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.impl.AbstractItem;

import java.util.function.Function;

public class ClickableItem extends AbstractItem {

    private final ItemProvider itemProvider;
    private final Function<Player, Void> function;

    public ClickableItem(final ItemProvider itemProvider, Function<Player, Void> function) {
        this.itemProvider = itemProvider;
        this.function = function;
    }

    @Override
    public ItemProvider getItemProvider() {
        return itemProvider;
    }

    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        function.apply(player);
        if(event.getClickedInventory() != null) event.getClickedInventory().close();
    }
}
