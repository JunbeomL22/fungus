# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Java 21 Maven project focused on high-performance data structures and utilities for trading systems. The project emphasizes memory efficiency, thread safety, and performance optimization.

## Development Commands

### Build and Test
```bash
# Full build
mvn clean compile

# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=UniqueIdTest
mvn test -Dtest=OrderIdTest

# Run specific test method
mvn test -Dtest=UniqueIdTest#testUniqueIdEquality

# Run tests with verbose output
mvn test -Dtest=UniqueIdTest -Dmaven.surefire.debug=true
```

### Performance Testing
The project includes JMH (Java Microbenchmark Harness) for performance testing:
```bash
# Compile and run benchmarks (when benchmark classes exist)
mvn clean compile
mvn exec:java -Dexec.mainClass="org.openjdk.jmh.Main" -Dexec.args=".*Benchmark.*"
```

### Documentation
```bash
# Generate Javadoc
mvn javadoc:javadoc
```

## Core Architecture

### Package Structure
- `com.junbeom.common`: Core data structures and utilities
- `com.junbeom.utils`: Utility classes and helper functions
- `com.junbeom.benchmark`: JMH performance benchmarks

### Key Components

#### UniqueId Class (`src/main/java/com/junbeom/common/UniqueId.java`)
A highly efficient string interning system that stores strings as 64-bit integers internally:
- **Purpose**: Memory-efficient string storage with O(1) equality checks
- **Thread Safety**: Uses ConcurrentHashMap for lock-free operations
- **Jackson Support**: Custom serializers handle both string and JSON object serialization
- **Key Methods**: `fromString()`, `toString()`, `add()`, `merged()`
- **Performance**: Fixed 8-byte memory footprint regardless of string length

#### OrderId Class (`src/main/java/com/junbeom/common/OrderId.java`)
Encodes venue and order information in a single 64-bit Long:
- **Bit Structure**: 8-bit venue ID (bits 56-63) + 56-bit order ID (bits 0-55)
- **Venue Support**: dummy, krxDrv, krxKts, smb venues
- **Jackson Support**: Serializes as structured JSON with venue, venueId, orderId, combinedId
- **Factory Methods**: `new OrderId(venue, orderId)`, `fromCombinedId(combinedId)`

### Design Principles

#### Memory Efficiency
Both core classes prioritize minimal memory usage:
- UniqueId: Fixed 8-byte size via string interning
- OrderId: Single Long field encoding multiple data points

#### Thread Safety
- UniqueId uses ConcurrentHashMap for cache operations
- OrderId is immutable and thread-safe by design
- No synchronization required for read operations

#### Performance Optimization
- O(1) equality checks using numeric comparison
- String deduplication through interning
- Cache-friendly small object sizes
- JMH benchmarking for performance validation

#### JSON Serialization Strategy
- UniqueId: Intelligent serialization (JSON objects vs strings)
- OrderId: Structured object format with multiple deserializer formats
- Both support round-trip serialization with Jackson

## Testing Architecture

### Test Organization
Tests use JUnit 5 with nested test classes for logical grouping:
- `BasicFunctionalityTests`: Core functionality verification
- `EqualityAndHashingTests`: Equality contracts and hash consistency
- `CachingTests`: String deduplication and cache behavior
- `ThreadSafetyTests`: Concurrent access validation
- `PerformanceAndEdgeCaseTests`: Performance benchmarks and edge cases

### Test Execution Patterns
- Extensive use of parameterized tests for coverage
- Thread safety testing with ExecutorService and CountDownLatch
- Performance assertions with time-based thresholds
- Comprehensive edge case testing (null values, Unicode, special characters)

## Development Environment

### SSL Configuration
The project includes comprehensive SSL bypass configuration for development environments (see `ssl_bypass.md`). This is particularly important for Maven dependency resolution in corporate networks.

### Key Dependencies
- **JUnit 5.11.0**: Testing framework with parameterized and nested test support
- **JMH 1.37**: Java Microbenchmark Harness for performance testing
- **Jackson 2.17.2**: JSON serialization with custom serializers/deserializers

## Code Quality Standards

### Documentation
All public APIs include comprehensive Javadoc with:
- Performance characteristics (time/space complexity)
- Thread safety guarantees
- Usage examples
- Security considerations where applicable

### Testing Requirements
- Unit tests must achieve comprehensive coverage of functionality
- Thread safety tests for concurrent data structures
- Performance tests for time-critical operations
- JSON serialization round-trip verification

### Performance Considerations
- Always measure performance impact of changes using JMH
- Consider memory allocation patterns in hot paths
- Validate thread safety under concurrent load
- Test with realistic data sizes and access patterns

### Documentation Rules
- location documents/items
- rules
    - File Location must be under the title with link
    - the md File must be linked to the documents/src/README.md