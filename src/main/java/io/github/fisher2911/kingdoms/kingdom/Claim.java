package io.github.fisher2911.kingdoms.kingdom;

import io.github.fisher2911.kingdoms.kingdom.permission.KPermissible;
import io.github.fisher2911.kingdoms.world.KChunk;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface Claim extends KPermissible {

    boolean isWilderness();
    int getKingdomId();
    @Nullable
    <T> T getData(String key);
    UUID getWorld();
    KChunk getChunk();

}
