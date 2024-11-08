package me.noci.challenges.headcomponent;

import java.net.URL;

@FunctionalInterface
public interface ColorMapper {
    int[] colors(URL url, boolean useOverlay);
}
