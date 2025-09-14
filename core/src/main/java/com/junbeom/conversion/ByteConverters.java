package com.junbeom.conversion;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Utility class for converting byte arrays to numeric types using little-endian byte order.
 *
 * <p>This class provides static methods to convert byte arrays to various numeric types
 * (long, int, short) using little-endian byte order. Input arrays shorter than the target
 * size are automatically padded with trailing zeros to match the required byte length.</p>
 *
 * <p><strong>Thread Safety:</strong> All methods are thread-safe as they are stateless
 * and operate only on input parameters.</p>
 *
 * <p><strong>Performance:</strong> Methods use {@link java.nio.ByteBuffer} for efficient
 * byte-to-numeric conversions with minimal memory allocation.</p>
 *
 * @since 1.0
 */
public class ByteConverters {
    
    /**
     * Converts a byte array to a long value using little-endian byte order.
     *
     * <p>The input array can contain up to 8 bytes. Arrays shorter than 8 bytes
     * are automatically padded with trailing zeros. Arrays longer than 8 bytes
     * will cause an {@link ArrayIndexOutOfBoundsException}.</p>
     *
     * @param input the byte array to convert (maximum 8 bytes)
     * @return the long value represented by the byte array in little-endian format
     * @throws NullPointerException if input is null
     * @throws ArrayIndexOutOfBoundsException if input array length exceeds 8 bytes
     *
     * @see java.nio.ByteOrder#LITTLE_ENDIAN
     */
    public static long bytesToLongLE(byte[] input) {
        if (input.length > 8) {
            throw new ArrayIndexOutOfBoundsException("Input array length exceeds 8 bytes");
        }

        byte[] bytes = new byte[8];
        System.arraycopy(input, 0, bytes, 0, input.length);

        return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getLong();
    }
    
    /**
     * Converts a byte array to an int value using little-endian byte order.
     *
     * <p>The input array can contain up to 4 bytes. Arrays shorter than 4 bytes
     * are automatically padded with trailing zeros. Arrays longer than 4 bytes
     * will cause an {@link ArrayIndexOutOfBoundsException}.</p>
     *
     * @param input the byte array to convert (maximum 4 bytes)
     * @return the int value represented by the byte array in little-endian format
     * @throws NullPointerException if input is null
     * @throws ArrayIndexOutOfBoundsException if input array length exceeds 4 bytes
     *
     * @see java.nio.ByteOrder#LITTLE_ENDIAN
     */
    public static int bytesToIntLE(byte[] input) {
        if (input.length > 4) {
            throw new ArrayIndexOutOfBoundsException("Input array length exceeds 4 bytes");
        }

        byte[] bytes = new byte[4];
        System.arraycopy(input, 0, bytes, 0, input.length);

        return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getInt();
    }
    
    /**
     * Converts a byte array to a short value using little-endian byte order.
     *
     * <p>The input array can contain up to 2 bytes. Arrays shorter than 2 bytes
     * are automatically padded with trailing zeros. Arrays longer than 2 bytes
     * will cause an {@link ArrayIndexOutOfBoundsException}.</p>
     *
     * @param input the byte array to convert (maximum 2 bytes)
     * @return the short value represented by the byte array in little-endian format
     * @throws NullPointerException if input is null
     * @throws ArrayIndexOutOfBoundsException if input array length exceeds 2 bytes
     *
     * @see java.nio.ByteOrder#LITTLE_ENDIAN
     */
    public static short bytesToShortLE(byte[] input) {
        if (input.length > 2) {
            throw new ArrayIndexOutOfBoundsException("Input array length exceeds 2 bytes");
        }

        byte[] bytes = new byte[2];
        System.arraycopy(input, 0, bytes, 0, input.length);

        return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getShort();
    }
}