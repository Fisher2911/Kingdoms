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

public record TransactionResult(TransactionResultType type, double amount) {

    public static TransactionResult of(TransactionResultType type, double amount) {
        return new TransactionResult(type, amount);
    }

    public static TransactionResult withdrawSuccess(double amount) {
        return new TransactionResult(TransactionResultType.WITHDRAW_SUCCESS, amount);
    }

    public static TransactionResult depositSuccess(double amount) {
        return new TransactionResult(TransactionResultType.DEPOSIT_SUCCESS, amount);
    }

    public static TransactionResult notEnoughFunds(double amount) {
        return new TransactionResult(TransactionResultType.NOT_ENOUGH_FUNDS, amount);
    }

    public static TransactionResult bankNotLargeEnough(double amount) {
        return new TransactionResult(TransactionResultType.BANK_NOT_LARGE_ENOUGH, amount);
    }

    public static TransactionResult notAllowed(double amount) {
        return new TransactionResult(TransactionResultType.NOT_ALLOWED, amount);
    }

}
