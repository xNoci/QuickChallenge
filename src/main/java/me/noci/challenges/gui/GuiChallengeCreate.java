package me.noci.challenges.gui;

import me.noci.challenges.challenge.ChallengeController;
import me.noci.challenges.settings.Option;
import me.noci.quickutilities.gui.Gui;
import me.noci.quickutilities.gui.PagedGui;
import me.noci.quickutilities.utils.InventoryPattern;
import me.noci.quickutilities.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class GuiChallengeCreate {

    private static final int[] MODIFIER_SLOTS = InventoryPattern.box(3, 3);

    //TODO In the overview ui just add the modifier than give the option in this ui to edit them with right click
    // Mabye move this ui in ChallengeController
    // No longer us an AcceptDialog, clicking add just adds the modifier
    // Decouple Modifier UI code from ModifierRegistry
    // Use json/yaml to save challegne instead of byte file https://docs.papermc.io/paper/dev/plugin-configurations/#custom-configuration-files
    // One file for overall challenge settings, and than one file per modifier (if the modifier has configuration)
    // https://docs.papermc.io/paper/dev/plugin-configurations/


    public static void openGui(Player player, ChallengeController controller) {
        PagedGui pagedGui = Gui.pageed()
                .title(Option.Gui.ChallengeCreate.TITLE.get())
                .rows(9)
                .create();

        pagedGui.setItem(2, 5,
                ItemBuilder.of(Material.ENDER_EYE, Option.Gui.ChallengeCreate.ADD_MODIFIER.get()).asGuiItem());
        pagedGui.setItem(5, 4,
                ItemBuilder.of(Material.ANVIL, Option.Gui.ChallengeCreate.CREATE_MODIFIER.get()).asGuiItem());
        pagedGui.setItem(5, 6, ItemBuilder.of(Material.BARRIER, Option.Gui.ChallengeCreate.CANCEL.get()).asGuiItem());


        pagedGui.fillPattern(ItemBuilder.of(Material.BLACK_STAINED_GLASS_PANE).build(), InventoryPattern.FULL);
        Arrays.stream(MODIFIER_SLOTS).forEach(pagedGui::removeItem);

        pagedGui.open(player);
    }


}
