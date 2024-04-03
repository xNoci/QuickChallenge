package me.noci.challenges;

import lombok.Getter;
import me.noci.quickutilities.utils.BukkitUnit;

public class TimeRange {

    public static TimeRange seconds(int min, int max) {
        return new TimeRange(BukkitUnit.SECONDS, min, max);
    }

    public static TimeRange of(BukkitUnit unit, int min, int max) {
        return new TimeRange(unit, min, max);
    }

    public static TimeRange oneSecond() {
        return new TimeRange(BukkitUnit.SECONDS, 1, 1);
    }

    @Getter private final BukkitUnit unit;
    @Getter private final int min;
    @Getter private final int max;

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
