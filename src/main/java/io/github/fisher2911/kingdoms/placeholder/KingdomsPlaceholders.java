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

package io.github.fisher2911.kingdoms.placeholder;

import io.github.fisher2911.fisherlib.command.help.CommandHelp;
import io.github.fisher2911.fisherlib.economy.Price;
import io.github.fisher2911.fisherlib.economy.TransactionResult;
import io.github.fisher2911.fisherlib.gui.BaseGui;
import io.github.fisher2911.fisherlib.gui.GuiKey;
import io.github.fisher2911.fisherlib.placeholder.Placeholder;
import io.github.fisher2911.fisherlib.placeholder.Placeholders;
import io.github.fisher2911.fisherlib.upgrade.Upgrades;
import io.github.fisher2911.fisherlib.world.ChunkPos;
import io.github.fisher2911.fisherlib.world.WorldPosition;
import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.chat.ChatChannel;
import io.github.fisher2911.kingdoms.command.help.CommandInfo;
import io.github.fisher2911.kingdoms.gui.GuiKeys;
import io.github.fisher2911.kingdoms.kingdom.Kingdom;
import io.github.fisher2911.kingdoms.kingdom.relation.RelationInfo;
import io.github.fisher2911.kingdoms.kingdom.relation.RelationType;
import io.github.fisher2911.kingdoms.kingdom.role.Role;
import io.github.fisher2911.kingdoms.placeholder.wrapper.PermissionWrapper;
import io.github.fisher2911.kingdoms.placeholder.wrapper.UpgradeLevelWrapper;
import io.github.fisher2911.kingdoms.placeholder.wrapper.UpgradesWrapper;
import io.github.fisher2911.kingdoms.placeholder.wrapper.UserKingdomWrapper;
import io.github.fisher2911.kingdoms.teleport.TeleportInfo;
import io.github.fisher2911.kingdoms.user.User;

import java.text.DecimalFormat;
import java.util.function.Function;

public class KingdomsPlaceholders extends Placeholders {

//    private static final MapOfMaps<Class<?>, KPlaceholder, Function<Object, Object>> KPlaceholders = new MapOfMaps<>(new HashMap<>(), HashMap::new);

    private static final DecimalFormat POSITION_FORMAT = new DecimalFormat("#.0");

    public KingdomsPlaceholders(final Kingdoms plugin) {
        super.load();
        this.put(User.class, Placeholder.USER_UUID, u -> castAndParse(User.class, u, User::getId));
        this.put(User.class, Placeholder.USER_NAME, u -> castAndParse(User.class, u, User::getName));
        this.put(User.class, Placeholder.USER_BALANCE, u -> castAndParse(User.class, u, User::getMoney));
        this.put(User.class, KPlaceholder.USER_KINGDOM_NAME, u -> castAndParse(User.class, u, user -> plugin.getKingdomManager().
                getKingdom(user.getKingdomId(), false).
                map(Kingdom::getName).
                orElse("Wilderness"))
        );

        this.put(Kingdom.class, KPlaceholder.KINGDOM_ID, k -> castAndParseKingdom(k, Kingdom::getId));
        this.put(Kingdom.class, KPlaceholder.KINGDOM_NAME, k -> castAndParseKingdom(k, Kingdom::getName));
        this.put(Kingdom.class, KPlaceholder.KINGDOM_DESCRIPTION, k -> castAndParseKingdom(k, Kingdom::getDescription));
        this.put(Kingdom.class, KPlaceholder.KINGDOM_BANK_BALANCE, k -> castAndParseKingdom(k, kingdom -> kingdom.getBank().getBalance()));
        this.put(Kingdom.class, KPlaceholder.KINGDOM_MEMBERS, k -> castAndParseKingdom(k,
                kingdom -> String.join(", ", kingdom.getUsers().stream().
                        map(user -> user.getName() + " (" + kingdom.getRole(user).displayName() + ")").
                        toList()))
        );
        this.put(Kingdom.class, KPlaceholder.KINGDOM_ALLIES, k -> castAndParseKingdom(k,
                kingdom -> getRelationString(kingdom, RelationType.ALLY))
        );
        this.put(Kingdom.class, KPlaceholder.KINGDOM_TRUCES, k -> castAndParseKingdom(k,
                kingdom -> getRelationString(kingdom, RelationType.TRUCE))
        );
        this.put(Kingdom.class, KPlaceholder.KINGDOM_ENEMIES, k -> castAndParseKingdom(k,
                kingdom -> getRelationString(kingdom, RelationType.ENEMY))
        );
        this.put(Kingdom.class, KPlaceholder.KINGDOM_TOTAL_CLAIMS, k -> castAndParseKingdom(k,
                kingdom -> kingdom.getClaimedChunks().size())
        );

        this.put(PermissionWrapper.class,
                KPlaceholder.PERMISSION_VALUE,
                p -> castAndParsePermissionWrapper(p, PermissionWrapper::value)
        );
        this.put(PermissionWrapper.class,
                KPlaceholder.PERMISSION_DISPLAY_VALUE,
                p -> castAndParsePermissionWrapper(p, w -> w.value() ? "<green>True" : "<red>False")
        );
        this.put(PermissionWrapper.class,
                KPlaceholder.PERMISSION_NAME,
                p -> castAndParsePermissionWrapper(p, w -> w.permission().toString())
        );
        this.put(PermissionWrapper.class,
                KPlaceholder.PERMISSION_DISPLAY_NAME,
                p -> castAndParsePermissionWrapper(p, w -> w.permission().displayName())
        );

        this.put(Upgrades.class,
                KPlaceholder.UPGRADE_DISPLAY_NAME,
                u -> castAndParseUpgrades(u, Upgrades::getDisplayName)
        );
        this.put(Upgrades.class,
                KPlaceholder.UPGRADE_ID,
                u -> castAndParseUpgrades(u, Upgrades::getId)
        );
        this.put(UpgradesWrapper.class,
                KPlaceholder.UPGRADE_DISPLAY_NAME,
                u -> castAndParseUpgradesWrapper(u, o -> o.upgrades().getDisplayName())
        );
        this.put(UpgradesWrapper.class,
                KPlaceholder.UPGRADE_ID,
                u -> castAndParseUpgradesWrapper(u, o -> o.upgrades().getId())
        );
        this.put(UpgradesWrapper.class,
                KPlaceholder.UPGRADE_DISPLAY_VALUE,
                u -> castAndParseUpgradesWrapper(u, o -> o.upgrades().getDisplayValueAtLevel(o.level()))
        );
        this.put(UpgradesWrapper.class,
                KPlaceholder.UPGRADE_VALUE,
                u -> castAndParseUpgradesWrapper(u, o -> o.upgrades().getValueAtLevel(o.level()))
        );
        this.put(UpgradesWrapper.class,
                KPlaceholder.UPGRADE_DISPLAY_PRICE,
                u -> castAndParseUpgradesWrapper(u, o -> {
                    Price price = o.upgrades().getPriceAtLevel(o.level());
                    if (price == null) return null;
                    return price.getDisplay();
                })
        );
        this.put(UpgradeLevelWrapper.class,
                KPlaceholder.UPGRADE_LEVEL,
                w -> castAndParseUpgradeLevelWrapper(w, u -> u.kingdom().getUpgradeLevel(u.id()))
        );

        this.put(ChunkPos.class,
                KPlaceholder.CHUNK_X,
                c -> castAndParseChunkPos(c, ChunkPos::x)
        );
        this.put(ChunkPos.class,
                KPlaceholder.CHUNK_Z,
                c -> castAndParseChunkPos(c, ChunkPos::z)
        );

        this.put(Role.class,
                KPlaceholder.ROLE_DISPLAY_NAME,
                r -> castAndParseKRole(r, Role::displayName)
        );

        this.put(Role.class,
                KPlaceholder.ROLE_WEIGHT,
                r -> castAndParseKRole(r, Role::weight)
        );

        this.put(Role.class,
                KPlaceholder.ROLE_ID,
                r -> castAndParseKRole(r, Role::id)
        );

        this.put(RelationType.class,
                KPlaceholder.RELATION_DISPLAY_NAME,
                r -> castAndParseRelationType(r, RelationType::displayName)
        );

        this.put(ChatChannel.class,
                KPlaceholder.CHAT_CHANNEL,
                c -> castAndParse(ChatChannel.class, c, ChatChannel::displayName)
        );

        this.put(TransactionResult.class,
                KPlaceholder.TRANSACTION_AMOUNT,
                t -> castAndParse(TransactionResult.class, t, TransactionResult::amount
                ));

        this.put(WorldPosition.class,
                KPlaceholder.POSITION_X,
                w -> castAndParse(WorldPosition.class, w, p -> POSITION_FORMAT.format(p.position().x()))
        );
        this.put(WorldPosition.class,
                KPlaceholder.POSITION_Y,
                w -> castAndParse(WorldPosition.class, w, p -> POSITION_FORMAT.format(p.position().y()))
        );
        this.put(WorldPosition.class,
                KPlaceholder.POSITION_Z,
                w -> castAndParse(WorldPosition.class, w, p -> POSITION_FORMAT.format(p.position().z()))
        );

        this.put(UserKingdomWrapper.class,
                KPlaceholder.KINGDOM_MEMBER_ROLE_DISPLAY_NAME,
                w -> castAndParse(UserKingdomWrapper.class, w, u -> u.kingdom().getRole(u.user()).displayName())
        );
        this.put(UserKingdomWrapper.class,
                KPlaceholder.KINGDOM_MEMBER_ROLE_ID,
                w -> castAndParse(UserKingdomWrapper.class, w, u -> u.kingdom().getRole(u.user()).id())
        );
        this.put(UserKingdomWrapper.class,
                KPlaceholder.KINGDOM_MEMBER_ROLE_WEIGHT,
                w -> castAndParse(UserKingdomWrapper.class, w, u -> u.kingdom().getRole(u.user()).weight())
        );
        this.put(UserKingdomWrapper.class,
                KPlaceholder.KINGDOM_MEMBER_UUID,
                w -> castAndParse(UserKingdomWrapper.class, w, u -> u.user().getId())
        );
        this.put(UserKingdomWrapper.class,
                KPlaceholder.KINGDOM_MEMBER_NAME,
                w -> castAndParse(UserKingdomWrapper.class, w, u -> u.user().getName())
        );

        this.put(TeleportInfo.class,
                KPlaceholder.TELEPORT_INFO_SECONDS_LEFT,
                t -> castAndParse(TeleportInfo.class, t, TeleportInfo::getSecondsLeft)
        );

        this.put(BaseGui.class,
                KPlaceholder.GUI_USER_ROLE_ID,
                g -> castAndParse(BaseGui.class, g, b -> {
                    final Kingdom kingdom = b.getMetadata(GuiKeys.KINGDOM, Kingdom.class);
                    final User user = b.getMetadata(GuiKey.USER, User.class);
                    if (kingdom == null || user == null) return null;
                    return kingdom.getRole(user).id();
                })
        );
        this.put(BaseGui.class,
                KPlaceholder.GUI_USER_ROLE_WEIGHT,
                g -> castAndParse(BaseGui.class, g, b -> {
                    final Kingdom kingdom = b.getMetadata(GuiKeys.KINGDOM, Kingdom.class);
                    final User user = b.getMetadata(GuiKey.USER, User.class);
                    if (kingdom == null || user == null) return null;
                    return kingdom.getRole(user).weight();
                })
        );
        this.put(BaseGui.class,
                KPlaceholder.GUI_USER_UUID,
                g -> castAndParse(BaseGui.class, g, b -> {
                    final User user = b.getMetadata(GuiKey.USER, User.class);
                    if (user == null) return null;
                    return user.getId();
                })
        );
        this.put(BaseGui.class,
                KPlaceholder.GUI_KINGDOM_ID,
                g -> castAndParse(BaseGui.class, g, b -> {
                    final Kingdom kingdom = b.getMetadata(GuiKeys.KINGDOM, Kingdom.class);
                    if (kingdom == null) return null;
                    return kingdom.getId();
                })
        );
        this.put(BaseGui.class,
                KPlaceholder.GUI_KINGDOM_NAME,
                g -> castAndParse(BaseGui.class, g, b -> {
                    final Kingdom kingdom = b.getMetadata(GuiKeys.KINGDOM, Kingdom.class);
                    if (kingdom == null) return null;
                    return kingdom.getName();
                })
        );

        this.put(CommandHelp.class,
                KPlaceholder.COMMAND_HELP_NAME,
                p -> castAndParse(CommandHelp.class, p, CommandHelp::getCommand)
        );
        this.put(CommandHelp.class,
                KPlaceholder.COMMAND_HELP_USAGE,
                p -> castAndParse(CommandHelp.class, p, CommandHelp::getUsage)
        );
        this.put(CommandHelp.class,
                KPlaceholder.COMMAND_HELP_PERMISSION,
                p -> castAndParse(CommandHelp.class, p, CommandHelp::getPermission)
        );

        this.put(CommandInfo.class,
                KPlaceholder.COMMAND_INFO_NEXT_PAGE_NUMBER,
                p -> castAndParse(CommandInfo.class, p, CommandInfo::nextPage)
        );
        this.put(CommandInfo.class,
                KPlaceholder.COMMAND_INFO_PREVIOUS_PAGE_NUMBER,
                p -> castAndParse(CommandInfo.class, p, CommandInfo::previousPage)
        );
    }

    private String getRelationString(Kingdom kingdom, RelationType type) {
        return String.join(", ", kingdom.getRelations(type).stream().
                map(RelationInfo::kingdomName).
                toList());
    }

    private Object castAndParseKingdom(Object o, Function<Kingdom, Object> parse) {
        return this.castAndParse(Kingdom.class, o, parse);
    }

    private Object castAndParsePermissionWrapper(Object o, Function<PermissionWrapper, Object> parse) {
        return this.castAndParse(PermissionWrapper.class, o, parse);
    }

    private Object castAndParseUpgrades(Object o, Function<Upgrades, Object> parse) {
        return this.castAndParse(Upgrades.class, o, parse);
    }

    private Object castAndParseUpgradesWrapper(Object o, Function<UpgradesWrapper, Object> parse) {
        return this.castAndParse(UpgradesWrapper.class, o, parse);
    }

    private Object castAndParseUpgradeLevelWrapper(Object o, Function<UpgradeLevelWrapper, Object> parse) {
        return this.castAndParse(UpgradeLevelWrapper.class, o, parse);
    }

    private Object castAndParseChunkPos(Object o, Function<ChunkPos, Object> parse) {
        return this.castAndParse(ChunkPos.class, o, parse);
    }

    private Object castAndParseKRole(Object o, Function<Role, Object> parse) {
        return this.castAndParse(Role.class, o, parse);
    }

    private Object castAndParseRelationType(Object o, Function<RelationType, Object> parse) {
        return this.castAndParse(RelationType.class, o, parse);
    }

    private Object castAndParseUserKingdomWrapper(Object o, Function<UserKingdomWrapper, Object> parse) {
        return this.castAndParse(UserKingdomWrapper.class, o, parse);
    }

}
