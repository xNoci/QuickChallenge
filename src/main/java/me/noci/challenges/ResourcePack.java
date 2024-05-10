package me.noci.challenges;

import me.noci.challenges.challenge.modifiers.allitem.AllItem;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.KeyPattern;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public final class ResourcePack {

    public static final String URL = "https://github.com/xNoci/QuickChallenge/raw/main/ResourcePack.zip";
    public static final String HASH = "E0AFB08EFC0A033E24310CB7F0BE901009778CCA";
    public static final boolean REQUIRED = true;
    public static final Component PROMPT = Component.text()
            .append(Component.text("QuickChallenge Resource Pack", TextColor.color(195, 49, 214)))
            .append(Component.text(" - ", TextColor.color(64, 56, 64)))
            .append(Component.text(" Used for some challenges", TextColor.color(52, 158, 79)))
            .asComponent();

    private static Component value(char fontChar, @KeyPattern String font) {
        return Component.text(fontChar).font(Key.key(font));
    }

    public static class TrafficLight {
        private static final String FONT_NAME = "traffic_light";

        public static final Component GREEN_LIGHT = value('\uEff1', FONT_NAME);
        public static final Component YELLOW_LIGHT = value('\uEff2', FONT_NAME);
        public static final Component RED_LIGHT = value('\uEff3', FONT_NAME);
    }

    public static class AllItems {
        private static final String FONT_NAME = "all_items";

        public static final Component SPACE_16 = value('\uF001', FONT_NAME);

        public static Component itemIcon(AllItem item) {
            int value = 0xE001;
            value += item.ordinal();
            return value((char) value, FONT_NAME);
        }

    }

}
