package me.noci.challenges.colors;

import com.google.common.collect.Lists;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

import java.util.Iterator;
import java.util.List;

public class ColorUtils {

    public static Component gradientText(String text, TextColor colorA, TextColor colorB) {
        return gradientText(text, colorA, colorB, (currentIndex, stringLength) -> (float) currentIndex / stringLength);
    }

    public static Component gradientText(String text, TextColor colorA, TextColor colorB, ProgressTransformer translation) {
        Iterator<TextColor> colors = gradient(colorA, colorB, text.length(), translation);
        var component = Component.text();
        text.chars().forEachOrdered(value -> component.append(Component.text((char) value, colors.next())));
        return component.asComponent();
    }

    private static Iterator<TextColor> gradient(TextColor colorA, TextColor colorB, int length, ProgressTransformer transformer) {
        List<TextColor> colors = Lists.newArrayListWithCapacity(length);


        for (int i = 0; i <= length; i++) {
            float progress = transformer.transform(i, length);

            int r = interpolate(colorA.red(), colorB.red(), progress);
            int g = interpolate(colorA.green(), colorB.green(), progress);
            int b = interpolate(colorA.blue(), colorB.blue(), progress);

            colors.add(TextColor.color(r, g, b));
        }

        return colors.iterator();
    }

    private static int interpolate(int startValue, int endValue, float progress) {
        return Math.round(startValue + (endValue - startValue) * progress);
    }

    @FunctionalInterface
    public interface ProgressTransformer {
        float transform(int currentIndex, int stringLength);
    }

}
