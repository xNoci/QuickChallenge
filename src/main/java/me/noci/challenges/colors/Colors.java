package me.noci.challenges.colors;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.util.RGBLike;

public class Colors {

    // ------- GUI -------
    public static final TextColor GUI_TITLE = TextColor.color(179, 164, 34);


    // ------- CHAT -------
    public static final TextColor CHAT_COLOR = NamedTextColor.GRAY;
    public static final TextColor JOIN_INDICATOR_JOIN = TextColor.color(82, 158, 66);
    public static final TextColor JOIN_INDICATOR_QUIT = TextColor.color(158, 82, 66);
    public static final TextColor JOIN_PLAYER_NAME = TextColor.color(31, 150, 173);

    // ------- Modifiers -------
    public static final TextColor TIMER_PRIMARY_COLOR = TextColor.color(197, 14, 207);
    public static final TextColor TIMER_ACCENT_COLOR = TextColor.color(61, 38, 163);
}
