package io.github.fisher2911.kingdoms.config.serializer;

import io.github.fisher2911.kingdoms.gui.BaseGuiItem;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public class GuiItemSerializer implements TypeSerializer<BaseGuiItem> {

    public static final GuiItemSerializer INSTANCE = new GuiItemSerializer();

    private GuiItemSerializer() {}

    @Override
    public BaseGuiItem deserialize(Type type, ConfigurationNode node) throws SerializationException {
        return null;
    }

    @Override
    public void serialize(Type type, @Nullable BaseGuiItem obj, ConfigurationNode node) throws SerializationException {

    }
}
