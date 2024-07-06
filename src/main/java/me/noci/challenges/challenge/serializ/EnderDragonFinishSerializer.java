package me.noci.challenges.challenge.serializ;

import me.noci.challenges.challenge.modifiers.EnderDragonFinishModifier;
import me.noci.challenges.serializer.TypeSerializer;

import java.util.Optional;

import static me.noci.challenges.serializer.TypeSerializers.BOOLEAN;

public class EnderDragonFinishSerializer {

    static final TypeSerializer<Optional<EnderDragonFinishModifier>> SERIALIZER = TypeSerializer.fixed(1, buffer -> {
        boolean enabled = BOOLEAN.read(buffer);
        if (!enabled) return Optional.empty();
        return Optional.of(new EnderDragonFinishModifier());
    }, (buffer, value) -> BOOLEAN.write(buffer, value.isPresent()));

}
