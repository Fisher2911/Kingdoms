package io.github.fisher2911.kingdoms.message;

import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.config.Config;
import io.github.fisher2911.kingdoms.placeholder.PlaceholderBuilder;
import io.github.fisher2911.kingdoms.user.User;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

public class MessageHandler extends Config {

    public static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private final Map<Message, String> messages = new EnumMap<>(Message.class);

    private MessageHandler(Kingdoms plugin, String... path) {
        super(plugin, path);
    }

    private static final MessageHandler INSTANCE;

    static {
        INSTANCE = new MessageHandler(Kingdoms.getPlugin(Kingdoms.class), "messages.yml");
    }

    public static void sendMessage(User user, Message message) {
        final String value = INSTANCE.getMessage(message);
        if (value.isBlank()) return;
        user.sendMessage(MINI_MESSAGE.deserialize(value));
    }

    public static void sendMessage(User user, Message message, Object... placeholders) {
        final String value = INSTANCE.getMessage(message);
        if (value.isBlank()) return;
        user.sendMessage(MINI_MESSAGE.deserialize(PlaceholderBuilder.apply(value, placeholders)));
    }

    public static void sendMessage(User user, String message) {
        user.sendMessage(MINI_MESSAGE.deserialize(message));
    }

    public static void sendMessage(User user, String message, Object... placeholders) {
        user.sendMessage(MINI_MESSAGE.deserialize(PlaceholderBuilder.apply(message, placeholders)));
    }

    private String getMessage(Message message) {
        return this.messages.get(message);
    }

    public static void reload() {
        MessageHandler.INSTANCE.messages.clear();
        load();
    }

    public static void load() {
        INSTANCE.loadMessages();
    }

    private void loadMessages() {
        final File file = this.path.toFile();
        final boolean exists = file.exists();
        if (!exists) {
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        final YamlConfigurationLoader loader = YamlConfigurationLoader.
                builder().
                path(this.path).nodeStyle(NodeStyle.BLOCK).
                defaultOptions(opts ->
                        opts.serializers(build -> {
                        }))
                .build();
        try {
            final ConfigurationNode source = loader.load();
            for (Message message : Message.values()) {
                final String messagePath = message.getConfigPath();
                if (!source.hasChild(messagePath)) {
                    source.node(messagePath).set(message.toString());
                    continue;
                }
                this.messages.put(message, source.node(messagePath).getString(""));
            }
            loader.save(source);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
