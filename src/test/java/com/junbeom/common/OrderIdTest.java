package com.junbeom.common;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("OrderId Class Tests")
public class OrderIdTest {

    @Test
    @DisplayName("Test OrderId creation with valid venue and orderId")
    void testOrderIdCreation() {
        OrderId orderId = new OrderId(1L, 1000L);
        assertEquals(1000L, orderId.orderId());
        assertEquals("krxDrv", orderId.venueName());
    }

    @Test
    @DisplayName("Test OrderId creation with dummy venue")
    void testDummyVenue() {
        OrderId orderId = new OrderId(0L, 500L);
        assertEquals(500L, orderId.orderId());
        assertEquals("dummy", orderId.venueName());
    }

    @Test
    @DisplayName("Test OrderId creation with different venues")
    void testDifferentVenues() {
        OrderId orderId1 = new OrderId(1L, 100L);
        assertEquals("krxDrv", orderId1.venueName());

        OrderId orderId2 = new OrderId(2L, 200L);
        assertEquals("krxKts", orderId2.venueName());

        OrderId orderId3 = new OrderId(3L, 300L);
        assertEquals("smb", orderId3.venueName());
    }

    @Test
    @DisplayName("Test OrderId creation with invalid venue")
    void testInvalidVenue() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new OrderId(256L, 100L)
        );
        assertEquals("Venue must be between 0 and 255", exception.getMessage());
    }

    @ParameterizedTest
    @DisplayName("Parameterized test for venue names")
    @CsvSource({
        "0, dummy",
        "1, krxDrv",
        "2, krxKts",
        "3, smb"
    })
    void testVenueNames(Long venue, String expectedName) {
        OrderId orderId = new OrderId(venue, 100L);
        assertEquals(expectedName, orderId.venueName());
    }

    @Test
    @DisplayName("Test orderId extraction")
    void testOrderIdExtraction() {
        OrderId orderId = new OrderId(2L, 0xFFFFFFFFFFFFL); // Max 56-bit value
        assertEquals(0xFFFFFFFFFFFFL, orderId.orderId());
    }
}
