package me.noci.challenges.headcomponent;

import java.net.URL;
import java.util.UUID;

public enum SkinSource {

    MOJANG(new MojangUrlMapper(), new MojangColorMapper()),
    CRAFATAR((uuid, overlay) -> "https://crafatar.com/avatars/%s?size=8%s".formatted(uuid, overlay ? "&overlay" : ""), DefaultColorMapper.INSTANCE),
    MINOTAR((uuid, overlay) -> "https://minotar.net/%s/%s/8".formatted(overlay ? "helm" : "avatar", uuid), DefaultColorMapper.INSTANCE);

    private final UrlMapper urlMapper;
    private final ColorMapper colorMapper;

    SkinSource(UrlMapper urlMapper, ColorMapper colorMapper) {
        this.urlMapper = urlMapper;
        this.colorMapper = colorMapper;
    }

    public int[] colors(UUID uuid, boolean useOverlay) {
        URL url = urlMapper.url(uuid, useOverlay);
        return colorMapper.colors(url, useOverlay);
    }
}
