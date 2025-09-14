package com.junbeom.conversion;

import com.junbeom.conversion.BitManipulation;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.math.BigInteger;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
@Fork(1) // Run in same JVM to avoid classpath issues
@Warmup(iterations = 2, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
public class BitManipulationBenchmark {

    // Test data for benchmarks
    private short twoByteData;
    private int fourByteData;
    private long eightByteData;
    private long sixteenUpperData;
    private long sixteenLowerData;
    private BigInteger sixteenByteData;
    private long combinedSixteenData; // For fair comparison with splitting cost

    // Equivalent string representations for standard library comparison
    private String twoDigitString;
    private String fourDigitString;
    private String eightDigitString;
    private String sixteenDigitString;

    @Setup
    public void setup() {
        // Setup packed decimal test data
        twoByteData = (short) 0x0409;      // digits 4,9 -> 49
        fourByteData = 0x04030209;         // digits 4,3,2,9 -> 4329
        eightByteData = 0x0807060504030209L; // digits 8,7,6,5,4,3,2,9 -> 87654329
        sixteenUpperData = 0x0807060504030209L; // digits 8,7,6,5,4,3,2,9 -> 87654329
        sixteenLowerData = 0x0807060504030209L; // digits 8,7,6,5,4,3,2,9 -> 87654329
        sixteenByteData = new BigInteger("09080706050403020109080706050403", 16); // -> 9876543210987654

        // Create a combined value for fair benchmarking (simulating real usage)
        // In real usage, you'd have a single 128-bit value that needs to be split
        combinedSixteenData = ((long)sixteenUpperData << 64) | sixteenLowerData;

        // Corresponding string representations for standard library parsing
        twoDigitString = "49";
        fourDigitString = "4329";
        eightDigitString = "87654329";
        sixteenDigitString = "9876543210987654";
    }

    // =====================================================
    // Two-byte conversion benchmarks
    // =====================================================

    @Benchmark
    public short bitManipulationTwoToUnsignedShort() {
        return BitManipulation.twoToUnsignedShort(twoByteData);
    }

    @Benchmark
    public short standardLibraryParseShort() {
        return Short.parseShort(twoDigitString);
    }

    // =====================================================
    // Four-byte conversion benchmarks
    // =====================================================

    @Benchmark
    public int bitManipulationFourToUnsignedInt() {
        return BitManipulation.fourToUnsignedInt(fourByteData);
    }

    @Benchmark
    public int standardLibraryParseInt() {
        return Integer.parseInt(fourDigitString);
    }

    // =====================================================
    // Eight-byte conversion benchmarks
    // =====================================================

    @Benchmark
    public long bitManipulationEightToUnsignedLong() {
        return BitManipulation.eightToUnsignedLong(eightByteData);
    }

    @Benchmark
    public long standardLibraryParseLong() {
        return Long.parseLong(eightDigitString);
    }

    // =====================================================
    // Sixteen-byte conversion benchmarks
    // =====================================================

    @Benchmark
    public long bitManipulationSixteenToUnsignedLong() {
        return BitManipulation.sixteenToUnsignedLong(sixteenUpperData, sixteenLowerData);
    }

    @Benchmark
    public long standardLibraryParseLong16() {
        return Long.parseLong(sixteenDigitString);
    }
}