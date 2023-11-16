package net.plexverse.mapparser.util.asker;

import lombok.Data;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.plexverse.mapparser.util.event.Events;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class InputAsker {
    private final String question;
    private final Map<UUID, ActiveAsker> currentAskers;

    public InputAsker(String question) {
        this.question = question;
        this.currentAskers = new HashMap<>();
        this.initEvents();
    }

    public void ask(Player player, Consumer<String> answer) {
        final ActiveAsker activeAsker = new ActiveAsker(player, answer);
        activeAsker.ask();
        this.currentAskers.put(player.getUniqueId(), activeAsker);
    }

    private void initEvents() {
        Events.hook(PlayerQuitEvent.class, (event) -> {
            this.currentAskers.remove(event.getPlayer().getUniqueId());
        });

        Events.hook(PlayerChatEvent.class, (event) -> {
            final Player player = event.getPlayer();
            final String message = event.getMessage();
            if (!this.currentAskers.containsKey(player.getUniqueId())) {
                return;
            }

            final ActiveAsker activeAsker = this.currentAskers.remove(player.getUniqueId());
            activeAsker.answer.accept(message);
            event.setCancelled(true);
        });
    }

    @Data
    private class ActiveAsker {
        private final Player player;
        private final Consumer<String> answer;

        public void ask() {
            this.player.sendMessage(MiniMessage.miniMessage().deserialize(InputAsker.this.question));
        }
    }
}
