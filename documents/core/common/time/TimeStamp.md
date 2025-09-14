# TimeStamp

**Location:** [`core/src/main/java/com/junbeom/time/TimeStamp.java`](../../../../core/src/main/java/com/junbeom/common/time/TimeStamp.java)

## Overview

A high-performance timestamp implementation that stores time as nanoseconds since Unix epoch. This class provides efficient time representation with configurable timezone offset, suitable for trading systems and high-performance applications where precise timing and memory efficiency are critical.

## Key Features

### Memory Efficiency
- Uses a single 8-byte long field to store nanosecond-precision Unix time
- Fixed memory footprint regardless of timezone operations
- No additional allocation for time conversion operations

### Thread Safety
- Immutable and thread-safe by design
- Static offsetHour field is configurable at any time affecting all subsequent operations

### Performance
- O(1) time complexity for all operations
- Fast comparison via numeric equality checks
- Efficient JSON serialization with custom Jackson serializer

## Class Structure

### Static Fields
- `offsetHour` (int): Current timezone offset in hours, defaults to 9 (UTC+9/KST)
- `unixNano` (long): Underlying nanosecond-precision Unix timestamp storage

### Constructor Methods
- `private TimeStamp()`: Creates timestamp with millisecond precision
- `private TimeStamp(long unixNano)`: Creates timestamp with nanosecond precision

### Factory Methods
- `static TimeStamp now()`: Creates TimeStamp representing current time with nanosecond precision
- `static TimeStamp of(long unixNano)`: Creates TimeStamp from nanoseconds since Unix epoch
- `static long getCurrentUnixNano()`: Gets current nanosecond-precision Unix time

### Configuration Methods
- `static void setOffsetHour(int offsetHour)`: Sets timezone offset in hours for all timestamp operations

### Getter Methods
- `long getUnixNanoValue()`: Gets underlying nanosecond-precision Unix timestamp value
- `Instant toInstant()`: Converts to Java 8 Instant
- `ZonedDateTime toZonedDateTime()`: Converts to ZonedDateTime using configured timezone offset
- `long getUnixNano()`: JSON serializer for unixNano field
- `int getOffsetHour()`: JSON serializer for offsetHour field

### Jackson Integration
- Custom JSON serialization and deserialization support
- Serializes as formatted date-time string: "yyyy-MM-dd HH:mm:ss.SSSSSSSSS xxx"
- Deserializes from numeric timestamp

## Timezone Support

The class supports configurable timezone offset using the `offsetHour` static field:

```java
// Configure timezone offset
TimeStamp.setOffsetHour(0);    // UTC
TimeStamp.setOffsetHour(8);     // UTC+8 (China Standard Time)
TimeStamp.setOffsetHour(9);     // UTC+9 (Korea Standard Time)
TimeStamp.setOffsetHour(5.5);   // UTC+5:30 (India Standard Time)
TimeStamp.setOffsetHour(-5);    // UTC-5 (Eastern Standard Time)
```

## Usage Examples

### Basic Creation and Usage
```java
// Create current timestamp
TimeStamp now = TimeStamp.now();

// Create from specific nanoseconds
TimeStamp ts = TimeStamp.of(System.nanoTime());

// Convert to Instant and ZonedDateTime
Instant instant = ts.toInstant();
ZonedDateTime zoned = ts.toZonedDateTime();

// Configure timezone offset
TimeStamp.setOffsetHour(8); // UTC+8
```

### JSON Serialization
```java
// Serialization (automatic with Jackson)
ObjectMapper mapper = new ObjectMapper();
String json = mapper.writeValueAsString(ts);
// Output: "2023-12-07 14:30:45.123456789 +08:00"

// Deserialization
TimeStamp fromJson = mapper.readValue(json, TimeStamp.class);
```

### Equality and Comparison
```java
TimeStamp ts1 = TimeStamp.now();
TimeStamp ts2 = TimeStamp.of(ts1.getUnixNanoValue());

// Equality check is O(1) using numeric comparison
boolean equal = ts1.equals(ts2); // true if same nanosecond value

// Hash code derived from nanosecond value
int hash = ts1.hashCode();
```

## Performance Characteristics

| Operation | Time Complexity | Notes |
|-----------|----------------|--------|
| Creation | O(1) | System.nanoTime() call |
| Comparison | O(1) | Simple integer comparison |
| Instant conversion | O(1) | Constant-time arithmetic |
| ZonedDateTime conversion | O(1) | Instant conversion + zone offset |
| JSON serialization | O(1) | Format pre-allocated |

## Memory Usage

- **Fixed Size**: 8 bytes (long) for timestamp storage
- **No Additional Allocation**: Zero object allocation for time operations
- **Minimal GC Impact**: Small, immutable instances avoid garbage collection pressure

## Thread Safety

- **Immutable**: All fields are final (effectively) and no public mutators
- **Static Configuration**: offsetHour is thread-safe for reading
- **No Synchronization**: Lock-free design using immutable pattern

## JSON Serialization Format

### Serialized Format
- **String Format**: "yyyy-MM-dd HH:mm:ss.SSSSSSSSS xxx"
- **Example**: "2023-12-07 14:30:45.123456789 +09:00"

### Deserialization Formats
- JSON Object: `{"unixNano": 123456789, "offsetHour": 9}`
- JSON Number: Raw long value (handled by custom logic)

## Precision and Range

- **Precision**: Nanosecond precision (1 billionth of a second)
- **Range**: Long type supports ~292 years of timestamp range
- **Epoch**: Unix epoch (January 1, 1970, 00:00:00 UTC)

## Best Practices

1. **Timezone Configuration**: Set offset consistently across application
2. **Immutable Pattern**: Prefer creating new instances over mutation
3. **Equality Usage**: Use equals() for comparison, not identity checks
4. **JSON Serialization**: Leverage custom serializers for optimal format
5. **Performance**: Use numeric comparisons for time-based operations

## Related Classes

- [`UniqueId`](./UniqueId.md): Memory-efficient string interning system
- [`OrderId`](./OrderId.md): Encoded venue and order information
- `Instant`: Java 8 time representation
- `ZonedDateTime`: Java 8 timezone-aware time representation