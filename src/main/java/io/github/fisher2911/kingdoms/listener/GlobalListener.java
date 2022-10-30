package io.github.fisher2911.kingdoms.listener;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.WorldLoadEvent;

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

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        this.handle(event);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        this.handle(event);
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        this.handle(event);
    }

    @EventHandler
    public void onOpen(InventoryOpenEvent event) {
        this.handle(event);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        this.handle(event);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        this.handle(event);
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        this.handle(event);
    }

    @EventHandler
    public void onChunkUnload(ChunkLoadEvent event) {
        this.handle(event);
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        this.handle(event);
    }

    @EventHandler
    public void onWorldUnload(WorldLoadEvent event) {
        this.handle(event);
    }

    public <T extends Event> void register(Class<T> clazz, Consumer<T> consumer) {
        this.eventHandlers.put(clazz, (Consumer<Event>) consumer);
    }

    private void handle(Event event) {
        this.eventHandlers.get(event.getClass()).forEach(consumer -> consumer.accept(event));
    }
}
