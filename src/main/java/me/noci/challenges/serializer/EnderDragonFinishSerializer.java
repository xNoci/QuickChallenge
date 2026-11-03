package me.noci.challenges.serializer;

import me.noci.challenges.challenge.modifiers.EnderDragonFinishModifier;
import me.noci.challenges.settings.Option;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public final class EnderDragonFinishSerializer implements TypeSerializer<EnderDragonFinishModifier> {

    static final EnderDragonFinishSerializer INSTANCE = new EnderDragonFinishSerializer();
    static final Class<EnderDragonFinishModifier> TYPE = EnderDragonFinishModifier.class;

    private EnderDragonFinishSerializer() {

    }

    @Override
    public EnderDragonFinishModifier deserialize(Type type, ConfigurationNode node) throws SerializationException {
        return new EnderDragonFinishModifier();
    }

    @Override
    public void serialize(Type type, @Nullable EnderDragonFinishModifier obj, ConfigurationNode target)
            throws SerializationException {
        target.set(true);

    }
}
