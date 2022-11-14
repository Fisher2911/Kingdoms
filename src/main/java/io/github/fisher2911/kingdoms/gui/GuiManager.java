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

import io.github.fisher2911.fisherlib.config.serializer.ItemSerializers;
import io.github.fisher2911.fisherlib.gui.AbstractGuiManager;
import io.github.fisher2911.fisherlib.gui.Gui;
import io.github.fisher2911.fisherlib.gui.GuiKey;
import io.github.fisher2911.fisherlib.gui.GuiOpener;
import io.github.fisher2911.fisherlib.task.TaskChain;
import io.github.fisher2911.fisherlib.util.function.TriConsumer;
import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.config.condition.KingdomsConditionSerializer;
import io.github.fisher2911.kingdoms.config.serializer.KingdomsClickActionSerializer;
import io.github.fisher2911.kingdoms.config.serializer.KingdomsGuiItemSerializer;
import io.github.fisher2911.kingdoms.config.serializer.KingdomsGuiSerializer;
import io.github.fisher2911.kingdoms.kingdom.Kingdom;
import io.github.fisher2911.kingdoms.user.User;

import java.util.List;
import java.util.Map;

public class GuiManager extends AbstractGuiManager<User, Kingdoms> {

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

    public GuiManager(final Kingdoms plugin) {
        super(plugin, DEFAULT_FILES, new KingdomsGuiSerializer(new ItemSerializers<>(
                KingdomsGuiItemSerializer.INSTANCE,
                KingdomsClickActionSerializer.INSTANCE,
                KingdomsConditionSerializer.INSTANCE
        )));
    }

    private final Map<GuiKey, TriConsumer<Gui.Builder, User, Kingdom>> metadataMap = Map.of(
            GuiKeys.KINGDOM, (builder, user, kingdom) -> {
                builder.metadata(GuiKeys.KINGDOM, kingdom);
                builder.metadata(GuiKey.UPGRADEABLE, kingdom);
            },
            GuiKey.USER, (builder, user, kingdom) -> builder.metadata(GuiKey.USER, user),
            GuiKeys.CHUNK, (builder, user, kingdom) -> {
                if (!user.isOnline()) return;
                builder.metadata(GuiKeys.CHUNK, this.plugin.getWorldManager().getAt(user.getPlayer().getLocation()));
            },
            GuiKeys.ROLE_ID, (builder, user, kingdom) -> builder.metadata(GuiKeys.ROLE_ID, kingdom.getRole(user).id())/*,
            GuiKeys.USER_KINGDOM_WRAPPER, (builder, user, kingdom) -> builder.metadata(GuiKeys.USER_KINGDOM_WRAPPER, new UserKingdomWrapper(user, kingdom), false)*/
    );

    @Override
    protected void openHandler(GuiOpener<User> guiOpener, Gui.Builder builder, User user) {
        TaskChain.create(this.plugin)
                .supplyAsync(() -> this.plugin.getKingdomManager().getKingdom(user.getKingdomId(), true))
                .consumeSync(opt -> {
                    if (!user.isOnline()) return;
                    opt.ifPresent(kingdom -> {
                        for (final GuiKey key : guiOpener.getRequiredMetadata()) {
                            if (builder.getMetadata().get().containsKey(key)) continue;
                            this.metadataMap.get(key).accept(builder, user, kingdom);
                        }
                    });
                    builder.build().open(user.getPlayer());
                })
                .execute();
    }

    //    public void open(String gui, User user) {
//        final GuiOpener<User> opener = this.guiMap.get(gui);
//        if (opener == null) return;
//        opener.open(user);
//    }
//
//    public void open(String gui, User user, Map<Object, Object> metadata, Set<Object> keysToOverwrite) {
//        final GuiOpener<User> opener = this.guiMap.get(gui);
//        if (opener == null) return;
//        opener.open(user, metadata, keysToOverwrite);
//    }
//
//    @Nullable
//    public GuiOpener<User> getGuiOpener(String gui) {
//        return this.guiMap.get(gui);
//    }
//
//    public void addGuiOpener(GuiOpener<User> opener) {
//        this.guiMap.put(opener.getId(), opener);
//    }
//
//    public void load() {
//        final File folder = guiFolder.toFile();
//        this.createFiles(folder);
//        this.loadFolder(folder);
//    }
//
//    private void loadFolder(File folder) {
//        final File[] files = folder.listFiles();
//        if (files == null) return;
//        for (final File file : files) {
//            if (file.isDirectory()) {
//                this.loadFolder(file);
//            } else {
//                this.loadFile(file);
//            }
//        }
//    }
//
//    public void reload() {
//        this.guiMap.clear();
//        this.load();
//    }
//
//    private void loadFile(File file) {
//        final YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
//                .path(file.toPath())
//                .build();
//        try {
//            final var source = loader.load();
//            final GuiOpener<User> opener = GuiSerializer.deserialize(source);
//            this.guiMap.put(opener.getId(), opener);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void createFiles(File folder) {
//        if (!folder.exists()) {
//            folder.mkdirs();
//        }
//        for (final String file : DEFAULT_FILES) {
//            final Path path = this.guiFolder.resolve(file);
//            if (!path.toFile().exists()) {
//                this.plugin.saveResource("guis/" + file, false);
//            }
//        }
//    }
//
//    private void createFile(String name) {
//        final Path path = this.guiFolder.resolve(name);
//        if (!path.toFile().exists()) {
//            this.plugin.saveResource("guis/" + name, false);
//        }
//    }
}
