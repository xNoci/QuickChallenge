package me.noci.challenges.settings;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public interface Option<T> {

    interface Settings {
        Option<Boolean> DEBUG = create("settings.debug", false);
        Option<Component> MOTD = component("settings.motd");
        Option<Component> CHAT_LAYOUT = component("settings.chat_layout");

        interface Anvil {
            Option<Boolean> COLORED_NAME = create("settings.anvil.coloredNames", true);
            Option<Boolean> FREE_RENAME = create("settings.anvil.freeRename", false);
        }
    }

    interface AllItems {
        interface BossBar {
            Option<Component> NEXT_ITEM = component("all_items.boss_bar.next_item");
            Option<Component> COMPLETE = component("all_items.boss_bar.complete");
        }
    }

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
