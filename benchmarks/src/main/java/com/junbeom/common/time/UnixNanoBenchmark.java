package com.junbeom.common.time;

import com.junbeom.common.time.TimeStamp;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;

/**
 * JMH benchmark for comparing time nanosecond retrieval methods.
 * Tests System.nanoTime() method performance
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(value = 1, jvmArgs = {
    "--add-opens", "java.base/java.lang=ALL-UNNAMED",
    "--add-opens", "java.base/java.lang.reflect=ALL-UNNAMED", 
    "--add-opens", "java.base/sun.nio.ch=ALL-UNNAMED",
    "--add-opens", "java.base/java.io=ALL-UNNAMED",
    "--add-opens", "java.base/java.nio=ALL-UNNAMED"
})
@State(Scope.Benchmark)
public class UnixNanoBenchmark {

    @Benchmark
    public void getCurrentUnixNano(Blackhole bh) {
        long nano = TimeStamp.getCurrentUnixNano();
        bh.consume(nano);
    }
}