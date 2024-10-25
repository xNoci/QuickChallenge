package me.noci.challenges.settings;

import me.noci.challenges.QuickChallenge;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

public interface Option<T> {

    interface Settings {
        Option<Boolean> DEBUG = create("settings.debug", false);
        Option<Component> MOTD = component("settings.motd");
        Option<Component> CHAT_LAYOUT = component("settings.chat_layout");
        Option<Component> PLAYER_JOIN = component("settings.player_join");
        Option<Component> PLAYER_QUIT = component("settings.player_quit");
        Option<Component> SERVER_CLOSED = component("settings.server_closed");

        interface Anvil {
            Option<Boolean> COLORED_NAME = create("settings.anvil.coloredNames", true);
            Option<Boolean> FREE_RENAME = create("settings.anvil.freeRename", false);
        }

        interface ActionBar {
            Option<Component> NO_CHALLENGE_CREATED = component("settings.action_bar.no_challenge_created");
            Option<Component> CHALLENGE_NOT_STARTED = component("settings.action_bar.challenge_not_started");
        }

        interface TimerGradient {
            Option<TextColor> PRIMARY = create("settings.timer_gradient.primary", NamedTextColor.WHITE);
            Option<TextColor> ACCENT = create("settings.timer_gradient.accent", NamedTextColor.BLACK);
        }

    }

    interface AllItems {
        interface BossBar {
            Option<Component> NEXT_ITEM = component("all_items.boss_bar.next_item");
            Option<Component> COMPLETE = component("all_items.boss_bar.complete");
        }

        interface Chat {
            Option<Component> NEXT_ITEM = component("all_items.chat.next_item");
            Option<Component> ITEM_COLLECTED = component("all_items.chat.item_collected");
            Option<Component> ITEM_SKIPPED = component("all_items.chat.item_skipped");
            Option<Component> ITEM_SKIPPED_CONSOLE = component("all_items.chat.item_skipped_console");
        }
    }

    interface ResourcePack {
        Option<Component> WARNING = component("resource_pack.warning");
        Option<Component> PROMPT = component("resource_pack.prompt");
    }

    interface EnderDragonFinish {
        Option<Component> WITH_TIME = component("ender_dragon_finish.with_timer");
        Option<Component> WITHOUT_TIME = component("ender_dragon_finish.without_timer");
    }

    interface Command {
        Option<Component> DEBUG_COMMAND = component("command.debug_command");
        Option<Component> ONLY_FOR_PLAYERS = component("command.only_for_players");
        Option<Component> NO_PERMISSION = component("command.no_permission");

        interface AllItems {
            Option<Component> NOT_ENABLED = component("command.all_items.not_enabled");
            Option<Component> HELP = component("command.all_items.help");
        }

        interface Timer {
            Option<Component> ALREADY_STARTED = component("command.timer.already_started");
            Option<Component> NOT_RUNNING = component("command.timer.not_running");
            Option<Component> NOT_PAUSED = component("command.timer.not_paused");
            Option<Component> SUCCESSFULLY_STARTED = component("command.timer.successfully_started");
            Option<Component> SUCCESSFULLY_STOPPED = component("command.timer.successfully_stopped");
            Option<Component> SUCCESSFULLY_PAUSED = component("command.timer.successfully_paused");
            Option<Component> SUCCESSFULLY_RESUMED = component("command.timer.successfully_resumed");
            Option<Component> HELP = component("command.timer.help");
        }

        interface Challenge {
            Option<Component> NOT_CREATED = component("command.challenge.not_created");
            Option<Component> SUCCESSFULLY_DELETED = component("command.challenge.successfully_deleted");
            Option<Component> FAILED_DELETION = component("command.challenge.failed_deletion");
        }

    }

    interface Gui {
        Option<Component> NEXT_PAGE_ITEM = component("gui.next_page_item");
        Option<Component> PREVIOUS_PAGE_ITEM = component("gui.previous_page_item");
        Option<Component> BACK_ITEM = component("gui.back_item");
        Option<Component> MODIFIER_OVERVIEW_TITLE = component("gui.modifier_overview_title");

        interface Dialog {
            Option<Component> YES_ITEM = component("gui.dialog.yes_item");
            Option<Component> NO_ITEM = component("gui.dialog.no_item");
            Option<Component> ACCEPT_ITEM = component("gui.dialog.accept_item");
            Option<Component> DECLINE_ITEM = component("gui.dialog.decline_item");
            Option<Component> DESCRIPTION_ITEM = component("gui.dialog.description_item");
        }

        interface ChallengeCreate {
            Option<Component> TITLE = component("gui.challenge_create.title");
            Option<Component> ADD_MODIFIER = component("gui.challenge_create.add_modifier");
            Option<Component> CREATE_MODIFIER = component("gui.challenge_create.create_modifier");
            Option<Component> CANCEL = component("gui.challenge_create.cancel");
            Option<Component> MODIFIER_REMOVE_HINT = component("gui.challenge_create.modifier_remove_hint");

            interface RemoveDialog {
                Option<Component> TITLE = component("gui.challenge_create.remove_dialog.title");
                Option<Component> DESCRIPTION = component("gui.challenge_create.remove_dialog.description");
                Option<Component> MODIFIER_NAME = component("gui.challenge_create.remove_dialog.modifier_name");
            }
        }

        interface AllItems {
            Option<Component> TITLE = component("gui.all_items.title");
            Option<Component> ITEM_NAME = component("gui.all_items.item_name");
            Option<Component> COLLECTED_AT = component("gui.all_items.collected_at");
            Option<Component> COLLECTED_BY = component("gui.all_items.collected_by");
            Option<Component> SKIPPED_BY = component("gui.all_items.skipped_by");
            Option<Component> TIMESTAMP_SUFFIX = component("gui.all_items.timestamp_suffix");
            Option<Component> STATS_DISPLAYNAME= component("gui.all_items.stats_displayname");
            Option<Component> STATS_ENTRY = component("gui.all_items.stats_entry");
            Option<Component> STATS_ENTRY_SKIPPED = component("gui.all_items.stats_entry_skipped");
        }

        interface TrafficLightModifier {
            Option<Component> TITLE = component("gui.traffic_light_modifier.title");
            Option<Component> CANCEL = component("gui.traffic_light_modifier.cancel");
            Option<Component> ADD_MODIFIER = component("gui.traffic_light_modifier.add_modifier");
            Option<Component> GREEN_PHASE = component("gui.traffic_light_modifier.green_phase");
            Option<Component> YELLOW_PHASE = component("gui.traffic_light_modifier.yellow_phase");
            Option<Component> RED_PHASE = component("gui.traffic_light_modifier.red_phase");
            Option<Component> MIN_PHASE_VALUE = component("gui.traffic_light_modifier.min_phase_value");
            Option<Component> MAX_PHASE_VALUE = component("gui.traffic_light_modifier.max_phase_value");
            Option<Component> PHASE_TIME_UNIT = component("gui.traffic_light_modifier.phase_time_unit");
            Option<Component> CURRENT_VALUE = component("gui.traffic_light_modifier.current_value");
            Option<Component> LOW_DECREASE = component("gui.traffic_light_modifier.low_decrease");
            Option<Component> HIGH_DECREASE = component("gui.traffic_light_modifier.high_decrease");
            Option<Component> LOW_INCREASE = component("gui.traffic_light_modifier.low_increase");
            Option<Component> HIGH_INCREASE = component("gui.traffic_light_modifier.high_increase");
            Option<Component> PREVIOUS_UNIT = component("gui.traffic_light_modifier.previous_unit");
            Option<Component> NEXT_UNIT = component("gui.traffic_light_modifier.next_unit");
        }

        interface ModifierAcceptDialog {
            Option<Component> TITLE = component("gui.modifier_accept_dialog.title");
            Option<Component> DESCRIPTION = component("gui.modifier_accept_dialog.description");
            Option<Component> MODIFIER_NAME = component("gui.modifier_accept_dialog.modifier_name");
        }

    }

    interface Modifiers {
        Option<Component> STOP_ON_DEATH = component("modifiers.stop_on_death");
        Option<Component> ENDER_DRAGON_FINISH = component("modifiers.ender_dragon_finish");
        Option<Component> TIMER = component("modifiers.timer");
        Option<Component> ALL_ITEM = component("modifiers.all_item");
        Option<Component> TRAFFIC_LIGHT = component("modifiers.traffic_light");
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

    default T get() {
        return QuickChallenge.instance().config().get(this);
    }

}
