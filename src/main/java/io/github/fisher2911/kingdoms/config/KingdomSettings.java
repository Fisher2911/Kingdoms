package io.github.fisher2911.kingdoms.config;

import io.github.fisher2911.kingdoms.Kingdoms;

import java.nio.file.Path;

public class KingdomSettings {

    private final Kingdoms plugin;
    private final Path path;

    public KingdomSettings(Kingdoms plugin) {
        this.plugin = plugin;
        this.path = this.plugin.getDataFolder().toPath().resolve("kingdom-settings.yml");
    }

    public void load() {

    }
}
