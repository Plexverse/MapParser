package net.plexverse.mapparser.listener;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.plexverse.mapparser.constant.Keys;
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
import org.bukkit.event.entity.EntityPlaceEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class ArmorStandListener {

    public ArmorStandListener() {
        this.onArmorStandPlace();
        this.onArmorStandClick();
    }

    private void onArmorStandPlace() {
        Events.hook(EntityPlaceEvent.class, (event) -> {
            if (!(event.getEntity() instanceof ArmorStand)) {
                return;
            }

            final ArmorStand entity = (ArmorStand) event.getEntity();
            entity.setGravity(false);
            final ItemStack itemStack = event.getPlayer().getItemInHand();
            if (itemStack == null || itemStack.getType() != Material.ARMOR_STAND || !itemStack.hasItemMeta() || !itemStack.getItemMeta().getPersistentDataContainer().has(Keys.DATAPOINT_KEY)) {
                return;
            }

            final String dataPointName = itemStack.getItemMeta().getPersistentDataContainer().get(Keys.DATAPOINT_KEY, PersistentDataType.STRING);
            entity.getPersistentDataContainer().set(Keys.DATAPOINT_KEY, PersistentDataType.STRING, dataPointName);
            final String teamId = itemStack.getItemMeta().getPersistentDataContainer().get(Keys.TEAM_KEY, PersistentDataType.STRING);
            if (teamId == null) {
                return;
            }

            entity.getPersistentDataContainer().set(Keys.TEAM_KEY, PersistentDataType.STRING, teamId);

            final Team team = Team.getExistingOrCreate(teamId);
            entity.customName(MiniMessage.miniMessage().deserialize(team.getDisplayName() + " <red>" + dataPointName));
            entity.setCustomNameVisible(true);
            entity.getEquipment().setHelmet(ItemBuilder.create(Material.LEATHER_HELMET).color(team.getHelmetColor()).build());
        });
    }

    private void onArmorStandClick() {
        Events.hook(PlayerInteractAtEntityEvent.class, (event) -> {
            final Player player = event.getPlayer();
            final Entity clickedEntity = event.getRightClicked();
            if (!(clickedEntity instanceof ArmorStand)) {
                return;
            }

            if (clickedEntity.getPersistentDataContainer().has(Keys.DATAPOINT_KEY)) {
                new ModifyMenu(player, (LivingEntity) clickedEntity).open();
                return;
            }

            new DataPointMenu(player, (LivingEntity) clickedEntity).open();
        });
    }
}
