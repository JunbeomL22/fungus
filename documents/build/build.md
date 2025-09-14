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
# Build all modules first (required)
mvn clean install

# Run all benchmarks with quick settings using JAR approach (RECOMMENDED)
cd benchmarks && java -cp "target/fungus-benchmarks-1.0-SNAPSHOT.jar;target/dependency/*" org.openjdk.jmh.Main ".*Benchmark.*" -f 1 -wi 1 -i 1

# Alternative: Maven exec approach (may have classpath issues)
mvn -pl benchmarks exec:java -Dexec.mainClass="org.openjdk.jmh.Main" -Dexec.args=".*Benchmark.* -f 1 -wi 1 -i 1"
```

### Run Specific Benchmarks

```bash
# BitManipulation benchmarks (RECOMMENDED JAR approach)
cd benchmarks && java -cp "target/fungus-benchmarks-1.0-SNAPSHOT.jar;target/dependency/*" org.openjdk.jmh.Main BitManipulationBenchmark -f 1 -wi 1 -i 1

# UniqueId benchmarks
cd benchmarks && java -cp "target/fungus-benchmarks-1.0-SNAPSHOT.jar;target/dependency/*" org.openjdk.jmh.Main UniqueIdBenchmark -f 1 -wi 1 -i 1

# OrderId benchmarks
cd benchmarks && java -cp "target/fungus-benchmarks-1.0-SNAPSHOT.jar;target/dependency/*" org.openjdk.jmh.Main OrderIdHashBenchmark -f 1 -wi 1 -i 1

# Unix timestamp benchmarks
cd benchmarks && java -cp "target/fungus-benchmarks-1.0-SNAPSHOT.jar;target/dependency/*" org.openjdk.jmh.Main UnixNanoBenchmark -f 1 -wi 1 -i 1

# Alternative Maven approach (may have classpath issues):
# mvn -pl benchmarks exec:java -Dexec.mainClass="org.openjdk.jmh.Main" -Dexec.args="BitManipulationBenchmark -f 1 -wi 1 -i 1"
```

### Benchmark Configuration Options

```bash
# Quick benchmark run (1 warmup, 1 iteration) - JAR approach
cd benchmarks && java -cp "target/fungus-benchmarks-1.0-SNAPSHOT.jar;target/dependency/*" org.openjdk.jmh.Main BitManipulationBenchmark -f 1 -wi 1 -i 1

# Thorough benchmark run (3 forks, 5 warmups, 10 iterations)
cd benchmarks && java -cp "target/fungus-benchmarks-1.0-SNAPSHOT.jar;target/dependency/*" org.openjdk.jmh.Main BitManipulationBenchmark -f 3 -wi 5 -i 10

# Benchmark with specific thread count
cd benchmarks && java -cp "target/fungus-benchmarks-1.0-SNAPSHOT.jar;target/dependency/*" org.openjdk.jmh.Main BitManipulationBenchmark -t 4

# Benchmark with profiler
cd benchmarks && java -cp "target/fungus-benchmarks-1.0-SNAPSHOT.jar;target/dependency/*" org.openjdk.jmh.Main BitManipulationBenchmark -prof gc
```

### Benchmark Output Options

```bash
# Output results to JSON
cd benchmarks && java -cp "target/fungus-benchmarks-1.0-SNAPSHOT.jar;target/dependency/*" org.openjdk.jmh.Main BitManipulationBenchmark -rf json -rff results.json

# Output results to CSV
cd benchmarks && java -cp "target/fungus-benchmarks-1.0-SNAPSHOT.jar;target/dependency/*" org.openjdk.jmh.Main BitManipulationBenchmark -rf csv -rff results.csv
```

### Alternative Benchmark Execution

```bash
# Using JAR directly with classpath (RECOMMENDED - same as above examples)
cd benchmarks
java -cp "target/fungus-benchmarks-1.0-SNAPSHOT.jar;target/dependency/*" org.openjdk.jmh.Main BitManipulationBenchmark

# With JVM options
cd benchmarks
java -Xmx2g -cp "target/fungus-benchmarks-1.0-SNAPSHOT.jar;target/dependency/*" org.openjdk.jmh.Main BitManipulationBenchmark -f 1 -wi 2 -i 3

# Note: Plain JAR execution (java -jar) may not work due to missing dependencies
# Use the -cp approach instead for reliable execution
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
cd benchmarks && java -cp "target/fungus-benchmarks-1.0-SNAPSHOT.jar;target/dependency/*" org.openjdk.jmh.Main BitManipulationBenchmark -rf json -rff baseline.json

# 2. Make performance improvements
# 3. Test performance again
mvn clean install
cd benchmarks && java -cp "target/fungus-benchmarks-1.0-SNAPSHOT.jar;target/dependency/*" org.openjdk.jmh.Main BitManipulationBenchmark -rf json -rff improved.json

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
cd benchmarks && java -cp "target/fungus-benchmarks-1.0-SNAPSHOT.jar;target/dependency/*" org.openjdk.jmh.Main ".*Benchmark.*" -f 1 -wi 1 -i 1
```

## Troubleshooting

### Common Issues

1. **Benchmark dependency issues**: Ensure core module is installed first
   ```bash
   mvn -pl core install
   ```

2. **JMH ClassNotFoundException**: This error occurs when JMH can't find the ForkedMain class. Use the JAR approach instead of Maven exec:
   ```bash
   # Full build ensures all dependencies are properly installed
   mvn clean install

   # RECOMMENDED: Use JAR with classpath approach
   cd benchmarks && java -cp "target/fungus-benchmarks-1.0-SNAPSHOT.jar;target/dependency/*" org.openjdk.jmh.Main ".*Benchmark.*" -f 1 -wi 1 -i 1

   # If Maven exec fails with classpath issues, always use the JAR approach above
   # The Maven exec approach may have classpath problems:
   # mvn -pl benchmarks exec:java -Dexec.mainClass="org.openjdk.jmh.Main" -Dexec.args=".*Benchmark.* -f 1 -wi 1 -i 1"
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