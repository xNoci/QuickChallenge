package me.noci.challenges.challenge.serializ;

import me.noci.challenges.challenge.modifiers.timer.TimerModifier;
import me.noci.challenges.serializer.TypeSerializer;

import java.util.Optional;

import static me.noci.challenges.serializer.TypeSerializers.BOOLEAN;
import static me.noci.challenges.serializer.TypeSerializers.LONG;

public class TimerSerializer {

    static final TypeSerializer<Optional<TimerModifier>> SERIALIZER = TypeSerializer.fixed(9, buffer -> {
        boolean enabled = BOOLEAN.read(buffer);
        long ticksPlayed = LONG.read(buffer);
        if (!enabled) return Optional.empty();
        return Optional.of(new TimerModifier(ticksPlayed));
    }, (buffer, value) -> {
        BOOLEAN.write(buffer, value.isPresent());
        LONG.write(buffer, value.map(TimerModifier::ticksPlayed).orElse(0L));
    });

}
