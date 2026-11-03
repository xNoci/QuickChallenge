package me.noci.challenges.serializer;

import me.noci.challenges.challenge.modifiers.StopOnDeathModifier;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public final class StopOnDeathSerializer implements TypeSerializer<StopOnDeathModifier> {

    static final StopOnDeathSerializer INSTANCE = new StopOnDeathSerializer();
    static final Class<StopOnDeathModifier> TYPE = StopOnDeathModifier.class;

    private StopOnDeathSerializer() {

    }

    @Override
    public StopOnDeathModifier deserialize(Type type, ConfigurationNode node) throws SerializationException {
        return new StopOnDeathModifier();
    }

    @Override
    public void serialize(Type type, @Nullable StopOnDeathModifier obj, ConfigurationNode target)
            throws SerializationException {
        target.set(true);
    }
}
