package io.github.fisher2911.kingdoms.util.builder;

import io.github.fisher2911.kingdoms.placeholder.PlaceholderBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.UUID;

@SuppressWarnings("unchecked")
public class SkullBuilder extends BaseItemBuilder {

    private String ownerUUIDPlaceholder;

    public SkullBuilder(Material material) {
        super(material);
    }

    public SkullBuilder(ItemStack itemStack) {
        super(itemStack);
    }

    public SkullBuilder owner(String ownerUUIDPlaceholder) {
        this.ownerUUIDPlaceholder = ownerUUIDPlaceholder;
        return this;
    }

    public ItemStack build(Object... placeholders) {
        final ItemStack itemStack = super.build(placeholders);
        if (this.ownerUUIDPlaceholder == null) return itemStack;
        final ItemMeta itemMeta = itemStack.getItemMeta();
        if (!(itemMeta instanceof final SkullMeta meta)) return itemStack;
        final String ownerUUID = PlaceholderBuilder.apply(this.ownerUUIDPlaceholder, placeholders);
        if (ownerUUID == null) return itemStack;
        try {
            meta.setOwningPlayer(Bukkit.getOfflinePlayer(UUID.fromString(ownerUUID)));
            itemStack.setItemMeta(meta);
            return itemStack;
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Could not parse UUID: " + ownerUUID);
        }
    }

    public ItemStack build() {
        final ItemStack itemStack = super.build();
        if (this.ownerUUIDPlaceholder == null) return itemStack;
        final ItemMeta itemMeta = itemStack.getItemMeta();
        if (!(itemMeta instanceof final SkullMeta meta)) return itemStack;
        try {
            meta.setOwningPlayer(Bukkit.getOfflinePlayer(UUID.fromString(this.ownerUUIDPlaceholder)));
            itemStack.setItemMeta(meta);
            return itemStack;
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Could not parse UUID: " + this.ownerUUIDPlaceholder);
        }
    }

    public <T extends BaseItemBuilder> T copy() {
        final SkullBuilder builder = new SkullBuilder(this.material).amount(this.amount);
        if (this.itemMeta == null) return (T) builder;
        builder.itemMeta = this.itemMeta.clone();
        return (T) builder;
    }
}
