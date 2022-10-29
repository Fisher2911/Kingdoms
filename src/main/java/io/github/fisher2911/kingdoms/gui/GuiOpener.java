package io.github.fisher2911.kingdoms.gui;

import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.kingdom.Kingdom;
import io.github.fisher2911.kingdoms.user.User;
import io.github.fisher2911.kingdoms.util.function.TriConsumer;

import java.util.List;
import java.util.Map;

public class GuiOpener {

    private static final Kingdoms PLUGIN = Kingdoms.getPlugin(Kingdoms.class);

    private final String id;
    private final Gui.Builder builder;
    private final List<GuiKeys> requiredMetadata;

    public GuiOpener(String id, Gui.Builder builder, List<GuiKeys> requiredMetadata) {
        this.id = id;
        this.builder = builder;
        this.requiredMetadata = requiredMetadata;
    }

    private static final Map<GuiKeys, TriConsumer<Gui.Builder, User, Kingdom>> METADATA_MAP = Map.of(
            GuiKeys.KINGDOM, (builder, user, kingdom) -> builder.metadata(GuiKeys.KINGDOM, kingdom),
            GuiKeys.USER, (builder, user, kingdom) -> builder.metadata(GuiKeys.USER, user),
            GuiKeys.CHUNK, (builder, user, kingdom) -> {
                if (!user.isOnline()) return;
                builder.metadata(GuiKeys.CHUNK, PLUGIN.getWorldManager().getAt(user.getPlayer().getLocation()));
            },
            GuiKeys.ROLE_ID, (builder, user, kingdom) -> builder.metadata(GuiKeys.ROLE_ID, kingdom.getRole(user).id())
    );

    public void open(User user, Map<Object, Object> metadata) {
        final Gui.Builder copy = this.builder.copy().metadata(metadata);
        copy.metadata(GuiKeys.USER, user);
        if (!user.isOnline()) return;
        PLUGIN.getKingdomManager().getKingdom(user.getKingdomId()).ifPresent(kingdom -> {
            for (final GuiKeys key : this.requiredMetadata) {
                METADATA_MAP.get(key).accept(copy, user, kingdom);
            }
        });
        copy.build().open(user.getPlayer());
    }

    public void open(User user) {
        this.open(user, Map.of());
    }

    public String getId() {
        return id;
    }
}
