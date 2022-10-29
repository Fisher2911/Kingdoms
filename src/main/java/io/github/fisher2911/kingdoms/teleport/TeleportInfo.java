package io.github.fisher2911.kingdoms.teleport;

import io.github.fisher2911.kingdoms.user.User;
import io.github.fisher2911.kingdoms.world.WorldPosition;

public class TeleportInfo {

    private final User user;
    private final WorldPosition to;
    private int secondsLeft;
    private final WorldPosition startPosition;

    public TeleportInfo(User user, WorldPosition to, int secondsLeft, WorldPosition startPosition) {
        this.user = user;
        this.to = to;
        this.secondsLeft = secondsLeft;
        this.startPosition = startPosition;
    }

    public User getUser() {
        return user;
    }

    public WorldPosition getTo() {
        return to;
    }

    public int getSecondsLeft() {
        return secondsLeft;
    }

    public WorldPosition getStartPosition() {
        return startPosition;
    }

    public void setSecondsLeft(int secondsLeft) {
        this.secondsLeft = secondsLeft;
    }

    public void decSeconds() {
        this.secondsLeft--;
    }
}
