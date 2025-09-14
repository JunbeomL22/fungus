# Build Guide

This document provides comprehensive build, test, and benchmarking commands for the fungus multi-module Maven project.

## Project Structure

```
fungus/
├── pom.xml (parent/aggregator)
├── core/
│   ├── pom.xml
│   ├── src/main/java/com/junbeom/ (production code)
│   │   ├── App.java
│   │   ├── common/ (unique packages)
│   │   ├── conversion/
│   │   ├── data/
│   │   ├── order/
│   │   ├── time/
│   │   └── UniqueId.java
│   └── src/test/java/com/junbeom/ (unit tests)
└── benchmarks/
    ├── pom.xml
    └── src/main/java/com/junbeom/ (JMH benchmarks)
        ├── UniqueIdBenchmark.java
        ├── common/
        │   ├── order/
        │   └── time/
```

## Build Commands

### Full Project Build

```bash
# Clean and compile all modules
mvn clean compile

# Clean, compile, test, and package all modules
mvn clean install

# Clean and package without tests
mvn clean package -DskipTests
```

### Module-Specific Build

```bash
# Build only core module
mvn -pl core clean compile

# Build only benchmarks module
mvn -pl benchmarks clean compile

# Build core and install to local repository
mvn -pl core clean install
```

## Testing Commands

### Run All Tests

```bash
# Run all tests (core module only)
mvn test

# Run tests with verbose output
mvn test -Dmaven.surefire.debug=true
```

### Core Module Testing

```bash
# Run specific test class
mvn -pl core test -Dtest=UniqueIdTest
mvn -pl core test -Dtest=OrderIdTest
mvn -pl core test -Dtest=TimeStampTest
mvn -pl core test -Dtest=WorkerAwareDataBufferTest

# Run specific test method
mvn -pl core test -Dtest=UniqueIdTest#testUniqueIdEquality
mvn -pl core test -Dtest=OrderIdTest#testOrderIdCreation
mvn -pl core test -Dtest=TimeStampTest#testTimeStampConversion

# Run all tests in a nested test class
mvn -pl core test -Dtest=UniqueIdTest$BasicFunctionalityTests
mvn -pl core test -Dtest=UniqueIdTest$ThreadSafetyTests
mvn -pl core test -Dtest=UniqueIdTest$PerformanceAndEdgeCaseTests
```

### Test Coverage and Reports

```bash
# Generate test reports
mvn -pl core surefire-report:report

# Run tests with debugging
mvn -pl core test -Dtest=UniqueIdTest -Dmaven.surefire.debug=true
```

## Benchmarking Commands

### Build Benchmarks

```bash
# Compile benchmarks (requires core module built first)
mvn clean install
mvn -pl benchmarks clean compile
```

### Run All Benchmarks

```bash
# Build and run all benchmarks (this will show benchmark discovery)
mvn -pl benchmarks exec:java -Dexec.mainClass="org.openjdk.jmh.Main" -Dexec.args=""

# Run all benchmarks with quick settings
mvn -pl benchmarks exec:java -Dexec.mainClass="org.openjdk.jmh.Main" -Dexec.args=".*Benchmark.* -f 1 -wi 1 -i 1"
```

### Run Specific Benchmarks

```bash
# UniqueId benchmarks
mvn -pl benchmarks exec:java -Dexec.mainClass="org.openjdk.jmh.Main" -Dexec.args="UniqueIdBenchmark.* -f 1 -wi 1 -i 1"

# OrderId benchmarks
mvn -pl benchmarks exec:java -Dexec.mainClass="org.openjdk.jmh.Main" -Dexec.args="OrderIdHashBenchmark.* -f 1 -wi 1 -i 1"

# Unix timestamp benchmarks
mvn -pl benchmarks exec:java -Dexec.mainClass="org.openjdk.jmh.Main" -Dexec.args="UnixNanoBenchmark.* -f 1 -wi 1 -i 1"
```

### Benchmark Configuration Options

```bash
# Quick benchmark run (1 warmup, 1 iteration)
mvn -pl benchmarks exec:java -Dexec.mainClass="org.openjdk.jmh.Main" -Dexec.args="UniqueIdBenchmark -f 1 -wi 1 -i 1"

# Thorough benchmark run (3 forks, 5 warmups, 10 iterations)
mvn -pl benchmarks exec:java -Dexec.mainClass="org.openjdk.jmh.Main" -Dexec.args="UniqueIdBenchmark -f 3 -wi 5 -i 10"

# Benchmark with specific thread count
mvn -pl benchmarks exec:java -Dexec.mainClass="org.openjdk.jmh.Main" -Dexec.args="UniqueIdBenchmark -t 4"

# Benchmark with profiler
mvn -pl benchmarks exec:java -Dexec.mainClass="org.openjdk.jmh.Main" -Dexec.args="UniqueIdBenchmark -prof gc"
```

### Benchmark Output Options

```bash
# Output results to JSON
mvn -pl benchmarks exec:java -Dexec.mainClass="org.openjdk.jmh.Main" -Dexec.args="UniqueIdBenchmark -rf json -rff results.json"

# Output results to CSV
mvn -pl benchmarks exec:java -Dexec.mainClass="org.openjdk.jmh.Main" -Dexec.args="UniqueIdBenchmark -rf csv -rff results.csv"
```

### Alternative Benchmark Execution

```bash
# Using JAR directly (after mvn install)
cd benchmarks
java -jar target/fungus-benchmarks-1.0-SNAPSHOT.jar UniqueIdBenchmark

# With JVM options
cd benchmarks
java -Xmx2g -jar target/fungus-benchmarks-1.0-SNAPSHOT.jar UniqueIdBenchmark -f 1 -wi 2 -i 3
```

## Documentation Commands

```bash
# Generate Javadoc for all modules
mvn javadoc:javadoc

# Generate Javadoc for core module only
mvn -pl core javadoc:javadoc

# Generate aggregated site documentation
mvn site
```

## Common Development Workflows

### Development Cycle

```bash
# 1. Make code changes in core module
# 2. Run tests to verify changes
mvn -pl core test

# 3. If tests pass, install core module
mvn -pl core install

# 4. Run benchmarks to check performance impact
mvn -pl benchmarks exec:java -Dexec.mainClass="org.openjdk.jmh.Main" -Dexec.args="UniqueIdBenchmark -f 1 -wi 1 -i 1"
```

### Performance Testing Workflow

```bash
# 1. Baseline performance test
mvn -pl benchmarks exec:java -Dexec.mainClass="org.openjdk.jmh.Main" -Dexec.args="UniqueIdBenchmark -rf json -rff baseline.json"

# 2. Make performance improvements
# 3. Test performance again
mvn clean install
mvn -pl benchmarks exec:java -Dexec.mainClass="org.openjdk.jmh.Main" -Dexec.args="UniqueIdBenchmark -rf json -rff improved.json"

# 4. Compare results (manual comparison or using JMH tools)
```

### Full Verification Pipeline

```bash
# Complete verification pipeline
mvn clean                           # Clean all artifacts
mvn compile                         # Compile all modules
mvn -pl core test                   # Run all unit tests
mvn install                         # Install to local repository
mvn -pl core javadoc:javadoc        # Generate documentation
mvn -pl benchmarks exec:java -Dexec.mainClass="org.openjdk.jmh.Main" -Dexec.args=".*Benchmark.* -f 1 -wi 1 -i 1"
```

## Troubleshooting

### Common Issues

1. **Benchmark dependency issues**: Ensure core module is installed first
   ```bash
   mvn -pl core install
   ```

2. **JMH ClassNotFoundException**: This error occurs when JMH can't find the ForkedMain class. Run benchmarks from the project root:
   ```bash
   # Full build ensures all dependencies are properly installed
   mvn clean install
   # Then run benchmarks
   mvn -pl benchmarks exec:java -Dexec.mainClass="org.openjdk.jmh.Main" -Dexec.args=".*Benchmark.* -f 1 -wi 1 -i 1"

   # Alternative: Run with JAR file (may work better in some environments)
   cd benchmarks
   java -jar target/fungus-benchmarks-1.0-SNAPSHOT.jar
   ```

3. **Project Structure**: The project uses Maven standard structure with nested `com/junbeom/` packages. The benchmarks module no longer has an extra `benchmark` folder - all classes are directly under `src/main/java/com/junbeom/`.

4. **Test failures**: Run with debug output
   ```bash
   mvn -pl core test -X
   ```

4. **SSL issues**: See [ssl_bypass.md](ssl_bypass.md) for SSL configuration

5. **Memory issues during benchmarks**: Increase heap size
   ```bash
   export MAVEN_OPTS="-Xmx4g"
   ```

### Performance Optimization

- Use `-T` flag for parallel builds: `mvn -T 4 install`
- Skip tests during development: `mvn install -DskipTests`
- Use offline mode if dependencies are cached: `mvn -o test`

## JMH Benchmark Parameters Reference

| Parameter | Description | Example |
|-----------|-------------|---------|
| `-f` | Number of forks | `-f 3` |
| `-wi` | Warmup iterations | `-wi 5` |
| `-i` | Measurement iterations | `-i 10` |
| `-t` | Number of threads | `-t 4` |
| `-rf` | Result format | `-rf json` |
| `-rff` | Result file | `-rff results.json` |
| `-prof` | Profiler | `-prof gc` |
| `-jvmArgs` | JVM arguments | `-jvmArgs "-Xmx2g"` |

## Environment Variables

Set these for optimal development experience:

```bash
# Windows
set MAVEN_OPTS=-Xmx2g -XX:ReservedCodeCacheSize=512m

# Linux/Mac
export MAVEN_OPTS="-Xmx2g -XX:ReservedCodeCacheSize=512m"
```