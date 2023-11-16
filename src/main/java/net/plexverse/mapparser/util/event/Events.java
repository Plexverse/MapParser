package net.plexverse.mapparser.util.event;

import net.plexverse.mapparser.MapParser;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;

import java.io.Closeable;
import java.util.function.Consumer;

public class Events {
    public static <T extends Event> Closeable hook(Class<T> eventClass, Consumer<T> eventConsumer) {
        final HookedEvent hookedEvent = new HookedEvent();
        hookedEvent.internalEvent = ($, event) -> {
            if (!eventClass.isInstance(event)) {
                return;
            }

            eventConsumer.accept((T) event);
        };

        Bukkit.getPluginManager().registerEvent(
            eventClass,
            hookedEvent.internalEvent,
            EventPriority.NORMAL,
            hookedEvent.internalEvent,
            MapParser.getPlugin(MapParser.class)
        );

        return () -> HandlerList.unregisterAll(hookedEvent.internalEvent);
    }

    public interface InternalEvent extends Listener, EventExecutor {
    }

    private static class HookedEvent {
        private InternalEvent internalEvent;
    }
}
