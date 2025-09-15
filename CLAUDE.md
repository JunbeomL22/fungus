# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Java 21 Maven multi-module project focused on high-performance data structures and utilities for trading systems. The project emphasizes memory efficiency, thread safety, and performance optimization.

### Module Structure
- **core/**: Core data structures and utilities with unit tests
- **benchmarks/**: JMH performance benchmarks (separate from main codebase)

## Development Commands

### Build and Test
```bash
# Full build (all modules)
mvn clean compile

# Run all tests (core module only)
mvn test
# OR specifically for core module
mvn -pl core test

# Run specific test class
mvn -pl core test -Dtest=UniqueIdTest
mvn -pl core test -Dtest=OrderIdTest

# Run specific test method
mvn -pl core test -Dtest=UniqueIdTest#testUniqueIdEquality

# Run tests with verbose output
mvn -pl core test -Dtest=UniqueIdTest -Dmaven.surefire.debug=true
```

### Performance Testing
The benchmarks are now in a separate module:
```bash
# Build all modules and package benchmarks with dependencies
mvn package -DskipTests

# Run all benchmarks using the built JAR
cd benchmarks && java -jar target/fungus-benchmarks-1.0-SNAPSHOT.jar ".*Benchmark.*"

# Run specific benchmark
cd benchmarks && java -jar target/fungus-benchmarks-1.0-SNAPSHOT.jar "UniqueIdBenchmark"
```

### Documentation
```bash
# Generate Javadoc for all modules
mvn javadoc:javadoc

# Generate Javadoc for core module only
mvn -pl core javadoc:javadoc
```

## Core Architecture

### Package Structure
- **core/**: `com.junbeom.common`, `com.junbeom.conversion`, `com.junbeom.data` - Core data structures and utilities
- **benchmarks/**: `com.junbeom.benchmark` - JMH performance benchmarks

### Key Components

#### UniqueId Class (`core/src/main/java/com/junbeom/common/UniqueId.java`)
A string interning system that stores strings as 64-bit integers internally:
- **Purpose**: Memory-efficient string storage with O(1) equality checks
- **Thread Safety**: Uses ConcurrentHashMap for lock-free operations
- **Jackson Support**: Custom serializers handle both string and JSON object serialization
- **Key Methods**: `fromString()`, `toString()`, `add()`, `merged()`
- **Performance**: Fixed 8-byte memory footprint regardless of string length

#### OrderId Class (`core/src/main/java/com/junbeom/common/order/OrderId.java`)
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

### Javadoc Rules
- rules:
    - avoid over-using term "high-performance"

### Documentation Rules for Core
- location: documents/core
- rules
    - File Location must be under the title with link
    - the md File must be linked to the documents/core/README.md
    - avoid over-using term "high-performance"


### JMH Configuration
- 2 warmup iterations
- 3 measurement iterations
- Don't be too long

### Test
- In test, do not include test on performance
- also, focus on behavioral testing