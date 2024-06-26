package me.noci.challenges.gui.modifier;

import me.noci.challenges.challenge.modifiers.trafficlight.LightStatus;
import me.noci.challenges.challenge.modifiers.trafficlight.TimeRange;
import me.noci.challenges.challenge.modifiers.trafficlight.TrafficLightModifier;
import me.noci.challenges.colors.Colors;
import me.noci.challenges.gui.InventoryConstants;
import me.noci.quickutilities.inventory.GuiItem;
import me.noci.quickutilities.inventory.GuiProvider;
import me.noci.quickutilities.inventory.InventoryContent;
import me.noci.quickutilities.inventory.Slot;
import me.noci.quickutilities.utils.BukkitUnit;
import me.noci.quickutilities.utils.EnumUtils;
import me.noci.quickutilities.utils.QuickItemStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class GuiTrafficLightModifier extends ModifierCreateGui<TrafficLightModifier> {


    private BukkitUnit greenDurationUnit = BukkitUnit.SECONDS;
    private int greenDurationMin = 90;
    private int greenDurationMax = 300;

    private BukkitUnit yellowDurationUnit = BukkitUnit.TICKS;
    private int yellowDurationMin = 30;
    private int yellowDurationMax = 100;

    private BukkitUnit redDurationUnit = BukkitUnit.SECONDS;
    private int redDurationMin = 5;
    private int redDurationMax = 30;

    public GuiTrafficLightModifier(GuiProvider parentGui, Consumer<Supplier<TrafficLightModifier>> createdModifier) {
        super(parentGui, createdModifier, Component.text("Timer Modifier", Colors.GUI_TITLE), InventoryConstants.FULL_SIZE);
    }

    @Override
    public void init(Player player, InventoryContent content) {
        content.fill(InventoryConstants.GLAS_PANE);

        content.setItem(Slot.getSlot(6, 4), new QuickItemStack(Material.RED_WOOL, Component.text("Abbrechen", NamedTextColor.RED)).asGuiItem(event -> {
            if (event.getClick() != ClickType.LEFT) return;
            parentGui.provide(event.getPlayer());
        }));

        content.setItem(Slot.getSlot(6, 6), new QuickItemStack(Material.GREEN_WOOL, Component.text("Hinzufügen", NamedTextColor.GREEN)).asGuiItem(event -> {
            if (event.getClick() != ClickType.LEFT) return;
            var greenDuration = TimeRange.of(greenDurationUnit, greenDurationMin, greenDurationMax);
            var yellowDuration = TimeRange.of(yellowDurationUnit, yellowDurationMin, yellowDurationMax);
            var redDuration = TimeRange.of(redDurationUnit, redDurationMin, redDurationMax);

            onModifierCreate.accept(() -> new TrafficLightModifier(greenDuration, yellowDuration, redDuration, LightStatus.GREEN));
        }));
    }

    @Override
    public void update(Player player, InventoryContent content) {

        setSettingColumn(content, "Green Duration", 3, SettingValues.of(() -> greenDurationUnit, unit -> greenDurationUnit = unit), SettingValues.of(() -> greenDurationMin, min -> greenDurationMin = min), SettingValues.of(() -> greenDurationMax, max -> greenDurationMax = max));
        setSettingColumn(content, "Yellow Duration", 5, SettingValues.of(() -> yellowDurationUnit, unit -> yellowDurationUnit = unit), SettingValues.of(() -> yellowDurationMin, min -> yellowDurationMin = min), SettingValues.of(() -> yellowDurationMax, max -> yellowDurationMax = max));
        setSettingColumn(content, "Red Duration", 7, SettingValues.of(() -> redDurationUnit, unit -> redDurationUnit = unit), SettingValues.of(() -> redDurationMin, min -> redDurationMin = min), SettingValues.of(() -> redDurationMax, max -> redDurationMax = max));
    }

    private void setSettingColumn(InventoryContent content, String name, int column, SettingValues<BukkitUnit> unit, SettingValues<Integer> min, SettingValues<Integer> max) {

        GuiItem minItem = new QuickItemStack(Material.CHERRY_BUTTON)
                .displayName(Component.text("Min value for ", Colors.GRAY).append(Component.text(name, Colors.PRIMARY)))
                .itemLore(
                        Component.empty(),
                        Component.text("Current: ", Colors.GRAY).append(Component.text(min.get(), Colors.PRIMARY)),
                        Component.empty(),
                        Component.text("Left click: -1", Colors.GRAY),
                        Component.text("Shift-Left click: -10", Colors.GRAY),
                        Component.text("Right click: +1", Colors.GRAY),
                        Component.text("Shift-Right click: -10", Colors.GRAY)
                )
                .asGuiItem(event -> {
                    int current = min.get();
                    int nextValue = switch (event.getClick()) {
                        case LEFT -> current - 1;
                        case RIGHT -> current + 1;
                        case SHIFT_LEFT -> current - 10;
                        case SHIFT_RIGHT -> current + 10;
                        default -> current;
                    };

                    nextValue = Math.clamp(nextValue, 1, 1000);
                    if (nextValue != current) {
                        min.set(nextValue);
                    }
                });

        GuiItem unitItem = new QuickItemStack(Material.GLOW_ITEM_FRAME)
                .displayName(Component.text("Time Unit value for ", Colors.GRAY).append(Component.text(name, Colors.PRIMARY)))
                .itemLore(
                        Component.empty(),
                        Component.text("Current: ", Colors.GRAY).append(Component.text(unit.get().name(), Colors.PRIMARY)),
                        Component.empty(),
                        Component.text("Left click: Previous (%s)".formatted(EnumUtils.previous(unit.get())), Colors.GRAY),
                        Component.text("Right click: Next (%s)".formatted(EnumUtils.next(unit.get())), Colors.GRAY)
                )
                .asGuiItem(event -> {
                    BukkitUnit current = unit.get();

                    BukkitUnit newUnit = switch (event.getClick()) {
                        case LEFT -> EnumUtils.previous(current);
                        case RIGHT -> EnumUtils.next(current);
                        default -> current;
                    };

                    if (newUnit != current) {
                        unit.set(newUnit);
                    }
                });

        GuiItem maxItem = new QuickItemStack(Material.CHERRY_BUTTON)
                .displayName(Component.text("Max value for ", Colors.GRAY).append(Component.text(name, Colors.PRIMARY)))
                .itemLore(
                        Component.empty(),
                        Component.text("Current: ", Colors.GRAY).append(Component.text(max.get(), Colors.PRIMARY)),
                        Component.empty(),
                        Component.text("Left click: -1", Colors.GRAY),
                        Component.text("Shift-Left click: -10", Colors.GRAY),
                        Component.text("Right click: +1", Colors.GRAY),
                        Component.text("Shift-Right click: -10", Colors.GRAY)
                )
                .asGuiItem(event -> {
                    int current = max.get();
                    int nextValue = switch (event.getClick()) {
                        case LEFT -> current - 1;
                        case RIGHT -> current + 1;
                        case SHIFT_LEFT -> current - 10;
                        case SHIFT_RIGHT -> current + 10;
                        default -> current;
                    };

                    nextValue = Math.clamp(nextValue, 1, 1000);

                    if (nextValue != current) {
                        max.set(nextValue);
                    }
                });

        content.setItem(Slot.getSlot(2, column), minItem);
        content.setItem(Slot.getSlot(3, column), unitItem);
        content.setItem(Slot.getSlot(4, column), maxItem);

    }

    private record SettingValues<T>(Supplier<T> getter, Consumer<T> setter) {
        public static <T> SettingValues<T> of(Supplier<T> getter, Consumer<T> setter) {
            return new SettingValues<>(getter, setter);
        }

        public T get() {
            return getter.get();
        }

        public void set(T value) {
            setter.accept(value);
        }

    }

}
