package me.noci.challenges.headcomponent;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;

import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

public class MojangUrlMapper implements UrlMapper {

    private static final String MOJANG_PROFILE = "https://sessionserver.mojang.com/session/minecraft/profile/";

    private static final String ALEX_URL = "https://minecraft.wiki/images/Alex_%28slim_texture%29_JE3.png";
    private static final String ARI_URL = "https://minecraft.wiki/images/Ari_%28slim_texture%29_JE1.png";
    private static final String EFE_URL = "https://minecraft.wiki/images/Efe_%28slim_texture%29_JE1.png";
    private static final String KAI_URL = "https://minecraft.wiki/images/Kai_%28slim_texture%29_JE1.png";
    private static final String MAKENA_URL = "https://minecraft.wiki/images/Makena_%28slim_texture%29_JE1.png";
    private static final String NOOR_URL = "https://minecraft.wiki/images/Noor_%28slim_texture%29_JE1.png";
    private static final String STEVE_URL = "https://minecraft.wiki/images/Steve_%28slim_texture%29_JE2.png";
    private static final String SUNNY_URL = "https://minecraft.wiki/images/Sunny_%28slim_texture%29_JE1.png";
    private static final String ZURI_URL = "https://minecraft.wiki/images/Zuri_%28slim_texture%29_JE1.png";

    private static final String[] DEFAULT_SKINS = new String[]{
            ALEX_URL, ARI_URL, EFE_URL, KAI_URL, MAKENA_URL, NOOR_URL, STEVE_URL, SUNNY_URL, ZURI_URL,
            ALEX_URL, ARI_URL, EFE_URL, KAI_URL, MAKENA_URL, NOOR_URL, STEVE_URL, SUNNY_URL, ZURI_URL,
    };

    private static final Gson GSON = new Gson();

    protected MojangUrlMapper() {
    }

    private static String getDefaultSkin(String uuid) {
        return DEFAULT_SKINS[Math.floorMod(UUID.fromString(uuid).hashCode(), DEFAULT_SKINS.length)];
    }

    @SneakyThrows
    @Override
    public String map(String uuid, boolean useOverlay) {
        URL url = new URI(MOJANG_PROFILE + uuid).toURL();
        String rawJSON = IOUtils.toString(url, StandardCharsets.UTF_8);
        JsonObject json = GSON.fromJson(rawJSON, JsonObject.class);
        JsonArray properties = json.getAsJsonArray("properties");

        String skinURL = null;
        for (JsonElement property : properties) {
            JsonObject prop = property.getAsJsonObject();
            if (prop.has("name")) {
                String value = prop.get("value").getAsString();
                value = new String(Base64.getDecoder().decode(value), StandardCharsets.UTF_8);
                JsonObject valueObject = GSON.fromJson(value, JsonObject.class);
                JsonObject texture = valueObject.getAsJsonObject("textures");
                if (!texture.has("SKIN")) continue;
                skinURL = texture.getAsJsonObject("SKIN").get("url").getAsString();
                break;
            }
        }

        if (skinURL == null) {
            skinURL = getDefaultSkin(uuid);
        }

        return skinURL;
    }
}
