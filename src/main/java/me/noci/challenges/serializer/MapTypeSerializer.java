package me.noci.challenges.serializer;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MapTypeSerializer<K, V> implements TypeSerializer<Map<K, V>> {

    private final TypeSerializer<K> keySerializer;
    private final TypeSerializer<V> valueSerializer;
    private final Supplier<Map<K, V>> mapSupplier;

    protected MapTypeSerializer(TypeSerializer<K> keySerializer, TypeSerializer<V> valueSerializer, Supplier<Map<K, V>> mapSupplier) {
        this.keySerializer = keySerializer;
        this.valueSerializer = valueSerializer;
        this.mapSupplier = mapSupplier;
    }

    @Override
    public int byteSize(Map<K, V> value) {
        return value.entrySet().stream()
                .mapToInt(entry -> keySerializer.byteSize(entry.getKey()) + valueSerializer.byteSize(entry.getValue()))
                .sum() + TypeSerializers.U_BYTE.byteSize(null);
    }

    @Override
    public Map<K, V> read(ByteBuffer buffer) {
        int count = TypeSerializers.U_BYTE.read(buffer);
        return IntStream.range(0, count)
                .mapToObj(value -> new KeyValuePair<>(keySerializer.read(buffer), valueSerializer.read(buffer)))
                .collect(Collectors.toMap(KeyValuePair::key, KeyValuePair::value, (v, v1) -> v, mapSupplier));
    }

    @Override
    public void write(ByteBuffer buffer, Map<K, V> map) {
        TypeSerializers.U_BYTE.write(buffer, map.size());
        map.forEach((key, value) -> {
            keySerializer.write(buffer, key);
            valueSerializer.write(buffer, value);
        });
    }

    private record KeyValuePair<K, V>(K key, V value) {
    }

}