package io.github.fisher2911.kingdoms.kingdom.permission;

import io.github.fisher2911.kingdoms.util.StringUtils;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Locale;
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
    UPGRADE_KINGDOM(PermissionContext.KINGDOM)

    ;

    private final Set<PermissionContext> permissionContextSet;

    KPermission() {
        this.permissionContextSet = EnumSet.allOf(PermissionContext.class);
    }

    KPermission(PermissionContext... contexts) {
        this.permissionContextSet = EnumSet.copyOf(Arrays.asList(contexts));
    }

    public boolean hasContext(PermissionContext context) {
        return this.permissionContextSet.contains(context);
    }

    public String displayName() {
        return StringUtils.capitalize(this.toString().replace("_", " ").toLowerCase(Locale.ROOT));
    }
}
