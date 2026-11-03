package me.noci.challenges.gui;

import com.google.common.collect.Sets;
import me.noci.challenges.challenge.ChallengeController;
import me.noci.challenges.challenge.modifiers.ChallengeModifier;
import me.noci.challenges.challenge.modifiers.registry.ModifierRegistry;
import me.noci.challenges.gui.modifier.GuiModifierOverview;
import me.noci.challenges.paper.QuickChallenge;
import me.noci.challenges.settings.Option;
import me.noci.quickutilities.gui.Gui;
import me.noci.quickutilities.gui.GuiItem;
import me.noci.quickutilities.gui.PagedGui;
import me.noci.quickutilities.utils.InventoryPattern;
import me.noci.quickutilities.utils.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class GuiChallengeCreate {

    private static final int[] MODIFIER_SLOTS = InventoryPattern.box(3, 3);
    private final PagedGui creationGui;
    private final Set<ChallengeModifier> modifiers = Sets.newHashSet();

    public GuiChallengeCreate() {
        this.creationGui = createGui();
    }

    private PagedGui createGui() {
        PagedGui creationGui = Gui.paged()
                .title(Option.Gui.ChallengeCreate.TITLE.get())
                .rows(6)
                .create();


        creationGui.fillPattern(InventoryConstants.GLAS_PANE, InventoryPattern.FULL);
        Arrays.stream(MODIFIER_SLOTS).forEach(creationGui::removeItem);

        creationGui.setItem(1, 3, InventoryConstants.previousPageItem().asGuiItem(_ -> creationGui.previous()));
        creationGui.setItem(9, 3, InventoryConstants.nextPageItem().asGuiItem(_ -> creationGui.next()));
        creationGui.setItem(5, 2,
                ItemBuilder.of(Material.ENDER_EYE, Option.Gui.ChallengeCreate.ADD_MODIFIER.get())
                        .asGuiItem(this::onAddModifier));
        creationGui.setItem(4, 5,
                ItemBuilder.of(Material.BARRIER, Option.Gui.ChallengeCreate.CANCEL.get())
                        .asGuiItem(this::onCancel));
        creationGui.setItem(6, 5,
                ItemBuilder.of(Material.ANVIL, Option.Gui.ChallengeCreate.CREATE.get())
                        .asGuiItem(this::onCreateChallenge));
        return creationGui;
    }

    public void openGui(HumanEntity player) {
        creationGui.open(player);
    }

    public Set<ChallengeModifier> modifiers() {
        return new HashSet<>(modifiers);
    }

    public void removeModifier(ChallengeModifier modifier) {
        this.modifiers.remove(modifier);
        updatePageItems();
    }

    public void addModifier(ChallengeModifier modifier) {
        modifiers.add(modifier);
        creationGui.addItem(ModifierRegistry.displayItem(modifier.getClass()).asGuiItem());
        updatePageItems();
    }

    private void updatePageItems() {
        creationGui.clearPageItems();
        modifiers.stream().map(this::appliedModifierItem).forEach(creationGui::addItem);
        creationGui.update();
    }

    private void onAddModifier(InventoryClickEvent event) {
        new GuiModifierOverview(this).openGui(event.getWhoClicked());
    }

    private synchronized void onCreateChallenge(InventoryClickEvent event) {
        ChallengeController controller = QuickChallenge.instance().challengeController();
        controller.createNewChallenge(event.getWhoClicked(), modifiers);
        event.getWhoClicked().closeInventory();
    }

    private void onCancel(InventoryClickEvent event) {
        event.getWhoClicked().closeInventory();
    }

    private GuiItem appliedModifierItem(ChallengeModifier modifier) {
        return ModifierRegistry.displayItem(modifier.getClass())
                .lore(
                        Component.empty(),
                        Option.Gui.ChallengeCreate.MODIFIER_REMOVE_HINT.get()
                )
                .asGuiItem(e -> GuiAcceptDialog.builder()
                        .title(Option.Gui.ChallengeCreate.RemoveDialog.TITLE.get())
                        .description(
                                Component.empty(),
                                Option.Gui.ChallengeCreate.RemoveDialog.DESCRIPTION.get(),
                                Component.empty(),
                                Option.Gui.ChallengeCreate.RemoveDialog.MODIFIER_NAME.resolve(
                                        Placeholder.component("modifier_name",
                                                ModifierRegistry.displayItem(modifier.getClass()).build()
                                                        .effectiveName()))
                        )
                        .acceptAction(event -> {
                            removeModifier(modifier);
                            creationGui.open(event.getWhoClicked());
                        })
                        .declineAction(event -> creationGui.open(event.getWhoClicked()))
                        .open(e.getWhoClicked()));
    }

}
