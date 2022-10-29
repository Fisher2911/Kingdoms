package io.github.fisher2911.kingdoms.kingdom.permission;

import io.github.fisher2911.kingdoms.util.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

// not enum for API purposes
public class KPermission {

    private static final Map<String, KPermission> allPermissions = new HashMap<>();

    public static final KPermission MINE_BLOCK = register("mine-block");
    public static final KPermission PLACE_BLOCK = register("place-block");
    public static final KPermission OPEN_CONTAINER = register("open-container");
    public static final KPermission BREAK_CONTAINER = register("break-container");
    public static final KPermission PLACE_CONTAINER = register("place-container");
    public static final KPermission USE_LEVER = register("use-lever");
    public static final KPermission USE_BUTTON = register("use-button");
    public static final KPermission USE_PRESSURE_PLATE = register("use-pressure-plate");
    public static final KPermission USE_DOOR = register("use-door");
    public static final KPermission USE_TRAPDOOR = register("use-trapdoor");
    public static final KPermission USE_FENCE_GATE = register("use-fence-gate");
    public static final KPermission KILL_MOBS = register("kill-mobs");
    public static final KPermission FARM_CROPS = register("farm-crops");
    public static final KPermission CLAIM_LAND = register("claim-land", PermissionContext.KINGDOM);
    public static final KPermission UNCLAIM_LAND = register("unclaim-land");
    public static final KPermission EDIT_LOWER_ROLES_PERMISSIONS = register("edit-lower-roles-permissions", PermissionContext.KINGDOM);
    public static final KPermission INVITE_MEMBER = register("invite-member", PermissionContext.KINGDOM);
    public static final KPermission UPGRADE_KINGDOM = register("upgrade-kingdom", PermissionContext.KINGDOM);
    public static final KPermission KICK_MEMBER = register("kick-member", PermissionContext.KINGDOM);
    public static final KPermission SET_MEMBER_ROLE = register("set-member-role", PermissionContext.KINGDOM);
    public static final KPermission ADD_ENEMY = register("add-enemy", PermissionContext.KINGDOM);
    public static final KPermission ADD_NEUTRAL = register("add-neutral", PermissionContext.KINGDOM);
    public static final KPermission ADD_TRUCE = register("add-truce", PermissionContext.KINGDOM);
    public static final KPermission ADD_ALLY = register("add-ally", PermissionContext.KINGDOM);
    public static final KPermission REMOVE_ENEMY = register("remove-enemy", PermissionContext.KINGDOM);
    public static final KPermission REMOVE_NEUTRAL = register("remove-neutral", PermissionContext.KINGDOM);
    public static final KPermission REMOVE_TRUCE = register("remove-truce", PermissionContext.KINGDOM);
    public static final KPermission REMOVE_ALLY = register("remove-ally", PermissionContext.KINGDOM);
    public static final KPermission DEPOSIT_MONEY = register("deposit-money", PermissionContext.KINGDOM);
    public static final KPermission WITHDRAW_MONEY = register("withdraw-money", PermissionContext.KINGDOM);
    public static final KPermission VIEW_BANK_BALANCE = register("view-bank-balance", PermissionContext.KINGDOM);
    public static final KPermission SET_KINGDOM_HOME = register("set-kingdom-home", PermissionContext.KINGDOM);
    public static final KPermission TELEPORT_TO_KINGDOM_HOME = register("teleport-to-kingdom-home", PermissionContext.KINGDOM);

    public static Collection<KPermission> values() {
        return allPermissions.values();
    }

    public static KPermission register(String id, PermissionContext... contexts) {
        final KPermission permission = new KPermission(id, contexts);
        allPermissions.put(id, permission);
        return permission;
    }

    public static KPermission register(String id) {
        final KPermission permission = new KPermission(id);
        allPermissions.put(id, permission);
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
        for (KPermission perm : allPermissions.values()) {
            map.put(perm, false);
        }
        return map;
    }

    @Nullable
    public static KPermission get(String id) {
        return allPermissions.get(id);
    }

    public boolean hasContext(PermissionContext context) {
        return this.permissionContextSet.contains(context);
    }

    public String displayName() {
        return StringUtils.capitalize(this.toString().replace("-", " ").toLowerCase(Locale.ROOT));
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
