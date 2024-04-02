package me.noci.challenges;

import me.noci.quickutilities.utils.BukkitUnit;

public class TimeRange {

    public static TimeRange seconds(int min, int max) {
        return new TimeRange(BukkitUnit.SECONDS, min, max);
    }

    public static TimeRange of(BukkitUnit unit, int min, int max) {
        return new TimeRange(unit, min, max);
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
        int value = RandomHolder.random().nextInt(min, max + 1);
        return unit.toTicks(value);
    }

}
