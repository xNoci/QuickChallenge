package me.noci.challenges;

import com.google.common.io.BaseEncoding;
import me.noci.challenges.challenge.modifiers.allitem.AllItem;
import me.noci.challenges.settings.Option;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.KeyPattern;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Range;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.UUID;

public final class ResourcePack {

    public static final TexturePack DEFAULT = TexturePack.of("F556A1E28370B937E7E1C6955336670E1BBAA7C6", "https://github.com/xNoci/QuickChallenge/raw/main/ResourcePacks/DefaultResourcePack.zip", true);
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

    public static class PlayerHead {
        private static final String FONT_NAME = "player_head";

        public static Component negative(@Range(from = 1, to = 2) int value) {
            return value((char) (0xE000 + value), FONT_NAME);
        }

        public static Component pixel(int height, int color) {
            int value = 0xEff0 + height;
            return value((char) value, FONT_NAME).color(TextColor.color(color));
        }
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
            byte[] hashArray = BaseEncoding.base16().lowerCase().decode(hash.toLowerCase(Locale.ROOT));
            return new TexturePack(hashArray, url, UUID.nameUUIDFromBytes(url.getBytes(StandardCharsets.UTF_8)), required);
        }

        public void apply(Player player) {
            player.addResourcePack(uuid, url, hash, GsonComponentSerializer.gson().serialize(Option.ResourcePack.PROMPT.get()), !player.isOp() && required);
        }

        public boolean isSame(UUID uuid) {
            return this.uuid.equals(uuid);
        }

    }

}
