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

package io.github.fisher2911.kingdoms.chat;

import io.github.fisher2911.kingdoms.kingdom.Kingdom;
import io.github.fisher2911.kingdoms.kingdom.relation.RelationType;
import io.github.fisher2911.kingdoms.user.User;
import io.github.fisher2911.fisherlib.util.StringUtils;

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
