package me.noci.challenges.serializer;

import java.nio.ByteBuffer;

public class FixedSizeTypeSerializer<T> implements TypeSerializer<T> {

    private final int byteSize;
    private final Reader<T> reader;
    private final Writer<T> writer;

    protected FixedSizeTypeSerializer(int byteSize, Reader<T> reader, Writer<T> writer) {
        this.byteSize = byteSize;
        this.reader = reader;
        this.writer = writer;
    }

    @Override
    public int byteSize(T value) {
        return this.byteSize;
    }

    @Override
    public T read(ByteBuffer buffer) {
        return reader.read(buffer);
    }

    @Override
    public void write(ByteBuffer buffer, T value) {
        writer.write(buffer, value);
    }

    @FunctionalInterface
    public interface Reader<T> {
        T read(ByteBuffer buffer);
    }

    @FunctionalInterface
    public interface Writer<T> {
        void write(ByteBuffer buffer, T value);
    }

}