package me.noci.challenges.headcomponent;

import lombok.SneakyThrows;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.net.URL;

public class DefaultColorMapper implements ColorMapper {

    protected static ColorMapper INSTANCE = new DefaultColorMapper();

    private DefaultColorMapper() {
    }

    @SneakyThrows
    @Override
    public int[] colors(URL url, boolean useOverlay) {
        BufferedImage image = ImageIO.read(url);

        int[] colors = new int[64];
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                colors[x + y * 8] = image.getRGB(x, y) & 0xFFFFFF;
            }
        }

        return colors;
    }
}
