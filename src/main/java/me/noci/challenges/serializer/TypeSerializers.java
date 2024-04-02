package me.noci.challenges.serializer;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class TypeSerializers {

    private static final Charset CHARSET = StandardCharsets.UTF_16;

    public static final TypeSerializer<Byte> BYTE = TypeSerializer.fixed(1, ByteBuffer::get, ByteBuffer::put);
    public static final TypeSerializer<Short> SHORT = TypeSerializer.fixed(2, ByteBuffer::getShort, ByteBuffer::putShort);
    public static final TypeSerializer<Integer> U_BYTE = TypeSerializer.fixed(1, buffer -> buffer.get() & 0xFF, (buffer, value) -> buffer.put(value.byteValue()));
    public static final TypeSerializer<UUID> UUID = TypeSerializer.fixed(16, buffer -> new UUID(buffer.getLong(), buffer.getLong()), (buffer, uuid) -> {
        buffer.putLong(uuid.getMostSignificantBits());
        buffer.putLong(uuid.getLeastSignificantBits());
    });
    public static final TypeSerializer<String> STRING = TypeSerializer.dynamic(s -> s.getBytes(CHARSET).length, data -> new String(data, CHARSET), (buffer, s) -> buffer.put(s.getBytes(CHARSET)));

}