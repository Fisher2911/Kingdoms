package io.github.fisher2911.kingdoms.listener;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;

public class GlobalListener implements Listener {

    private final Multimap<Class<? extends Event>, Consumer<Event>> eventHandlers;

    public GlobalListener() {
        this.eventHandlers = Multimaps.newListMultimap(new HashMap<>(), ArrayList::new);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        this.handle(event);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        this.handle(event);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        this.handle(event);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        this.handle(event);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        this.handle(event);
    }

    public <T extends Event> void register(Class<T> clazz, Consumer<T> consumer) {
        this.eventHandlers.put(clazz, (Consumer<Event>) consumer);
    }

    private void handle(Event event) {
        this.eventHandlers.get(event.getClass()).forEach(consumer -> consumer.accept(event));
    }
}
