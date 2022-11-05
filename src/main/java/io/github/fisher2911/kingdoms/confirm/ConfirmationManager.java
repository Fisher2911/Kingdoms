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

package io.github.fisher2911.kingdoms.confirm;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import io.github.fisher2911.kingdoms.Kingdoms;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public class ConfirmationManager {

    private final Kingdoms plugin;
    private final Multimap<String, UUID> confirmations;

    public ConfirmationManager(Kingdoms plugin) {
        this.plugin = plugin;
        this.confirmations = Multimaps.newSetMultimap(new HashMap<>(), HashSet::new);
    }

    public void addConfirmation(Confirmation key, UUID uuid, int ticks) {
        this.addConfirmation(key, uuid, ticks, null);
    }

    public void addConfirmation(Confirmation key, UUID uuid, int ticks, @Nullable Runnable onExpire) {
        this.addConfirmation(key.toString(), uuid, ticks, onExpire);
    }

    public void addConfirmation(String key, UUID uuid, int ticks) {
        this.addConfirmation(key, uuid, ticks, null);
    }

    public void addConfirmation(String key, UUID uuid, int ticks, @Nullable Runnable onExpire) {
        this.confirmations.put(key, uuid);
        Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
            if (this.confirmations.containsEntry(key, uuid)) {
                this.confirmations.remove(key, uuid);
                if (onExpire == null) return;
                onExpire.run();
            }
        }, ticks);
    }

    public boolean hasConfirmation(String key, UUID uuid, boolean remove) {
        if (this.confirmations.containsEntry(key, uuid)) {
            if (remove) this.confirmations.remove(key, uuid);
            return true;
        }
        return false;
    }

    public boolean hasConfirmation(Confirmation confirmation, UUID uuid, boolean remove) {
        return this.hasConfirmation(confirmation.toString(), uuid, remove);
    }
}
