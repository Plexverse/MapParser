package net.plexverse.mapparser.util.item;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.plexverse.mapparser.util.message.Replacer;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

public class ItemBuilder {
    private static final ItemFlag[] ALL_FLAGS = ItemFlag.values();
    private ItemStack itemStack;

    public ItemBuilder(ItemStack itemStack) {
        this.itemStack = itemStack;
    }


    public static ItemBuilder create() {
        return ItemBuilder.create(Material.DIRT);
    }


    public static ItemBuilder create(Material material) {
        return ItemBuilder.create(new ItemStack(material));
    }


    public static ItemBuilder create(ItemStack itemStack) {
        return new ItemBuilder(itemStack.clone());
    }


    public ItemBuilder type(Material material) {
        return this.transform(itemStack -> itemStack.setType(material));
    }


    public ItemBuilder transform(Consumer<ItemStack> itemStack) {
        itemStack.accept(this.itemStack);
        return this;
    }


    public ItemBuilder transformMeta(Consumer<ItemMeta> meta) {
        final ItemMeta itemMeta = this.itemStack.getItemMeta();
        if (itemMeta != null) {
            meta.accept(itemMeta);
            this.itemStack.setItemMeta(itemMeta);
        }

        return this;
    }


    public ItemBuilder lore(String... lines) {
        return this.lore(Arrays.asList(lines));
    }


    public ItemBuilder lore(Iterable<String> lines) {
        return this.transformMeta(meta -> {
            final List<Component> lore = meta.hasLore() ? Objects.requireNonNull(meta.lore()) : Lists.newArrayList();

            for (final String line : lines) {
                lore.add(MiniMessage.miniMessage().deserialize(line));
            }

            meta.lore(lore);
        });
    }


    public ItemBuilder lore(List<Component> lore) {
        return this.transformMeta(meta -> meta.lore(lore));
    }

    private List<Component> setLore(List<Component> lore) {
        final List<Component> newLore = Lists.newLinkedList();

        if (this.itemStack.getType() == Material.AIR) {
            return lore;
        }

        for (final Component component : lore) {
            boolean newLine = false;
            boolean anyMatch = false;

            for (final Component child : component.children()) {
                if (child.equals(Component.newline())) {
                    newLine = true;
                    anyMatch = true;
                    continue;
                }

                if (newLine) {
                    newLine = false;
                    newLore.add(child);
                }
            }

            if (!anyMatch) {
                newLore.add(component);
            }
        }

        return newLore;
    }


    public ItemBuilder name(Component component) {
        return this.transformMeta(meta -> {
            meta.displayName(component);
        });
    }

    public <T> ItemBuilder persistentData(NamespacedKey key, PersistentDataType<?, T> type, T value) {
        return this.transformMeta(meta -> meta.getPersistentDataContainer().set(key, type, value));
    }

    public <T> ItemBuilder persistentData(NamespacedKey key, String value) {
        return this.persistentData(key, PersistentDataType.STRING, value);
    }


    public ItemBuilder clearLore() {
        return this.transformMeta(meta -> meta.lore(Lists.newArrayList()));
    }

    public ItemBuilder hideAttributes() {
        return this.flag(ItemBuilder.ALL_FLAGS);
    }

    public ItemBuilder flag(ItemFlag... flags) {
        return this.transformMeta(meta -> meta.addItemFlags(flags));
    }

    public ItemBuilder showAttributes() {
        return this.unflag(ItemBuilder.ALL_FLAGS);
    }

    public ItemBuilder unflag(ItemFlag... flags) {
        return this.transformMeta(meta -> meta.removeItemFlags(flags));
    }

    public ItemBuilder name(String name) {
        return this.name(MiniMessage.miniMessage().deserialize(name));
    }

    public ItemBuilder replace(Replacer replacer) {
        final ItemMeta itemMeta = this.itemStack.getItemMeta();
        if (itemMeta == null) {
            return this;
        }

        boolean changed = false;

        if (itemMeta.hasDisplayName()) {
            final Component newDisplayName = replacer.accept(itemMeta.displayName());
            itemMeta.displayName(newDisplayName);
            changed = true;
        }

        if (itemMeta.hasLore()) {
            List<Component> lore = itemMeta.lore();
            lore = replacer.accept(lore);

            itemMeta.lore(this.setLore(lore));
            changed = true;
        }

        if (changed) {
            this.itemStack.setItemMeta(itemMeta);
        }

        return this;
    }

    public ItemStack build() {
        return this.itemStack;
    }
}
