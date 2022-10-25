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
