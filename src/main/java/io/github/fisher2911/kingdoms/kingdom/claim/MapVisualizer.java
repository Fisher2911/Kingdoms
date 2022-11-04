package io.github.fisher2911.kingdoms.kingdom.claim;

import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.config.KingdomsSettings;
import io.github.fisher2911.kingdoms.kingdom.ClaimedChunk;
import io.github.fisher2911.kingdoms.kingdom.KingdomManager;
import io.github.fisher2911.kingdoms.kingdom.WorldManager;
import io.github.fisher2911.kingdoms.message.MessageHandler;
import io.github.fisher2911.kingdoms.user.User;
import io.github.fisher2911.kingdoms.world.KChunk;
import io.github.fisher2911.kingdoms.world.Position;
import io.github.fisher2911.kingdoms.world.WorldPosition;
import net.kyori.adventure.text.Component;

import java.util.UUID;

public class MapVisualizer {

    private final Kingdoms plugin;
    private final KingdomsSettings settings;
    private final KingdomManager kingdomManager;
    private final WorldManager worldManager;

    public MapVisualizer(Kingdoms plugin) {
        this.plugin = plugin;
        this.settings = plugin.getKingdomSettings();
        this.kingdomManager = plugin.getKingdomManager();
        this.worldManager = plugin.getWorldManager();
    }

    public void show(User user) {
        final WorldPosition worldPosition = user.getPosition();
        if (worldPosition == null) return;
        final Position position = worldPosition.position();
        final int xAmount = settings.getKingdomMapWidth() / 2;
        final int zAmount = settings.getKingdomMapHeight() / 2;
        final UUID world = worldPosition.world();
        Component builder = Component.text("");
        final int chunkX = position.getBlockX() >> 4;
        final int chunkZ = position.getBlockZ() >> 4;
        for (int z = chunkZ - zAmount; z < chunkZ + zAmount; z++) {
            for (int x = chunkX - xAmount; x < chunkX + xAmount; x++) {
                final ClaimedChunk chunk = this.worldManager.getAt(world, x, z);
                builder = builder.append(this.getChunkMessage(chunk, user));
            }
            builder = builder.append(Component.newline());
        }
        MessageHandler.sendMessage(
                user,
                builder
        );
    }

    private Component getChunkMessage(ClaimedChunk chunk, User viewer) {
        final KChunk kChunk = chunk.getChunk();
        if (chunk.isWilderness()) {
            return MessageHandler.deserialize(this.settings.getKingdomMapWildernessSymbol(), kChunk);
        }
        final int ownedBy = chunk.getKingdomId();
        final boolean ownedBySelf = ownedBy == viewer.getKingdomId();
        return this.kingdomManager.getKingdom(ownedBy, false)
                .map(kingdom -> {
                    if (ownedBySelf) {
                        return MessageHandler.deserialize(this.settings.getKingdomMapSelfClaimedLandSymbol(), kingdom, kChunk);
                    }
                    return MessageHandler.deserialize(this.settings.getKingdomMapClaimedLandSymbol(), kingdom, kChunk);
                })
                .orElse(MessageHandler.deserialize(this.settings.getKingdomMapWildernessSymbol(), kChunk));
    }

}
