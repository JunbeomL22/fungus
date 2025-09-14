package com.junbeom.conversion;

import java.math.BigInteger;

public final class BitManipulation {

    private static final BigInteger MASK_0F_128 = new BigInteger("0f000f000f000f000f000f000f000f00", 16);
    private static final BigInteger MASK_000F_128 = new BigInteger("000f000f000f000f000f000f000f000f", 16);
    private static final BigInteger MASK_00FF_128 = new BigInteger("00ff000000ff000000ff000000ff0000", 16);
    private static final BigInteger MASK_FF_128 = new BigInteger("000000ff000000ff000000ff000000ff", 16);
    private static final BigInteger MASK_FFFF_128 = new BigInteger("0000ffff000000000000ffff00000000", 16);
    private static final BigInteger MASK_FFFF_LOW_128 = new BigInteger("000000000000ffff000000000000ffff", 16);
    private static final BigInteger MASK_FFFFFFFF_HIGH_128 = new BigInteger("00000000ffffffff0000000000000000", 16);
    private static final BigInteger MASK_FFFFFFFF_LOW_128 = new BigInteger("000000000000000000000000ffffffff", 16);

    private static final BigInteger HUNDRED = BigInteger.valueOf(100);
    private static final BigInteger TEN_THOUSAND = BigInteger.valueOf(10000);
    private static final BigInteger HUNDRED_MILLION = BigInteger.valueOf(100_000_000);

    private BitManipulation() {}

    /**
     * Converts a 16-bit chunk containing two packed BCD digits to an unsigned integer.
     *
     * <p>Expects a chunk where each nibble (4 bits) represents a decimal digit.
     * The structure is interpreted as follows:</p>
     * <pre>
     *   [byte1_high] [byte1_low] [byte0_high] [byte0_low]
     *   [0x0X]       [0x0X]      [0x0X]      [0x0X]
     *   = byte1_high * 10 + byte1_low
     * </pre>
     *
     * <p><b>Performance:</b> O(1) time complexity - constant time bit operations.
     * <b>Memory:</b> No heap allocations, pure bit manipulation.</p>
     * <p><b>Thread Safety:</b> This method is thread-safe as it only performs
     * local variable operations and reads.</p>
     *
     * @param chunk the 16-bit chunk containing BCD digits (big-endian format)
     * @return the unsigned integer value (0-99)
     * @throws IllegalArgumentException if the chunk does not contain valid decimal digits
     * @see #checkDecimal(short)
     * @see #twoToUnsignedShort(short)
     */
    public static short twoToUnsignedShort(short chunk) {
        return (short) (((chunk & 0x0f00) >>> 8) + (chunk & 0x000f) * 10);
    }

    /**
     * Converts a 32-bit chunk containing four packed BCD digits to an unsigned integer.
     *
     * <p>Expects a chunk where each nibble (4 bits) represents a decimal digit.
     * The conversion is performed in two passes:</p>
     * <ol>
     * <li>First pass: Process high and low bytes separately to create two 2-digit numbers</li>
     * <li>Second pass: Combine the two 2-digit numbers into a single 4-digit number</li>
     * </ol>
     * <p>Structure:</p>
     * <pre>
     *   [byte3_high] [byte3_low] [byte2_high] [byte2_low] [byte1_high] [byte1_low] [byte0_high] [byte0_low]
     *   = (byte3_high * 1000 + byte3_low * 100) + (byte2_high * 10 + byte2_low) + (byte1_high * 10 + byte1_low)
     *   = digit3 * 1000 + digit2 * 100 + digit1 * 10 + digit0
     * </pre>
     *
     * <p><b>Performance:</b> O(1) time complexity - constant time bit operations.
     * <b>Memory:</b> No heap allocations, pure bit manipulation.</p>
     * <p><b>Thread Safety:</b> This method is thread-safe as it only performs
     * local variable operations and reads.</p>
     *
     * @param chunk the 32-bit chunk containing BCD digits (big-endian format)
     * @return the unsigned integer value (0-9999)
     * @throws IllegalArgumentException if the chunk does not contain valid decimal digits
     * @see #checkDecimal(int)
     * @see #fourToUnsignedInt(int)
     */
    public static int fourToUnsignedInt(int chunk) {
        int lowerDigits = (chunk & 0x0f000f00) >>> 8;
        int upperDigits = (chunk & 0x000f000f) * 10;
        chunk = lowerDigits + upperDigits;

        lowerDigits = (chunk & 0x00ff0000) >>> 16;
        upperDigits = (chunk & 0x000000ff) * 100;
        chunk = lowerDigits + upperDigits;

        return chunk;
    }

    /**
     * Converts a 64-bit chunk containing eight packed BCD digits to an unsigned long.
     *
     * <p>Expects a chunk where each nibble (4 bits) represents a decimal digit.
     * The conversion is performed in three passes:</p>
     * <ol>
     * <li>First pass: Process each 8-byte group to create two 2-digit numbers</li>
     * <li>Second pass: Combine pairs of 2-digit numbers into four 2-digit groups</li>
     * <li>Third pass: Combine the four groups into the final 8-digit number</li>
     * </ol>
     * <p>Structure:</p>
     * <pre>
     *   [byte7_high] [byte7_low] ... [byte0_high] [byte0_low]
     *   = byte7_high * 10000000 + byte7_low * 1000000 + ... + byte0_high * 10 + byte0_low
     *   = digit7 * 10000000 + digit6 * 1000000 + ... + digit1 * 10 + digit0
     * </pre>
     *
     * <p><b>Performance:</b> O(1) time complexity - constant time bit operations.
     * <b>Memory:</b> No heap allocations, pure bit manipulation.</p>
     * <p><b>Thread Safety:</b> This method is thread-safe as it only performs
     * local variable operations and reads.</p>
     *
     * @param chunk the 64-bit chunk containing BCD digits (big-endian format)
     * @return the unsigned long value (0-99999999)
     * @throws IllegalArgumentException if the chunk does not contain valid decimal digits
     * @see #checkDecimal(long)
     * @see #eightToUnsignedLong(long)
     */
    public static long eightToUnsignedLong(long chunk) {
        long lowerDigits = (chunk & 0x0f000f000f000f00L) >>> 8;
        long upperDigits = (chunk & 0x000f000f000f000fL) * 10;
        chunk = lowerDigits + upperDigits;

        lowerDigits = (chunk & 0x00ff000000ff0000L) >>> 16;
        upperDigits = (chunk & 0x000000ff000000ffL) * 100;
        chunk = lowerDigits + upperDigits;

        lowerDigits = (chunk & 0x0000ffff00000000L) >>> 32;
        upperDigits = (chunk & 0x000000000000ffffL) * 10000;
        chunk = lowerDigits + upperDigits;

        return chunk;
    }

    /**
     * Converts two 64-bit chunks containing 16 packed BCD digits to a single unsigned long.
     *
     * <p>Combines two 8-digit BCD numbers (from upper and lower chunks) into a single
     * 16-digit unsigned long by concatenating the digits. Upper chunk digits become
     * the high-order digits, lower chunk digits become the low-order digits.</p>
     *
     * <p>Structure:</p>
     * <pre>
     *   upper: [digit15-8] 8 digits
     *   lower: [digit7-0] 8 digits
     *   Result: upper * 100000000 + lower
     *           = digit15 * 10^15 + digit14 * 10^14 + ... + digit0 * 10^0
     * </pre>
     *
     * <p><b>Performance:</b> O(1) time complexity - constant time bit operations.
     * <b>Memory:</b> No heap allocations, pure bit manipulation.</p>
     * <p><b>Thread Safety:</b> This method is thread-safe as it only performs
     * local variable operations and reads.</p>
     *
     * @param upper the upper 64-bit chunk containing digits 15-8 (high-order 8 digits)
     * @param lower the lower 64-bit chunk containing digits 7-0 (low-order 8 digits)
     * @return the unsigned long value (0-9999999999999999)
     * @throws IllegalArgumentException if either chunk does not contain valid decimal digits
     * @see #sixteenToUnsignedBigInteger(BigInteger)
     * @see #eightToUnsignedLong(long)
     */
    public static long sixteenToUnsignedLong(long upper, long lower) {
        long lowerResult = eightToUnsignedLong(lower);       
        long upperResult = eightToUnsignedLong(upper);       
        return lowerResult * 100_000_000L + upperResult;
    }

    /*
     * Not that efficient. For now, not used
     */
    private static BigInteger sixteenToUnsignedBigInteger(BigInteger chunk) {
        long lowLong = chunk.longValue();                    // Low 8 bytes (rightmost digits)
        long highLong = chunk.shiftRight(64).longValue();    // High 8 bytes (leftmost digits)

        long lowResult = eightToUnsignedLong(lowLong);       // Process low chunk
        long highResult = eightToUnsignedLong(highLong);     // Process high chunk

        return BigInteger.valueOf(lowResult).multiply(HUNDRED_MILLION).add(BigInteger.valueOf(highResult));
    }

    /**
     * Checks if all bytes in a 16-bit chunk represent valid decimal digits (0-9).
     *
     * <p>Uses bit manipulation to efficiently validate that each byte contains
     * a character between '0' (0x30) and '9' (0x39). The method operates by
     * checking that no byte is above 0x3A and no byte is below 0x2F.</p>
     *
     * <p><b>Performance:</b> O(1) time complexity - constant time bit operations.</p>
     * <p><b>Thread Safety:</b> This method is thread-safe as it only performs
     * local variable operations and reads.</p>
     *
     * @param chunk the 16-bit chunk to validate
     * @return {@code true} if both bytes are decimal digits, {@code false} otherwise
     * @see #twoToUnsignedShort(short)
     * @see #checkedConversionU16(byte[])
     */
    public static boolean checkDecimal(short chunk) {
        int notAbove = chunk - 0x3A3A;
        int notBelow = 0x2F2F - chunk;
        return (notAbove & notBelow & 0x8080) == 0x8080;
    }

    /**
     * Checks if all bytes in a 32-bit chunk represent valid decimal digits (0-9).
     *
     * <p>Uses bit manipulation to efficiently validate that each byte contains
     * a character between '0' (0x30) and '9' (0x39). The method operates by
     * checking that no byte is above 0x3A and no byte is below 0x2F.</p>
     *
     * <p><b>Performance:</b> O(1) time complexity - constant time bit operations.</p>
     * <p><b>Thread Safety:</b> This method is thread-safe as it only performs
     * local variable operations and reads.</p>
     *
     * @param chunk the 32-bit chunk to validate
     * @return {@code true} if all four bytes are decimal digits, {@code false} otherwise
     * @see #fourToUnsignedInt(int)
     * @see #checkedConversionU32(byte[])
     */
    public static boolean checkDecimal(int chunk) {
        int notAbove = chunk - 0x39393939 - 1;  // Same fix as long version
        int notBelow = 0x2F2F2F2F - chunk;
        return (notAbove & notBelow & 0x80808080) == 0x80808080;
    }

    /**
     * Checks if all bytes in a 64-bit chunk represent valid decimal digits (0-9).
     *
     * <p>Uses bit manipulation to efficiently validate that each byte contains
     * a character between '0' (0x30) and '9' (0x39). The method operates by
     * checking that no byte is above 0x3A and no byte is below 0x2F.</p>
     *
     * <p><b>Performance:</b> O(1) time complexity - constant time bit operations.</p>
     * <p><b>Thread Safety:</b> This method is thread-safe as it only performs
     * local variable operations and reads.</p>
     *
     * @param chunk the 64-bit chunk to validate
     * @return {@code true} if all eight bytes are decimal digits, {@code false} otherwise
     * @see #eightToUnsignedLong(long)
     * @see #checkedConversionU64(byte[])
     */
    public static boolean checkDecimal(long chunk) {
        // Fix: Use 0x39 (after '9') so ':' (0x3A) gives positive result, setting no sign bit
        long notAbove = chunk - 0x3939393939393939L - 1;  // Now ':' gives 0x3A - 0x39 - 1 = 0 (no sign bit)
        long notBelow = 0x2F2F2F2F2F2F2F2FL - chunk;
        return (notAbove & notBelow & 0x8080808080808080L) == 0x8080808080808080L;
    }

    /**
     * Performs checked conversion from a byte array to a 8-bit unsigned integer.
     *
     * <p>Validates that the input contains a decimal digit (0x30-0x39) and converts
     * it to an integer value. Throws {@link ConversionException.ParseErr.NonDecimal}
     * if the input is not a valid decimal digit.</p>
     *
     * @param input the byte array containing the digit to convert (minimum 1 byte)
     * @return the converted 8-bit unsigned integer value
     * @throws ConversionException.ParseErr.NonDecimal if the input is not a decimal digit
     * @throws ArrayIndexOutOfBoundsException if input array is empty
     */
    public static byte checkedConversionU8(byte[] input) throws ConversionException.ParseErr {
        if (input[0] >= 0x30 && input[0] <= 0x39) {
            return (byte) (input[0] - 0x30);
        } else {
            throw new ConversionException.ParseErr.NonDecimal();
        }
    }

    /**
     * Performs checked conversion from a byte array to a 16-bit unsigned integer.
     *
     * <p>Converts the byte array to a 16-bit value using little-endian byte order,
     * validates that both bytes represent decimal digits using {@link #checkDecimal(short)},
     * then converts the digits to an integer. Throws {@link ConversionException.ParseErr.NonDecimal}
     * if the input contains non-decimal characters.</p>
     *
     * @param input the byte array containing the two digits to convert (minimum 2 bytes)
     * @return the converted 16-bit unsigned integer value
     * @throws ConversionException.ParseErr.NonDecimal if the input contains non-decimal characters
     * @throws ArrayIndexOutOfBoundsException if input array length is insufficient for ByteConverters
     * @see ByteConverters#bytesToShortLE(byte[])
     * @see #checkDecimal(short)
     */
    public static short checkedConversionU16(byte[] input) throws ConversionException.ParseErr {
        short chunk = ByteConverters.bytesToShortLE(input);
        if (checkDecimal(chunk)) {
            return twoToUnsignedShort(chunk);
        } else {
            throw new ConversionException.ParseErr.NonDecimal();
        }
    }

    /**
     * Performs checked conversion from a byte array to a 32-bit unsigned integer.
     *
     * <p>Converts the byte array to a 32-bit value using little-endian byte order,
     * validates that all four bytes represent decimal digits using {@link #checkDecimal(int)},
     * then converts the digits to an integer. Throws {@link ConversionException.ParseErr.NonDecimal}
     * if the input contains non-decimal characters.</p>
     *
     * @param input the byte array containing the four digits to convert (minimum 4 bytes)
     * @return the converted 32-bit unsigned integer value
     * @throws ConversionException.ParseErr.NonDecimal if the input contains non-decimal characters
     * @throws ArrayIndexOutOfBoundsException if input array length is insufficient for ByteConverters
     * @see ByteConverters#bytesToIntLE(byte[])
     * @see #checkDecimal(int)
     */
    public static int checkedConversionU32(byte[] input) throws ConversionException.ParseErr {
        int chunk = ByteConverters.bytesToIntLE(input);
        if (checkDecimal(chunk)) {
            return fourToUnsignedInt(chunk);
        } else {
            throw new ConversionException.ParseErr.NonDecimal();
        }
    }

    /**
     * Performs checked conversion from a byte array to a 64-bit unsigned integer.
     *
     * <p>Converts the byte array to a 64-bit value using little-endian byte order,
     * validates that all eight bytes represent decimal digits using {@link #checkDecimal(long)},
     * then converts the digits to a long. Throws {@link ConversionException.ParseErr.NonDecimal}
     * if the input contains non-decimal characters.</p>
     *
     * @param input the byte array containing the eight digits to convert (minimum 8 bytes)
     * @return the converted 64-bit unsigned integer value
     * @throws ConversionException.ParseErr.NonDecimal if the input contains non-decimal characters
     * @throws ArrayIndexOutOfBoundsException if input array length is insufficient for ByteConverters
     * @see ByteConverters#bytesToLongLE(byte[])
     * @see #checkDecimal(long)
     */
    public static long checkedConversionU64(byte[] input) throws ConversionException.ParseErr {
        long chunk = ByteConverters.bytesToLongLE(input);
        if (checkDecimal(chunk)) {
            return eightToUnsignedLong(chunk);
        } else {
            throw new ConversionException.ParseErr.NonDecimal();
        }
    }
}