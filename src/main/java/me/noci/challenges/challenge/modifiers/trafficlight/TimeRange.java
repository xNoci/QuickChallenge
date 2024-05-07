package me.noci.challenges.challenge.modifiers.trafficlight;

import me.noci.challenges.RandomHolder;
import me.noci.quickutilities.utils.BukkitUnit;

public record TimeRange(BukkitUnit unit, int minDelay, int maxDelay) {

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
