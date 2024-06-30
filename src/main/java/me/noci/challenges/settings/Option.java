package me.noci.challenges.settings;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;

public interface Option<T> {

    Option<Boolean> DEBUG = create("settings.debug", false);
    Option<Component> MOTD = create("settings.motd", Component.text("                  Challenges", Style.style(TextDecoration.BOLD)));
    Option<Boolean> ANVIL_COLORED_NAMES = create("settings.anvil.coloredNames", true);
    Option<Boolean> ANVIL_FREE_RENAME = create("settings.anvil.freeRename", false);
    Option<Component> ALL_ITEMS_BOSS_BAR_NEXT_ITEM = create("all_items.boss_bar.next_item", Component.text("Failed to load 'all_items.boss_bar.next_item' from config", NamedTextColor.RED));
    Option<Component> ALL_ITEMS_BOSS_BAR_COMPLETE = create("all_items.boss_bar.complete", Component.text("Failed to load 'all_items.boss_bar.complete' from config", NamedTextColor.RED));

    private static <T> Option<T> create(String path, T def) {
        return new Option<>() {
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
