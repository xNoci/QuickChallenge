package me.noci.challenges;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public class ResourcePack {

    public static final String URL = "https://github.com/xNoci/QuickChallenge/raw/main/ResourcePack.zip";
    public static final String HASH = "9baaa5dbd2429266c884738ac24023770f7730a7";
    public static final boolean REQUIRED = true;
    public static final Component PROMPT = Component.text()
            .append(Component.text("QuickChallenge Resource Pack", TextColor.color(195, 49, 214)))
            .append(Component.text(" - ", TextColor.color(64, 56, 64)))
            .append(Component.text(" Used for some challenges", TextColor.color(52, 158, 79)))
            .asComponent();
}
