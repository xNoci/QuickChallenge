package me.noci.challenges.headcomponent;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.SneakyThrows;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class HeadColorRepository {

    private static final Cache<UUID, int[]> NON_OVERLAY = CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.HOURS).build();
    private static final Cache<UUID, int[]> OVERLAY = CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.HOURS).build();

    @SneakyThrows
    public static int[] color(SkinSource source, UUID uuid, boolean overlay) {
        var cache = overlay ? OVERLAY : NON_OVERLAY;
        return cache.get(uuid, () -> source.colors(uuid, overlay));
    }


}
