package me.noci.challenges.serializer;

import java.nio.ByteBuffer;

public class DynamicSizeTypeSerializer<T> implements TypeSerializer<T> {

    private final Size<T> size;
    private final Reader<T> reader;
    private final Writer<T> writer;

    protected DynamicSizeTypeSerializer(Size<T> byteSize, Reader<T> reader, Writer<T> writer) {
        this.size = byteSize;
        this.reader = reader;
        this.writer = writer;
    }

    @Override
    public int byteSize(T value) {
        return this.size.byteSize(value) + 1;
    }

    @Override
    public T read(ByteBuffer buffer) {
        int length = TypeSerializers.U_BYTE.read(buffer);
        byte[] data = new byte[length];
        buffer.get(data);
        return reader.read(ByteBuffer.wrap(data));
    }

    @Override
    public void write(ByteBuffer buffer, T value) {
        TypeSerializers.U_BYTE.write(buffer, this.size.byteSize(value));
        writer.write(buffer, value);
    }

    @FunctionalInterface
    public interface Size<T> {
        int byteSize(T value);
    }

    @FunctionalInterface
    public interface Reader<T> {
        T read(ByteBuffer data);
    }

    @FunctionalInterface
    public interface Writer<T> {
        void write(ByteBuffer buffer, T value);
    }

}