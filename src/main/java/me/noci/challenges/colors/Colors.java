package me.noci.challenges.colors;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

public class Colors {

    public static final TextColor PRIMARY = TextColor.color(99, 128, 101);
    public static final TextColor GRAY = NamedTextColor.GRAY;

    // ------- GUI -------
    public static final TextColor GUI_TITLE = TextColor.color(132, 120, 157);


    // ------- CHAT -------
    public static final TextColor CHAT_COLOR = NamedTextColor.GRAY;
    public static final TextColor JOIN_INDICATOR_JOIN = TextColor.color(82, 158, 66);
    public static final TextColor JOIN_INDICATOR_QUIT = TextColor.color(158, 82, 66);
    public static final TextColor PLAYER_NAME = TextColor.color(31, 150, 173);

    // ------- Modifiers -------
    public static final TextColor TIMER_PRIMARY_COLOR = TextColor.color(220, 74, 188);
    public static final TextColor TIMER_ACCENT_COLOR = TextColor.color(97, 107, 205);
}
