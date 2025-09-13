package com.junbeom.common.time;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import net.openhft.chronicle.core.time.SystemTimeProvider;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"unixNano", "offsetHour"})
@JsonSerialize(using = TimeStamp.TimeStampSerializer.class)
public class TimeStamp {
    private static int offsetHour = 9;

    private long unixNano;

    private TimeStamp() {
        this(System.currentTimeMillis() * 1_000_000L);
    }

    private TimeStamp(long unixNano) {
        this.unixNano = unixNano;
    }

    public static TimeStamp now() {
        return new TimeStamp(System.nanoTime());
    }

    public static TimeStamp of(long unixNano) {
        return new TimeStamp(unixNano);
    }

    public static void setOffsetHour(int offsetHour) {
        TimeStamp.offsetHour = offsetHour;
    }

    public static long getCurrentSystemNanoTime() {
        return System.nanoTime();
    }

    public static long getCurrentUnixNano() {
        return SystemTimeProvider.INSTANCE.currentTimeNanos();
    }

    public long getUnixNanoValue() {
        return unixNano;
    }

    public Instant toInstant() {
        return Instant.ofEpochSecond(unixNano / 1_000_000_000, unixNano % 1_000_000_000);
    }

    public ZonedDateTime toZonedDateTime() {
        return toInstant().atZone(ZoneOffset.ofHours(offsetHour));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimeStamp timeStamp = (TimeStamp) o;
        return unixNano == timeStamp.unixNano;
    }

    @Override
    public int hashCode() {
        return (int) (unixNano ^ (unixNano >>> 32));
    }

    @Override
    public String toString() {
        return "TimeStamp{" +
                "unixNano=" + unixNano +
                ", offsetHour=" + offsetHour +
                '}';
    }

    @JsonProperty("unixNano")
    public void setUnixNano(long unixNano) {
        this.unixNano = unixNano;
    }

    @JsonProperty("unixNano")
    public long getUnixNano() {
        return unixNano;
    }

    @JsonProperty("offsetHour")
    public int getOffsetHour() {
        return offsetHour;
    }

    public static class TimeStampSerializer extends JsonSerializer<TimeStamp> {
        private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSSSS xxx");

        @Override
        public void serialize(TimeStamp value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            ZonedDateTime zonedDateTime = value.toZonedDateTime();
            gen.writeString(zonedDateTime.format(FORMATTER));
        }
    }
}