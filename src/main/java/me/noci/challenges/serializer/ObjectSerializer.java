package me.noci.challenges.serializer;

import com.google.common.collect.Lists;
import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class ObjectSerializer<T> {

    public static <V> ObjectSerializer.Builder<V> of(Class<V> type) {
        return new Builder<>();
    }

    private final List<ByteWriter<T, ?>> serializers;
    private final ByteReader<T> reader;

    private ObjectSerializer(List<ByteWriter<T, ?>> serializers, ByteReader<T> reader) {
        this.serializers = serializers;
        this.reader = reader;
    }

    public byte[] serialize(T value) {
        int capacity = capacity(value);
        ByteBuffer buffer = ByteBuffer.allocate(capacity);
        serializers.forEach(serializers -> serializers.write(buffer, value));
        return buffer.array();
    }

    public Optional<T> read(ByteBuffer buffer) {
        return Optional.ofNullable(reader.read(buffer));
    }

    private int capacity(T value) {
        return serializers.stream().mapToInt(writer -> writer.byteSize(value)).sum();
    }

    public static class Builder<T> {

        private final List<ByteWriter<T, ?>> serializers = Lists.newArrayList();
        private ByteReader<T> reader = null;

        private Builder() {
        }

        public <V> Builder<T> layout(TypeSerializer<V> serializer, Function<T, V> mapper) {
            serializers.add(new ByteWriter<>(serializer, mapper));
            return this;
        }

        public Builder<T> read(ByteReader<T> reader) {
            this.reader = reader;
            return this;
        }

        public ObjectSerializer<T> get() {
            return new ObjectSerializer<>(serializers, reader);
        }

    }

    private record ByteWriter<T, V>(TypeSerializer<V> serializer, Function<T, V> mapper) {
        public void write(ByteBuffer buffer, T value) {
            serializer.write(buffer, mapper.apply(value));
        }

        public int byteSize(T value) {
            return serializer().byteSize(mapper.apply(value));
        }
    }

    @FunctionalInterface
    public interface ByteReader<T> {
        @Nullable T read(ByteBuffer buffer);
    }

}