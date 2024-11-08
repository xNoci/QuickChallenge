package me.noci.challenges.headcomponent;

import lombok.SneakyThrows;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.net.URL;

public class MojangColorMapper implements ColorMapper {

    protected MojangColorMapper() {
    }

    @SneakyThrows
    @Override
    public int[] colors(URL url, boolean useOverlay) {
        BufferedImage image = ImageIO.read(url);

        int[] colors = new int[64];
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                int pixel = image.getRGB(x + 8, y + 8) & 0xFFFFFF;

                if (useOverlay) {
                    int overlay = image.getRGB(x + 40, y + 8);
                    int alpha = overlay >> 24 & 0xFF;
                    if (alpha == 0xFF) pixel = overlay & 0xFFFFFF;
                }

                colors[x + y * 8] = pixel;
            }
        }

        return colors;
    }
}
