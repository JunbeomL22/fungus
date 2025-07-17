package com.junbeom.common;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;
import static com.junbeom.common.OrderId.venueMask;
import static com.junbeom.common.OrderId.venueShift;
import static com.junbeom.common.OrderId.orderMask;

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

    @Test
    @DisplayName("Test getCombinedId method")
    void testGetCombinedId() {
        OrderId orderId = new OrderId(1L, 1000L);
        Long combinedId = orderId.getCombinedId();

        // Verify the combined ID structure
        assertEquals(1L, (combinedId & venueMask) >> venueShift); // Venue should be 1
        assertEquals(1000L, combinedId & orderMask); // Order ID should be 1000
    }

    @Test
    @DisplayName("Test getCombinedId with different venues")
    void testGetCombinedIdDifferentVenues() {
        OrderId orderId1 = new OrderId(0L, 100L);
        assertEquals(0L, (orderId1.getCombinedId() & venueMask) >> venueShift);

        OrderId orderId2 = new OrderId(2L, 200L);
        assertEquals(2L, (orderId2.getCombinedId() & venueMask) >> venueShift);

        OrderId orderId3 = new OrderId(3L, 300L);
        assertEquals(3L, (orderId3.getCombinedId() & venueMask) >> venueShift);
    }
}
