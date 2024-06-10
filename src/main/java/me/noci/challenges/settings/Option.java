package me.noci.challenges.settings;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;

public interface Option<T> {

    Option<Boolean> DEBUG = create("settings.debug", false);
    Option<Component> MOTD = create("settings.motd", Component.text("                  Challenges").decorate(TextDecoration.BOLD));
    Option<Boolean> ANVIL_COLORED_NAMES = create("settings.anvil.coloredNames", true);
    Option<Boolean> ANVIL_FREE_RENAME = create("settings.anvil.freeRename", false);

    private static <T> Option<T> create(String path, T def) {
        return new Option<T>() {
            @Override
            public String path() {
                return path;
            }

            @Override
            public T defaultValue() {
                return def;
            }
        };
    }

    String path();

    T defaultValue();

}
