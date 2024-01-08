package net.plexverse.mapparser.listener;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.md_5.bungee.api.ChatColor;
import net.plexverse.mapparser.constant.Keys;
import net.plexverse.mapparser.enums.DataPointType;
import net.plexverse.mapparser.mapsettings.MapSettingsManager;
import net.plexverse.mapparser.menu.DataPointMenu;
import net.plexverse.mapparser.menu.ModifyMenu;
import net.plexverse.mapparser.objects.Team;
import net.plexverse.mapparser.util.event.Events;
import net.plexverse.mapparser.util.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPlaceEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import xyz.xenondevs.invui.window.Window;

import java.io.IOException;

import static net.plexverse.mapparser.enums.GameType.SKYWARS;

public class ArmorStandListener implements Listener {

    @EventHandler
    private void onArmorStandPlace(EntityPlaceEvent event) {
            if (!(event.getEntity() instanceof ArmorStand entity)) {
                return;
            }
            entity.setGravity(false);
            final ItemStack itemStack = event.getPlayer().getItemInHand();
            if (itemStack.isEmpty() || itemStack.getType() != Material.ARMOR_STAND || !itemStack.hasItemMeta() || !itemStack.getItemMeta().getPersistentDataContainer().has(Keys.DATAPOINT_KEY)) {
                return;
            }
            final String dataPointName = itemStack.getItemMeta().getPersistentDataContainer().get(Keys.DATAPOINT_KEY, PersistentDataType.STRING);
            entity.getPersistentDataContainer().set(Keys.DATAPOINT_KEY, PersistentDataType.STRING, dataPointName);
            final String teamId = itemStack.getItemMeta().getPersistentDataContainer().get(Keys.TEAM_KEY, PersistentDataType.STRING);
            if (teamId == null) {
                entity.customName(MiniMessage.miniMessage().deserialize("<red>" + dataPointName));
                return;
            }
            entity.getPersistentDataContainer().set(Keys.TEAM_KEY, PersistentDataType.STRING, teamId);
            final Team team = Team.getExistingOrCreate(teamId);
            entity.customName(MiniMessage.miniMessage().deserialize(team.getDisplayName().toUpperCase() + " <red>" + dataPointName));
            entity.setCustomNameVisible(true);
            entity.getEquipment().setHelmet(ItemBuilder.create(Material.LEATHER_HELMET).color(team.getHelmetColor()).build());
    }

    @EventHandler
    private void onArmorStandClick(PlayerInteractAtEntityEvent event) throws IOException {
        if (!(event.getRightClicked() instanceof ArmorStand clickedEntity)) {
            return;
        }

        if(MapSettingsManager.getMapSettings(clickedEntity.getWorld()).isEmpty()) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "Oh no! You have not yet set up /mapsettings!");
            return;
        }

        if (clickedEntity.getPersistentDataContainer().has(Keys.DATAPOINT_KEY)) {
            DataPointType dataPointType = DataPointType.valueOf(clickedEntity.getPersistentDataContainer().get(Keys.DATAPOINT_KEY, PersistentDataType.STRING));
            Window.single().setViewer(event.getPlayer()).setGui(new ModifyMenu(clickedEntity,dataPointType)).build().open();
            return;
        }

        Window.single().setGui(new DataPointMenu(clickedEntity)).setTitle("Datapoint Menu").build(event.getPlayer()).open();
    }
}
