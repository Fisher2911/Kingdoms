package io.github.fisher2911.kingdoms.kingdom.location;

import io.github.fisher2911.kingdoms.world.WorldPosition;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class KingdomLocations {

    public static final String HOME = "home";

    private final Map<String, WorldPosition> savedPositions;

    public KingdomLocations(final Map<String, WorldPosition> savedPositions) {
        this.savedPositions = savedPositions;
    }

    public Map<String, WorldPosition> getSavedPositions() {
        return savedPositions;
    }

    @Nullable
    public WorldPosition getPosition(String id) {
        return this.savedPositions.get(id);
    }

    public WorldPosition setPosition(String id, WorldPosition position) {
        return this.savedPositions.put(id, position);
    }

}
