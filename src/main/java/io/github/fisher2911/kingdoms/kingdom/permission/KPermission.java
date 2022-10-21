package io.github.fisher2911.kingdoms.kingdom.permission;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

public enum KPermission {

    MINE_BLOCK(PermissionContext.CLAIM),
    PLACE_BLOCK(PermissionContext.CLAIM),
    OPEN_CONTAINER(PermissionContext.CLAIM),
    BREAK_CONTAINER(PermissionContext.CLAIM),
    PLACE_CONTAINER(PermissionContext.CLAIM),
    USE_LEVER(PermissionContext.CLAIM),
    USE_BUTTON(PermissionContext.CLAIM),
    USE_PRESSURE_PLATE(PermissionContext.CLAIM),
    KILL_MOBS(PermissionContext.CLAIM),
    FARM_CROPS(PermissionContext.CLAIM),
    CLAIM_LAND(PermissionContext.KINGDOM);

    private final Set<PermissionContext> permissionContextSet;

    KPermission(PermissionContext... contexts) {
        this.permissionContextSet = EnumSet.copyOf(Arrays.asList(contexts));
    }

    public boolean hasContext(PermissionContext context) {
        return this.permissionContextSet.contains(context);
    }
}
