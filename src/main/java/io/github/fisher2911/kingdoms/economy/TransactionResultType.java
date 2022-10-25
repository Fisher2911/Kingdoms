package io.github.fisher2911.kingdoms.economy;

import io.github.fisher2911.kingdoms.message.Message;

public enum TransactionResultType {

    WITHDRAW_SUCCESS(true, Message.KINGDOM_BANK_WITHDRAW_SUCCESS),
    DEPOSIT_SUCCESS(true, Message.KINGDOM_BANK_DEPOSIT_SUCCESS),
    NOT_ALLOWED(false, Message.NO_KINGDOM_PERMISSION),
    NOT_ENOUGH_FUNDS(false, Message.NOT_ENOUGH_FUNDS),
    BANK_NOT_LARGE_ENOUGH(false, Message.KINGDOM_BANK_NOT_LARGE_ENOUGH);

    private final boolean isSuccessful;
    private final Message message;

    TransactionResultType(boolean isSuccessful, Message message) {
        this.isSuccessful = isSuccessful;
        this.message = message;
    }

    public Message getMessage() {
        return message;
    }

    public boolean isSuccessful() {
        return isSuccessful;
    }
}
