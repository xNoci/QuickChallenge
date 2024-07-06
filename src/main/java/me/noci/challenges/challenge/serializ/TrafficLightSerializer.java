package me.noci.challenges.challenge.serializ;

import me.noci.challenges.challenge.modifiers.trafficlight.LightStatus;
import me.noci.challenges.challenge.modifiers.trafficlight.TimeRange;
import me.noci.challenges.challenge.modifiers.trafficlight.TrafficLightModifier;
import me.noci.challenges.serializer.TypeSerializer;

import java.util.Optional;

import static me.noci.challenges.serializer.TypeSerializers.*;

public class TrafficLightSerializer {

    static final TypeSerializer<Optional<TrafficLightModifier>> SERIALIZER = TypeSerializer.fixed(37, buffer -> {
        boolean enabled = BOOLEAN.read(buffer);
        TimeRange greenDuration = TIME_RANGE.read(buffer);
        TimeRange yellowDuration = TIME_RANGE.read(buffer);
        TimeRange redDuration = TIME_RANGE.read(buffer);
        LightStatus lightStatus = TRAFFIC_LIGHT_STATUS.read(buffer);
        long nextAction = LONG.read(buffer);
        if (!enabled) return Optional.empty();
        return Optional.of(new TrafficLightModifier(greenDuration, yellowDuration, redDuration, lightStatus, nextAction));
    }, (buffer, value) -> {
        BOOLEAN.write(buffer, value.isPresent());
        TIME_RANGE.write(buffer, value.map(TrafficLightModifier::greenDuration).orElse(TimeRange.oneSecond()));
        TIME_RANGE.write(buffer, value.map(TrafficLightModifier::yellowDuration).orElse(TimeRange.oneSecond()));
        TIME_RANGE.write(buffer, value.map(TrafficLightModifier::redDuration).orElse(TimeRange.oneSecond()));
        TRAFFIC_LIGHT_STATUS.write(buffer, value.map(TrafficLightModifier::lightStatus).orElse(LightStatus.GREEN));
        LONG.write(buffer, value.map(TrafficLightModifier::nextAction).orElse(0L));
    });


}
