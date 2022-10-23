package io.github.fisher2911.kingdoms.kingdom.relation;

import io.github.fisher2911.kingdoms.kingdom.Kingdom;
import io.github.fisher2911.kingdoms.user.User;

import java.time.Instant;
import java.util.Objects;

public record RelationInvite(Kingdom inviter, User user, Kingdom invited, Instant invitedAt) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final RelationInvite that = (RelationInvite) o;
        return Objects.equals(inviter.getId(), that.inviter.getId()) && Objects.equals(invited.getId(), that.invited.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(inviter.getId(), invited.getId());
    }
}
