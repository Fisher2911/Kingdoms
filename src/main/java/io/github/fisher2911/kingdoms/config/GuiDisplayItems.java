package io.github.fisher2911.kingdoms.config;

import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.kingdom.permission.KPermission;
import io.github.fisher2911.kingdoms.placeholder.Placeholder;
import io.github.fisher2911.kingdoms.util.builder.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GuiDisplayItems {

    private final Kingdoms plugin;
    private final Map<KPermission, ItemBuilder> permissionItems;

    public GuiDisplayItems(Kingdoms plugin) {
        this.plugin = plugin;
        this.permissionItems = new HashMap<>();
    }

    public Map<KPermission, ItemBuilder> getPermissionItems() {
        return permissionItems;
    }

    public void load() {
        final Material[] temp = Arrays.stream(Material.values()).filter(m -> m.toString().contains("WOOL") || m.toString().contains("GLASS")).
                toList().
                toArray(new Material[0]);
        for (KPermission permission : KPermission.values()) {
            Material random = null;
            ItemMeta itemMeta = null;
            while (random == null || random.isAir() || itemMeta == null) {
                random = temp[(int) (Math.random() * temp.length)];
                itemMeta = Bukkit.getItemFactory().getItemMeta(random);
            }
            final ItemStack itemStack = new ItemStack(random);
            final List<String> lore = new ArrayList<>();
            lore.add(ChatColor.BLUE + "Enabled: " + Placeholder.PERMISSION_VALUE_GUI_DISPLAY.asString());
            final ItemBuilder itemBuilder = ItemBuilder.from(itemStack).
                    amount(1).
                    name(ChatColor.GREEN + Placeholder.PERMISSION_NAME.asString()).
                    lore(lore);
            this.permissionItems.put(permission, itemBuilder);
        }
    }
}
