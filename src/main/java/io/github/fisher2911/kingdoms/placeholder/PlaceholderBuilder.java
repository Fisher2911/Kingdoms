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

import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.chat.ChatChannel;
import io.github.fisher2911.kingdoms.command.help.CommandHelp;
import io.github.fisher2911.kingdoms.command.help.CommandInfo;
import io.github.fisher2911.kingdoms.economy.Price;
import io.github.fisher2911.kingdoms.economy.TransactionResult;
import io.github.fisher2911.kingdoms.gui.BaseGui;
import io.github.fisher2911.kingdoms.gui.GuiKeys;
import io.github.fisher2911.kingdoms.kingdom.Kingdom;
import io.github.fisher2911.kingdoms.kingdom.relation.RelationInfo;
import io.github.fisher2911.kingdoms.kingdom.relation.RelationType;
import io.github.fisher2911.kingdoms.kingdom.role.Role;
import io.github.fisher2911.kingdoms.kingdom.upgrade.Upgrades;
import io.github.fisher2911.kingdoms.message.MessageHandler;
import io.github.fisher2911.kingdoms.placeholder.wrapper.PermissionWrapper;
import io.github.fisher2911.kingdoms.placeholder.wrapper.UpgradeLevelWrapper;
import io.github.fisher2911.kingdoms.placeholder.wrapper.UpgradesWrapper;
import io.github.fisher2911.kingdoms.placeholder.wrapper.UserKingdomWrapper;
import io.github.fisher2911.kingdoms.teleport.TeleportInfo;
import io.github.fisher2911.kingdoms.user.User;
import io.github.fisher2911.kingdoms.util.MapOfMaps;
import io.github.fisher2911.kingdoms.world.KChunk;
import io.github.fisher2911.kingdoms.world.WorldPosition;
import net.kyori.adventure.text.Component;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class PlaceholderBuilder {

    private static final MapOfMaps<Class<?>, Placeholder, Function<Object, Object>> placeholders = new MapOfMaps<>(new HashMap<>(), HashMap::new);

    private static final DecimalFormat POSITION_FORMAT = new DecimalFormat("#.0");

    static {
        final Kingdoms plugin = Kingdoms.getPlugin(Kingdoms.class);

        put(User.class, Placeholder.USER_NAME, u -> castAndParseUser(u, User::getName));
        put(User.class, Placeholder.USER_BALANCE, u -> castAndParseUser(u, User::getMoney));
        put(User.class, Placeholder.USER_KINGDOM_NAME, u -> castAndParseUser(u, user -> plugin.getKingdomManager().
                getKingdom(user.getKingdomId(), false).
                map(Kingdom::getName).
                orElse("Wilderness"))
        );


        put(Kingdom.class, Placeholder.KINGDOM_ID, k -> castAndParseKingdom(k, Kingdom::getId));
        put(Kingdom.class, Placeholder.KINGDOM_NAME, k -> castAndParseKingdom(k, Kingdom::getName));
        put(Kingdom.class, Placeholder.KINGDOM_DESCRIPTION, k -> castAndParseKingdom(k, Kingdom::getDescription));
        put(Kingdom.class, Placeholder.KINGDOM_BANK_BALANCE, k -> castAndParseKingdom(k, kingdom -> kingdom.getBank().getBalance()));
        put(Kingdom.class, Placeholder.KINGDOM_MEMBERS, k -> castAndParseKingdom(k,
                kingdom -> String.join(", ", kingdom.getMembers().stream().
                        map(user -> user.getName() + " (" + kingdom.getRole(user).displayName() + ")").
                        toList()))
        );
        put(Kingdom.class, Placeholder.KINGDOM_ALLIES, k -> castAndParseKingdom(k,
                kingdom -> getRelationString(kingdom, RelationType.ALLY))
        );
        put(Kingdom.class, Placeholder.KINGDOM_TRUCES, k -> castAndParseKingdom(k,
                kingdom -> getRelationString(kingdom, RelationType.TRUCE))
        );
        put(Kingdom.class, Placeholder.KINGDOM_ENEMIES, k -> castAndParseKingdom(k,
                kingdom -> getRelationString(kingdom, RelationType.ENEMY))
        );

        put(PermissionWrapper.class,
                Placeholder.PERMISSION_VALUE,
                p -> castAndParsePermissionWrapper(p, PermissionWrapper::value)
        );
        put(PermissionWrapper.class,
                Placeholder.PERMISSION_DISPLAY_VALUE,
                p -> castAndParsePermissionWrapper(p, w -> w.value() ? "<green>True" : "<red>False")
        );
        put(PermissionWrapper.class,
                Placeholder.PERMISSION_NAME,
                p -> castAndParsePermissionWrapper(p, w -> w.permission().toString())
        );
        put(PermissionWrapper.class,
                Placeholder.PERMISSION_DISPLAY_NAME,
                p -> castAndParsePermissionWrapper(p, w -> w.permission().displayName())
        );

        put(Upgrades.class,
                Placeholder.UPGRADE_DISPLAY_NAME,
                u -> castAndParseUpgrades(u, Upgrades::getDisplayName)
        );
        put(Upgrades.class,
                Placeholder.UPGRADE_ID,
                u -> castAndParseUpgrades(u, Upgrades::getId)
        );
        put(UpgradesWrapper.class,
                Placeholder.UPGRADE_DISPLAY_NAME,
                u -> castAndParseUpgradesWrapper(u, o -> o.upgrades().getDisplayName())
        );
        put(UpgradesWrapper.class,
                Placeholder.UPGRADE_ID,
                u -> castAndParseUpgradesWrapper(u, o -> o.upgrades().getId())
        );
        put(UpgradesWrapper.class,
                Placeholder.UPGRADE_DISPLAY_VALUE,
                u -> castAndParseUpgradesWrapper(u, o -> o.upgrades().getDisplayValueAtLevel(o.level()))
        );
        put(UpgradesWrapper.class,
                Placeholder.UPGRADE_VALUE,
                u -> castAndParseUpgradesWrapper(u, o -> o.upgrades().getValueAtLevel(o.level()))
        );
        put(UpgradesWrapper.class,
                Placeholder.UPGRADE_DISPLAY_PRICE,
                u -> castAndParseUpgradesWrapper(u, o -> {
                    Price price = o.upgrades().getPriceAtLevel(o.level());
                    if (price == null) return null;
                    return price.getDisplay();
                })
        );
        put(UpgradeLevelWrapper.class,
                Placeholder.UPGRADE_LEVEL,
                w -> castAndParseUpgradeLevelWrapper(w, u -> u.kingdom().getUpgradeLevel(u.id()))
        );

        put(KChunk.class,
                Placeholder.CHUNK_X,
                c -> castAndParseKChunk(c, KChunk::x)
        );
        put(KChunk.class,
                Placeholder.CHUNK_Z,
                c -> castAndParseKChunk(c, KChunk::z)
        );

        put(Role.class,
                Placeholder.ROLE_DISPLAY_NAME,
                r -> castAndParseKRole(r, Role::displayName)
        );

        put(Role.class,
                Placeholder.ROLE_WEIGHT,
                r -> castAndParseKRole(r, Role::weight)
        );

        put(Role.class,
                Placeholder.ROLE_ID,
                r -> castAndParseKRole(r, Role::id)
        );

        put(RelationType.class,
                Placeholder.RELATION_DISPLAY_NAME,
                r -> castAndParseRelationType(r, RelationType::displayName)
        );

        put(ChatChannel.class,
                Placeholder.CHAT_CHANNEL,
                c -> castAndParse(ChatChannel.class, c, ChatChannel::displayName)
        );

        put(TransactionResult.class,
                Placeholder.TRANSACTION_AMOUNT,
                t -> castAndParse(TransactionResult.class, t, TransactionResult::amount
                ));

        put(WorldPosition.class,
                Placeholder.POSITION_X,
                w -> castAndParse(WorldPosition.class, w, p -> POSITION_FORMAT.format(p.position().x()))
        );
        put(WorldPosition.class,
                Placeholder.POSITION_Y,
                w -> castAndParse(WorldPosition.class, w, p -> POSITION_FORMAT.format(p.position().y()))
        );
        put(WorldPosition.class,
                Placeholder.POSITION_Z,
                w -> castAndParse(WorldPosition.class, w, p -> POSITION_FORMAT.format(p.position().z()))
        );

        put(UserKingdomWrapper.class,
                Placeholder.KINGDOM_MEMBER_ROLE_DISPLAY_NAME,
                w -> castAndParse(UserKingdomWrapper.class, w, u -> u.kingdom().getRole(u.user()).displayName())
        );
        put(UserKingdomWrapper.class,
                Placeholder.KINGDOM_MEMBER_ROLE_ID,
                w -> castAndParse(UserKingdomWrapper.class, w, u -> u.kingdom().getRole(u.user()).id())
        );
        put(UserKingdomWrapper.class,
                Placeholder.KINGDOM_MEMBER_ROLE_WEIGHT,
                w -> castAndParse(UserKingdomWrapper.class, w, u -> u.kingdom().getRole(u.user()).weight())
        );
        put(UserKingdomWrapper.class,
                Placeholder.KINGDOM_MEMBER_UUID,
                w -> castAndParse(UserKingdomWrapper.class, w, u -> u.user().getId())
        );
        put(UserKingdomWrapper.class,
                Placeholder.KINGDOM_MEMBER_NAME,
                w -> castAndParse(UserKingdomWrapper.class, w, u -> u.user().getName())
        );

        put(TeleportInfo.class,
                Placeholder.TELEPORT_INFO_SECONDS_LEFT,
                t -> castAndParse(TeleportInfo.class, t, TeleportInfo::getSecondsLeft)
        );

        put(BaseGui.class,
                Placeholder.GUI_USER_ROLE_ID,
                g -> castAndParse(BaseGui.class, g, b -> {
                    final Kingdom kingdom = b.getMetadata(GuiKeys.KINGDOM, Kingdom.class);
                    final User user = b.getMetadata(GuiKeys.USER, User.class);
                    if (kingdom == null || user == null) return null;
                    return kingdom.getRole(user).id();
                })
        );
        put(BaseGui.class,
                Placeholder.GUI_USER_ROLE_WEIGHT,
                g -> castAndParse(BaseGui.class, g, b -> {
                    final Kingdom kingdom = b.getMetadata(GuiKeys.KINGDOM, Kingdom.class);
                    final User user = b.getMetadata(GuiKeys.USER, User.class);
                    if (kingdom == null || user == null) return null;
                    return kingdom.getRole(user).weight();
                })
        );
        put(BaseGui.class,
                Placeholder.GUI_USER_UUID,
                g -> castAndParse(BaseGui.class, g, b -> {
                    final User user = b.getMetadata(GuiKeys.USER, User.class);
                    if (user == null) return null;
                    return user.getId();
                })
        );
        put(BaseGui.class,
                Placeholder.GUI_KINGDOM_ID,
                g -> castAndParse(BaseGui.class, g, b -> {
                    final Kingdom kingdom = b.getMetadata(GuiKeys.KINGDOM, Kingdom.class);
                    if (kingdom == null) return null;
                    return kingdom.getId();
                })
        );
        put(BaseGui.class,
                Placeholder.GUI_KINGDOM_NAME,
                g -> castAndParse(BaseGui.class, g, b -> {
                    final Kingdom kingdom = b.getMetadata(GuiKeys.KINGDOM, Kingdom.class);
                    if (kingdom == null) return null;
                    return kingdom.getName();
                })
        );

        put(CommandHelp.class,
                Placeholder.COMMAND_HELP_NAME,
                p -> castAndParse(CommandHelp.class, p, CommandHelp::getCommand)
        );
        put(CommandHelp.class,
                Placeholder.COMMAND_HELP_USAGE,
                p -> castAndParse(CommandHelp.class, p, CommandHelp::getUsage)
        );
        put(CommandHelp.class,
                Placeholder.COMMAND_HELP_PERMISSION,
                p -> castAndParse(CommandHelp.class, p, CommandHelp::getPermission)
        );

        put(CommandInfo.class,
                Placeholder.COMMAND_INFO_NEXT_PAGE_NUMBER,
                p -> castAndParse(CommandInfo.class, p, CommandInfo::nextPage)
        );
        put(CommandInfo.class,
                Placeholder.COMMAND_INFO_PREVIOUS_PAGE_NUMBER,
                p -> castAndParse(CommandInfo.class, p, CommandInfo::previousPage)
        );
    }

    private static String getRelationString(Kingdom kingdom, RelationType type) {
        return String.join(", ", kingdom.getRelations(type).stream().
                map(RelationInfo::kingdomName).
                toList());
    }

    private static Object castAndParseKingdom(Object o, Function<Kingdom, Object> parse) {
        return castAndParse(Kingdom.class, o, parse);
    }

    private static Object castAndParseUser(Object o, Function<User, Object> parse) {
        return castAndParse(User.class, o, parse);
    }

    private static Object castAndParsePermissionWrapper(Object o, Function<PermissionWrapper, Object> parse) {
        return castAndParse(PermissionWrapper.class, o, parse);
    }

    private static Object castAndParseUpgrades(Object o, Function<Upgrades, Object> parse) {
        return castAndParse(Upgrades.class, o, parse);
    }

    private static Object castAndParseUpgradesWrapper(Object o, Function<UpgradesWrapper, Object> parse) {
        return castAndParse(UpgradesWrapper.class, o, parse);
    }

    private static Object castAndParseUpgradeLevelWrapper(Object o, Function<UpgradeLevelWrapper, Object> parse) {
        return castAndParse(UpgradeLevelWrapper.class, o, parse);
    }

    private static Object castAndParseKChunk(Object o, Function<KChunk, Object> parse) {
        return castAndParse(KChunk.class, o, parse);
    }

    private static Object castAndParseKRole(Object o, Function<Role, Object> parse) {
        return castAndParse(Role.class, o, parse);
    }

    private static Object castAndParseRelationType(Object o, Function<RelationType, Object> parse) {
        return castAndParse(RelationType.class, o, parse);
    }

    private static Object castAndParseUserKingdomWrapper(Object o, Function<UserKingdomWrapper, Object> parse) {
        return castAndParse(UserKingdomWrapper.class, o, parse);
    }

    private static <T> Object castAndParse(Class<T> clazz, Object o, Function<T, Object> parse) {
        return parse.apply(clazz.cast(o));
    }

    private static <T> void put(Class<T> clazz, Placeholder placeholder, Function<Object, Object> parse) {
        placeholders.put(clazz, placeholder, parse);
    }

    private String current;

    public PlaceholderBuilder(String current) {
        this.current = current;
    }

    public PlaceholderBuilder apply(Object... objects) {
        this.current = apply(this.current, objects);
        return this;
    }

    public static String apply(String s, Object... objects) {
        for (Object o : objects) {
            s = replaceSuperClasses(s, o);
        }
        return s;
    }

    private static String replaceInterfaces(String s, Object o, Class<?> clazz) {
        for (Class<?> i : clazz.getInterfaces()) {
            final var map = placeholders.get(i);
            if (map == null) continue;
            for (var entry : map.entrySet()) {
                final String key = entry.getKey().toString();
                final Object value = entry.getValue().apply(o);
                s = replace(s, key, value);
            }
        }
        return s;
    }

    private static String replaceSuperClasses(String s, Object o) {
        var superClass = o.getClass();
        Map<Placeholder, Function<Object, Object>> map = null;
        while (superClass != null && (map == null || map.isEmpty())) {
            s = replaceInterfaces(s, o, superClass);
            map = placeholders.get(superClass);
            if (map != null && !map.isEmpty()) break;
            superClass = superClass.getSuperclass();
            map = placeholders.get(superClass);
        }
        if (map == null) return s;
        for (var entry : map.entrySet()) {
            final String key = entry.getKey().toString();
            final Object value = entry.getValue().apply(o);
            s = replace(s, key, value);
        }
        return s;
    }

    private static String replace(String original, String key, Object value) {
        if (value instanceof Component component) {
            return original.replace(key, MessageHandler.MINI_MESSAGE.serialize(component));
        }
        return original.replace(key, String.valueOf(value));
    }

}
