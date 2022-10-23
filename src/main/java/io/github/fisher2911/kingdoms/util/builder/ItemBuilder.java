package io.github.fisher2911.kingdoms.util.builder;

import io.github.fisher2911.kingdoms.message.MessageHandler;
import io.github.fisher2911.kingdoms.placeholder.PlaceholderBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

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
        this.itemMeta.displayName(MessageHandler.MINI_MESSAGE.deserialize(name));
        return this;
    }

    public ItemBuilder lore(List<String> lore) {
        if (this.itemMeta == null) return this;
        final List<Component> newLore = new ArrayList<>();
        for (String s : lore) {
            newLore.add(MessageHandler.MINI_MESSAGE.deserialize(s));
        }
        this.itemMeta.lore(newLore);
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

    public ItemStack build(Object... placeholders) {
        final ItemStack itemStack = new ItemStack(this.material);
        itemStack.setAmount(Math.max(1, this.amount));
        if (this.itemMeta == null) return itemStack;
        final ItemMeta itemMeta = this.itemMeta.clone();
        final Component name = itemMeta.displayName();
        if (name != null) itemMeta.displayName(PlaceholderBuilder.apply(name, placeholders));
        final List<Component> lore = itemMeta.lore();
        if (lore != null) {
            final List<Component> newLore = new ArrayList<>();
            for (Component c : lore) {
                newLore.add(PlaceholderBuilder.apply(c, placeholders));
            }
            itemMeta.lore(newLore);
        }
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public ItemStack build() {
        final ItemStack itemStack = new ItemStack(this.material);
        itemStack.setAmount(Math.max(1, this.amount));
        if (this.itemMeta == null) return itemStack;
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    protected Object copy() {
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
