package io.github.fisher2911.kingdoms.placeholder;

import io.github.fisher2911.kingdoms.economy.Price;
import io.github.fisher2911.kingdoms.kingdom.Kingdom;
import io.github.fisher2911.kingdoms.kingdom.upgrade.Upgrades;
import io.github.fisher2911.kingdoms.placeholder.wrapper.PermissionWrapper;
import io.github.fisher2911.kingdoms.placeholder.wrapper.UpgradeLevelWrapper;
import io.github.fisher2911.kingdoms.placeholder.wrapper.UpgradesWrapper;
import io.github.fisher2911.kingdoms.util.MapOfMaps;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class PlaceholderBuilder {

    private static final MapOfMaps<Class<?>, Placeholder, Function<Object, Object>> placeholders = new MapOfMaps<>(new HashMap<>(), HashMap::new);

    static {
        put(Kingdom.class, Placeholder.KINGDOM_ID, k -> castAndParseKingdom(k, Kingdom::getId));
        put(Kingdom.class, Placeholder.KINGDOM_NAME, k -> castAndParseKingdom(k, Kingdom::getName));

        put(PermissionWrapper.class,
                Placeholder.PERMISSION_VALUE,
                p -> castAndParsePermissionWrapper(p, PermissionWrapper::value)
        );
        put(PermissionWrapper.class,
                Placeholder.PERMISSION_DISPLAY_VALUE,
                p -> castAndParsePermissionWrapper(p, w -> w.value() ? ChatColor.GREEN + "True" : ChatColor.RED + "False")
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
    }

    private static Object castAndParseKingdom(Object o, Function<Kingdom, Object> parse) {
        return castAndParse(Kingdom.class, o, parse);
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
            var superClass = o.getClass();
            Map<Placeholder, Function<Object, Object>> map = null;
            while (superClass != null && map == null) {
                map = placeholders.get(o.getClass());
                superClass = superClass.getSuperclass();
            }
            if (map == null) continue;
            for (var entry : map.entrySet()) {
                s = s.replace(entry.getKey().toString(), String.valueOf(entry.getValue().apply(o)));
            }
        }
        return s;
    }

    public static Component apply(Component component, Object... objects) {
        for (Object o : objects) {
            var superClass = o.getClass();
            Map<Placeholder, Function<Object, Object>> map = null;
            while (superClass != null && map == null) {
                map = placeholders.get(superClass);
                superClass = superClass.getSuperclass();
            }
            if (map == null) continue;
            for (var entry : map.entrySet()) {
                component = component.replaceText(builder -> builder.matchLiteral(entry.getKey().toString()).replacement(String.valueOf(entry.getValue().apply(o))));
            }
        }
        return component;
    }

}
