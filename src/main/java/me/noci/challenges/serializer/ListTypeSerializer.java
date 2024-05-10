package me.noci.challenges.serializer;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

public class ListTypeSerializer<T> implements TypeSerializer<List<T>> {

    private final TypeSerializer<Integer> countSerializer;
    private final TypeSerializer<T> serializer;
    private final Supplier<List<T>> listSupplier;

    protected ListTypeSerializer(TypeSerializer<T> serializer, Supplier<List<T>> listSupplier, TypeSerializer<Integer> countSerializer) {
        this.serializer = serializer;
        this.listSupplier = listSupplier;
        this.countSerializer = countSerializer;
    }

    @Override
    public int byteSize(List<T> value) {
        return value.stream().mapToInt(serializer::byteSize).sum() + countSerializer.byteSize(null);
    }

    @Override
    public List<T> read(ByteBuffer buffer) {
        int count = countSerializer.read(buffer);
        return LongStream.range(0, count).mapToObj(value -> serializer.read(buffer)).collect(Collectors.toCollection(listSupplier));
    }

    @Override
    public void write(ByteBuffer buffer, List<T> value) {
        countSerializer.write(buffer, value.size());
        value.forEach(v -> serializer.write(buffer, v));
    }

}