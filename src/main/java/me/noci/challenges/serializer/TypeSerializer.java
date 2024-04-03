package me.noci.challenges.serializer;

import com.google.common.collect.Lists;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.function.Supplier;

public interface TypeSerializer<T> {

    static <V> TypeSerializer<V> dynamic(DynamicSizeTypeSerializer.Size<V> byteSize, DynamicSizeTypeSerializer.Reader<V> reader, DynamicSizeTypeSerializer.Writer<V> writer) {
        return new DynamicSizeTypeSerializer<>(byteSize, reader, writer);
    }

    static <V> TypeSerializer<V> fixed(int byteSize, FixedSizeTypeSerializer.Reader<V> reader, FixedSizeTypeSerializer.Writer<V> writer) {
        return new FixedSizeTypeSerializer<>(byteSize, reader, writer);
    }

    static <V extends Enum<?>> TypeSerializer<V> enumSerializer(Class<V> enumClass) {
        return fixed(1, buffer -> enumClass.getEnumConstants()[buffer.get() & 0xFF], (buffer, value) -> buffer.put(((Integer) value.ordinal()).byteValue()));
    }

    static <V> TypeSerializer<List<V>> list(TypeSerializer<V> serializer) {
        return list(serializer, Lists::newArrayList);
    }

    static <V> TypeSerializer<List<V>> list(TypeSerializer<V> serializer, Supplier<List<V>> listSupplier) {
        return new ListTypeSerializer<>(serializer, listSupplier);
    }

    int byteSize(T value);

    T read(ByteBuffer buffer);

    void write(ByteBuffer buffer, T value);

}