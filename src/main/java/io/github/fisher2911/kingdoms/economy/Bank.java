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

import io.github.fisher2911.kingdoms.data.Saveable;
import io.github.fisher2911.kingdoms.kingdom.Kingdom;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.BiFunction;

public class Bank<T> implements Saveable {

    private final Collection<BiFunction<T, Double, TransactionResultType>> depositPredicates;
    private final Collection<BiFunction<T, Double, TransactionResultType>> withdrawPredicates;
    private double balance;
    private boolean dirty;

    public Bank(Collection<BiFunction<T, Double, TransactionResultType>> depositPredicates, Collection<BiFunction<T, Double, TransactionResultType>> withdrawPredicates, double balance) {
        this.depositPredicates = depositPredicates;
        this.withdrawPredicates = withdrawPredicates;
        this.balance = balance;
        this.dirty = true;
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

    /**
     *
     * @param t the bank owner type
     * @param withdrawAmount the amount to deposit
     * @return a pair of the transaction result type and the new balance
     */
    public TransactionResult withdraw(T t, double withdrawAmount) {
        TransactionResultType lastSuccess = TransactionResultType.WITHDRAW_SUCCESS;
        for (BiFunction<T, Double, TransactionResultType> predicate : this.withdrawPredicates) {
            final var result = predicate.apply(t, withdrawAmount);
            if (!result.isSuccessful()) return TransactionResult.of(result, this.balance);
        }
        if (this.balance < withdrawAmount) return TransactionResult.of(TransactionResultType.NOT_ENOUGH_FUNDS, this.balance);
        this.balance -= withdrawAmount;
        this.setDirty(true);
        return TransactionResult.of(lastSuccess, this.balance);
    }

    /**
     *
      * @param t the bank owner type
     * @param depositAmount the amount to deposit
     * @return a pair of the transaction result type and the new balance
     */
    public TransactionResult deposit(T t, double depositAmount) {
        TransactionResultType lastSuccess = TransactionResultType.DEPOSIT_SUCCESS;
        for (BiFunction<T, Double, TransactionResultType> predicate : this.depositPredicates) {
            final var result = predicate.apply(t, depositAmount);
            if (!result.isSuccessful()) return TransactionResult.of(result, this.balance);
        }
        this.balance += depositAmount;
        this.setDirty(true);
        return TransactionResult.of(lastSuccess, this.balance);
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
        this.setDirty(true);
    }

    @Override
    public boolean isDirty() {
        return dirty;
    }

    @Override
    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

}
