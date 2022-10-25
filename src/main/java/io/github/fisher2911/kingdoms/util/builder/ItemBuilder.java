package io.github.fisher2911.kingdoms.util.builder;

import io.github.fisher2911.kingdoms.message.MessageHandler;
import io.github.fisher2911.kingdoms.placeholder.PlaceholderBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ItemBuilder {

    private Material material;
    private int amount;
    private ItemMeta itemMeta;

    private ItemBuilder(Material material) {
        this.material = material;
        this.itemMeta = Bukkit.getItemFactory().getItemMeta(material);
        this.amount = 1;
    }

    private ItemBuilder(ItemStack itemStack) {
        this.material = itemStack.getType();
        this.itemMeta = itemStack.getItemMeta();
        this.amount = itemStack.getAmount();
    }

    public static ItemBuilder from(Material material) {
        return new ItemBuilder(material);
    }

    public static ItemBuilder from(ItemStack itemStack) {
        return new ItemBuilder(itemStack);
    }

    public ItemBuilder amount(int amount) {
        this.amount = amount;
        return this;
    }

    public ItemBuilder name(String name) {
        if (this.itemMeta == null) return this;
        this.itemMeta.setDisplayName(name);
//        this.itemMeta.setDisplayName(MessageHandler.serialize(name));
        return this;
    }

    public ItemBuilder lore(List<String> lore) {
        if (this.itemMeta == null) return this;
//        final List<String> newLore = new ArrayList<>();
//        for (String s : lore) {
//            newLore.add(MessageHandler.serialize(s));
//        }
        this.itemMeta.setLore(lore);
        return this;
    }

    public ItemBuilder unbreakable() {
        if (this.itemMeta == null) return this;
        this.itemMeta.setUnbreakable(true);
        return this;
    }

    public ItemBuilder enchant(Enchantment enchantment, int level) {
        if (this.itemMeta == null) return this;
        this.itemMeta.addEnchant(enchantment, level, true);
        return this;
    }

    public ItemBuilder flag(ItemFlag... flags) {
        if (this.itemMeta == null) return this;
        this.itemMeta.addItemFlags(flags);
        return this;
    }

    public ItemBuilder enchantments(Map<Enchantment, Integer> enchants) {
        if (this.itemMeta == null) return this;
        for (final Map.Entry<Enchantment, Integer> entry : enchants.entrySet()) {
            this.enchant(entry.getKey(), entry.getValue());
        }
        return this;
    }

    public ItemBuilder glow(boolean glow) {
        if (this.itemMeta == null) return this;
        if (glow) {
            this.enchant(Enchantment.LUCK, 1);
            if (!this.itemMeta.getEnchants().isEmpty()) return this;
            this.flag(ItemFlag.HIDE_ENCHANTS);
            return this;
        }
        this.itemMeta.removeEnchant(Enchantment.LUCK);
        if (!this.itemMeta.getEnchants().isEmpty()) return this;
        this.itemMeta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
        return this;
    }

    public ItemStack build(Object... placeholders) {
        final ItemStack itemStack = new ItemStack(this.material);
        itemStack.setAmount(Math.max(1, this.amount));
        if (this.itemMeta == null) return itemStack;
        final ItemMeta itemMeta = this.itemMeta.clone();
        final String name = itemMeta.getDisplayName();
        if (name != null) itemMeta.setDisplayName(MessageHandler.serialize(PlaceholderBuilder.apply(name, placeholders)));
        final List<String> lore = itemMeta.getLore();
        if (lore != null) {
            itemMeta.setLore(this.buildLore(lore, placeholders));
        }
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    private List<String> buildLore(List<String> lore, Object... placeholders) {
        return lore.stream().
                map(s -> MessageHandler.serialize(PlaceholderBuilder.apply(s, placeholders))).
                collect(Collectors.toList());
    }

    public ItemStack build() {
        final ItemStack itemStack = new ItemStack(this.material);
        itemStack.setAmount(Math.max(1, this.amount));
        if (this.itemMeta == null) return itemStack;
        final ItemMeta itemMeta = this.itemMeta.clone();
        final String name = itemMeta.getDisplayName();
        if (name != null) itemMeta.setDisplayName(MessageHandler.serialize(name));
        final List<String> lore = itemMeta.getLore();
        if (lore != null) {
            itemMeta.setLore(this.buildLore(lore));
        }
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public ItemBuilder copy() {
        final ItemBuilder builder = new ItemBuilder(this.material).amount(this.amount);
        if (this.itemMeta == null) return builder;
        builder.itemMeta = this.itemMeta.clone();
        return builder;
    }

    @Override
    public String toString() {
        return "ItemBuilder{" +
                "material=" + material +
                ", amount=" + amount +
                ", itemMeta=" + itemMeta +
                '}';
    }
}
