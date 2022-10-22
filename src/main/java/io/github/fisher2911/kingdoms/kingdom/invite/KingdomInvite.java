package io.github.fisher2911.kingdoms.kingdom.invite;

import io.github.fisher2911.kingdoms.user.User;

import java.time.Instant;
import java.util.Objects;

public record KingdomInvite(int kingdomId, User inviter, User invited, Instant invitedAt) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final KingdomInvite that = (KingdomInvite) o;
        return kingdomId == that.kingdomId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(kingdomId);
    }
}
