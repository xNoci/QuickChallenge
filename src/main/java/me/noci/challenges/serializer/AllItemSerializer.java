package me.noci.challenges.serializer;

import me.noci.challenges.challenge.modifiers.allitem.AllItem;
import me.noci.challenges.challenge.modifiers.allitem.AllItemModifier;
import me.noci.challenges.challenge.modifiers.allitem.CollectedItem;
import me.noci.challenges.serializer.utils.Node;
import me.noci.quickutilities.utils.EnumUtils;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.List;

public final class AllItemSerializer implements TypeSerializer<AllItemModifier> {

    static final AllItemSerializer INSTANCE = new AllItemSerializer();
    static final Class<AllItemModifier> TYPE = AllItemModifier.class;

    private AllItemSerializer() {

    }

    private static final String COLLECTED_ITEMS = "collected-items";
    private static final String CURRENT_ITEM = "current-item";
    private static final String ALL_COLLECTED = "finish";

    @Override
    public AllItemModifier deserialize(Type type, ConfigurationNode node) throws SerializationException {
        List<CollectedItem> collectedItems = Node.nonVirtual(node, COLLECTED_ITEMS).getList(CollectedItem.class);
        AllItem currentItem = Node.nonVirtual(node, CURRENT_ITEM).get(AllItem.class, EnumUtils.random(AllItem.class));
        boolean allCollected = Node.nonVirtual(node, ALL_COLLECTED).getBoolean(false);
        return new AllItemModifier(collectedItems, currentItem, allCollected);
    }

    @Override
    public void serialize(Type type, @Nullable AllItemModifier obj, ConfigurationNode target)
            throws SerializationException {
        if (obj == null) {
            target.raw(null);
            return;
        }

        target.node(COLLECTED_ITEMS).setList(CollectedItem.class, obj.collectedItems());
        target.node(CURRENT_ITEM).set(obj.currentItem());
        target.node(ALL_COLLECTED).set(obj.allItemsCollected());

    }
}
