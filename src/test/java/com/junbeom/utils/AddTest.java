package com.junbeom.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

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
}
