package net.plexverse.mapparser.menu.buttons;

import lombok.Data;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.impl.SimpleItem;
import xyz.xenondevs.invui.item.impl.controlitem.ControlItem;

import java.util.Objects;

@Data
    public class ExitButton extends ControlItem<Gui> {

        @Override
        public ItemProvider getItemProvider(Gui gui) {
            final ItemStack itemStack = new ItemStack(Material.BARRIER);
            final ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName("Exit");
            itemStack.setItemMeta(itemMeta);
            return new SimpleItem(itemStack).getItemProvider();
        }

        @Override
        public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
            Objects.requireNonNull(event.getClickedInventory()).close();
        }
    }

