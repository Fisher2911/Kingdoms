package io.github.fisher2911.kingdoms.config;

import io.github.fisher2911.kingdoms.Kingdoms;

import java.nio.file.Path;

public abstract class Config {

    protected final Kingdoms plugin;
    protected final Path path;

    public Config(Kingdoms plugin, String... path) {
        this.plugin = plugin;
        if (path.length == 0) throw new IllegalArgumentException("No file path provided");
        final String first = path[0];
        if (path.length == 1) {
            this.path = this.plugin.getDataFolder().toPath().resolve(Path.of(first));
            if (!this.path.toFile().exists()) {
                this.plugin.saveResource(first, false);
            }
            return;
        }
        final String[] theRest = new String[path.length - 1];
        System.arraycopy(path, 1, theRest, 0, theRest.length);
        this.path = this.plugin.getDataFolder().toPath().resolve(Path.of(first, theRest));
        if (!this.path.toFile().exists()) {
            this.plugin.saveResource(Path.of(first, theRest).toString(), false);
        }
    }

}
