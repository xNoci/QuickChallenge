package me.noci.challenges.challenge.serializ;

import me.noci.challenges.challenge.modifiers.StopOnDeathModifier;
import me.noci.challenges.serializer.TypeSerializer;

import java.util.Optional;

import static me.noci.challenges.serializer.TypeSerializers.BOOLEAN;

public class StopOnDeathSerializer {

    static final TypeSerializer<Optional<StopOnDeathModifier>> SERIALIZER = TypeSerializer.fixed(1, buffer -> {
        boolean enabled = BOOLEAN.read(buffer);
        if (!enabled) return Optional.empty();
        return Optional.of(new StopOnDeathModifier());
    }, (buffer, value) -> BOOLEAN.write(buffer, value.isPresent()));

}
