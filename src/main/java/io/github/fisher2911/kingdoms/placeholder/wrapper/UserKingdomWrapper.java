package io.github.fisher2911.kingdoms.placeholder.wrapper;

import io.github.fisher2911.kingdoms.kingdom.Kingdom;
import io.github.fisher2911.kingdoms.user.User;

public record UserKingdomWrapper(User user, Kingdom kingdom) {

    @Override
    public String toString() {
        return "UserKingdomWrapper{" +
                "user=" + user +
                ", kingdom=" + kingdom +
                '}';
    }
}
