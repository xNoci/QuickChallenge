package me.noci.challenges.challenge.modifiers.trafficlight;

import me.noci.challenges.RandomHolder;
import me.noci.quickutilities.utils.time.BukkitUnit;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public record TimeRange(@Setting("unit") BukkitUnit unit, @Setting("min-delay") int minDelay,
                        @Setting("max-delay") int maxDelay) {

    public static TimeRange of(BukkitUnit unit, int min, int max) {
        return new TimeRange(unit, min, max);
    }

    public static TimeRange oneSecond() {
        return new TimeRange(BukkitUnit.SECONDS, 1, 1);
    }

    public long randomAsTick() {
        long minTicks = unit.toTicks(minDelay);
        long maxTicks = unit.toTicks(maxDelay);

        return RandomHolder.random().nextLong(minTicks, maxTicks + 1);
    }

    @Override
    public String toString() {
        return "TimeRange{" +
                "unit=" + unit +
                ", min=" + minDelay +
                ", max=" + maxDelay +
                '}';
    }
}
