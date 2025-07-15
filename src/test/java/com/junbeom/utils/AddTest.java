package com.junbeom.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Add Class Tests")
public class AddTest {

    @Test
    @DisplayName("Test add(int, int)")
    void testIntegerAddition() {
        assertEquals(5, Add.add(2, 3), "2 + 3 should equal 5");
        assertEquals(-1, Add.add(-2, 1), "-2 + 1 should equal -1");
        assertEquals(0, Add.add(0, 0), "0 + 0 should equal 0");
    }

    @Test
    @DisplayName("Test add(double, double)")
    void testDoubleAddition() {
        assertEquals(5.5, Add.add(2.2, 3.3), 1e-5, "2.2 + 3.3 should equal 5.5");
        assertEquals(-1.1, Add.add(-2.2, 1.1), 1e-5, "-2.2 + 1.1 should equal -1.1");
        assertEquals(0.0, Add.add(0.0, 0.0), 1e-5, "0.0 + 0.0 should equal 0.0");
    }

    @ParameterizedTest
    @DisplayName("Parameterized test for integer addition")
    @CsvSource({
        "1, 2, 3",
        "10, 20, 30",
        "-5, 5, 0",
        "100, -50, 50"
    })
    void testParameterizedIntegerAddition(int a, int b, int expected) {
        assertEquals(expected, Add.add(a, b), a + " + " + b + " should equal " + expected);
    }

    @Test
    @DisplayName("Test addAll with multiple integers")
    void testVarArgs() {
        assertEquals(15, Add.addAll(1, 2, 3, 4, 5), "1 + 2 + 3 + 4 + 5 should equal 15");
    }
}
