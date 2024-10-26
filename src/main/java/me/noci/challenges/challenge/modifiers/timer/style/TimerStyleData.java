package me.noci.challenges.challenge.modifiers.timer.style;

public record TimerStyleData(float gradientSpeed, float gradientPeriod, float gradientAccentStrength,
                             Transformer transformer) {

    @FunctionalInterface
    protected interface Transformer {
        float transform(float gradientPeriod, float gradientAccentStrength, float gradientTranslation, int currentIndex);
    }

}
