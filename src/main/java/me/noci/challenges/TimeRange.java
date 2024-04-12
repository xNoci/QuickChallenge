package me.noci.challenges;

import lombok.Getter;
import me.noci.quickutilities.utils.BukkitUnit;

@Getter
public class TimeRange {

    public static TimeRange of(BukkitUnit unit, int min, int max) {
        return new TimeRange(unit, min, max);
    }

    public static TimeRange oneSecond() {
        return new TimeRange(BukkitUnit.SECONDS, 1, 1);
    }

    private final BukkitUnit unit;
    private final int min;
    private final int max;

    private TimeRange(BukkitUnit unit, int min, int max) {
        this.unit = unit;
        this.min = min;
        this.max = max;
    }

    public long randomAsTick() {
        long minTicks = unit.toTicks(min);
        long maxTicks = unit.toTicks(max);

        return RandomHolder.random().nextLong(minTicks, maxTicks + 1);
    }

    @Override
    public String toString() {
        return "TimeRange{" +
                "unit=" + unit +
                ", min=" + min +
                ", max=" + max +
                '}';
    }
}
