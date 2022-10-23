package io.github.fisher2911.kingdoms.kingdom.permission;

import io.github.fisher2911.kingdoms.util.StringUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

// not enum for API purposes
public class KPermission {

    private static final Set<KPermission> allPermissions = new HashSet<>();

    public static final KPermission MINE_BLOCK = register("mine_block");
    public static final KPermission PLACE_BLOCK = register("place_block");
    public static final KPermission OPEN_CONTAINER = register("open_container");
    public static final KPermission BREAK_CONTAINER = register("break_container");
    public static final KPermission PLACE_CONTAINER = register("place_container");
    public static final KPermission USE_LEVER = register("use_lever");
    public static final KPermission USE_BUTTON = register("use_button");
    public static final KPermission USE_PRESSURE_PLATE = register("use_pressure_plate");
    public static final KPermission KILL_MOBS = register("kill_mobs");
    public static final KPermission FARM_CROPS = register("farm_crops");
    public static final KPermission CLAIM_LAND = register("claim_land", PermissionContext.KINGDOM);
    public static final KPermission UNCLAIM_LAND = register("unclaim_land");
    public static final KPermission EDIT_LOWER_ROLES_PERMISSIONS = register("edit_lower_roles_permissions", PermissionContext.KINGDOM);
    public static final KPermission INVITE_MEMBER = register("invite_member", PermissionContext.KINGDOM);
    public static final KPermission UPGRADE_KINGDOM = register("upgrade_kingdom", PermissionContext.KINGDOM);
    public static final KPermission KICK_MEMBER = register("kick_member", PermissionContext.KINGDOM);
    public static final KPermission SET_MEMBER_ROLE = register("set_member_role", PermissionContext.KINGDOM);
    public static final KPermission ADD_ENEMY = register("add_enemy", PermissionContext.KINGDOM);
    public static final KPermission ADD_NEUTRAL = register("add_neutral", PermissionContext.KINGDOM);
    public static final KPermission ADD_TRUCE = register("add_truce", PermissionContext.KINGDOM);
    public static final KPermission ADD_ALLY = register("add_ally", PermissionContext.KINGDOM);
    public static final KPermission REMOVE_ENEMY = register("remove_enemy", PermissionContext.KINGDOM);
    public static final KPermission REMOVE_NEUTRAL = register("remove_neutral", PermissionContext.KINGDOM);
    public static final KPermission REMOVE_TRUCE = register("remove_truce", PermissionContext.KINGDOM);
    public static final KPermission REMOVE_ALLY = register("remove_ally", PermissionContext.KINGDOM);

    public static Collection<KPermission> values() {
        return allPermissions;
    }

    public static KPermission register(String id, PermissionContext... contexts) {
        final KPermission permission = new KPermission(id, contexts);
        allPermissions.add(permission);
        return permission;
    }

    public static KPermission register(String id) {
        final KPermission permission = new KPermission(id);
        allPermissions.add(permission);
        return permission;
    }


    private final String id;
    private final Set<PermissionContext> permissionContextSet;

    private KPermission(String id) {
        this.id = id;
        this.permissionContextSet = EnumSet.allOf(PermissionContext.class);
    }

    private KPermission(String id, PermissionContext... contexts) {
        this.id = id;
        this.permissionContextSet = EnumSet.copyOf(Arrays.asList(contexts));
    }

    public static Map<KPermission, Boolean> mapOfAll() {
        final Map<KPermission, Boolean> map = new HashMap<>();
        for (KPermission perm : allPermissions) {
            map.put(perm, false);
        }
        return map;
    }

    public boolean hasContext(PermissionContext context) {
        return this.permissionContextSet.contains(context);
    }

    public String displayName() {
        return StringUtils.capitalize(this.toString().replace("_", " ").toLowerCase(Locale.ROOT));
    }

    @Override
    public String toString() {
        return this.id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final KPermission that = (KPermission) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
