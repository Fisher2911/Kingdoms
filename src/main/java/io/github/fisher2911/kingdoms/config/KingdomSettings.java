package io.github.fisher2911.kingdoms.config;

import io.github.fisher2911.kingdoms.Kingdoms;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.IOException;

public class KingdomSettings extends Config {

    public KingdomSettings(Kingdoms plugin) {
        super(plugin, "kingdom-defaults", "settings.yml");
    }

    public static final String DEFAULT_KINGDOM_DESCRIPTION = "default-kingdom-description";

    private String defaultKingdomDescription;

    public void load() {
        final YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                .path(this.path)
                .build();

        try {
            final var source = loader.load();
            this.defaultKingdomDescription = source.node(DEFAULT_KINGDOM_DESCRIPTION).getString("");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getDefaultKingdomDescription() {
        return this.defaultKingdomDescription;
    }
}
