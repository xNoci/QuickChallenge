package me.noci.challenges.headcomponent;

import lombok.SneakyThrows;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.net.URI;
import java.util.UUID;

public enum SkinSource {

    CRAFATAR((uuid, overlay) -> "https://crafatar.com/avatars/%s?size=8%s".formatted(uuid, overlay ? "&overlay" : "")),
    MINOTAR((uuid, overlay) -> "https://minotar.net/%s/%s/8".formatted(overlay ? "helm" : "avatar", uuid));
    //TODO Maybe support mojang

    private final UrlMapper urlMapper;

    SkinSource(UrlMapper urlMapper) {
        this.urlMapper = urlMapper;
    }

    @SneakyThrows
    public int[] colors(UUID uuid, boolean overlay) {
        String url = urlMapper.map(uuid.toString(), overlay);

        BufferedImage image = ImageIO.read(new URI(url).toURL());

        int[] colors = new int[64];
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                colors[x + y * 8] = image.getRGB(x, y) & 0xFFFFFF;
            }
        }

        return colors;
    }

    @FunctionalInterface
    private interface UrlMapper {
        String map(String uuid, boolean overlay);
    }

}
