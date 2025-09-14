package com.junbeom.common.time;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@JsonSerialize(using = TimeStamp.Serializer.class)
public class TimeStamp {

    /**
     * The current timezone offset in hours for all timestamp operations.
     * Defaults to 9 (UTC+9/KST).
     * This is a static field that affects all TimeStamp instances.
     */
    private static int offsetHour = 9;

    /**
     * The underlying nanosecond-precision Unix timestamp storage.
     * Stores nanoseconds since Unix epoch (January 1, 1970, 00:00:00 UTC).
     * Using long type provides up to 292 years of precision range.
     */
    private long unixNano;

    /**
     * Private constructor to create timestamp with millisecond precision.
     *
     * @param unixMilli milliseconds since Unix epoch
     */
    private TimeStamp() {
        this(System.currentTimeMillis() * 1_000_000L);
    }

    /**
     * Private constructor to create timestamp with nanosecond precision.
     *
     * @param unixNano nanoseconds since Unix epoch
     */
    private TimeStamp(long unixNano) {
        this.unixNano = unixNano;
    }

    /**
     * Creates a TimeStamp representing the current time with nanosecond precision.
     *
     * <p>Uses {@link System#nanoTime()} for precise timing measurement.</p>
     *
     * @return new TimeStamp instance representing current time
     */
    public static TimeStamp now() {
        return new TimeStamp(System.nanoTime());
    }

    /**
     * Creates a TimeStamp from nanoseconds since Unix epoch.
     *
     * <p>This is the primary factory method for creating timestamps from specific
     * nanosecond values, providing nanosecond precision.</p>
     *
     * @param unixNano nanoseconds since Unix epoch
     * @return new TimeStamp instance
     */
    public static TimeStamp of(long unixNano) {
        return new TimeStamp(unixNano);
    }

    /**
     * Sets the timezone offset in hours for all timestamp operations.
     *
     * <p>This is a static method that affects all TimeStamp instances created
     * after this call. Common offsets:
     * <ul>
     *   <li>0 - UTC</li>
     *   <li>8 - UTC+8 (China Standard Time)</li>
     *   <li>9 - UTC+9 (Korea Standard Time)</li>
     *   <li>5.5 - UTC+5:30 (India Standard Time)</li>
     *   <li>-5 - UTC-5 (Eastern Standard Time)</li>
     * </ul></p>
     *
     * @param offsetHour timezone offset in hours (typically -12 to +14)
     */
    public static void setOffsetHour(int offsetHour) {
        TimeStamp.offsetHour = offsetHour;
    }

    /**
     * Gets current nanosecond-precision Unix time.
     *
     * <p>Directly returns the value from {@link System#nanoTime()}.</p>
     *
     * @return current nanoseconds since Unix epoch
     */
    public static long getCurrentUnixNano() {
        return System.nanoTime();
    }

    /**
     * Gets the underlying nanosecond-precision Unix timestamp value.
     *
     * <p>This provides direct access to the raw 64-bit nanosecond value
     * used for storage and comparisons.</p>
     *
     * @return nanoseconds since Unix epoch
     */
    public long getUnixNanoValue() {
        return unixNano;
    }

    /**
     * Converts this TimeStamp to Java 8 Instant.
     *
     * <p>This conversion provides nanosecond precision and can be used with
     * other Java 8 Time API components.</p>
     *
     * @return Instant representation of this timestamp
     */
    public Instant toInstant() {
        return Instant.ofEpochSecond(unixNano / 1_000_000_000, unixNano % 1_000_000_000);
    }

    /**
     * Converts this TimeStamp to ZonedDateTime using the configured timezone offset.
     *
     * <p>Uses the current offsetHour for timezone conversion. Call
     * {@link #setOffsetHour(int)} to change the timezone.</p>
     *
     * @return ZonedDateTime in the configured timezone
     */
    public ZonedDateTime toZonedDateTime() {
        return toInstant().atZone(ZoneOffset.ofHours(offsetHour));
    }

    /**
     * Compares this TimeStamp with another object for equality.
     *
     * <p>Two TimeStamp instances are equal if they have the same
     * nanosecond-precision Unix timestamp value.</p>
     *
     * @param o object to compare with
     * @return true if equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimeStamp timeStamp = (TimeStamp) o;
        return unixNano == timeStamp.unixNano;
    }

    /**
     * Returns a hash code value for this TimeStamp.
     *
     * <p>Derived from the underlying nanosecond-precision Unix timestamp value.</p>
     *
     * @return hash code
     */
    @Override
    public int hashCode() {
        return (int) (unixNano ^ (unixNano >>> 32));
    }

    /**
     * Returns a string representation of this TimeStamp.
     *
     * <p>Shows the nanosecond timestamp and current timezone offset.</p>
     *
     * @return string representation
     */
    @Override
    public String toString() {
        return "{" +
                "unixNano=" + unixNano +
                ", offsetHour=" + offsetHour +
                '}';
    }

    /**
     * JSON deserializer for the unixNano field.
     *
     * <p>This is used by Jackson for JSON deserialization. The value is stored
     * as nanoseconds since Unix epoch.</p>
     *
     * @param unixNano nanoseconds since Unix epoch
     */
    @JsonProperty("unixNano")
    public void setUnixNano(long unixNano) {
        this.unixNano = unixNano;
    }

    /**
     * JSON serializer for the unixNano field.
     *
     * <p>This is used by Jackson for JSON serialization. Returns the stored
     * nanosecond-precision Unix timestamp value.</p>
     *
     * @return nanoseconds since Unix epoch
     */
    @JsonProperty("unixNano")
    public long getUnixNano() {
        return unixNano;
    }

    /**
     * JSON serializer for the offsetHour field.
     *
     * <p>This is used by Jackson for JSON serialization. Returns the current
     * timezone offset in hours used for timezone conversions.</p>
     *
     * @return timezone offset in hours
     */
    @JsonProperty("offsetHour")
    public int getOffsetHour() {
        return offsetHour;
    }

    /**
     * Custom JSON serializer for TimeStamp instances.
     *
     * <p>Serializes TimeStamp objects as formatted date-time strings using
     * the pattern "yyyy-MM-dd HH:mm:ss.SSSSSSSSS xxx" to preserve nanosecond
     * precision and timezone information.</p>
     *
     * <p><b>Example Output:</b> "2023-12-07 14:30:45.123456789 +09:00"</p>
     */
    public static class Serializer extends JsonSerializer<TimeStamp> {
        private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSSSS xxx");

        /**
         * Serializes a TimeStamp object to a formatted date-time string.
         *
         * @param value the TimeStamp to serialize
         * @param gen JSON generator for writing output
         * @param provider serializer provider
         * @throws IOException if an I/O error occurs
         */
        @Override
        public void serialize(TimeStamp value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            ZonedDateTime zonedDateTime = value.toZonedDateTime();
            gen.writeString(zonedDateTime.format(FORMATTER));
        }
    }
}