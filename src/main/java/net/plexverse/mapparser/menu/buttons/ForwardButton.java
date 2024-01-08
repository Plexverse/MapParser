package net.plexverse.mapparser.menu.buttons;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.xenondevs.invui.gui.PagedGui;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.impl.SimpleItem;
import xyz.xenondevs.invui.item.impl.controlitem.PageItem;

public class ForwardButton extends PageItem {
    public ForwardButton() {
        super(true);
    }

    @Override
    public ItemProvider getItemProvider(PagedGui<?> gui) {
        if (gui.hasNextPage()) {
            final ItemStack itemStack = new ItemStack(Material.ARROW);
            final ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName("Next Page");
            itemStack.setItemMeta(itemMeta);
            return new SimpleItem(itemStack).getItemProvider();
        }
        return new SimpleItem(new ItemStack(Material.AIR)).getItemProvider();
    }
}