package com.junbeom.common.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
// import org.junit.jupiter.params.shadow.com.univocity.parsers.annotations.Nested;

import static org.junit.jupiter.api.Assertions.*;
import static com.junbeom.common.order.OrderId.venueMask;
import static com.junbeom.common.order.OrderId.venueShift;
import static com.junbeom.common.order.OrderId.orderMask;

@DisplayName("OrderId Class Tests")
public class OrderIdTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Nested
    @DisplayName("Jackson Serialization/Deserialization Tests")
    class JacksonTests {

        @Test
        @DisplayName("Test serialization of OrderId")
        void testSerialization() throws Exception {
            OrderId orderId = new OrderId(1L, 123456789L);
            String json = objectMapper.writeValueAsString(orderId);


            // Parse back to verify structure
            assertTrue(json.contains("\"venue\":\"krxGeneral\""), () -> "Expected venue not found in JSON: " + json);
            assertTrue(json.contains("\"venueId\":1"), () -> "Expected venueId not found in JSON: " + json);
            assertTrue(json.contains("\"orderId\":123456789"), () -> "Expected orderId not found in JSON: " + json);
            assertTrue(json.contains("\"combinedId\":72057594161384725"), () -> "Expected combinedId not found in JSON: " + json);
        }

        @Test
        @DisplayName("Test serialization of different venue correctly")
        void testSerializationDifferentVenue() throws Exception {
            OrderId[] orderIds = {
                new OrderId(0L, 100L),
                new OrderId(1L, 200L),
                new OrderId(2L, 300L),
                new OrderId(3L, 400L)
            };

            String[] expectedVenues = {"dummy", "krxGeneral", "krxKts", "smb"};

            for (int i = 0; i < orderIds.length; i++) {
                String json = objectMapper.writeValueAsString(orderIds[i]);
                assertTrue(json.contains("\"venue\":\"" + expectedVenues[i] + "\""));
                assertTrue(json.contains("\"venueId\":" + i));
            }
        }

        @Test
        @DisplayName("Test deserialization of OrderId")
        void testDeserialization() throws Exception {
            String json = """
                    {
                        "venue": "krxGeneral",
                        "venueId": 2,
                        "orderId": 54321,
                        "combinedId": 144115188075910193
                    }
                    """;
            OrderId orderId = objectMapper.readValue(json, OrderId.class);

            assertEquals(2L, orderId.venueId());
            assertEquals(54321L, orderId.orderId());
            assertEquals("krxKts", orderId.venueName());
        }
    }

    @Test
    @DisplayName("Test OrderId creation with valid venue and orderId")
    void testOrderIdCreation() {
        OrderId orderId = new OrderId(1L, 1000L);
        assertEquals(1000L, orderId.orderId());
        assertEquals("krxGeneral", orderId.venueName());
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
        assertEquals("krxGeneral", orderId1.venueName());

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
        "1, krxGeneral",
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
