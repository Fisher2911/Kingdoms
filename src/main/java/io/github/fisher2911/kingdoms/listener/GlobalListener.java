/*
 *     Kingdoms Plugin
 *     Copyright (C) 2022  Fisher2911
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.fisher2911.kingdoms.listener;

import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.util.MapOfMaps;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class GlobalListener implements Listener {

    private final Kingdoms plugin;
    private final MapOfMaps<Class<? extends Event>, EventPriority, RegisteredListener> listeners;

    public GlobalListener(Kingdoms plugin) {
        this.plugin = plugin;
        this.listeners = new MapOfMaps<>(new HashMap<>(), HashMap::new);
    }

    public <T extends Event> void register(Class<T> clazz, Consumer<T> consumer) {
        this.register(clazz, consumer, EventPriority.NORMAL);
    }

    public void register(Class<? extends Event> clazz, Consumer<? extends Event> consumer, EventPriority priority) {
        final Map<EventPriority, RegisteredListener> listeners = this.listeners.get(clazz);
        final RegisteredListener registeredListener = listeners.computeIfAbsent(
                priority,
                k -> new RegisteredListener(clazz, new ArrayList<>(), priority)
        );
        if (registeredListener.consumers().isEmpty()) {
            this.plugin.getServer().getPluginManager().registerEvent(
                    clazz,
                    this,
                    priority,
                    (listener, event) -> registeredListener.consumers().forEach(handler -> handler.accept(event)),
                    this.plugin
            );
        }
        registeredListener.consumers().add((Consumer<Event>) consumer);
    }

}
