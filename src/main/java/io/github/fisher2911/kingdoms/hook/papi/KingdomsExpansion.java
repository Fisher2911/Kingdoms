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

package io.github.fisher2911.kingdoms.hook.papi;

import io.github.fisher2911.fisherlib.message.MessageHandler;
import io.github.fisher2911.fisherlib.upgrade.Upgrades;
import io.github.fisher2911.fisherlib.world.WorldPosition;
import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.kingdom.ClaimedChunk;
import io.github.fisher2911.kingdoms.kingdom.Kingdom;
import io.github.fisher2911.kingdoms.kingdom.KingdomManager;
import io.github.fisher2911.kingdoms.kingdom.WildernessKingdom;
import io.github.fisher2911.kingdoms.kingdom.WorldManager;
import io.github.fisher2911.kingdoms.user.User;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class KingdomsExpansion extends PlaceholderExpansion {

    private static final String IDENTIFIER = "kingdoms";
    private final Kingdoms plugin;
    private final String author;
    private final String version;

    public KingdomsExpansion(Kingdoms plugin) {
        this.plugin = plugin;
        this.author = String.join(",", this.plugin.getDescription().getAuthors());
        this.version = this.plugin.getDescription().getVersion();
    }

    @Override
    @NotNull
    public String getAuthor() {
        return this.author;
    }

    @Override
    @NotNull
    public String getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    @NotNull
    public String getVersion() {
        return this.version;
    }

    @Override
    public boolean persist() {
        return true; // This is required or else PlaceholderAPI will unregister the Expansion on reload
    }

    /*
    %kingdoms_kingdom_name%
    %kingdoms_kingdom_name_<kingdom-name>%
    %kingdoms_kingdom_description%
    %kingdoms_kingdom_description_<kingdom-name>%
    %kingdoms_kingdom_id%
    %kingdoms_kingdom_role-id%
    %kingdoms_kingdom_role-display-name%
    %kingdom_kingdom_member-count%
    %kingdoms_kingdom_member-count_<kingdom-name>%
    %kingdoms_chunk_kingdom-name%
    %kingdoms_upgrade_level_<upgrade-id>%
    %kingdoms_upgrade_level_<upgrade-id>_<kingdom-name>%
    %kingdoms_upgrade_max_level_<upgrade-id>%
    %kingdoms_upgrade_display-name_<upgrade-id>%
     */

    private static final String KINGDOM_ARG = "kingdom";
    private static final String CHUNK_ARG = "chunk";
    private static final String UPGRADE_ARG = "upgrade";

    private static final String NAME_ARG = "name";
    private static final String DESCRIPTION_ARG = "description";
    private static final String ID_ARG = "id";
    private static final String ROLE_ID_ARG = "role-id";
    private static final String ROLE_DISPLAY_NAME_ARG = "role-display-name";
    private static final String MEMBER_COUNT_ARG = "member-count";

    private static final String KINGDOM_NAME_ARG = "kingdom-name";

    private static final String LEVEL_ARG = "level";
    private static final String MAX_LEVEL_ARG = "max-level";
    private static final String UPGRADE_DISPLAY_NAME_ARG = "display-name";

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        final String[] parts = params.split("_");
        if (parts.length == 0) return null;
        final String first = parts[0];
        final String[] partsIgnoringFirst = new String[parts.length - 1];
        System.arraycopy(parts, 1, partsIgnoringFirst, 0, parts.length - 1);
        final User user;
        if (player == null) {
            user = null;
        } else {
            user = this.plugin.getUserManager().forceGet(player.getUniqueId());
        }
        return switch (first) {
            case KINGDOM_ARG -> this.handleKingdomRequest(user, partsIgnoringFirst);
            case CHUNK_ARG -> this.handleChunkRequest(user, partsIgnoringFirst);
            case UPGRADE_ARG -> this.handleUpgradeRequest(user, partsIgnoringFirst);
            default -> null;
        };
    }

    /*
    %kingdoms_kingdom_name%
    %kingdoms_kingdom_name_<kingdom-name>%
    %kingdoms_kingdom_description%
    %kingdoms_kingdom_description_<kingdom-name>%
    %kingdoms_kingdom_id%
    %kingdoms_kingdom_role-id%
    %kingdoms_kingdom_role-display-name%
    %kingdom_kingdom_member-count%
    %kingdoms_kingdom_member-count_<kingdom-name>%
     */

    private String handleKingdomRequest(@Nullable User user, String[] parts) {
        if (parts.length == 0) return null;
        final KingdomManager kingdomManager = this.plugin.getKingdomManager();
        final String first = parts[0];
        return switch (first) {
            case NAME_ARG -> {
                if (user == null) yield null;
                yield kingdomManager.getKingdom(user.getKingdomId(), false)
                        .map(Kingdom::getName)
                        .orElse(WildernessKingdom.INSTANCE.getName());
            }
            case DESCRIPTION_ARG -> {
                if (parts.length == 1) {
                    if (user == null) yield null;
                    yield kingdomManager.getKingdom(user.getKingdomId(), false)
                            .map(Kingdom::getDescription)
                            .orElse(WildernessKingdom.INSTANCE.getDescription());
                }
                final String kingdomName = parts[1];
                yield kingdomManager.getKingdomByName(kingdomName, false)
                        .map(Kingdom::getDescription)
                        .orElse(WildernessKingdom.INSTANCE.getDescription());
            }
            case ID_ARG -> {
                if (user == null) yield null;
                yield String.valueOf(
                        kingdomManager.getKingdom(user.getKingdomId(), false)
                                .map(Kingdom::getId)
                                .orElse(WildernessKingdom.INSTANCE.getId())
                );
            }
            case ROLE_ID_ARG -> {
                if (user == null) yield null;
                yield kingdomManager.getKingdom(user.getKingdomId(), false)
                        .map(kingdom -> String.valueOf(kingdom.getRole(user).id()))
                        .orElse(null);
            }
            case ROLE_DISPLAY_NAME_ARG -> {
                if (user == null) yield null;
                yield kingdomManager.getKingdom(user.getKingdomId(), false)
                        .map(kingdom -> MessageHandler.serialize(kingdom.getRole(user).displayName()))
                        .orElse(null);
            }
            case MEMBER_COUNT_ARG -> {
                if (parts.length == 1) {
                    if (user == null) yield null;
                    yield kingdomManager.getKingdom(user.getKingdomId(), false)
                            .map(kingdom -> String.valueOf(kingdom.getUsers().size()))
                            .orElse(null);
                }
                final String kingdomName = parts[1];
                yield kingdomManager.getKingdomByName(kingdomName, false)
                        .map(kingdom -> String.valueOf(kingdom.getUsers().size()))
                        .orElse(null);
            }
            default -> null;
        };
    }

    /*
    %kingdoms_chunk_kingdom-name%
     */
    private String handleChunkRequest(@Nullable User user, String[] parts) {
        if (parts.length == 0) return null;
        if (user == null) return null;
        final WorldPosition position = user.getPosition();
        if (position == null) return null;
        final WorldManager worldManager = this.plugin.getWorldManager();
        final KingdomManager kingdomManager = this.plugin.getKingdomManager();
        final ClaimedChunk chunk = worldManager.getAt(position);
        if (chunk.isWilderness()) {
            return WildernessKingdom.INSTANCE.getName();
        }
        final String first = parts[0];
        return switch (first) {
            case KINGDOM_NAME_ARG -> kingdomManager.getKingdom(chunk.getKingdomId(), false)
                    .map(Kingdom::getName)
                    .orElse(WildernessKingdom.INSTANCE.getName());
            default -> null;
        };
    }

    /*
    %kingdoms_upgrade_level_<upgrade-id>%
    %kingdoms_upgrade_level_<upgrade-id>_<kingdom-name>%
    %kingdoms_upgrade_max_level_<upgrade-id>%
    %kingdoms_upgrade_display-name_<upgrade-id>%
     */
    private String handleUpgradeRequest(@Nullable User user, String[] parts) {
        if (parts.length <= 1) return null;
        final KingdomManager kingdomManager = this.plugin.getKingdomManager();
        final String first = parts[0];
        final String upgradeId = parts[1];
        return switch (first) {
            case LEVEL_ARG -> {
                if (parts.length == 2) {
                    if (user == null) yield null;
                    yield kingdomManager.getKingdom(user.getKingdomId(), false)
                            .map(kingdom -> String.valueOf(kingdom.getUpgradeLevel(upgradeId)))
                            .orElse(null);
                }
                final String kingdomName = parts[2];
                yield kingdomManager.getKingdomByName(kingdomName, false)
                        .map(kingdom -> String.valueOf(kingdom.getUpgradeLevel(upgradeId)))
                        .orElse(null);
            }
            case MAX_LEVEL_ARG -> Optional.ofNullable(this.plugin.getUpgradeManager()
                            .getUpgradeHolder()
                            .getUpgrades(upgradeId))
                    .map(Upgrades::getMaxLevel)
                    .map(String::valueOf)
                    .orElse(null);
            case UPGRADE_DISPLAY_NAME_ARG -> Optional.ofNullable(this.plugin.getUpgradeManager()
                    .getUpgradeHolder().getUpgrades(upgradeId))
                    .map(Upgrades::getDisplayName)
                    .map(MessageHandler::serialize)
                    .orElse(null);
            default -> null;
        };
    }

}
