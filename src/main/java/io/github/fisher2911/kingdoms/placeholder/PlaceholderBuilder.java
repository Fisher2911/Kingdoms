package io.github.fisher2911.kingdoms.placeholder;

import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.economy.Price;
import io.github.fisher2911.kingdoms.kingdom.Kingdom;
import io.github.fisher2911.kingdoms.kingdom.relation.RelationInfo;
import io.github.fisher2911.kingdoms.kingdom.relation.RelationType;
import io.github.fisher2911.kingdoms.kingdom.role.Role;
import io.github.fisher2911.kingdoms.kingdom.upgrade.Upgrades;
import io.github.fisher2911.kingdoms.message.MessageHandler;
import io.github.fisher2911.kingdoms.placeholder.wrapper.PermissionWrapper;
import io.github.fisher2911.kingdoms.placeholder.wrapper.UpgradeLevelWrapper;
import io.github.fisher2911.kingdoms.placeholder.wrapper.UpgradesWrapper;
import io.github.fisher2911.kingdoms.user.User;
import io.github.fisher2911.kingdoms.util.MapOfMaps;
import io.github.fisher2911.kingdoms.world.KChunk;
import net.kyori.adventure.text.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class PlaceholderBuilder {

    private static final MapOfMaps<Class<?>, Placeholder, Function<Object, Object>> placeholders = new MapOfMaps<>(new HashMap<>(), HashMap::new);

    static {
        final Kingdoms plugin = Kingdoms.getPlugin(Kingdoms.class);

        put(User.class, Placeholder.USER_NAME, u -> castAndParseUser(u, User::getName));
        put(User.class, Placeholder.USER_BALANCE, u -> castAndParseUser(u, User::getMoney));
        put(User.class, Placeholder.USER_KINGDOM_NAME, u -> castAndParseUser(u, user -> plugin.getKingdomManager().
                getKingdom(user.getKingdomId()).
                map(Kingdom::getName).
                orElse("Wilderness"))
        );


        put(Kingdom.class, Placeholder.KINGDOM_ID, k -> castAndParseKingdom(k, Kingdom::getId));
        put(Kingdom.class, Placeholder.KINGDOM_NAME, k -> castAndParseKingdom(k, Kingdom::getName));
        put(Kingdom.class, Placeholder.KINGDOM_DESCRIPTION, k -> castAndParseKingdom(k, Kingdom::getDescription));
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
                p -> castAndParsePermissionWrapper(p, w -> MessageHandler.MINI_MESSAGE.deserialize(w.value() ? "<green>True" : "<red>False"))
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

        put(RelationType.class,
                Placeholder.RELATION_DISPLAY_NAME,
                r -> castAndParseRelationType(r, RelationType::displayName)
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
        while (superClass != null && map == null) {
            s = replaceInterfaces(s, o, superClass);
            map = placeholders.get(superClass);
            superClass = superClass.getSuperclass();
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

    public static Component apply(Component component, Object... objects) {
        for (Object o : objects) {
            component = replaceSuperClasses(component, o);
        }
        return component;
    }

    private static Component replaceInterfaces(Component component, Class<?> clazz, Object o) {
        for (Class<?> i : clazz.getInterfaces()) {
            final var map = placeholders.get(i);
            if (map == null) continue;
            for (var entry : map.entrySet()) {
                final String key = entry.getKey().toString();
                final Object value = entry.getValue().apply(o);
                component = replace(component, key, value);
            }
        }
        return component;
    }

    private static Component replaceSuperClasses(Component component, Object o) {
        var superClass = o.getClass();
        Map<Placeholder, Function<Object, Object>> map = null;
        while (superClass != null && map == null) {
            map = placeholders.get(superClass);
            component = replaceInterfaces(component, superClass, o);
            superClass = superClass.getSuperclass();
        }
        if (map == null) return component;
        for (var entry : map.entrySet()) {
            final String key = entry.getKey().toString();
            final Object value = entry.getValue().apply(o);
            component = replace(component, key, value);
        }
        return component;
    }

    private static Component replace(Component original, String key, Object value) {
        if (value instanceof Component component) {
            return original.replaceText(builder -> builder.matchLiteral(key).replacement(component));
        }
        return original.replaceText(builder -> builder.matchLiteral(key).replacement(String.valueOf(value)));
    }

}
