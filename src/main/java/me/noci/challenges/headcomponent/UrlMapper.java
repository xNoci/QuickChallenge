package me.noci.challenges.headcomponent;

import lombok.SneakyThrows;

import java.net.URI;
import java.net.URL;
import java.util.UUID;

@FunctionalInterface
public interface UrlMapper {
    String map(String uuid, boolean useOverlay);

    @SneakyThrows
    default URL url(UUID uuid, boolean useOverlay) {
        return new URI(map(uuid.toString(), useOverlay)).toURL();
    }

}
