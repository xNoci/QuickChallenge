package me.noci.challenges.serializer;

import me.noci.challenges.challenge.modifiers.StopOnDeathModifier;
import me.noci.challenges.challenge.modifiers.timer.TimerModifier;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public final class TimerSerializer implements TypeSerializer<TimerModifier> {

    static final TimerSerializer INSTANCE = new TimerSerializer();
    static final Class<TimerModifier> TYPE = TimerModifier.class;

    private TimerSerializer() {

    }

    private static final String TIME_PLAYED = "ticks-played";

    @Override
    public TimerModifier deserialize(Type type, ConfigurationNode node) throws SerializationException {
        long timePlayed = node.node(TIME_PLAYED).getLong(0);
        return new TimerModifier(timePlayed);
    }

    @Override
    public void serialize(Type type, @Nullable TimerModifier obj, ConfigurationNode target)
            throws SerializationException {
        if (obj == null) {
            target.raw(null);
            return;
        }
        target.node(TIME_PLAYED).set(obj.ticksPlayed());
    }
}
