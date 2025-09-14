package com.junbeom.conversion;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test suite for ByteConverters utility class.
 * Tests cover basic functionality, edge cases, error conditions, and performance characteristics.
 */
@DisplayName("ByteConverters Tests")
class ByteConvertersTest {

    @Nested
    @DisplayName("Long Conversion Tests")
    class LongConversionTests {

        @Test
        @DisplayName("Should convert single byte to long")
        void testSingleByteToLong() {
            byte[] input = {0x42};
            long result = ByteConverters.bytesToLongLE(input);
            assertEquals(0x42L, result);
        }

        @Test
        @DisplayName("Should convert multiple bytes to long in little-endian order")
        void testMultipleBytesToLong() {
            byte[] input = "4321".getBytes();
            long result = ByteConverters.bytesToLongLE(input);
            // Little-endian: 0x78563412 (padded with leading zeros)
            assertEquals(0x31323334L, result);
        }

        @Test
        @DisplayName("Should handle maximum 8-byte array")
        void testMaxBytesToLong() {
            byte[] input = {0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08};
            long result = ByteConverters.bytesToLongLE(input);
            assertEquals(0x0807060504030201L, result);
        }

        @Test
        @DisplayName("Should handle empty array")
        void testEmptyArrayToLong() {
            byte[] input = {};
            long result = ByteConverters.bytesToLongLE(input);
            assertEquals(0L, result);
        }

        @Test
        @DisplayName("Should throw exception for oversized array")
        void testOversizedArrayToLong() {
            byte[] input = new byte[9]; // Too large
            assertThrows(ArrayIndexOutOfBoundsException.class,
                () -> ByteConverters.bytesToLongLE(input));
        }

        @Test
        @DisplayName("Should throw exception for null input")
        void testNullInputToLong() {
            assertThrows(NullPointerException.class,
                () -> ByteConverters.bytesToLongLE(null));
        }

        @ParameterizedTest
        @MethodSource("longTestCases")
        @DisplayName("Should correctly convert various byte arrays to long")
        void testVariousLongConversions(byte[] input, long expected) {
            assertEquals(expected, ByteConverters.bytesToLongLE(input));
        }

        static Stream<Arguments> longTestCases() {
            return Stream.of(
                Arguments.of(new byte[]{0x00}, 0L),
                Arguments.of(new byte[]{(byte)0xFF}, 0xFFL),
                Arguments.of(new byte[]{0x01, 0x00}, 0x0001L),
                Arguments.of(new byte[]{0x00, 0x01}, 0x0100L),
                Arguments.of(new byte[]{(byte)0xFF, (byte)0xFF}, 0xFFFFL),
                Arguments.of(new byte[]{0x12, 0x34, 0x56}, 0x563412L)
            );
        }
    }

    @Nested
    @DisplayName("Int Conversion Tests")
    class IntConversionTests {

        @Test
        @DisplayName("Should convert single byte to int")
        void testSingleByteToInt() {
            byte[] input = {0x42};
            int result = ByteConverters.bytesToIntLE(input);
            assertEquals(0x42, result);
        }

        @Test
        @DisplayName("Should convert multiple bytes to int in little-endian order")
        void testMultipleBytesToInt() {
            byte[] input = {0x12, 0x34, 0x56, 0x78};
            int result = ByteConverters.bytesToIntLE(input);
            assertEquals(0x78563412, result);
        }

        @Test
        @DisplayName("Should handle empty array")
        void testEmptyArrayToInt() {
            byte[] input = {};
            int result = ByteConverters.bytesToIntLE(input);
            assertEquals(0, result);
        }

        @Test
        @DisplayName("Should throw exception for oversized array")
        void testOversizedArrayToInt() {
            byte[] input = new byte[5]; // Too large
            assertThrows(ArrayIndexOutOfBoundsException.class,
                () -> ByteConverters.bytesToIntLE(input));
        }

        @Test
        @DisplayName("Should throw exception for null input")
        void testNullInputToInt() {
            assertThrows(NullPointerException.class,
                () -> ByteConverters.bytesToIntLE(null));
        }

        @ParameterizedTest
        @MethodSource("intTestCases")
        @DisplayName("Should correctly convert various byte arrays to int")
        void testVariousIntConversions(byte[] input, int expected) {
            assertEquals(expected, ByteConverters.bytesToIntLE(input));
        }

        static Stream<Arguments> intTestCases() {
            return Stream.of(
                Arguments.of(new byte[]{0x00}, 0),
                Arguments.of(new byte[]{(byte)0xFF}, 0xFF),
                Arguments.of(new byte[]{0x01, 0x00}, 0x0001),
                Arguments.of(new byte[]{0x00, 0x01}, 0x0100),
                Arguments.of(new byte[]{(byte)0xFF, (byte)0xFF}, 0xFFFF),
                Arguments.of(new byte[]{0x12, 0x34, 0x56}, 0x563412)
            );
        }
    }

    @Nested
    @DisplayName("Short Conversion Tests")
    class ShortConversionTests {

        @Test
        @DisplayName("Should convert single byte to short")
        void testSingleByteToShort() {
            byte[] input = {0x42};
            short result = ByteConverters.bytesToShortLE(input);
            assertEquals((short)0x42, result);
        }

        @Test
        @DisplayName("Should convert two bytes to short in little-endian order")
        void testTwoBytesToShort() {
            byte[] input = {0x12, 0x34};
            short result = ByteConverters.bytesToShortLE(input);
            assertEquals((short)0x3412, result);
        }

        @Test
        @DisplayName("Should handle empty array")
        void testEmptyArrayToShort() {
            byte[] input = {};
            short result = ByteConverters.bytesToShortLE(input);
            assertEquals((short)0, result);
        }

        @Test
        @DisplayName("Should throw exception for oversized array")
        void testOversizedArrayToShort() {
            byte[] input = new byte[3]; // Too large
            assertThrows(ArrayIndexOutOfBoundsException.class,
                () -> ByteConverters.bytesToShortLE(input));
        }

        @Test
        @DisplayName("Should throw exception for null input")
        void testNullInputToShort() {
            assertThrows(NullPointerException.class,
                () -> ByteConverters.bytesToShortLE(null));
        }

        @ParameterizedTest
        @MethodSource("shortTestCases")
        @DisplayName("Should correctly convert various byte arrays to short")
        void testVariousShortConversions(byte[] input, short expected) {
            assertEquals(expected, ByteConverters.bytesToShortLE(input));
        }

        static Stream<Arguments> shortTestCases() {
            return Stream.of(
                Arguments.of(new byte[]{0x00}, (short)0x00),
                Arguments.of(new byte[]{(byte)0xFF}, (short)0xFF),
                Arguments.of(new byte[]{0x01, 0x00}, (short)0x0001),
                Arguments.of(new byte[]{0x00, 0x01}, (short)0x0100),
                Arguments.of(new byte[]{(byte)0xFF, (byte)0xFF}, (short)0xFFFF)
            );
        }
    }

    @Nested
    @DisplayName("Edge Cases and Performance Tests")
    class EdgeCasesAndPerformanceTests {

        @Test
        @DisplayName("Should handle negative byte values correctly")
        void testNegativeByteValues() {
            byte[] input = {(byte)0x80}; // -128 as signed byte

            long longResult = ByteConverters.bytesToLongLE(input);
            int intResult = ByteConverters.bytesToIntLE(input);
            short shortResult = ByteConverters.bytesToShortLE(input);

            assertEquals(0x80L, longResult);
            assertEquals(0x80, intResult);
            assertEquals((short)0x80, shortResult);
        }

        @Test
        @DisplayName("Should maintain consistency across different methods")
        void testConsistencyAcrossMethods() {
            byte[] input = {0x12, 0x34};

            long longResult = ByteConverters.bytesToLongLE(input);
            int intResult = ByteConverters.bytesToIntLE(input);
            short shortResult = ByteConverters.bytesToShortLE(input);

            assertEquals(longResult, intResult);
            assertEquals(intResult, shortResult);
            assertEquals(0x3412L, longResult);
        }

        @Test
        @DisplayName("Should handle maximum values for each type")
        void testMaximumValues() {
            // Maximum positive values that fit in each type
            byte[] maxLongBytes = {(byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF,
                                  (byte)0xFF, (byte)0xFF, (byte)0xFF, 0x7F}; // Max positive long
            byte[] maxIntBytes = {(byte)0xFF, (byte)0xFF, (byte)0xFF, 0x7F}; // Max positive int
            byte[] maxShortBytes = {(byte)0xFF, 0x7F}; // Max positive short

            long longResult = ByteConverters.bytesToLongLE(maxLongBytes);
            int intResult = ByteConverters.bytesToIntLE(maxIntBytes);
            short shortResult = ByteConverters.bytesToShortLE(maxShortBytes);

            assertEquals(Long.MAX_VALUE, longResult);
            assertEquals(Integer.MAX_VALUE, intResult);
            assertEquals(Short.MAX_VALUE, shortResult);
        }

        @Test
        @DisplayName("Should perform efficiently with repeated calls")
        void testPerformance() {
            byte[] longTestData = {0x12, 0x34, 0x56, 0x78};
            byte[] intTestData = {0x12, 0x34, 0x56, 0x78};
            byte[] shortTestData = {0x12, 0x34};

            long startTime = System.nanoTime();
            for (int i = 0; i < 10000; i++) {
                ByteConverters.bytesToLongLE(longTestData);
                ByteConverters.bytesToIntLE(intTestData);
                ByteConverters.bytesToShortLE(shortTestData);
            }
            long endTime = System.nanoTime();

            long durationMs = (endTime - startTime) / 1_000_000;
            assertTrue(durationMs < 100, "Performance test should complete in under 100ms");
        }
    }
}