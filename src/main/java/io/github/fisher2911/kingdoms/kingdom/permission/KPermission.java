package io.github.fisher2911.kingdoms.kingdom.permission;

import io.github.fisher2911.kingdoms.util.StringUtils;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public enum KPermission {

    MINE_BLOCK(),
    PLACE_BLOCK(),
    OPEN_CONTAINER(),
    BREAK_CONTAINER(),
    PLACE_CONTAINER(),
    USE_LEVER(),
    USE_BUTTON(),
    USE_PRESSURE_PLATE(),
    KILL_MOBS(),
    FARM_CROPS(),
    CLAIM_LAND(PermissionContext.KINGDOM),
    UNCLAIM_LAND(),
    EDIT_LOWER_ROLES_PERMISSIONS(PermissionContext.KINGDOM),
    INVITE_MEMBER(PermissionContext.KINGDOM),
    UPGRADE_KINGDOM(PermissionContext.KINGDOM),
    KICK_MEMBER(PermissionContext.KINGDOM),
    SET_MEMBER_ROLE(PermissionContext.KINGDOM),
    ADD_ENEMY(PermissionContext.KINGDOM),
    ADD_NEUTRAL(PermissionContext.KINGDOM),
    ADD_TRUCE(PermissionContext.KINGDOM),
    ADD_ALLY(PermissionContext.KINGDOM),
    REMOVE_ENEMY(PermissionContext.KINGDOM),
    REMOVE_NEUTRAL(PermissionContext.KINGDOM),
    REMOVE_TRUCE(PermissionContext.KINGDOM),
    REMOVE_ALLY(PermissionContext.KINGDOM),


    ;

    private final Set<PermissionContext> permissionContextSet;

    KPermission() {
        this.permissionContextSet = EnumSet.allOf(PermissionContext.class);
    }

    KPermission(PermissionContext... contexts) {
        this.permissionContextSet = EnumSet.copyOf(Arrays.asList(contexts));
    }

    public static Map<KPermission, Boolean> mapOfAll() {
        final Map<KPermission, Boolean> map = new EnumMap<>(KPermission.class);
        for (KPermission perm : KPermission.values()) {
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
}
