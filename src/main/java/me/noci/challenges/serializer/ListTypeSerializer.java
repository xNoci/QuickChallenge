package me.noci.challenges.serializer;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ListTypeSerializer<T> implements TypeSerializer<List<T>> {

    private final TypeSerializer<T> serializer;
    private final Supplier<List<T>> listSupplier;

    protected ListTypeSerializer(TypeSerializer<T> serializer, Supplier<List<T>> listSupplier) {
        this.serializer = serializer;
        this.listSupplier = listSupplier;
    }

    @Override
    public int byteSize(List<T> value) {
        return value.stream().mapToInt(serializer::byteSize).sum() + TypeSerializers.U_BYTE.byteSize(null);
    }

    @Override
    public List<T> read(ByteBuffer buffer) {
        int count = TypeSerializers.U_BYTE.read(buffer);
        return IntStream.range(0, count).mapToObj(value -> serializer.read(buffer)).collect(Collectors.toCollection(listSupplier));
    }

    @Override
    public void write(ByteBuffer buffer, List<T> value) {
        TypeSerializers.U_BYTE.write(buffer, value.size());
        value.forEach(v -> serializer.write(buffer, v));
    }

}