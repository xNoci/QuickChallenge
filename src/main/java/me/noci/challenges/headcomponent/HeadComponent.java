package me.noci.challenges.headcomponent;

import me.noci.challenges.ResourcePack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;

import java.util.UUID;

public class HeadComponent {

    public static HeadComponent create(UUID uuid) {
        return new HeadComponent(uuid);
    }

    private final UUID uuid;
    private boolean overlay = true;
    private SkinSource source = SkinSource.MINOTAR;

    public HeadComponent(UUID uuid) {
        this.uuid = uuid;
    }

    public HeadComponent source(SkinSource source) {
        this.source = source;
        return this;
    }

    public HeadComponent overlay(boolean overlay) {
        this.overlay = overlay;
        return this;
    }

    public TextComponent build() {
        TextComponent head = Component.empty();

        int[] colors = HeadColorRepository.color(source, uuid, overlay);

        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                int color = colors[x + y * 8];
                head = head.append(ResourcePack.PlayerHead.pixel(y + 1, color));
                head = head.append(ResourcePack.PlayerHead.negative(y == 7 ? 1 : 2));
            }
        }

        return head;
    }

}
