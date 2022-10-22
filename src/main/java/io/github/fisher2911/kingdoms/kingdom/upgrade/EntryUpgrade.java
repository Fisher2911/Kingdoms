package io.github.fisher2911.kingdoms.kingdom.upgrade;

import io.github.fisher2911.kingdoms.user.User;

public interface EntryUpgrade {

    void onEnter(User user, int level);
    void onLeave(User user, int level);


}
