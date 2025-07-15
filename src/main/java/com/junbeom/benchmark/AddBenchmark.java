package com.junbeom.benchmark;

import com.junbeom.utils.Add;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;   
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.Runner;
// options and option builder
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
// TimeUnit
import java.util.concurrent.TimeUnit;

/**
 * Benchmark for the Add class.
 * 
 * main concepts of JMH:
 * - @Benchmark: Marks a method as a benchmark method.
 * - @BenchmarkMode: Defines the mode of the benchmark (e.g., throughput, average time).
 * - @Warmup: Specifies the warmup iterations before the actual benchmarking.
 * - @State: Defines the scope of the benchmark state (e.g., Thread, Benchmark, Group).   
 * - @Measurement: Specifies the number of measurement iterations.
 * - @Fork: Defines the number of forks for the benchmark (minimize JVM warmup effects).
 */

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
public class AddBenchmark {
    /**
     * class to manage benchmarking of the Add class.
     * @State(Scope.Benchmark) indicates that the state is shared across all benchmark methods.
     * @State(Scope.Thread) would indicate that the state is unique to each thread.
     * @State(Scope.Group) would indicate that the state is shared across all benchmark methods in a group.
     */
    @State(Scope.Benchmark)
    public static class BenchmarkState {
        public int intA = 12345;
        public int intB = 67890;
        public double doubleA = 123.456;
        public double doubleB = 789.012;
        public int[] numbers = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
    }

    @Benchmark
    public void benchmarkAddInt(BenchmarkState state, Blackhole blackhole) {
        int result = Add.add(state.intA, state.intB);
        blackhole.consume(result);
    }
}
