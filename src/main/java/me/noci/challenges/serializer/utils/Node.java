package me.noci.challenges.serializer.utils;

import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.Arrays;

public final class Node {

    private Node() {

    }


    public static ConfigurationNode nonVirtual(ConfigurationNode source, Object... path) throws SerializationException {
        if (!source.hasChild(path)) {
            throw new SerializationException("Required field " + Arrays.toString(path) + " was not present in node");
        }
        return source.node(path);
    }

}
