/*
 *     Kingdoms Plugin
 *     Copyright (C) 2022  Fisher2911
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.fisher2911.kingdoms.gui;

import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.config.serializer.GuiSerializer;
import io.github.fisher2911.kingdoms.user.User;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GuiManager {

    public static final String MAIN_GUI = "main";
    public static final String PERMISSIONS_GUI = "permissions";
    public static final String ROLES_GUI = "roles";
    public static final String UPGRADES_GUI = "upgrades";
    public static final String MEMBERS_GUI = "members";

    private static final List<String> DEFAULT_FILES = List.of(
            "chunk-permissions.yml",
            "permissions.yml",
            "main-menu.yml",
            "roles-menu.yml",
            "upgrades.yml",
            "members.yml",
            "set-member-role.yml"
    );

    private final Path guiFolder;

    private final Kingdoms plugin;
    private final Map<String, GuiOpener> guiMap;

    public GuiManager(final Kingdoms plugin) {
        this.plugin = plugin;
        this.guiMap = new HashMap<>();
        this.guiFolder = this.plugin.getDataFolder().toPath().resolve("guis");
    }

    public void open(String gui, User user) {
        final GuiOpener opener = this.guiMap.get(gui);
        if (opener == null) return;
        opener.open(user);
    }

    public void open(String gui, User user, Map<Object, Object> metadata, Set<Object> keysToOverwrite) {
        final GuiOpener opener = this.guiMap.get(gui);
        if (opener == null) return;
        opener.open(user, metadata, keysToOverwrite);
    }

    @Nullable
    public GuiOpener getGuiOpener(String gui) {
        return this.guiMap.get(gui);
    }

    public void addGuiOpener(GuiOpener opener) {
        this.guiMap.put(opener.getId(), opener);
    }

    public void load() {
        final File folder = guiFolder.toFile();
        this.createFiles(folder);
        this.loadFolder(folder);
    }

    private void loadFolder(File folder) {
        final File[] files = folder.listFiles();
        if (files == null) return;
        for (final File file : files) {
            if (file.isDirectory()) {
                this.loadFolder(file);
            } else {
                this.loadFile(file);
            }
        }
    }

    public void reload() {
        this.guiMap.clear();
        this.load();
    }

    private void loadFile(File file) {
        final YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                .path(file.toPath())
                .build();
        try {
            final var source = loader.load();
            final GuiOpener opener = GuiSerializer.deserialize(source);
            this.guiMap.put(opener.getId(), opener);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createFiles(File folder) {
        if (!folder.exists()) {
            folder.mkdirs();
        }
        for (final String file : DEFAULT_FILES) {
            final Path path = this.guiFolder.resolve(file);
            if (!path.toFile().exists()) {
                this.plugin.saveResource("guis/" + file, false);
            }
        }
    }

    private void createFile(String name) {
        final Path path = this.guiFolder.resolve(name);
        if (!path.toFile().exists()) {
            this.plugin.saveResource("guis/" + name, false);
        }
    }
}
