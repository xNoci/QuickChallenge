package me.noci.challenges.serializer;

import me.noci.challenges.challenge.modifiers.timer.TimerModifier;
import me.noci.challenges.challenge.modifiers.trafficlight.LightStatus;
import me.noci.challenges.challenge.modifiers.trafficlight.TimeRange;
import me.noci.challenges.challenge.modifiers.trafficlight.TrafficLightModifier;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public final class TrafficLightSerializer implements TypeSerializer<TrafficLightModifier> {

    static final TrafficLightSerializer INSTANCE = new TrafficLightSerializer();
    static final Class<TrafficLightModifier> TYPE = TrafficLightModifier.class;

    private TrafficLightSerializer() {

    }

    private static final String GREEN_DURATION = "green-duration";
    private static final String YELLOW_DURATION = "yellow-duration";
    private static final String RED_DURATION = "red-duration";
    private static final String LIGHT_STATUS = "light-status";

    @Override
    public TrafficLightModifier deserialize(Type type, ConfigurationNode node) throws SerializationException {
        TimeRange greenDuration = node.node(GREEN_DURATION).get(TimeRange.class);
        TimeRange yellowDuration = node.node(YELLOW_DURATION).get(TimeRange.class);
        TimeRange redDuration = node.node(RED_DURATION).get(TimeRange.class);
        LightStatus lightStatus = node.node(LIGHT_STATUS).get(LightStatus.class);
        return new TrafficLightModifier(greenDuration, yellowDuration, redDuration, lightStatus);
    }

    @Override
    public void serialize(Type type, @Nullable TrafficLightModifier obj, ConfigurationNode target)
            throws SerializationException {
        if (obj == null) {
            target.raw(null);
            return;
        }

        target.node(GREEN_DURATION).set(TimeRange.class, obj.greenDuration());
        target.node(YELLOW_DURATION).set(TimeRange.class, obj.yellowDuration());
        target.node(RED_DURATION).set(TimeRange.class, obj.redDuration());
        target.node(LIGHT_STATUS).set(obj.lightStatus());
    }
}
