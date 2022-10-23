package io.github.fisher2911.kingdoms.chat;

import io.github.fisher2911.kingdoms.kingdom.Kingdom;
import io.github.fisher2911.kingdoms.kingdom.relation.RelationType;
import io.github.fisher2911.kingdoms.user.User;
import io.github.fisher2911.kingdoms.util.StringUtils;

import java.util.function.BiPredicate;

public enum ChatChannel {

    KINGDOM((kingdom, user) -> user.getKingdomId() == kingdom.getId()),
    ALLIANCE((kingdom, user) -> user.getKingdomId() == kingdom.getId() || kingdom.getKingdomRelation(user.getKingdomId()) == RelationType.ALLY),
    TRUCE((kingdom, user) -> user.getKingdomId() == kingdom.getId() || kingdom.getKingdomRelation(user.getKingdomId()) == RelationType.TRUCE),
    GLOBAL((kingdom, user) -> true);

    private final BiPredicate<Kingdom, User> canSeeChat;

    ChatChannel(BiPredicate<Kingdom, User> canSeeChat) {
        this.canSeeChat = canSeeChat;
    }

    public boolean canSeeChat(Kingdom kingdom, User toCheck) {
        return this.canSeeChat.test(kingdom, toCheck);
    }

    public String displayName() {
        return StringUtils.capitalize(this.name().toLowerCase());
    }
}
