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

package io.github.fisher2911.kingdoms.api;

import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.command.KCommand;
import io.github.fisher2911.kingdoms.gui.GuiOpener;
import io.github.fisher2911.kingdoms.kingdom.ClaimedChunk;
import io.github.fisher2911.kingdoms.kingdom.Kingdom;
import io.github.fisher2911.kingdoms.kingdom.permission.KPermission;
import io.github.fisher2911.kingdoms.kingdom.permission.PermissionContext;
import io.github.fisher2911.kingdoms.kingdom.upgrade.UpgradeHolder;
import io.github.fisher2911.kingdoms.kingdom.upgrade.Upgrades;
import io.github.fisher2911.kingdoms.listener.GlobalListener;
import io.github.fisher2911.kingdoms.task.TaskChain;
import io.github.fisher2911.kingdoms.user.User;
import io.github.fisher2911.kingdoms.world.KChunk;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

@SuppressWarnings("unused")
public class KingdomsApi {

    private static final KingdomsApi INSTANCE = new KingdomsApi(Kingdoms.getPlugin(Kingdoms.class));

    public static KingdomsApi getInstance() {
        return INSTANCE;
    }

    private final Kingdoms plugin;

    private KingdomsApi(Kingdoms plugin) {
        this.plugin = plugin;
    }

    /**
     * @param <T> The consumed type of the {@link TaskChain}
     * @param <R> The return type of the {@link TaskChain}
     * @return a new {@link TaskChain}
     */
    public <T, R> TaskChain<T, R> createTaskChain() {
        return TaskChain.create(this.plugin);
    }

    /**
     * This will search the database for a {@link Kingdom} with the given name,
     * so this should usually be run async
     *
     * @param id the id of the kingdom to get
     * @return an {@link Optional} containing the {@link Kingdom} if it exists
     */
    public Optional<Kingdom> getKingdom(int id) {
        return this.plugin.getKingdomManager().getKingdom(id, true);
    }

    /**
     * @param id the id of the kingdom to get
     * @return an {@link Optional} containing the {@link Kingdom} if it exists
     */
    public Optional<Kingdom> getLoadedKingdom(int id) {
        return this.plugin.getKingdomManager()
                .getKingdom(id, false);
    }

    /**
     * @param world  the world the chunk is in
     * @param chunkX the x coordinate of the chunk
     * @param chunkZ the z coordinate of the chunk
     * @return a {@link ClaimedChunk} which will either be owned by an {@link Kingdom} or be unclaimed and
     * have the {@link io.github.fisher2911.kingdoms.kingdom.WildernessKingdom} id as the owner
     */
    @NotNull
    public ClaimedChunk getClaimAt(@NotNull UUID world, int chunkX, int chunkZ) {
        return this.plugin.getWorldManager().getAt(world, chunkX, chunkZ);
    }

    /**
     * @param location the location to check
     * @return a {@link ClaimedChunk} which will either be owned by an {@link Kingdom} or be unclaimed and
     * have the {@link io.github.fisher2911.kingdoms.kingdom.WildernessKingdom} id as the owner
     */
    @NotNull
    public ClaimedChunk getClaimAt(@NotNull Location location) {
        return this.plugin.getWorldManager().getAt(location);
    }

    /**
     *
     * @param chunk the chunk to get the {@link ClaimedChunk} for
     * @return a {@link ClaimedChunk} which will either be owned by an {@link Kingdom} or be unclaimed and
     * have the {@link io.github.fisher2911.kingdoms.kingdom.WildernessKingdom} id as the owner
     */
    @NotNull
    public ClaimedChunk getClaimAt(@NotNull KChunk chunk) {
        return this.plugin.getWorldManager().getAt(chunk);
    }

    /**
     * @param uuid the uuid of the user to get
     * @return an {@link Optional} containing the {@link User} if is loaded
     */
    @Nullable
    public User getUser(@NotNull UUID uuid) {
        return this.plugin.getUserManager().forceGet(uuid);
    }

    /**
     * This will search the database, so in most instances it should be run async
     *
     * @param uuid the uuid of the user to get
     * @return an {@link Optional} containing the {@link User} if it exists
     */
    public Optional<User> findUser(@NotNull UUID uuid) {
        return this.plugin.getUserManager().get(uuid);
    }

    /**
     * This will try to find the integer id of the permission if it exists by searching a file,
     * or else it will create a new one, so this should usually be run when the plugin is first enabled
     *
     * @param permission the string id permission to add
     * @param contexts   the contexts the permission will be applied to
     * @return the newly registered {@link KPermission}
     */
    public KPermission registerPermission(@NotNull String permission, @NotNull PermissionContext... contexts) {
        return KPermission.register(permission, contexts);
    }

    /**
     * This will try to find the integer id of the permission if it exists by searching a file,
     * or else it will create a new one, so this should usually be run when the plugin is first enabled
     *
     * @param permission the string id permission to add
     * @return the newly registered {@link KPermission}
     */
    public KPermission registerPermission(@NotNull String permission) {
        return KPermission.register(permission);
    }

    /**
     * @param upgrades the upgrades to register
     */
    public void registerUpgrades(@NotNull Upgrades<?> upgrades) {
        this.plugin.getUpgradeManager().register(upgrades);
    }

    /**
     * The {@link UpgradeHolder} is universal to all Kingdoms, it only stores the upgrades and no information about the levels
     *
     * @return the {@link UpgradeHolder} which holds all the registered {@link Upgrades}
     */
    public UpgradeHolder getUpgradeHolder() {
        return this.plugin.getUpgradeManager().getUpgradeHolder();
    }

    /**
     * @param id the id of the upgrade to get
     * @return the {@link Upgrades} if it exists, or else null
     */
    @Nullable
    public GuiOpener getGuiOpener(@NotNull String id) {
        return this.plugin.getGuiManager().getGuiOpener(id);
    }

    /**
     * @param guiOpener the {@link GuiOpener} to register
     */
    public void addGuiOpener(@NotNull GuiOpener guiOpener) {
        this.plugin.getGuiManager().addGuiOpener(guiOpener);
    }

    /**
     * @param command    the command to register
     * @param updateHelp whether to update the help commands, this should be run after you register the last
     *                   subcommand you are adding
     */
    public void addSubCommand(KCommand command, boolean updateHelp) {
        this.plugin.getKingdomCommand().addSubCommand(command, updateHelp);
    }

    /**
     *
     * @return the {@link GlobalListener} instance
     */
    public GlobalListener getGlobalListener() {
        return this.plugin.getGlobalListener();
    }

}
