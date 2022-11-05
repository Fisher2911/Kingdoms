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

package io.github.fisher2911.kingdoms.config.condition;

import io.github.fisher2911.kingdoms.config.condition.impl.KPermissionItemConditional;
import io.github.fisher2911.kingdoms.config.condition.impl.PlaceholderConditionals;
import io.github.fisher2911.kingdoms.config.serializer.GuiItemSerializer;
import io.github.fisher2911.kingdoms.gui.ConditionalItem;
import io.github.fisher2911.kingdoms.gui.GuiKeys;
import io.github.fisher2911.kingdoms.kingdom.Kingdom;
import io.github.fisher2911.kingdoms.kingdom.permission.KPermission;
import io.github.fisher2911.kingdoms.kingdom.role.Role;
import io.github.fisher2911.kingdoms.placeholder.wrapper.PermissionWrapper;
import io.github.fisher2911.kingdoms.util.EnumUtil;
import io.github.fisher2911.kingdoms.util.Metadata;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ConditionSerializer {

    private static final String REQUIRED_METADATA_PATH = "required-metadata";
    private static final String CONDITIONS_PATH = "conditions";
    private static final String ITEM_PATH = "item";

    // base, list node
    private static final Map<ConditionType, BiFunction<ConfigurationNode, ConfigurationNode, List<MetadataPredicate>>> LOADERS = Map.of(
            ConditionType.PARSE_PLACEHOLDERS, ConditionSerializer::loadPlaceholderConditions,
            ConditionType.HAS_K_PERMISSIONS, ConditionSerializer::loadKPermissionPlaceholderConditions
    );

    public static ItemConditions loadConditional(ConfigurationNode source) throws SerializationException {
        final ConditionalItem item = GuiItemSerializer.INSTANCE.deserialize(ConditionalItem.class, source);
        final List<MetadataPredicate> conditionalList = new ArrayList<>();
        for (var entry : source.node(CONDITIONS_PATH).childrenMap().entrySet()) {
            if (!(entry.getKey() instanceof final String key)) continue;
            final ConditionType type = EnumUtil.valueOf(ConditionType.class, key.toUpperCase());
            if (type == null) {
                throw new SerializationException("Invalid condition type: " + entry.getKey());
            }
            final var loader = LOADERS.get(type);
            if (loader == null) {
                throw new SerializationException("No loader for condition type: " + entry.getKey());
            }
            final List<MetadataPredicate> conditionals = loader.apply(source, entry.getValue());
            conditionalList.addAll(conditionals);
        }
        return new ItemConditions(conditionalList, item);
    }

    private static final Map<GuiKeys, Function<Metadata, List<Object>>> PLACEHOLDER_CONDITIONS_PLACEHOLDER_FUNCTIONS = Map.of(
            GuiKeys.PERMISSION, metadata -> {
                final KPermission permission = metadata.get(GuiKeys.PERMISSION, KPermission.class);
                final Kingdom kingdom = metadata.get(GuiKeys.KINGDOM, Kingdom.class);
                final String roleId = metadata.get(GuiKeys.ROLE_ID, String.class);
                if (permission == null || kingdom == null || roleId == null) {
                    return Collections.emptyList();
                }
                final Role role = kingdom.getRole(roleId);
                if (role == null) return Collections.emptyList();
                return List.of(new PermissionWrapper(permission, kingdom.hasPermission(role, permission)));
            }
    );

    private static List<MetadataPredicate> loadPlaceholderConditions(ConfigurationNode base, ConfigurationNode source) {
        try {
            final List<Function<Metadata, List<Object>>> placeholderFunctions = new ArrayList<>();
            base.node(REQUIRED_METADATA_PATH).getList(String.class, new ArrayList<>())
                    .stream()
                    .map(s -> EnumUtil.valueOf(GuiKeys.class, s))
                    .filter(guiKeys -> guiKeys != null)
                    .forEach(k -> placeholderFunctions.add(PLACEHOLDER_CONDITIONS_PLACEHOLDER_FUNCTIONS.get(k)));
            return source.getList(String.class, new ArrayList<>())
                    .stream()
                    .map(s -> PlaceholderConditionals.parse(s, placeholderFunctions)).
                    collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Could not load placeholder conditionals", e);
        }
    }

    private static List<MetadataPredicate> loadKPermissionPlaceholderConditions(ConfigurationNode base, ConfigurationNode source) {
        try {
            return List.of(new KPermissionItemConditional(source.getList(String.class, new ArrayList<>())
                    .stream()
                    .map(KPermission::getByName)
                    .collect(Collectors.toList())));
        } catch (IOException e) {
            throw new RuntimeException("Could not load placeholder conditionals", e);
        }
    }

}
