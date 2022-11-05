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

package io.github.fisher2911.kingdoms.config.serializer;

import io.github.fisher2911.kingdoms.util.EnumUtil;
import io.github.fisher2911.kingdoms.util.builder.BaseItemBuilder;
import io.github.fisher2911.kingdoms.util.builder.SkullBuilder;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemSerializer implements TypeSerializer<BaseItemBuilder> {

    public static final ItemSerializer INSTANCE = new ItemSerializer();

    private ItemSerializer() {}

    private static final String MATERIAL = "material";
    private static final String AMOUNT = "amount";
    private static final String NAME = "name";
    private static final String LORE = "lore";
    private static final String ENCHANTMENTS = "enchantments";
    private static final String ENCHANTMENT = "enchantment";
    private static final String LEVEL = "level";
    private static final String ITEM_FLAGS = "item-flags";
    private static final String GLOW = "glow";
    private static final String HEAD_OWNER = "skull-owner";

    @Override
    public BaseItemBuilder deserialize(Type type, ConfigurationNode node) throws SerializationException {
        final String material = node.node(MATERIAL).getString(Material.AIR.toString());
        final int amount = node.node(AMOUNT).getInt(1);
        final String name = node.node(NAME).getString("");
        final List<String> lore = node.node(LORE).getList(String.class);
        final Map<Enchantment, Integer> enchantments = new HashMap<>();
        for (final ConfigurationNode enchantmentNode : node.node(ENCHANTMENTS).childrenList()) {
            final String enchantment = enchantmentNode.node(ENCHANTMENT).getString();
            if (enchantment == null) continue;
            final int level = enchantmentNode.node(LEVEL).getInt();
            enchantments.put(Registry.ENCHANTMENT.get(NamespacedKey.minecraft(enchantment)), level);
        }

        final List<ItemFlag> itemFlags = node.node(ITEM_FLAGS).getList(String.class, new ArrayList<>()).
                stream().
                map(s -> EnumUtil.valueOf(ItemFlag.class, s)).
                filter(itemFlag -> itemFlag != null).
                toList();
        final boolean glow = node.node(GLOW).getBoolean();

        final BaseItemBuilder builder = BaseItemBuilder.from(Material.valueOf(material)).
                amount(amount).
                name(name).
                lore(lore).
                enchantments(enchantments).
                flag(itemFlags.toArray(new ItemFlag[0])).
                glow(glow);
        if (!(builder instanceof final SkullBuilder skullBuilder)) return builder;
        final String headOwner = node.node(HEAD_OWNER).getString();
        if (headOwner == null) return builder;
        return skullBuilder.owner(headOwner);
    }

    @Override
    public void serialize(Type type, @Nullable BaseItemBuilder obj, ConfigurationNode node) throws SerializationException {

    }
}
