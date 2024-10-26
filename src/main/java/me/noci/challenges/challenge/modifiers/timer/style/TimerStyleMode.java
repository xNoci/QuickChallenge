package me.noci.challenges.challenge.modifiers.timer.style;

import lombok.Getter;

public enum TimerStyleMode {

    BLINK(new TimerStyleData(0.008f, 5.5f, 10, (gradientPeriod, gradientAccentStrength, gradientTranslation, currentIndex) -> {
        float progress = (gradientPeriod * 10);
        progress += gradientTranslation;
        progress = 0.5f + (float) Math.sin(gradientPeriod * progress) / 2;
        return (float) Math.pow(progress, gradientAccentStrength);
    })),
    WAVE(new TimerStyleData(0.015f, 4.5f, 10, (gradientPeriod, gradientAccentStrength, gradientTranslation, currentIndex) -> {
        float progress = (float) currentIndex / (gradientPeriod * 10);
        progress += gradientTranslation;
        progress = 0.5f + (float) Math.sin(gradientPeriod * progress) / 2;
        return (float) Math.pow(progress, gradientAccentStrength);
    }));

    @Getter private final TimerStyleData styleData;

    TimerStyleMode(TimerStyleData styleData) {
        this.styleData = styleData;
    }

}
