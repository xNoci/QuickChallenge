package me.noci.challenges.challenge.serializ;

import com.google.common.collect.Lists;
import me.noci.challenges.ExitStrategy;
import me.noci.challenges.challenge.Challenge;
import me.noci.challenges.challenge.modifiers.ChallengeModifier;
import me.noci.challenges.challenge.modifiers.StopOnDeathModifier;
import me.noci.challenges.challenge.modifiers.TimerModifier;
import me.noci.challenges.challenge.modifiers.TrafficLightModifier;
import me.noci.challenges.serializer.ObjectSerializer;
import me.noci.challenges.serializer.TypeSerializers;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ChallengeSerializer {

    private static final short MAGIC_NUMBER = (short) 0xFE21;

    public static final ObjectSerializer<Challenge> VERSION_1 = ObjectSerializer.of(Challenge.class)
            .layout(TypeSerializers.SHORT, c -> MAGIC_NUMBER)
            .layout(TypeSerializers.U_BYTE, c -> 1)
            .layout(TypeSerializers.UUID, Challenge::handle)
            .layout(TypeSerializers.EXIT_STRATEGY, Challenge::exitStrategy)
            .layout(StopOnDeathModifier.SERIALIZER, challenge -> challenge.getModifier(StopOnDeathModifier.class))
            .layout(TimerModifier.SERIALIZER, challenge -> challenge.getModifier(TimerModifier.class))
            .layout(TrafficLightModifier.SERIALIZER, challenge -> challenge.getModifier(TrafficLightModifier.class))
            .read(buffer -> {
                if (TypeSerializers.SHORT.read(buffer) != MAGIC_NUMBER) return null;
                if (TypeSerializers.U_BYTE.read(buffer) != 1) return null;

                UUID uuid = TypeSerializers.UUID.read(buffer);
                ExitStrategy exitStrategy = TypeSerializers.EXIT_STRATEGY.read(buffer);
                Optional<StopOnDeathModifier> stopOnDeathModifier = StopOnDeathModifier.SERIALIZER.read(buffer);
                Optional<TimerModifier> timerModifier = TimerModifier.SERIALIZER.read(buffer);
                Optional<TrafficLightModifier> trafficLightModifier = TrafficLightModifier.SERIALIZER.read(buffer);

                List<ChallengeModifier> challengeModifiers = Lists.newArrayList();
                stopOnDeathModifier.ifPresent(challengeModifiers::add);
                timerModifier.ifPresent(challengeModifiers::add);
                trafficLightModifier.ifPresent(challengeModifiers::add);

                return new Challenge(uuid, exitStrategy, challengeModifiers);
            })
            .get();

}
