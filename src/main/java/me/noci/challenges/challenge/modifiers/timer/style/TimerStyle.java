package me.noci.challenges.challenge.modifiers.timer.style;

import lombok.Setter;

public class TimerStyle {

    @Setter private TimerStyleData styleData;
    private float gradientTranslation = 0;

    public TimerStyle(TimerStyleData styleData) {
        this.styleData = styleData;
    }

    public void tick() {
        gradientTranslation += styleData.gradientSpeed();
        gradientTranslation %= (float) (4 * Math.PI / styleData.gradientPeriod());
    }

    public float progressTransformer(int currentIndex, int stringLength) {
        return styleData.transformer().transform(styleData.gradientPeriod(), styleData.gradientAccentStrength(), gradientTranslation, currentIndex);
    }


}
