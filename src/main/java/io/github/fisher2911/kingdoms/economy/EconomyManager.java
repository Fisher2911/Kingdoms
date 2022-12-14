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

import io.github.fisher2911.fisherlib.economy.Bank;
import io.github.fisher2911.fisherlib.economy.TransactionResult;
import io.github.fisher2911.fisherlib.economy.TransactionResultType;
import io.github.fisher2911.fisherlib.message.MessageHandler;
import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.kingdom.Kingdom;
import io.github.fisher2911.kingdoms.kingdom.KingdomManager;
import io.github.fisher2911.kingdoms.kingdom.permission.KPermission;
import io.github.fisher2911.kingdoms.message.KMessage;
import io.github.fisher2911.fisherlib.task.TaskChain;
import io.github.fisher2911.kingdoms.user.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.BiFunction;

public class EconomyManager {

    private final Kingdoms plugin;
    private final MessageHandler messageHandler;
    private final KingdomManager kingdomManager;

    public EconomyManager(final Kingdoms plugin) {
        this.plugin = plugin;
        this.messageHandler = plugin.getMessageHandler();
        this.kingdomManager = plugin.getKingdomManager();
    }

    public static Bank<Kingdom> createKingdomBank(double startBalance) {
        final Collection<BiFunction<Kingdom, Double, TransactionResultType>> depositPredicates = new ArrayList<>();
        final Collection<BiFunction<Kingdom, Double, TransactionResultType>> withdrawPredicates = new ArrayList<>();

        depositPredicates.add((kingdom, amount) -> {
            final boolean canDeposit = kingdom.getBank().getBalance() + amount <= kingdom.getBankLimit();
            return canDeposit ? TransactionResultType.DEPOSIT_SUCCESS : TransactionResultType.BANK_NOT_LARGE_ENOUGH;
        });

        return new Bank<>(depositPredicates, withdrawPredicates, startBalance);
    }

    public void tryDeposit(User user, double amount) {
        TaskChain.create(this.plugin)
                .supplyAsync(() -> this.kingdomManager.getKingdom(user.getKingdomId(), true))
                .consumeSync(opt -> opt.ifPresentOrElse(kingdom -> {
                    if (!kingdom.hasPermission(user, KPermission.DEPOSIT_MONEY)) {
                        this.messageHandler.sendMessage(user, KMessage.NO_KINGDOM_PERMISSION);
                        return;
                    }
                    if (user.getMoney() < amount) {
                        this.messageHandler.sendMessage(user, KMessage.USER_NOT_ENOUGH_MONEY);
                        return;
                    }
                    final TransactionResult result = kingdom.getBank().deposit(kingdom, amount);
                    user.takeMoney(amount);
                    this.messageHandler.sendMessage(user, result.type().getMessage(), result.type().of(amount), kingdom);
                }, () -> this.messageHandler.sendMessage(user, KMessage.NOT_IN_KINGDOM)))
                .execute();
    }

    public void tryWithdraw(User user, double amount) {
        TaskChain.create(this.plugin)
                .supplyAsync(() -> this.kingdomManager.getKingdom(user.getKingdomId(), true))
                .consumeSync(opt -> opt.ifPresentOrElse(kingdom -> {
                    if (!kingdom.hasPermission(user, KPermission.WITHDRAW_MONEY)) {
                        this.messageHandler.sendMessage(user, KMessage.NO_KINGDOM_PERMISSION);
                        return;
                    }
                    final TransactionResult result = kingdom.getBank().withdraw(kingdom, amount);
                    user.addMoney(amount);
                    this.messageHandler.sendMessage(user, result.type().getMessage(), result.type().of(amount), kingdom);
                }, () -> this.messageHandler.sendMessage(user, KMessage.NOT_IN_KINGDOM)))
                .execute();
    }

    public void sendKingdomBalance(User user) {
        TaskChain.create(this.plugin)
                .supplyAsync(() -> this.kingdomManager.getKingdom(user.getKingdomId(), true))
                .consumeSync(opt -> opt.ifPresentOrElse(kingdom -> {
                    if (!kingdom.hasPermission(user, KPermission.VIEW_BANK_BALANCE)) {
                        this.messageHandler.sendMessage(user, KMessage.NO_KINGDOM_PERMISSION);
                        return;
                    }
                    this.messageHandler.sendMessage(user, KMessage.KINGDOM_BALANCE, kingdom);
                }, () -> this.messageHandler.sendMessage(user, KMessage.NOT_IN_KINGDOM)))
                .execute();
        ;
    }

}
