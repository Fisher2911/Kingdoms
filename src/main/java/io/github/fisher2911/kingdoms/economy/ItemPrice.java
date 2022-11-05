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

package io.github.fisher2911.kingdoms.economy;

import io.github.fisher2911.kingdoms.user.User;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class ItemPrice implements Price {

    private final Map<ItemStack, Integer> prices;

    public ItemPrice(Map<ItemStack, Integer> prices) {
        this.prices = prices;
    }

    @Override
    public boolean canAfford(User user) {
        return new PaymentSteps(this.prices, user.getInventory()).cycle().canAfford();
    }

    @Override
    public void pay(User user) {
        new PaymentSteps(this.prices, user.getInventory()).cycle().pay();
    }

    @Override
    public boolean payIfCanAfford(User user) {
        return new PaymentSteps(this.prices, user.getInventory()).cycle().pay();
    }

    @Override
    public String getDisplay() {
        final StringBuilder builder = new StringBuilder();
        int index = 0;
        for (var entry : this.prices.entrySet()) {
            builder.append(entry.getKey().getItemMeta().getDisplayName()).append(": ").append(entry.getValue());
            if (index < this.prices.size() - 1) builder.append(", ");
            index++;
        }
        return builder.toString();
    }

    private static class PaymentSteps {

        private final Map<ItemStack, Integer> prices;
        private final Map<Integer, ItemStack> inventory;
        private final Map<Integer, Integer> slotAmountsToTake;
        private boolean hasCycled;
        private boolean canAfford;

        public PaymentSteps(Map<ItemStack, Integer> prices, Map<Integer, ItemStack> inventory) {
            this.prices = prices;
            this.inventory = inventory;
            this.slotAmountsToTake = new HashMap<>();
        }

        public PaymentSteps cycle() {
            for (var entry : this.prices.entrySet()) {
                final ItemStack itemStack = entry.getKey();
                final int price = entry.getValue();
                int currentCount = 0;
                boolean canAfford = false;
                for (var inventoryEntry : this.inventory.entrySet()) {
                    final int slot = inventoryEntry.getKey();
                    final ItemStack inventoryItem = inventoryEntry.getValue();
                    if (inventoryItem == null || inventoryItem.getType() == Material.AIR) continue;
                    if (!inventoryItem.isSimilar(itemStack)) continue;
                    final int newAmount = Math.min(price, inventoryItem.getAmount() + currentCount);
                    this.slotAmountsToTake.put(slot, newAmount - currentCount);
                    if (newAmount < price) {
                        currentCount = newAmount;
                        continue;
                    }
                    canAfford = true;
                    break;
                }
                if (!canAfford) {
                    this.canAfford = false;
                    this.hasCycled = true;
                    return this;
                }
            }
            this.hasCycled = true;
            this.canAfford = true;
            return this;
        }

        public boolean pay() {
            if (!this.canAfford) return false;
            for (var entry : this.slotAmountsToTake.entrySet()) {
                final int slot = entry.getKey();
                final int amountToTake = entry.getValue();
                final ItemStack itemStack = this.inventory.get(slot);
                if (amountToTake >= itemStack.getAmount()) {
                    this.inventory.put(slot, new ItemStack(Material.AIR));
                    continue;
                }
                itemStack.setAmount(itemStack.getAmount() - amountToTake);
            }
            return true;
        }

        public Map<ItemStack, Integer> getPrices() {
            return this.prices;
        }

        public Map<Integer, ItemStack> getInventory() {
            return this.inventory;
        }

        public Map<Integer, Integer> getSlotAmountsToTake() {
            return this.slotAmountsToTake;
        }

        public boolean hasCycled() {
            return this.hasCycled;
        }

        public boolean canAfford() {
            return this.canAfford;
        }
    }
}
