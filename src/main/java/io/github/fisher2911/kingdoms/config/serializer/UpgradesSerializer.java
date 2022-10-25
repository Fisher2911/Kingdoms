package io.github.fisher2911.kingdoms.config.serializer;

import io.github.fisher2911.kingdoms.kingdom.upgrade.Upgrades;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public class UpgradesSerializer<T> implements TypeSerializer<Upgrades<T>> {

    @Override
    public Upgrades<T> deserialize(Type type, ConfigurationNode node) throws SerializationException {
        return null;
    }

    @Override
    public void serialize(Type type, @Nullable Upgrades<T> obj, ConfigurationNode node) throws SerializationException {

    }
}
