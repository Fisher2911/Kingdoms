package io.github.fisher2911.kingdoms.kingdom.role;

import java.util.Objects;

public record Role(String id, String displayName, int weight) {

    public boolean isHigherRankedThan(Role other) {
        return this.weight < other.weight;
    }

    public boolean isAtLeastRank(Role other) {
        return this.weight <= other.weight;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Role role = (Role) o;
        return Objects.equals(id, role.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
