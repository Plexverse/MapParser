package net.plexverse.mapparser.menu;

import net.plexverse.mapparser.menu.buttons.ExitButton;
import net.plexverse.mapparser.menu.buttons.ForwardButton;
import net.plexverse.mapparser.menu.buttons.PreviousButton;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.AbstractPagedGui;
import xyz.xenondevs.invui.gui.SlotElement;
import xyz.xenondevs.invui.gui.structure.Markers;
import xyz.xenondevs.invui.gui.structure.Structure;
import xyz.xenondevs.invui.item.Item;

import java.util.ArrayList;
import java.util.List;

public class PagedMenu extends AbstractPagedGui<Item> {

    public PagedMenu(@NotNull List<Item> pool) {
        super(9, 6, false, new Structure(
                "# # # # # # # # #",
                "# x x x x x x x #",
                "# x x x x x x x #",
                "# x x x x x x x #",
                "# x x x x x x x #",
                "# # # < - > # # #")
                .addIngredient('x', Markers.CONTENT_LIST_SLOT_HORIZONTAL)
                .addIngredient('<', new PreviousButton())
                .addIngredient('-', new ExitButton())
                .addIngredient('>', new ForwardButton()));
        setContent(pool);
    }

    @Override
    public void bake() {
        int contentSize = getContentListSlots().length;

        List<List<SlotElement>> pages = new ArrayList<>();
        List<SlotElement> page = new ArrayList<>(contentSize);

        for (Item item : content) {
            page.add(new SlotElement.ItemSlotElement(item));

            if (page.size() >= contentSize) {
                pages.add(page);
                page = new ArrayList<>(contentSize);
            }
        }

        if (!page.isEmpty()) {
            pages.add(page);
        }

        this.pages = pages;
        update();
    }
}