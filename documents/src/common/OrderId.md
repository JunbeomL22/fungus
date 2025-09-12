# OrderId

**File Location:** [src/main/java/com/junbeom/common/OrderId.java](../../../src/main/java/com/junbeom/common/OrderId.java)

A unique order identifier that efficiently encodes both venue and order information within a single 64-bit Long value, enabling differentiation between multiple trading venues.

## Overview

The OrderId class uses bit manipulation to pack venue and order information into a single Long value, providing memory efficiency and fast operations for high-frequency trading systems.

## Memory Alignment and Performance Design

The primary design goal is optimal **memory alignment** and **cache efficiency**. By encoding both venue and order information in a single 64-bit Long (8 bytes), this class ensures:

- **Perfect CPU alignment**: Aligns with CPU word boundaries on 64-bit systems
- **Minimal memory footprint**: Fixed 8-byte size regardless of venue/order values  
- **Cache-friendly access**: Single memory read operations for all data
- **High-frequency trading optimization**: Critical for scenarios where memory access speed directly impacts performance
- **Reduced memory fragmentation**: Compact representation reduces heap pressure

## Bit Structure

- **Bits 0-55 (56 bits):** Order identifier within the venue
- **Bits 56-63 (8 bits):** Venue identifier

This design supports:
- Up to 256 different venues (8-bit venue ID)
- Over 72 quadrillion unique orders per venue (56-bit order ID)
- Efficient extraction of both venue and order information
- Simple validation and range checking

## Supported Venues

| Venue ID | Constant | Name |
|----------|----------|------|
| 0 | `dummyVenue` | dummy |
| 1 | `krxGeneral` | krxGeneral |
| 2 | `krxKts` | krxKts |
| 3 | `smb` | smb |

## Construction

### Primary Constructor
```java
OrderId orderId = new OrderId(venue, orderId);
```
- `venue`: Long value (0-255)
- `orderId`: Long value (automatically masked to 56 bits)

### Factory Method
```java
OrderId orderId = OrderId.fromCombinedId(combinedId);
```
Creates an OrderId from a complete 64-bit combined ID value.

## Key Methods

### Information Extraction
- `orderId()`: Returns the 56-bit order ID without venue information
- `venueId()`: Returns the 8-bit venue ID
- `venueName()`: Returns the human-readable venue name
- `getCombinedId()`: Returns the complete 64-bit combined ID

### Example Usage
```java
// Create order ID for KRX General venue
OrderId orderId = new OrderId(OrderId.krxGeneral, 12345L);

// Extract information
Long venue = orderId.venueId();        // Returns 1
String venueName = orderId.venueName(); // Returns "krxGeneral"
Long orderNum = orderId.orderId();      // Returns 12345
Long combined = orderId.getCombinedId(); // Returns encoded 64-bit value

// Create from combined ID
OrderId reconstructed = OrderId.fromCombinedId(combined);
```

## JSON Serialization

### Serialization Format
```json
{
  "venue": "krxGeneral",
  "venueId": 1,
  "orderId": 12345,
  "combinedId": 72057594037939581
}
```

### Deserialization Support
The deserializer accepts multiple formats:
1. **Object format** with venue/venueId and orderId fields
2. **Single number** (treated as combinedId)
3. **String representation** of a number (treated as combinedId)

## Thread Safety

OrderId is **immutable** and **thread-safe**:
- All fields are final
- No mutable state after construction
- Safe for concurrent access without synchronization

## Performance Characteristics

- **Memory:** Fixed 8-byte footprint (single Long field)
- **Equality:** O(1) numeric comparison
- **Hashing:** O(1) using Long.hashCode()
- **Venue extraction:** O(1) bit manipulation
- **Order extraction:** O(1) bit manipulation

## Error Handling

### Construction Validation
- Venue ID must be between 0 and 255
- Invalid venue throws `IllegalArgumentException`

### JSON Deserialization Errors
- Missing required fields throws `IllegalArgumentException`
- Invalid number format throws `IllegalArgumentException`
- Unknown venue name throws `IllegalArgumentException`

## Constants

```java
// Bit manipulation constants
public static final Long venueShift = 56L;
public static final Long venueMask = 0xFFL << venueShift;
public static final Long orderMask = ~venueMask;

// Venue identifiers
public static final Long dummyVenue = 0L;
public static final Long krxGeneral = 1L;
public static final Long krxKts = 2L;
public static final Long smb = 3L;
```

## Design Benefits

1. **Memory Alignment**: Perfect 8-byte alignment with 64-bit CPU word boundaries for optimal cache performance
2. **Memory Efficiency**: Single Long storage regardless of venue/order size, reducing heap fragmentation
3. **Performance**: Fast bit operations for extraction and comparison, critical for high-frequency trading
4. **Type Safety**: Strongly typed venue and order identification
5. **Serialization**: Comprehensive JSON support with multiple input formats
6. **Scalability**: Supports up to 256 venues with massive order capacity per venue