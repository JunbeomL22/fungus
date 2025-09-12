package com.junbeom.benchmark;

import com.junbeom.common.UniqueId;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.HashSet;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * JMH benchmark for comparing hash performance between UniqueId and plain String.
 * Tests with many different strings and random hash access patterns.
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 1, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
@State(Scope.Benchmark)
public class UniqueIdBenchmark {
    
    private static final int NUM_STRINGS = 50;
    private String[] testStrings;
    private UniqueId[] testUniqueIds;
    private HashSet<String> stringSet;
    private HashSet<UniqueId> uniqueIdSet;
    
    @Setup
    public void setup() {
        testStrings = new String[NUM_STRINGS];
        testUniqueIds = new UniqueId[NUM_STRINGS];
        stringSet = new HashSet<>();
        uniqueIdSet = new HashSet<>();
        Random random = new Random(42); // Fixed seed for reproducibility
        
        // Generate diverse test strings
        for (int i = 0; i < NUM_STRINGS; i++) {
            StringBuilder sb = new StringBuilder();
            int length = 500;
            
            for (int j = 0; j < length; j++) {
                if (random.nextBoolean()) {
                    // Add letters
                    sb.append((char) ('a' + random.nextInt(26)));
                } else {
                    // Add numbers
                    sb.append((char) ('0' + random.nextInt(10)));
                }
            }
            
            testStrings[i] = sb.toString();
            testUniqueIds[i] = UniqueId.fromString(testStrings[i]);
            stringSet.add(testStrings[i]);
            uniqueIdSet.add(testUniqueIds[i]);
        }
    }
    
    @Benchmark
    public void hashAllUniqueIds(Blackhole bh) {
        for (UniqueId uniqueId : testUniqueIds) {
            bh.consume(uniqueId.hashCode());
        }
    }
    
    @Benchmark
    public void hashAllStrings(Blackhole bh) {
        for (String str : testStrings) {
            bh.consume(str.hashCode());
        }
    }
    
    @Benchmark
    public void equalityUniqueId(Blackhole bh) {
        int index = ThreadLocalRandom.current().nextInt(NUM_STRINGS);
        UniqueId target = testUniqueIds[index];
        
        for (UniqueId uniqueId : testUniqueIds) {
            bh.consume(uniqueId.equals(target));
        }
    }
    
    @Benchmark
    public void equalityString(Blackhole bh) {
        int index = ThreadLocalRandom.current().nextInt(NUM_STRINGS);
        String target = testStrings[index];
        
        for (String str : testStrings) {
            bh.consume(str.equals(target));
        }
    }
    
    @Benchmark
    public void hashSetIterationUniqueId(Blackhole bh) {
        for (UniqueId uniqueId : uniqueIdSet) {
            bh.consume(uniqueId.hashCode());
        }
    }
    
    @Benchmark
    public void hashSetIterationString(Blackhole bh) {
        for (String str : stringSet) {
            bh.consume(str.hashCode());
        }
    }
}