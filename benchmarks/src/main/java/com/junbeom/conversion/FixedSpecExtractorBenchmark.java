package com.junbeom.conversion;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
@Fork(1)
@Warmup(iterations = 0)
@Measurement(iterations = 1)
public class FixedSpecExtractorBenchmark {

    private FixedSpecExtractor extractor2;
    private FixedSpecExtractor extractor4;

    private String stringInput2;
    private String stringInput4;
    private byte[] byteInput2;
    private byte[] byteInput4;

    @Setup
    public void setup() {
        extractor2 = new FixedSpecExtractor(2);
        extractor4 = new FixedSpecExtractor(4);

        stringInput2 = "-0123.45";
        stringInput4 = "-01234.5678";
        byteInput2 = stringInput2.getBytes(StandardCharsets.UTF_8);
        byteInput4 = stringInput4.getBytes(StandardCharsets.UTF_8);
    }

    @Benchmark
    public void stringToInt2Decimal(Blackhole bh) {
        bh.consume(extractor2.stringToInt(stringInput2));
    }

    @Benchmark
    public void stringToInt4Decimal(Blackhole bh) {
        bh.consume(extractor4.stringToInt(stringInput4));
    }

    @Benchmark
    public void stringToLong2Decimal(Blackhole bh) {
        bh.consume(extractor2.stringToLong(stringInput2));
    }

    @Benchmark
    public void stringToLong4Decimal(Blackhole bh) {
        bh.consume(extractor4.stringToLong(stringInput4));
    }

    @Benchmark
    public void byteToInt2Decimal(Blackhole bh) {
        bh.consume(extractor2.byteToInt(byteInput2));
    }

    @Benchmark
    public void byteToInt4Decimal(Blackhole bh) {
        bh.consume(extractor4.byteToInt(byteInput4));
    }

    @Benchmark
    public void byteToLong2Decimal(Blackhole bh) {
        bh.consume(extractor2.byteToLong(byteInput2));
    }

    @Benchmark
    public void byteToLong4Decimal(Blackhole bh) {
        bh.consume(extractor4.byteToLong(byteInput4));
    }
}