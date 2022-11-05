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

import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.kingdom.KingdomManager;
import io.github.fisher2911.kingdoms.kingdom.permission.KPermission;
import io.github.fisher2911.kingdoms.message.Message;
import io.github.fisher2911.kingdoms.message.MessageHandler;
import io.github.fisher2911.kingdoms.task.TaskChain;
import io.github.fisher2911.kingdoms.user.User;

public class EconomyManager {

    private final Kingdoms plugin;
    private final KingdomManager kingdomManager;

    public EconomyManager(final Kingdoms plugin) {
        this.plugin = plugin;
        this.kingdomManager = plugin.getKingdomManager();
    }

    public void tryDeposit(User user, double amount) {
        TaskChain.create(this.plugin)
                .supplyAsync(() -> this.kingdomManager.getKingdom(user.getKingdomId(), true))
                .consumeSync(opt -> opt.ifPresentOrElse(kingdom -> {
                    if (!kingdom.hasPermission(user, KPermission.DEPOSIT_MONEY)) {
                        MessageHandler.sendMessage(user, Message.NO_KINGDOM_PERMISSION);
                        return;
                    }
                    if (user.getMoney() < amount) {
                        MessageHandler.sendMessage(user, Message.USER_NOT_ENOUGH_MONEY);
                        return;
                    }
                    final TransactionResult result = kingdom.getBank().deposit(kingdom, amount);
                    user.takeMoney(amount);
                    MessageHandler.sendMessage(user, result.type().getMessage(), result.type().of(amount), kingdom);
                }, () -> MessageHandler.sendNotInKingdom(user)))
                .execute();
    }

    public void tryWithdraw(User user, double amount) {
        TaskChain.create(this.plugin)
                .supplyAsync(() -> this.kingdomManager.getKingdom(user.getKingdomId(), true))
                .consumeSync(opt -> opt.ifPresentOrElse(kingdom -> {
                    if (!kingdom.hasPermission(user, KPermission.WITHDRAW_MONEY)) {
                        MessageHandler.sendMessage(user, Message.NO_KINGDOM_PERMISSION);
                        return;
                    }
                    final TransactionResult result = kingdom.getBank().withdraw(kingdom, amount);
                    user.addMoney(amount);
                    MessageHandler.sendMessage(user, result.type().getMessage(), result.type().of(amount), kingdom);
                }, () -> MessageHandler.sendNotInKingdom(user)))
                .execute();
    }

    public void sendKingdomBalance(User user) {
        TaskChain.create(this.plugin)
                .supplyAsync(() -> this.kingdomManager.getKingdom(user.getKingdomId(), true))
                .consumeSync(opt -> opt.ifPresentOrElse(kingdom -> {
                    if (!kingdom.hasPermission(user, KPermission.VIEW_BANK_BALANCE)) {
                        MessageHandler.sendMessage(user, Message.NO_KINGDOM_PERMISSION);
                        return;
                    }
                    MessageHandler.sendMessage(user, Message.KINGDOM_BALANCE, kingdom);
                }, () -> MessageHandler.sendNotInKingdom(user)))
                .execute();
        ;
    }

}
