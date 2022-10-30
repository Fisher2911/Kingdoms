package io.github.fisher2911.kingdoms.config;

import io.github.fisher2911.kingdoms.Kingdoms;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.IOException;

public class KingdomsSettings extends Config {

    public KingdomsSettings(Kingdoms plugin) {
        super(plugin, "kingdom-defaults", "settings.yml");
    }

    private static final String DEFAULT_KINGDOM_DESCRIPTION_PATH = "default-kingdom-description";
    private static final String TELEPORT_DELAY_PATH = "teleport-delay";
    private static final String SAVE_INTERVAL_PATH = "save-interval";

    private String defaultKingdomDescription;
    private int teleportDelay;
    private int saveInterval;

    public void load() {
        final YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                .path(this.path)
                .build();

        try {
            final var source = loader.load();
            this.defaultKingdomDescription = source.node(DEFAULT_KINGDOM_DESCRIPTION_PATH).getString("");
            this.teleportDelay = source.node(TELEPORT_DELAY_PATH).getInt(3);
            this.saveInterval = source.node(SAVE_INTERVAL_PATH).getInt(20 * 60 * 5);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getDefaultKingdomDescription() {
        return this.defaultKingdomDescription;
    }

    public int getTeleportDelay() {
        return teleportDelay;
    }

    public int getSaveInterval() {
        return saveInterval;
    }
}
