package io.github.fisher2911.kingdoms.kingdom.upgrade;

import io.github.fisher2911.kingdoms.kingdom.Kingdom;
import io.github.fisher2911.kingdoms.kingdom.relation.RelationType;
import io.github.fisher2911.kingdoms.user.User;

import java.util.Set;

public interface EntryUpgrade<T> extends Upgrades<T> {

    void onEnter(Kingdom entering, User user, int level);
    void onLeave(Kingdom leaving, User user, int level);
    Set<RelationType> appliesTo();
    boolean appliesToSelf();


}
