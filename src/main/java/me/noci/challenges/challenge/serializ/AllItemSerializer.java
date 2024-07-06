package me.noci.challenges.challenge.serializ;

import me.noci.challenges.challenge.modifiers.allitem.AllItem;
import me.noci.challenges.challenge.modifiers.allitem.AllItemModifier;
import me.noci.challenges.challenge.modifiers.allitem.CollectedItem;
import me.noci.challenges.serializer.TypeSerializer;

import java.util.List;
import java.util.Optional;

import static me.noci.challenges.serializer.TypeSerializers.*;

public class AllItemSerializer {

    static final TypeSerializer<Optional<AllItemModifier>> V_1 = TypeSerializer.dynamic(value -> BOOLEAN.byteSize(null) +
            COLLECTED_ITEM_LIST.byteSize(value.map(AllItemModifier::collectedItems).orElse(List.of())) +
            ALL_ITEM.byteSize(null) +
            BOOLEAN.byteSize(null), buffer -> {
        boolean enabled = BOOLEAN.read(buffer);
        List<CollectedItem> collectedItems = COLLECTED_ITEM_LIST.read(buffer);
        AllItem currentItem = ALL_ITEM.read(buffer);
        boolean allItemsCollected = BOOLEAN.read(buffer);
        if (!enabled) return Optional.empty();
        return Optional.of(new AllItemModifier(currentItem, collectedItems, allItemsCollected));
    }, (buffer, value) -> {
        BOOLEAN.write(buffer, value.isPresent());
        COLLECTED_ITEM_LIST.write(buffer, value.map(AllItemModifier::collectedItems).orElse(List.of()));
        ALL_ITEM.write(buffer, value.map(AllItemModifier::currentItem).orElse(AllItem.ACACIA_BOAT));
        BOOLEAN.write(buffer, value.map(AllItemModifier::allItemsCollected).orElse(false));
    });

    static final TypeSerializer<Optional<AllItemModifier>> V_2 = TypeSerializer.dynamic(value -> BOOLEAN.byteSize(null) +
            COLLECTED_ITEM_LIST_V2.byteSize(value.map(AllItemModifier::collectedItems).orElse(List.of())) +
            ALL_ITEM.byteSize(null) +
            BOOLEAN.byteSize(null), buffer -> {
        boolean enabled = BOOLEAN.read(buffer);
        List<CollectedItem> collectedItems = COLLECTED_ITEM_LIST_V2.read(buffer);
        AllItem currentItem = ALL_ITEM.read(buffer);
        boolean allItemsCollected = BOOLEAN.read(buffer);
        if (!enabled) return Optional.empty();
        return Optional.of(new AllItemModifier(currentItem, collectedItems, allItemsCollected));
    }, (buffer, value) -> {
        BOOLEAN.write(buffer, value.isPresent());
        COLLECTED_ITEM_LIST_V2.write(buffer, value.map(AllItemModifier::collectedItems).orElse(List.of()));
        ALL_ITEM.write(buffer, value.map(AllItemModifier::currentItem).orElse(AllItem.ACACIA_BOAT));
        BOOLEAN.write(buffer, value.map(AllItemModifier::allItemsCollected).orElse(false));
    });

    static final TypeSerializer<Optional<AllItemModifier>> V_3 = TypeSerializer.dynamic(value -> BOOLEAN.byteSize(null) +
            COLLECTED_ITEM_LIST_V3.byteSize(value.map(AllItemModifier::collectedItems).orElse(List.of())) +
            ALL_ITEM.byteSize(null) +
            BOOLEAN.byteSize(null), buffer -> {
        boolean enabled = BOOLEAN.read(buffer);
        List<CollectedItem> collectedItems = COLLECTED_ITEM_LIST_V3.read(buffer);
        AllItem currentItem = ALL_ITEM.read(buffer);
        boolean allItemsCollected = BOOLEAN.read(buffer);
        if (!enabled) return Optional.empty();
        return Optional.of(new AllItemModifier(currentItem, collectedItems, allItemsCollected));
    }, (buffer, value) -> {
        BOOLEAN.write(buffer, value.isPresent());
        COLLECTED_ITEM_LIST_V3.write(buffer, value.map(AllItemModifier::collectedItems).orElse(List.of()));
        ALL_ITEM.write(buffer, value.map(AllItemModifier::currentItem).orElse(AllItem.ACACIA_BOAT));
        BOOLEAN.write(buffer, value.map(AllItemModifier::allItemsCollected).orElse(false));
    });

}
