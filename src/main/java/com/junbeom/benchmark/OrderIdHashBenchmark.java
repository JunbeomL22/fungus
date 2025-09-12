package com.junbeom.benchmark;

import com.junbeom.common.order.OrderId;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * JMH benchmark comparing hash performance between OrderId (8 bytes, bit-packed)
 * and PlainOrderId (16 bytes, separate fields).
 * 
 * This benchmark measures the performance difference in hashCode computation
 * between the memory-optimized OrderId and the plain implementation.
 * 
 * @author junbeom
 * @version 1.0
 * @since 2025-09-12
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
@Fork(value = 1, warmups = 1)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
public class OrderIdHashBenchmark {
    
    private static final int ARRAY_SIZE = 10000;
    private OrderId[] orderIds;
    private PlainOrderId[] plainOrderIds;
    private Random random;
    
    @Setup
    public void setup() {
        random = new Random(42); // Fixed seed for reproducible results
        orderIds = new OrderId[ARRAY_SIZE];
        plainOrderIds = new PlainOrderId[ARRAY_SIZE];
        
        // Generate test data with realistic venue and order ID values
        for (int i = 0; i < ARRAY_SIZE; i++) {
            int intVenue = random.nextInt(4); // 0-3 (dummy, krxGeneral, krxKts, smb)
            Long venue = (long) intVenue;
            Long orderId = random.nextLong() & 0x00FFFFFFFFFFFFFFL; // 56-bit order ID
            
            orderIds[i] = new OrderId(venue, orderId);
            plainOrderIds[i] = new PlainOrderId(intVenue, orderId);
        }
    }
    
    @Benchmark
    public void hashCodeOrderId(Blackhole blackhole) {
        for (OrderId orderId : orderIds) {
            blackhole.consume(orderId.hashCode());
        }
    }
    
    @Benchmark
    public void hashCodePlainOrderId(Blackhole blackhole) {
        for (PlainOrderId plainOrderId : plainOrderIds) {
            blackhole.consume(plainOrderId.hashCode());
        }
    }
    
    @Benchmark
    public int singleHashCodeOrderId() {
        return orderIds[random.nextInt(ARRAY_SIZE)].hashCode();
    }
    
    @Benchmark
    public int singleHashCodePlainOrderId() {
        return plainOrderIds[random.nextInt(ARRAY_SIZE)].hashCode();
    }
    
    /**
     * Benchmark equals method performance as well since it's often used with hashCode
     */
    @Benchmark
    public boolean equalsOrderId() {
        int index = random.nextInt(ARRAY_SIZE - 1);
        return orderIds[index].equals(orderIds[index + 1]);
    }
    
    @Benchmark
    public boolean equalsPlainOrderId() {
        int index = random.nextInt(ARRAY_SIZE - 1);
        return plainOrderIds[index].equals(plainOrderIds[index + 1]);
    }
    
    /**
     * Memory access pattern benchmark - sequential hash computation
     */
    @Benchmark
    public void sequentialHashOrderId(Blackhole blackhole) {
        for (int i = 0; i < ARRAY_SIZE; i++) {
            blackhole.consume(orderIds[i].hashCode());
        }
    }
    
    @Benchmark
    public void sequentialHashPlainOrderId(Blackhole blackhole) {
        for (int i = 0; i < ARRAY_SIZE; i++) {
            blackhole.consume(plainOrderIds[i].hashCode());
        }
    }
    
    /**
     * Random access pattern benchmark - simulates real-world hash table usage
     */
    @Benchmark
    public void randomHashOrderId(Blackhole blackhole) {
        for (int i = 0; i < 1000; i++) {
            int index = random.nextInt(ARRAY_SIZE);
            blackhole.consume(orderIds[index].hashCode());
        }
    }
    
    @Benchmark
    public void randomHashPlainOrderId(Blackhole blackhole) {
        for (int i = 0; i < 1000; i++) {
            int index = random.nextInt(ARRAY_SIZE);
            blackhole.consume(plainOrderIds[index].hashCode());
        }
    }
}