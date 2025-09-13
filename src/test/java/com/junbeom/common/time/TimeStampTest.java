package com.junbeom.common.time;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.openhft.chronicle.core.time.SystemTimeProvider;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TimeStampTest {
    private static final long TEST_UNIX_NANO = 1705186245123456789L;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void testCreation() {
        TimeStamp ts1 = TimeStamp.now();
        assertNotNull(ts1);
        assertTrue(ts1.getUnixNanoValue() > 0);

        TimeStamp ts2 = TimeStamp.of(TEST_UNIX_NANO);
        assertEquals(TEST_UNIX_NANO, ts2.getUnixNanoValue());
    }

    @Test
    void testGetSystemNanoTime() {
        long nano = TimeStamp.getCurrentSystemNanoTime();
        assertTrue(nano > 0);

        nano = TimeStamp.getCurrentSystemNanoTime();
        assertTrue(nano > 0);
    }

    @Test
    void debugChronicleInit() {
        try {
            System.out.println("Java version: " + System.getProperty("java.version"));
            System.out.println("OS: " + System.getProperty("os.name"));
            
            long nano = SystemTimeProvider.INSTANCE.currentTimeNanos();
            System.out.println("Success: " + nano);
            
        } catch (ExceptionInInitializerError e) {
            System.out.println("InitializerError cause: " + e.getCause());
            e.getCause().printStackTrace();
            
            // 폴백
            long nano = System.nanoTime();
            System.out.println("Fallback nano: " + nano);
        }
    }

    @Test
    void testSetOffsetHour() {
        TimeStamp.setOffsetHour(8);
        TimeStamp ts = TimeStamp.of(TEST_UNIX_NANO);
        assertEquals(8, ts.getOffsetHour());

        TimeStamp.setOffsetHour(9);
        ts = TimeStamp.of(TEST_UNIX_NANO);
        assertEquals(9, ts.getOffsetHour());
    }

    @Test
    void testToInstant() {
        TimeStamp ts = TimeStamp.of(TEST_UNIX_NANO);
        Instant instant = ts.toInstant();

        assertEquals(TEST_UNIX_NANO / 1_000_000_000, instant.getEpochSecond());
        assertEquals(TEST_UNIX_NANO % 1_000_000_000, instant.getNano());
    }

    @Test
    void testToZonedDateTime() {
        TimeStamp ts = TimeStamp.of(TEST_UNIX_NANO);
        ZonedDateTime zdt = ts.toZonedDateTime();

        assertEquals(TEST_UNIX_NANO / 1_000_000_000, zdt.toInstant().getEpochSecond());
        assertEquals(TEST_UNIX_NANO % 1_000_000_000, zdt.toInstant().getNano());
        assertEquals("+09:00", zdt.getOffset().toString());
    }

    @Test
    void testEquality() {
        TimeStamp ts1 = TimeStamp.of(TEST_UNIX_NANO);
        TimeStamp ts2 = TimeStamp.of(TEST_UNIX_NANO);

        assertEquals(ts1, ts2);
        assertEquals(ts1.hashCode(), ts2.hashCode());
    }

    @Test
    void testInequality() {
        TimeStamp ts1 = TimeStamp.of(TEST_UNIX_NANO);
        TimeStamp ts2 = TimeStamp.of(TEST_UNIX_NANO + 1000);

        assertNotEquals(ts1, ts2);
        assertNotEquals(ts1.hashCode(), ts2.hashCode());
    }

    @Test
    void testSerialization() throws JsonProcessingException {
        TimeStamp ts = TimeStamp.of(TEST_UNIX_NANO);
        String json = objectMapper.writeValueAsString(ts);

        assertEquals("\"2024-01-14 07:50:45.123456789 +09:00\"", json);
    }

    @Test
    void testDeserialization() throws JsonProcessingException {
        String json = "{\"unixNano\": 1705186245123456789}";
        TimeStamp ts = objectMapper.readValue(json, TimeStamp.class);

        assertEquals(TEST_UNIX_NANO, ts.getUnixNanoValue());
    }

    @Test
    void testSerializationWithOffsetHour() throws JsonProcessingException {
        TimeStamp.setOffsetHour(1);
        TimeStamp ts = TimeStamp.of(TEST_UNIX_NANO);
        String json = objectMapper.writeValueAsString(ts);

        assertEquals("\"2024-01-13 23:50:45.123456789 +01:00\"", json);
        TimeStamp.setOffsetHour(9);
    }

    @Test
    void testSerializationFormatConsistency() throws JsonProcessingException {
        TimeStamp ts1 = TimeStamp.of(1705186245123456789L);
        TimeStamp ts2 = TimeStamp.of(1705186245123456789L);

        String json1 = objectMapper.writeValueAsString(ts1);
        String json2 = objectMapper.writeValueAsString(ts2);

        assertEquals(json1, json2);
    }

    @Test
    void testEdgeCaseZero() {
        TimeStamp ts = TimeStamp.of(0);
        assertEquals(0, ts.getUnixNanoValue());
        assertEquals(Instant.EPOCH, ts.toInstant());
    }

    @Test
    void testToString() {
        TimeStamp ts = TimeStamp.of(TEST_UNIX_NANO);
        String str = ts.toString();

        assertTrue(str.contains("unixNano=" + TEST_UNIX_NANO));
        assertTrue(str.contains("offsetHour=9"));
    }
}