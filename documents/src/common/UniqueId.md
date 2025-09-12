# UniqueId

**File Location:** [src/main/java/com/junbeom/common/UniqueId.java](../../../src/main/java/com/junbeom/common/UniqueId.java)

A highly efficient string interning system that reduces memory usage and improves performance by storing strings as 64-bit integers internally.

## Overview

UniqueId provides several key benefits over traditional string handling:

### Memory Efficiency
- **Fixed 8-byte size**: Regardless of the original string length, each UniqueId occupies exactly 8 bytes
- **String deduplication**: Identical strings share the same ID, eliminating duplicate storage
- **Cache-friendly**: Small size improves CPU cache utilization

### Performance Characteristics
- **Fast hashing**: Hashing a 64-bit integer is extremely fast compared to string hashing
- **O(1) equality checks**: Comparing two UniqueIds is a simple integer comparison
- **Efficient copying**: Being immutable, no heap allocations occur during assignment
- **Creation overhead**: String-to-ID mapping occurs only once per unique string

## Key Features

### Thread Safety
The internal cache uses `ConcurrentHashMap`, making UniqueId safe to use across threads. Creation of new IDs is lock-free and highly concurrent.

### JSON Serialization
Custom Jackson serializers handle both string and JSON object serialization:
- Intelligent serialization: JSON objects are preserved as JSON, strings as strings
- Round-trip compatibility with various JSON value types

## API Reference

### Static Methods

#### `fromString(String str)`
Creates a UniqueId from a string with efficient caching and deduplication.
- **Performance**: O(1) for cache lookup/insertion
- **Thread Safety**: Lock-free using ConcurrentHashMap
- **Throws**: `NullPointerException` if str is null

#### `defaultValue()`
Returns the default UniqueId (empty string).

#### `merged(Object... elements)`
Creates a new UniqueId by merging multiple elements into a single string.
- More efficient than multiple individual concatenations
- Single string allocation for the entire result

#### `count()`
Returns the total number of unique strings currently cached.

### Instance Methods

#### `toString()`
Converts the UniqueId back to its original string representation.
- **Performance**: O(1) cache lookup
- **Memory**: No string duplication, direct reference

#### `add(Object other)`
Concatenates this UniqueId with another value to create a new UniqueId.

#### `add(Object... others)`
Concatenates this UniqueId with multiple other values to create a new UniqueId.

#### `getId()`
Returns the internal ID for debugging purposes.

#### `equals(Object obj)`
Fast equality check based on internal ID comparison.

#### `hashCode()`
Hash code based on the internal ID - extremely fast compared to string hashing.

## Usage Examples

```java
// Create UniqueIds from strings
UniqueId id1 = UniqueId.fromString("symbol_AAPL");
UniqueId id2 = UniqueId.fromString("symbol_AAPL"); // Reuses existing ID

// Fast equality check (just comparing longs)
assert id1.equals(id2);

// Efficient concatenation
UniqueId combined = id1.add("_option");

// Convert back to string when needed
System.out.println("Symbol: " + id1);

// Create composite identifiers
UniqueId composite = UniqueId.merged("exchange", "_", "symbol", "_", "AAPL");
```

## Use Cases

- High-frequency string operations (comparisons, hashing, storage)
- Memory-constrained environments
- Performance-critical code paths requiring string-like identifiers
- Trading systems with frequent symbol lookups
- Caching systems where string deduplication is beneficial

## Performance Considerations

### Creation
- **First call**: O(1) for cache insertion + string storage
- **Subsequent calls**: O(1) for cache lookup (no allocation)

### Operations
- **Equality**: Simple long comparison
- **Hashing**: Long.hashCode() - much faster than string hashing
- **String conversion**: Direct cache lookup

### Memory
- Each UniqueId: exactly 8 bytes
- Cache overhead: one entry per unique string
- No string duplication across instances

## Implementation Details

### Internal Structure
- Single `long id` field (8 bytes)
- Static `ConcurrentHashMap<String, Long>` for string-to-ID mapping
- Static `ConcurrentHashMap<Long, String>` for ID-to-string mapping
- `AtomicLong` ID generator for unique ID assignment

### Thread Safety
- All operations are thread-safe
- Uses `computeIfAbsent` for atomic ID creation
- No locks required for read operations
- Highly concurrent design suitable for multi-threaded environments