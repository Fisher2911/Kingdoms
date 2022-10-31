package io.github.fisher2911.kingdoms.config.condition.impl;

import io.github.fisher2911.kingdoms.config.condition.MetadataPredicate;
import io.github.fisher2911.kingdoms.gui.GuiKeys;
import io.github.fisher2911.kingdoms.kingdom.Kingdom;
import io.github.fisher2911.kingdoms.kingdom.permission.KPermission;
import io.github.fisher2911.kingdoms.user.User;
import io.github.fisher2911.kingdoms.util.Metadata;

import java.util.List;

public class KPermissionItemConditional implements MetadataPredicate {

    private final List<KPermission> permissions;

    public KPermissionItemConditional(final List<KPermission> permissions) {
        this.permissions = permissions;
    }

    public boolean test(Metadata possible) {
        final Kingdom kingdom = possible.getMetadata(GuiKeys.KINGDOM, Kingdom.class);
        final User user = possible.getMetadata(GuiKeys.USER, User.class);
        if (kingdom == null || user == null) {
            return false;
        }
        for (KPermission permission : this.permissions) {
            if (!kingdom.hasPermission(user, permission)) {
                return false;
            }
        }
        return true;
    }

}
