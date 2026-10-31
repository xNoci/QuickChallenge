package me.noci.challenges.gui.modifier;

import me.noci.quickutilities.utils.ItemBuilder;

@FunctionalInterface
public interface DisplayItemSupplier {

    ItemBuilder  item();

}
