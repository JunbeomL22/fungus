package com.junbeom.conversion;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("FixedSpecExtractor Tests")
class FixedSpecExtractorTest {

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create extractor with valid decimal places")
        void shouldCreateExtractorWithValidDecimalPlaces() {
            assertDoesNotThrow(() -> new FixedSpecExtractor(2));
            assertDoesNotThrow(() -> new FixedSpecExtractor(0));
            assertDoesNotThrow(() -> new FixedSpecExtractor(17));
        }

        @Test
        @DisplayName("Should throw exception for invalid decimal places")
        void shouldThrowExceptionForInvalidDecimalPlaces() {
            assertThrows(IllegalArgumentException.class, () -> new FixedSpecExtractor(-1));
            assertThrows(IllegalArgumentException.class, () -> new FixedSpecExtractor(18));
        }

        @Test
        @DisplayName("Should return correct decimal places")
        void shouldReturnCorrectDecimalPlaces() {
            FixedSpecExtractor extractor = new FixedSpecExtractor(3);
            assertEquals(3, extractor.getDecimalPlaces());
        }
    }

    @Nested
    @DisplayName("String to Integer Conversion")
    class StringToIntTests {

        @Test
        @DisplayName("Should convert basic decimal strings")
        void shouldConvertBasicDecimalStrings() {
            FixedSpecExtractor extractor = new FixedSpecExtractor(2);
            assertEquals(12345, extractor.stringToInt("123.45"));
            assertEquals(100500, extractor.stringToInt("1005.00"));
            assertEquals(9876543, extractor.stringToInt("98765.43"));
        }

        @Test
        @DisplayName("Should handle zero decimal places")
        void shouldHandleZeroDecimalPlaces() {
            FixedSpecExtractor extractor = new FixedSpecExtractor(1);
            assertEquals(1230, extractor.stringToInt("123.0"));
            assertEquals(98760, extractor.stringToInt("9876.0"));
        }

        @Test
        @DisplayName("Should handle single digit values")
        void shouldHandleSingleDigitValues() {
            FixedSpecExtractor extractor1 = new FixedSpecExtractor(1);
            FixedSpecExtractor extractor2 = new FixedSpecExtractor(2);
            assertEquals(12, extractor1.stringToInt("1.2"));
            assertEquals(500, extractor2.stringToInt("5.00"));
        }

        @ParameterizedTest
        @MethodSource("stringToIntTestCases")
        @DisplayName("Should convert various string formats correctly")
        void shouldConvertVariousStringFormats(String input, int decimalPlaces, int expected) {
            FixedSpecExtractor extractor = new FixedSpecExtractor(decimalPlaces);
            assertEquals(expected, extractor.stringToInt(input));
        }

        static Stream<Arguments> stringToIntTestCases() {
            return Stream.of(
                Arguments.of("0.01", 2, 1),
                Arguments.of("0.99", 2, 99),
                Arguments.of("10.5", 1, 105),
                Arguments.of("999.999", 3, 999999),
                Arguments.of("1.0", 1, 10),
                Arguments.of("123.4567", 4, 1234567)
            );
        }

        @Test
        @DisplayName("Should handle edge case with maximum integer values")
        void shouldHandleMaxIntegerValues() {
            FixedSpecExtractor extractor = new FixedSpecExtractor(2);
            assertEquals(214748364, extractor.stringToInt("2147483.64"));
        }
    }

    @Nested
    @DisplayName("String to Long Conversion")
    class StringToLongTests {

        @Test
        @DisplayName("Should convert basic decimal strings")
        void shouldConvertBasicDecimalStrings() {
            FixedSpecExtractor extractor = new FixedSpecExtractor(2);
            assertEquals(12345L, extractor.stringToLong("123.45"));
            assertEquals(987654321L, extractor.stringToLong("9876543.21"));
        }

        @Test
        @DisplayName("Should handle large values")
        void shouldHandleLargeValues() {
            FixedSpecExtractor extractor = new FixedSpecExtractor(2);
            assertEquals(92233720368547758L, extractor.stringToLong("922337203685477.58"));
            assertEquals(1000000000000000L, extractor.stringToLong("10000000000000.00"));
        }

        @ParameterizedTest
        @MethodSource("stringToLongTestCases")
        @DisplayName("Should convert various long string formats correctly")
        void shouldConvertVariousLongFormats(String input, int decimalPlaces, long expected) {
            FixedSpecExtractor extractor = new FixedSpecExtractor(decimalPlaces);
            assertEquals(expected, extractor.stringToLong(input));
        }

        static Stream<Arguments> stringToLongTestCases() {
            return Stream.of(
                Arguments.of("0.001", 3, 1L),
                Arguments.of("123456789.123456789", 9, 123456789123456789L),
                Arguments.of("1.0000000000000000", 16, 10000000000000000L),
                Arguments.of("999999.999999", 6, 999999999999L)
            );
        }
    }

    @Nested
    @DisplayName("Byte Array to Integer Conversion")
    class ByteToIntTests {

        @Test
        @DisplayName("Should convert basic byte arrays")
        void shouldConvertBasicByteArrays() {
            FixedSpecExtractor extractor = new FixedSpecExtractor(2);
            byte[] bytes = "123.45".getBytes(StandardCharsets.UTF_8);
            assertEquals(12345, extractor.byteToInt(bytes));
        }

        @Test
        @DisplayName("Should handle zero decimal places")
        void shouldHandleZeroDecimalPlaces() {
            FixedSpecExtractor extractor = new FixedSpecExtractor(1);
            byte[] bytes = "987.0".getBytes(StandardCharsets.UTF_8);
            assertEquals(9870, extractor.byteToInt(bytes));
        }

        @Test
        @DisplayName("Should handle single digits")
        void shouldHandleSingleDigits() {
            FixedSpecExtractor extractor = new FixedSpecExtractor(1);
            byte[] bytes = "5.7".getBytes(StandardCharsets.UTF_8);
            assertEquals(57, extractor.byteToInt(bytes));
        }

        @ParameterizedTest
        @MethodSource("byteToIntTestCases")
        @DisplayName("Should convert various byte array formats correctly")
        void shouldConvertVariousByteFormats(String input, int decimalPlaces, int expected) {
            FixedSpecExtractor extractor = new FixedSpecExtractor(decimalPlaces);
            byte[] bytes = input.getBytes(StandardCharsets.UTF_8);
            assertEquals(expected, extractor.byteToInt(bytes));
        }

        static Stream<Arguments> byteToIntTestCases() {
            return Stream.of(
                Arguments.of("0.01", 2, 1),
                Arguments.of("999.999", 3, 999999),
                Arguments.of("1.23456", 5, 123456),
                Arguments.of("42.0", 1, 420)
            );
        }
    }

    @Nested
    @DisplayName("Byte Array to Long Conversion")
    class ByteToLongTests {

        @Test
        @DisplayName("Should convert basic byte arrays")
        void shouldConvertBasicByteArrays() {
            FixedSpecExtractor extractor = new FixedSpecExtractor(3);
            byte[] bytes = "123456.789".getBytes(StandardCharsets.UTF_8);
            assertEquals(123456789L, extractor.byteToLong(bytes));
        }

        @Test
        @DisplayName("Should handle large values")
        void shouldHandleLargeValues() {
            FixedSpecExtractor extractor = new FixedSpecExtractor(2);
            byte[] bytes = "123456789012345.67".getBytes(StandardCharsets.UTF_8);
            assertEquals(12345678901234567L, extractor.byteToLong(bytes));
        }

        @ParameterizedTest
        @MethodSource("byteToLongTestCases")
        @DisplayName("Should convert various long byte array formats correctly")
        void shouldConvertVariousLongByteFormats(String input, int decimalPlaces, long expected) {
            FixedSpecExtractor extractor = new FixedSpecExtractor(decimalPlaces);
            byte[] bytes = input.getBytes(StandardCharsets.UTF_8);
            assertEquals(expected, extractor.byteToLong(bytes));
        }

        static Stream<Arguments> byteToLongTestCases() {
            return Stream.of(
                Arguments.of("0.0000001", 7, 1L),
                Arguments.of("987654321.987654321", 9, 987654321987654321L),
                Arguments.of("1000000.000001", 6, 1000000000001L)
            );
        }
    }

    @Nested
    @DisplayName("Edge Cases and Error Handling")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle minimum valid decimal places")
        void shouldHandleMinimumDecimalPlaces() {
            FixedSpecExtractor extractor = new FixedSpecExtractor(1);
            assertEquals(1230, extractor.stringToInt("123.0"));
            assertEquals(1230L, extractor.stringToLong("123.0"));
        }

        @Test
        @DisplayName("Should handle maximum decimal places within array bounds")
        void shouldHandleMaximumDecimalPlaces() {
            FixedSpecExtractor extractor = new FixedSpecExtractor(17);
            String input = "1.12345678901234567";
            assertEquals(112345678901234567L, extractor.stringToLong(input));
        }

        @Test
        @DisplayName("Should work correctly with all valid decimal places")
        void shouldWorkCorrectlyWithAllValidDecimalPlaces() {
            for (int i = 0; i < 18; i++) {
                final int decimalPlaces = i;
                assertDoesNotThrow(() -> new FixedSpecExtractor(decimalPlaces));
            }
        }

        @Test
        @DisplayName("Should handle empty fractional parts correctly")
        void shouldHandleEmptyFractionalParts() {
            FixedSpecExtractor extractor = new FixedSpecExtractor(2);
            assertEquals(12300, extractor.stringToInt("123.00"));
            assertEquals(50000L, extractor.stringToLong("500.00"));
        }
    }

    @Nested
    @DisplayName("Consistency Between String and Byte Methods")
    class ConsistencyTests {

        @ParameterizedTest
        @MethodSource("consistencyTestCases")
        @DisplayName("String and byte methods should produce identical results")
        void stringAndByteMethodsShouldProduceIdenticalResults(String input, int decimalPlaces) {
            FixedSpecExtractor extractor = new FixedSpecExtractor(decimalPlaces);
            byte[] bytes = input.getBytes(StandardCharsets.UTF_8);

            int stringIntResult = extractor.stringToInt(input);
            int byteIntResult = extractor.byteToInt(bytes);
            assertEquals(stringIntResult, byteIntResult,
                "String and byte int conversion should match for: " + input);

            long stringLongResult = extractor.stringToLong(input);
            long byteLongResult = extractor.byteToLong(bytes);
            assertEquals(stringLongResult, byteLongResult,
                "String and byte long conversion should match for: " + input);
        }

        static Stream<Arguments> consistencyTestCases() {
            return Stream.of(
                Arguments.of("123.45", 2),
                Arguments.of("0.01", 2),
                Arguments.of("999.999", 3),
                Arguments.of("1.0", 1),
                Arguments.of("5678.1234", 4),
                Arguments.of("100.0", 1)
            );
        }
    }
}