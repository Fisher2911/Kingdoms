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

package io.github.fisher2911.kingdoms.config.condition.impl;

import io.github.fisher2911.kingdoms.config.condition.MetadataPredicate;
import io.github.fisher2911.kingdoms.gui.GuiKeys;
import io.github.fisher2911.kingdoms.kingdom.ClaimedChunk;
import io.github.fisher2911.kingdoms.kingdom.Kingdom;
import io.github.fisher2911.kingdoms.kingdom.permission.KPermission;
import io.github.fisher2911.kingdoms.kingdom.role.Role;
import io.github.fisher2911.kingdoms.user.User;
import io.github.fisher2911.kingdoms.util.Metadata;

import java.util.List;

public class KPermissionItemConditional implements MetadataPredicate {

    private final List<KPermission> permissions;

    public KPermissionItemConditional(final List<KPermission> permissions) {
        this.permissions = permissions;
    }

    public boolean test(Metadata possible) {
        final Kingdom kingdom = possible.get(GuiKeys.KINGDOM, Kingdom.class);
        final User user = possible.get(GuiKeys.USER, User.class);
        if (kingdom == null || user == null) {
            return false;
        }
        final Role role = kingdom.getRole(possible.get(GuiKeys.ROLE_ID, String.class));
        final KPermission editing = possible.get(GuiKeys.PERMISSION, KPermission.class);
        for (KPermission permission : this.permissions) {
            if (
                    editing != null &&
                    permission == KPermission.EDIT_LOWER_ROLES_PERMISSIONS &&
                            kingdom.hasPermission(user, permission) &&
                            (role.isAtLeastRank(kingdom.getRole(user)) ||
                            !kingdom.hasPermission(user, editing))
            ) {
                return false;
            }
            final ClaimedChunk chunk = possible.get(GuiKeys.CHUNK, ClaimedChunk.class);
            if  (chunk != null) {
                return kingdom.hasPermission(user, permission, chunk);
            }
            if (!kingdom.hasPermission(user, permission)) {
                return false;
            }
        }
        return true;
    }

}
