package me.noci.challenges;

import me.noci.challenges.challenge.modifiers.allitem.AllItem;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.KeyPattern;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.entity.Player;

import java.util.UUID;

public final class ResourcePack {

    private static final Component PROMPT = Component.text()
            .append(Component.text("QuickChallenge Resource Pack", TextColor.color(195, 49, 214)))
            .append(Component.text(" - ", TextColor.color(64, 56, 64)))
            .append(Component.text(" Used for some challenges", TextColor.color(52, 158, 79)))
            .asComponent();

    public static final TexturePack DEFAULT = TexturePack.of("E0AFB08EFC0A033E24310CB7F0BE901009778CCA", "https://github.com/xNoci/QuickChallenge/raw/main/ResourcePacks/DefaultResourcePack.zip", true);
    public static final TexturePack HIGH_RES_OVERLAY = TexturePack.of("EF31A3D1693C29742ADF57E8FFB80B9DD9152632", "https://github.com/xNoci/QuickChallenge/raw/main/ResourcePacks/AllItems_Overlay(High-Resolution).zip", true);

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

        public static Component itemIcon(AllItem item) {
            int value = 0xE001;
            value += item.ordinal();
            return value((char) value, FONT_NAME);
        }

    }

    public record TexturePack(byte[] hash, String url, UUID uuid, boolean required) {

        public static TexturePack of(String hash, String url, boolean required) {
            return new TexturePack(hash.getBytes(), url, UUID.nameUUIDFromBytes(url.getBytes()), required);
        }

        public void apply(Player player) {
            player.addResourcePack(uuid, url, hash, GsonComponentSerializer.gson().serialize(ResourcePack.PROMPT), !player.isOp() && required);
        }

    }

}
