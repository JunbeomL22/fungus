package com.junbeom.conversion;

public class FixedSpecExtractor {
    private static final long[] POWERS_OF_TEN = {
        1L, 10L, 100L, 1000L, 10000L, 100000L, 1000000L, 10000000L,
        100000000L, 1000000000L, 10000000000L, 100000000000L,
        1000000000000L, 10000000000000L, 100000000000000L,
        1000000000000000L, 10000000000000000L, 100000000000000000L
    };

    private static final int[] POWERS_OF_TEN_INT = {
        1, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000, 1000000000
    };

    private final int decimalPlaces;

    public FixedSpecExtractor(int decimalPlaces) {
        if (decimalPlaces < 0 || decimalPlaces >= POWERS_OF_TEN.length) {
            throw new IllegalArgumentException("decimalPlaces must be between 0 and " + (POWERS_OF_TEN.length - 1));
        }
        this.decimalPlaces = decimalPlaces;
    }

    /**
     * Converts a string to integer with decimal places processing
     * @param floatStr String to convert (including decimal point)
     * @return Converted integer value
     */
    public int stringToInt(String floatStr) {
        // Original implementation (commented out)
        // int dotPos = floatStr.length() - decimalPlaces - 1;
        // int integerPart = Integer.parseInt(floatStr, 0, dotPos, 10);
        // int fractionalPart = Integer.parseInt(floatStr, dotPos + 1, floatStr.length(), 10);
        // return integerPart * POWERS_OF_TEN_INT[decimalPlaces] + fractionalPart;

        // New implementation using iteration like byteToInt
        // Decimal point position = total length - decimal places - 1
        int dotPos = floatStr.length() - decimalPlaces - 1;

        // Parse integer part
        int integerPart = 0;
        for (int i = 0; i < dotPos; i++) {
            integerPart = integerPart * 10 + (floatStr.charAt(i) - '0');
        }

        // Parse fractional part (skip dot at dotPos)
        int fractionalPart = 0;
        for (int i = dotPos + 1; i < floatStr.length(); i++) {
            fractionalPart = fractionalPart * 10 + (floatStr.charAt(i) - '0');
        }

        return integerPart * POWERS_OF_TEN_INT[decimalPlaces] + fractionalPart;
    }

    /**
     * Converts a string to long with decimal places processing
     * @param floatStr String to convert (including decimal point)
     * @return Converted long value
     */
    public long stringToLong(String floatStr) {
        // Original implementation (commented out)
        // int dotPos = floatStr.length() - decimalPlaces - 1;
        // long integerPart = Long.parseLong(floatStr, 0, dotPos, 10);
        // long fractionalPart = Long.parseLong(floatStr, dotPos + 1, floatStr.length(), 10);
        // return integerPart * POWERS_OF_TEN[decimalPlaces] + fractionalPart;

        // New implementation using iteration like byteToLong
        // Decimal point position = total length - decimal places - 1
        int dotPos = floatStr.length() - decimalPlaces - 1;

        // Parse integer part
        long integerPart = 0;
        for (int i = 0; i < dotPos; i++) {
            integerPart = integerPart * 10 + (floatStr.charAt(i) - '0');
        }

        // Parse fractional part (skip dot at dotPos)
        long fractionalPart = 0;
        for (int i = dotPos + 1; i < floatStr.length(); i++) {
            fractionalPart = fractionalPart * 10 + (floatStr.charAt(i) - '0');
        }

        return integerPart * POWERS_OF_TEN[decimalPlaces] + fractionalPart;
    }

    /**
     * Converts byte array to integer with decimal places processing
     * @param bytes Byte array to convert
     * @return Converted integer value
     */
    public int byteToInt(byte[] bytes) {
        // Decimal point position = total length - decimal places - 1
        int dotPos = bytes.length - decimalPlaces - 1;

        // Parse integer part
        int integerPart = 0;
        for (int i = 0; i < dotPos; i++) {
            integerPart = integerPart * 10 + (bytes[i] - '0');
        }

        // Parse fractional part (skip dot at dotPos)
        int fractionalPart = 0;
        for (int i = dotPos + 1; i < bytes.length; i++) {
            fractionalPart = fractionalPart * 10 + (bytes[i] - '0');
        }

        return integerPart * POWERS_OF_TEN_INT[decimalPlaces] + fractionalPart;
    }

    /**
     * Converts byte array to long with decimal places processing
     * @param bytes Byte array to convert
     * @return Converted long value
     */
    public long byteToLong(byte[] bytes) {
        // Decimal point position = total length - decimal places - 1
        int dotPos = bytes.length - decimalPlaces - 1;

        // Parse integer part
        long integerPart = 0;
        for (int i = 0; i < dotPos; i++) {
            integerPart = integerPart * 10 + (bytes[i] - '0');
        }

        // Parse fractional part (skip dot at dotPos)
        long fractionalPart = 0;
        for (int i = dotPos + 1; i < bytes.length; i++) {
            fractionalPart = fractionalPart * 10 + (bytes[i] - '0');
        }

        return integerPart * POWERS_OF_TEN[decimalPlaces] + fractionalPart;
    }

    public int getDecimalPlaces() {
        return decimalPlaces;
    }
}
