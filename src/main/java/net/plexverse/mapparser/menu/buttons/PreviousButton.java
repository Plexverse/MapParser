package net.plexverse.mapparser.menu.buttons;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.xenondevs.invui.gui.PagedGui;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.impl.SimpleItem;
import xyz.xenondevs.invui.item.impl.controlitem.PageItem;

public class PreviousButton extends PageItem {
    public PreviousButton() {
        super(false);
    }

    @Override
    public ItemProvider getItemProvider(PagedGui<?> gui) {
        if (gui.hasPreviousPage()) {
            final ItemStack itemStack = new ItemStack(Material.ARROW);
            final ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName("Previous Page");
            itemStack.setItemMeta(itemMeta);
            return new SimpleItem(itemStack).getItemProvider();
        }
        return new SimpleItem(new ItemStack(Material.AIR)).getItemProvider();
    }
}