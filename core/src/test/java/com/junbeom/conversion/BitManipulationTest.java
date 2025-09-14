package com.junbeom.conversion;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigInteger;

@DisplayName("BitManipulation Tests")
class BitManipulationTest {

    @Nested
    @DisplayName("Two Bytes to Unsigned Short Tests")
    class TwoToUnsignedShortTests {

        @Test
        @DisplayName("Basic decimal conversion")
        void testBasicDecimalConversion() {
            // Test digits 1,2 -> 0x0201 -> 12 (lower nibbles: 1*10 + 2)
            short chunk = (short) 0x0201;
            assertEquals(12, BitManipulation.twoToUnsignedShort(chunk));
        }

        @Test
        @DisplayName("Single digit conversion")
        void testSingleDigitConversion() {
            // Test digits 0,5 -> 0x0500 -> 5
            short chunk = (short) 0x0500;
            assertEquals(5, BitManipulation.twoToUnsignedShort(chunk));
        }

        @Test
        @DisplayName("Zero conversion")
        void testZeroConversion() {
            // Test digits 0,0 -> 0x0000 -> 0
            short chunk = (short) 0x0000;
            assertEquals(0, BitManipulation.twoToUnsignedShort(chunk));
        }

        @Test
        @DisplayName("Maximum two-digit number")
        void testMaxTwoDigit() {
            // Test digits 9,9 -> 0x0909 -> 99
            short chunk = (short) 0x0909;
            assertEquals(99, BitManipulation.twoToUnsignedShort(chunk));
        }
    }

    @Nested
    @DisplayName("Four Bytes to Unsigned Int Tests")
    class FourToUnsignedIntTests {

        @Test
        @DisplayName("Basic four-digit conversion")
        void testBasicFourDigitConversion() {
            // Test digits 1,2,3,4 -> 0x04030201 -> 1234
            int chunk = 0x04030201;
            assertEquals(1234, BitManipulation.fourToUnsignedInt(chunk));
        }

        @Test
        @DisplayName("Zero padding conversion")
        void testZeroPaddingConversion() {
            // Test digits 0,1,2,3 -> 0x03020100 -> 123
            int chunk = 0x03020100;
            assertEquals(123, BitManipulation.fourToUnsignedInt(chunk));
        }

        @Test
        @DisplayName("All zeros conversion")
        void testAllZerosConversion() {
            // Test digits 0,0,0,0 -> 0x00000000 -> 0
            int chunk = 0x00000000;
            assertEquals(0, BitManipulation.fourToUnsignedInt(chunk));
        }

        @Test
        @DisplayName("Maximum four-digit number")
        void testMaxFourDigit() {
            // Test digits 9,9,9,9 -> 0x09090909 -> 9999
            int chunk = 0x09090909;
            assertEquals(9999, BitManipulation.fourToUnsignedInt(chunk));
        }
    }

    @Nested
    @DisplayName("Eight Bytes to Unsigned Long Tests")
    class EightToUnsignedLongTests {

        @Test
        @DisplayName("Basic eight-digit conversion")
        void testBasicEightDigitConversion() {
            // Test digits 1,2,3,4,5,6,7,8 -> 12345678
            long chunk = 0x0807060504030201L;
            assertEquals(12345678L, BitManipulation.eightToUnsignedLong(chunk));
        }

        @Test
        @DisplayName("Zero padding conversion")
        void testZeroPaddingConversion() {
            // Test digits 0,0,0,0,1,2,3,4 -> 1234
            long chunk = 0x0403020100000000L;
            assertEquals(1234L, BitManipulation.eightToUnsignedLong(chunk));
        }

        @Test
        @DisplayName("All zeros conversion")
        void testAllZerosConversion() {
            // Test digits 0,0,0,0,0,0,0,0 -> 0
            long chunk = 0x0000000000000000L;
            assertEquals(0L, BitManipulation.eightToUnsignedLong(chunk));
        }

        @Test
        @DisplayName("Maximum eight-digit number")
        void testMaxEightDigit() {
            // Test digits 9,9,9,9,9,9,9,9 -> 99999999
            long chunk = 0x0909090909090909L;
            assertEquals(99999999L, BitManipulation.eightToUnsignedLong(chunk));
        }
    }

    @Nested
    @DisplayName("Check Decimal Tests")
    class CheckDecimalTests {

        @Nested
        @DisplayName("Short (16-bit) Tests")
        class CheckDecimalShortTests {

            @Test
            @DisplayName("Valid decimal digits")
            void testValidDecimalsShort() {
                // Test '31' -> 0x3131 (both '3' and '1' are decimal digits)
                short chunk = (short) 0x3131;
                assertTrue(BitManipulation.checkDecimal(chunk));
            }

            @Test
            @DisplayName("Valid single digit pairs")
            void testSingleDigitPairsShort() {
                // Test various single digit pairs
                assertTrue(BitManipulation.checkDecimal((short) 0x3030)); // "00"
                assertTrue(BitManipulation.checkDecimal((short) 0x3939)); // "99"
                assertTrue(BitManipulation.checkDecimal((short) 0x3139)); // "19"
                assertTrue(BitManipulation.checkDecimal((short) 0x3931)); // "91"
            }

            @Test
            @DisplayName("Invalid decimal digits")
            void testInvalidDecimalsShort() {
                // Test with non-decimal characters
                assertFalse(BitManipulation.checkDecimal((short) 0x4141)); // "AA" (letters)
                assertFalse(BitManipulation.checkDecimal((short) 0x2020)); // "  " (spaces)
                assertFalse(BitManipulation.checkDecimal((short) 0x3A3A)); // "99" but 0x3A is ':'
                assertFalse(BitManipulation.checkDecimal((short) 0x2F2F)); // "99" but 0x2F is '/'
            }

            @Test
            @DisplayName("Mixed valid and invalid")
            void testMixedInvalidShort() {
                // Test one valid, one invalid digit
                assertFalse(BitManipulation.checkDecimal((short) 0x3141)); // "1A"
                assertFalse(BitManipulation.checkDecimal((short) 0x4131)); // "A1"
            }
        }

        @Nested
        @DisplayName("Int (32-bit) Tests")
        class CheckDecimalIntTests {

            @Test
            @DisplayName("Valid decimal digits")
            void testValidDecimalsInt() {
                // Test '1234' -> 0x34333231 (all decimal digits)
                int chunk = 0x34333231;
                assertTrue(BitManipulation.checkDecimal(chunk));
            }

            @Test
            @DisplayName("Valid four-digit sequences")
            void testFourDigitSequencesInt() {
                // Test various four-digit sequences
                assertTrue(BitManipulation.checkDecimal(0x30303030)); // "0000"
                assertTrue(BitManipulation.checkDecimal(0x39393939)); // "9999"
                assertTrue(BitManipulation.checkDecimal(0x31323334)); // "1234"
                assertTrue(BitManipulation.checkDecimal(0x39383736)); // "9876"
            }

            @Test
            @DisplayName("Invalid decimal digits")
            void testInvalidDecimalsInt() {
                // Test with non-decimal characters
                assertFalse(BitManipulation.checkDecimal(0x31314141)); // "11AA"
                assertFalse(BitManipulation.checkDecimal(0x20202020)); // "    "
                assertFalse(BitManipulation.checkDecimal(0x313A3A3A)); // "1:::"
                assertFalse(BitManipulation.checkDecimal(0x2F2F2F2F)); // "////"
            }

            @Test
            @DisplayName("Mixed valid and invalid")
            void testMixedInvalidInt() {
                // Test with some valid, some invalid digits
                assertFalse(BitManipulation.checkDecimal(0x31323341)); // "123A"
                assertFalse(BitManipulation.checkDecimal(0x41323334)); // "A123"
                assertFalse(BitManipulation.checkDecimal(0x31394131)); // "19A1"
            }
        }

        @Nested
        @DisplayName("Long (64-bit) Tests")
        class CheckDecimalLongTests {

            @Test
            @DisplayName("Valid decimal digits")
            void testValidDecimalsLong() {
                // Test '12345678' -> 0x3837363534333231 (all decimal digits)
                long chunk = 0x3837363534333231L;
                assertTrue(BitManipulation.checkDecimal(chunk));
            }

            @Test
            @DisplayName("Valid eight-digit sequences")
            void testEightDigitSequencesLong() {
                // Test various eight-digit sequences
                assertTrue(BitManipulation.checkDecimal(0x3030303030303030L)); // "00000000"
                assertTrue(BitManipulation.checkDecimal(0x3939393939393939L)); // "99999999"
                assertTrue(BitManipulation.checkDecimal(0x3132333435363738L)); // "12345678"
                assertTrue(BitManipulation.checkDecimal(0x3938373635343332L)); // "98765432"
            }

            @Test
            @DisplayName("Invalid decimal digits")
            void testInvalidDecimalsLong() {
                // Test with non-decimal characters
                assertFalse(BitManipulation.checkDecimal(0x4141414141414141L)); // "AAAAAAAA"
                assertFalse(BitManipulation.checkDecimal(0x2020202020202020L)); // "        "
                assertFalse(BitManipulation.checkDecimal(0x3A3A3A3A3A3A3A3AL)); // "::::::::"
                assertFalse(BitManipulation.checkDecimal(0x2F2F2F2F2F2F2F2FL)); // "////////"
            }

            @Test
            @DisplayName("Mixed valid and invalid")
            void testMixedInvalidLong() {
                // Test with some valid, some invalid digits
                assertFalse(BitManipulation.checkDecimal(0x3132333435363741L)); // "1234567A"
                assertFalse(BitManipulation.checkDecimal(0x4132333435363738L)); // "A1234567"
                assertFalse(BitManipulation.checkDecimal(0x3139383736353441L)); // "1987654A"
            }
        }

        @Test
        @DisplayName("Performance test - multiple calls")
        void testPerformanceMultipleCalls() {
            // Test that static constants are reused efficiently
            short shortChunk = (short) 0x3131;
            int intChunk = 0x31323334;
            long longChunk = 0x3132333435363738L;

            // Multiple calls should use cached constants and be fast
            for (int i = 0; i < 1000; i++) {
                assertTrue(BitManipulation.checkDecimal(shortChunk));
                assertTrue(BitManipulation.checkDecimal(intChunk));
                assertTrue(BitManipulation.checkDecimal(longChunk));
            }
        }

        @Test
        @DisplayName("Bit manipulation correctness verification")
        void testBitManipulationCorrectness() {
            // Verify that the bit manipulation logic correctly identifies decimal digits
            // The logic checks: each byte is between '0' (0x30) and '9' (0x39)

            // Test exact boundaries
            assertTrue(BitManipulation.checkDecimal((short) 0x3030)); // "00" - should pass
            assertTrue(BitManipulation.checkDecimal((short) 0x3939)); // "99" - should pass

            // Test just below '0'
            assertFalse(BitManipulation.checkDecimal((short) 0x2F2F)); // "//" - should fail

            // Test just above '9'
            assertFalse(BitManipulation.checkDecimal((short) 0x3A3A)); // "::" - should fail
        }
    }

    @Nested
    @DisplayName("Checked Conversion Tests")
    class CheckedConversionTests {

        @Nested
        @DisplayName("Checked Conversion U8 Tests")
        class CheckedConversionU8Tests {

            @Test
            @DisplayName("Valid decimal digit conversion")
            void testValidDecimalConversionU8() throws ConversionException.ParseErr {
                byte[] input = {'5'};
                assertEquals(5, BitManipulation.checkedConversionU8(input));
            }

            @Test
            @DisplayName("Zero digit conversion")
            void testZeroDigitConversionU8() throws ConversionException.ParseErr {
                byte[] input = {'0'};
                assertEquals(0, BitManipulation.checkedConversionU8(input));
            }

            @Test
            @DisplayName("Maximum digit conversion")
            void testMaxDigitConversionU8() throws ConversionException.ParseErr {
                byte[] input = {'9'};
                assertEquals(9, BitManipulation.checkedConversionU8(input));
            }

            @Test
            @DisplayName("Invalid digit throws exception")
            void testNonDecimalThrowsExceptionU8() {
                byte[] input = {'A'};
                assertThrows(ConversionException.ParseErr.NonDecimal.class,
                    () -> BitManipulation.checkedConversionU8(input));
            }

            @Test
            @DisplayName("Non-decimal character throws exception")
            void testNonDecimalCharacterThrowsExceptionU8() {
                byte[] input = {0x2F}; // '/'
                assertThrows(ConversionException.ParseErr.NonDecimal.class,
                    () -> BitManipulation.checkedConversionU8(input));
            }

            @Test
            @DisplayName("Character above '9' throws exception")
            void testCharacterAboveNineThrowsExceptionU8() {
                byte[] input = {0x3A}; // ':'
                assertThrows(ConversionException.ParseErr.NonDecimal.class,
                    () -> BitManipulation.checkedConversionU8(input));
            }

            @Test
            @DisplayName("Character below '0' throws exception")
            void testCharacterBelowZeroThrowsExceptionU8() {
                byte[] input = {' '};
                assertThrows(ConversionException.ParseErr.NonDecimal.class,
                    () -> BitManipulation.checkedConversionU8(input));
            }
        }

        @Nested
        @DisplayName("Checked Conversion U16 Tests")
        class CheckedConversionU16Tests {

            @Test
            @DisplayName("Valid two-digit number conversion")
            void testValidTwoDigitConversionU16() throws ConversionException.ParseErr {
                byte[] input = "12".getBytes();
                assertEquals(12, BitManipulation.checkedConversionU16(input));
            }

            @Test
            @DisplayName("Zero padding conversion")
            void testZeroPaddingConversionU16() throws ConversionException.ParseErr {
                byte[] input = {'0', '0'}; // "00" -> 0
                assertEquals(0, BitManipulation.checkedConversionU16(input));
            }

            @Test
            @DisplayName("Maximum two-digit conversion")
            void testMaxTwoDigitConversionU16() throws ConversionException.ParseErr {
                byte[] input = {'9', '9'}; // "99" -> 99
                assertEquals(99, BitManipulation.checkedConversionU16(input));
            }

            @Test
            @DisplayName("Mixed valid digits conversion")
            void testMixedValidDigitsConversionU16() throws ConversionException.ParseErr {
                byte[] input = {'4', '2'}; // "24" -> 42
                assertEquals(42, BitManipulation.checkedConversionU16(input));
            }

            @Test
            @DisplayName("Invalid digit throws exception")
            void testNonDecimalThrowsExceptionU16() throws ConversionException.ParseErr {
                byte[] input = {'1', 'A'};
                assertThrows(ConversionException.ParseErr.NonDecimal.class,
                    () -> BitManipulation.checkedConversionU16(input));
            }

            @Test
            @DisplayName("Non-decimal character throws exception")
            void testNonDecimalCharacterThrowsExceptionU16() throws ConversionException.ParseErr {
                byte[] input = "1A".getBytes();
                assertThrows(ConversionException.ParseErr.NonDecimal.class,
                    () -> BitManipulation.checkedConversionU16(input));
            }
        }

        @Nested
        @DisplayName("Checked Conversion U32 Tests")
        class CheckedConversionU32Tests {

            @Test
            @DisplayName("Valid four-digit number conversion")
            void testValidFourDigitConversionU32() throws ConversionException.ParseErr {
                byte[] input = {'1', '2', '3', '4'}; // "4321" in little-endian -> 1234
                assertEquals(1234, BitManipulation.checkedConversionU32(input));
            }

            @Test
            @DisplayName("Zero padding conversion")
            void testZeroPaddingConversionU32() throws ConversionException.ParseErr {
                byte[] input = {'1', '2', '3', '4'}; // "4321" in little-endian -> 1234
                assertEquals(1234, BitManipulation.checkedConversionU32(input));
            }

            @Test
            @DisplayName("Maximum four-digit conversion")
            void testMaxFourDigitConversionU32() throws ConversionException.ParseErr {
                byte[] input = {'9', '9', '9', '9'}; // "9999" -> 9999
                assertEquals(9999, BitManipulation.checkedConversionU32(input));
            }

            @Test
            @DisplayName("Mixed valid digits conversion")
            void testMixedValidDigitsConversionU32() throws ConversionException.ParseErr {
                byte[] input = {0x32, 0x33, 0x34, 0x35}; // "2345" -> should convert to 2345
                assertEquals(2345, BitManipulation.checkedConversionU32(input));
            }

            @Test
            @DisplayName("Invalid digit throws exception")
            void testNonDecimalThrowsExceptionU32() {
                byte[] input = {'1', '2', '3', 'A'};
                assertThrows(ConversionException.ParseErr.NonDecimal.class,
                    () -> BitManipulation.checkedConversionU32(input));
            }

            @Test
            @DisplayName("Non-decimal character throws exception")
            void testNonDecimalCharacterThrowsExceptionU32() throws ConversionException.ParseErr {
                byte[] input = "123:".getBytes();
                assertThrows(ConversionException.ParseErr.NonDecimal.class,
                    () -> BitManipulation.checkedConversionU32(input));
            }
        }

        @Nested
        @DisplayName("Checked Conversion U64 Tests")
        class CheckedConversionU64Tests {

            @Test
            @DisplayName("Valid eight-digit number conversion")
            void testValidEightDigitConversionU64() throws ConversionException.ParseErr {
                byte[] input = "12345678".getBytes();
                assertEquals(12345678L, BitManipulation.checkedConversionU64(input));
            }

            @Test
            @DisplayName("Zero padding conversion")
            void testZeroPaddingConversionU64() throws ConversionException.ParseErr {
                byte[] input = {'0', '0', '1', '2', '3', '4', '5', '6'}; // "65432100" in little-endian -> 123456
                assertEquals(123456L, BitManipulation.checkedConversionU64(input));
            }

            @Test
            @DisplayName("Maximum eight-digit conversion")
            void testMaxEightDigitConversionU64() throws ConversionException.ParseErr {
                byte[] input = {'9', '9', '9', '9', '9', '9', '9', '9'}; // "99999999" -> 99999999
                assertEquals(99999999L, BitManipulation.checkedConversionU64(input));
            }

            @Test
            @DisplayName("Mixed valid digits conversion")
            void testMixedValidDigitsConversionU64() throws ConversionException.ParseErr {
                byte[] input = "12345678".getBytes();
                assertEquals(12345678L, BitManipulation.checkedConversionU64(input));
            }

            @Test
            @DisplayName("Invalid digit throws exception")
            void testNonDecimalThrowsExceptionU64() {
                byte[] input = {'1', '2', '3', '4', '5', '6', '7', 'A'};
                assertThrows(ConversionException.ParseErr.NonDecimal.class,
                    () -> BitManipulation.checkedConversionU64(input));
            }

            @Test
            @DisplayName("Non-decimal character throws exception")
            void testNonDecimalCharacterThrowsExceptionU64() throws ConversionException.ParseErr {
                byte[] input = "1234567:".getBytes();
                assertThrows(ConversionException.ParseErr.NonDecimal.class,
                    () -> BitManipulation.checkedConversionU64(input));
            }
        }

        @Nested
        @DisplayName("Edge Cases and Performance Tests")
        class EdgeCaseTests {

        @Test
        @DisplayName("Verify bit manipulation correctness")
        void testBitManipulationCorrectness() {
            // Test that our bit manipulation matches expected decimal conversion
            // For digits 4,2 -> 0x0204 -> should extract 4 and 2, then compute 4*10 + 2 = 42
            short chunk = (short) 0x0204; // digit 4 in upper nibble, digit 2 in lower nibble
            int lowerDigit = (chunk & 0x000f); // Should be 4
            int upperDigit = ((chunk & 0x0f00) >>> 8); // Should be 2
            assertEquals(4, lowerDigit);
            assertEquals(2, upperDigit);
            assertEquals(42, BitManipulation.twoToUnsignedShort(chunk));
        }

        @Test
        @DisplayName("Performance test - multiple calls")
        void testPerformanceMultipleCalls() {
            // Test that static constants are reused efficiently across different methods
            short shortChunk = (short) 0x3131;
            int intChunk = 0x31323334;
            long longChunk = 0x3132333435363738L;

            // Multiple calls should use cached constants
            for (int i = 0; i < 100; i++) {
                assertTrue(BitManipulation.checkDecimal(shortChunk));
                assertTrue(BitManipulation.checkDecimal(intChunk));
                assertTrue(BitManipulation.checkDecimal(longChunk));
            }
        }

        @Test
        @DisplayName("Performance test - multiple checked conversion calls")
        void testPerformanceMultipleCheckedConversionCalls() throws ConversionException.ParseErr {
            // Test that checked conversion methods work efficiently
            byte[] inputU8 = {'5'};
            byte[] inputU16 = {'5', '0'}; // "05" -> 50
            byte[] inputU32 = {0x30, 0x30, 0x30, 0x35}; // ASCII "5000" -> should convert to 5
            byte[] inputU64 = {0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x35}; // ASCII "50000000" -> should convert to 5

            // Multiple calls should be fast
            for (int i = 0; i < 1000; i++) {
                assertEquals(5, BitManipulation.checkedConversionU8(inputU8));
                assertEquals(50, BitManipulation.checkedConversionU16(inputU16));
                assertEquals(5, BitManipulation.checkedConversionU32(inputU32));
                assertEquals(5L, BitManipulation.checkedConversionU64(inputU64));
            }
        }
        }
    }
}