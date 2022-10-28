package io.github.fisher2911.kingdoms.gui.type;

import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.gui.BaseGui;
import io.github.fisher2911.kingdoms.kingdom.Kingdom;
import io.github.fisher2911.kingdoms.kingdom.upgrade.Upgrades;
import io.github.fisher2911.kingdoms.util.builder.ItemBuilder;
import org.bukkit.entity.HumanEntity;

public class UpgradeGui {

    private final BaseGui gui;
    private final Kingdom kingdom;

    private UpgradeGui(BaseGui gui, Kingdom kingdom) {
        this.gui = gui;
        this.kingdom = kingdom;
    }

    public static UpgradeGui create(final Kingdoms plugin, Kingdom kingdom) {
        return null;
//        final UpgradeHolder upgradesHolder = kingdom.getUpgradeHolder();
//        final KingdomManager kingdomManager = plugin.getKingdomManager();
//        final UserManager userManager = plugin.getUserManager();
//        final Map<Integer, BaseGuiItem> items = new HashMap<>();
//        int index = 0;
//        final Consumer<InventoryEventWrapper<InventoryClickEvent>> clickHandler = wrapper -> {
//            final InventoryClickEvent event = wrapper.event();
//            event.setCancelled(true);
//            final User user = userManager.wrap(event.getWhoClicked());
//            final BaseGui gui = wrapper.gui();
//            final int slot = event.getSlot();
//            final BaseGuiItem clicked = gui.getItem(slot);
//            if (clicked == null) return;
//            final Upgrades<?> upgrades = clicked.getMetadata(GuiItemKeys.UPGRADE, Upgrades.class);
//            if (upgrades == null) return;
//            kingdomManager.tryLevelUpUpgrade(kingdom, user, upgrades);
//            final Integer newLevel = kingdom.getUpgradeLevel(upgrades.getId());
//            if (newLevel != null && newLevel >= upgrades.getMaxLevel()) {
//                gui.set(slot, clicked.withItem(getUpgradeItem(upgrades, newLevel)));
//            }
//            gui.refresh(slot);
//        };
//
//        for (var entry : upgradesHolder.getUpgradesMap().entrySet()) {
//            final String id = entry.getKey();
//            final Upgrades<?> upgrades = entry.getValue();
//            final Integer upgradeLevel = kingdom.getUpgradeLevel(id);
//            if (upgradeLevel == null) continue;
//            final ItemBuilder builder = getUpgradeItem(
//                    upgrades,
//                    upgradeLevel
//            );
//            final List<Supplier<Object>> placeholders = new ArrayList<>();
//            placeholders.add(() -> {
//                final Integer newLevel = kingdom.getUpgradeLevel(id);
//                if (newLevel == null) return new UpgradesWrapper(upgrades, upgradeLevel);
//                return new UpgradesWrapper(upgrades, newLevel);
//            });
//            placeholders.add(() -> new UpgradeLevelWrapper(kingdom, id));
//            items.put(index, GuiItem.builder(builder).
//                    clickHandler(clickHandler).
//                    metadata(Map.of(GuiItemKeys.UPGRADE, upgrades)).
//                    placeholders(placeholders).
//                    build());
//            index++;
//        }
//        return new UpgradeGui(
//                Gui.builder("upgrade").
//                        name("<gray>Permissions").
//                        items(items).
//                        rows(items.size() / 9 + 1).
//                        cancelAllClicks().
//                        build(),
//                kingdom
//        );
    }

    private static ItemBuilder getUpgradeItem(Upgrades<?> upgrades, int upgradeLevel) {
        final ItemBuilder builder;
        if (upgradeLevel >= upgrades.getMaxLevel()) {
            builder = upgrades.getMaxLevelGuiItem();
        } else {
            builder = upgrades.getGuiItem();
        }
        return builder;
    }

    public void open(HumanEntity human) {
        this.gui.open(human);
    }

}
