package me.noci.challenges.serializer;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public interface TypeSerializer<T> {

    static <V> TypeSerializer<V> dynamic(DynamicSizeTypeSerializer.Size<V> byteSize, DynamicSizeTypeSerializer.Reader<V> reader, DynamicSizeTypeSerializer.Writer<V> writer) {
        return new DynamicSizeTypeSerializer<>(byteSize, reader, writer);
    }

    static <V> TypeSerializer<V> fixed(int byteSize, FixedSizeTypeSerializer.Reader<V> reader, FixedSizeTypeSerializer.Writer<V> writer) {
        return new FixedSizeTypeSerializer<>(byteSize, reader, writer);
    }

    static <V extends Enum<?>> TypeSerializer<V> enumSerializer(Class<V> enumClass) {
        return enumSerializer(enumClass, TypeSerializers.U_BYTE);
    }

    static <V extends Enum<?>> TypeSerializer<V> enumSerializer(Class<V> enumClass, TypeSerializer<Integer> serializer) {
        return fixed(serializer.byteSize(null), buffer -> enumClass.getEnumConstants()[serializer.read(buffer)], (buffer, value) -> serializer.write(buffer, value.ordinal()));
    }

    static <V> TypeSerializer<List<V>> list(TypeSerializer<V> serializer) {
        return list(serializer, Lists::newArrayList);
    }

    static <V> TypeSerializer<List<V>> list(TypeSerializer<V> serializer, Supplier<List<V>> listSupplier) {
        return new ListTypeSerializer<>(serializer, listSupplier, TypeSerializers.U_BYTE);
    }

    static <V> TypeSerializer<List<V>> list(TypeSerializer<V> serializer, TypeSerializer<Integer> countSerializer) {
        return new ListTypeSerializer<>(serializer, Lists::newArrayList, countSerializer);
    }

    static <V> TypeSerializer<List<V>> list(TypeSerializer<V> serializer, Supplier<List<V>> listSupplier, TypeSerializer<Integer> countSerializer) {
        return new ListTypeSerializer<>(serializer, listSupplier, countSerializer);
    }

    static <K, V> TypeSerializer<Map<K, V>> map(TypeSerializer<K> keySerializer, TypeSerializer<V> valueSerializer) {
        return map(keySerializer, valueSerializer, Maps::newHashMap);
    }

    static <K, V> TypeSerializer<Map<K, V>> map(TypeSerializer<K> keySerializer, TypeSerializer<V> valueSerializer, Supplier<Map<K, V>> mapSupplier) {
        return new MapTypeSerializer<>(keySerializer, valueSerializer, mapSupplier);
    }

    int byteSize(T value);

    T read(ByteBuffer buffer);

    void write(ByteBuffer buffer, T value);

}