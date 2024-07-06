package me.noci.challenges.settings;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public interface Option<T> {

    Option<Boolean> DEBUG = create("settings.debug", false);
    Option<Component> MOTD = component("settings.motd");
    Option<Boolean> ANVIL_COLORED_NAMES = create("settings.anvil.coloredNames", true);
    Option<Boolean> ANVIL_FREE_RENAME = create("settings.anvil.freeRename", false);
    Option<Component> ALL_ITEMS_BOSS_BAR_NEXT_ITEM = component("all_items.boss_bar.next_item");
    Option<Component> ALL_ITEMS_BOSS_BAR_COMPLETE = component("all_items.boss_bar.complete");
    Option<Component> CHAT_LAYOUT = component("settings.chat_layout");

    private static Option<Component> component(String path) {
        return create(path, Component.text("Failed to load '" + path + "' from config", NamedTextColor.RED));
    }

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
