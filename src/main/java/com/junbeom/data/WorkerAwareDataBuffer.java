package com.junbeom.data;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Optional;

/**
 * Exceptions that can occur in worker-aware data buffer system
 */
class WorkerAwareDataBufferException extends Exception {
    public WorkerAwareDataBufferException(String message) {
        super(message);
    }
}

/**
 * Unique identifier for worker threads
 * 
 * Designed for efficient state tracking and synchronization through bit manipulation
 * Each worker has a unique ID (0-63) and corresponding bit flag
 */
class WorkerId {
    public static final int ID_BOUND = 63;
    
    private final int id;
    private final long idBit;
    private final long idBitNeg;
    
    /**
     * Creates a new WorkerId
     * 
     * @param id Worker ID (0-63)
     * @throws WorkerAwareDataBufferException If ID exceeds 63
     */
    public WorkerId(int id) throws WorkerAwareDataBufferException {
        if (id > ID_BOUND) {
            throw new WorkerAwareDataBufferException("WorkerId bound error - ID must be <= " + ID_BOUND);
        }
        this.id = id;
        this.idBit = 1L << id;
        this.idBitNeg = ~this.idBit;
    }
    
    /**
     * Returns worker completion mask (for clearing status bits)
     */
    public long workDoneMask() {
        return idBitNeg;
    }
    
    public int getId() { return id; }
    public long getIdBit() { return idBit; }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        WorkerId workerId = (WorkerId) obj;
        return id == workerId.id;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
    
    @Override
    public String toString() {
        return String.valueOf(id);
    }
}

/**
 * Lock-free data sharing buffer
 * 
 * Structure for high-performance data sharing between TradingEngine and MessageBroker threads
 * Ensures thread safety through atomic operations and bit manipulation without traditional locks
 * 
 * @param <T> Type of data to be shared
 */
public class WorkerAwareDataBuffer<T> {
    private final AtomicReference<T> dataBuffer;
    private final AtomicLong notifyingStatus; // 0 means idle state
    private final AtomicLong readingStatus;   // 0 means ready for update
    private final long fullStatus;
    
    /**
     * Creates a new WorkerAwareDataBuffer
     * 
     * @param initialData Initial data
     * @param workers List of worker IDs
     * @throws WorkerAwareDataBufferException If there are duplicate worker IDs
     */
    public WorkerAwareDataBuffer(T initialData, List<WorkerId> workers) throws WorkerAwareDataBufferException {
        // Check for duplicate worker IDs
        Set<Integer> workerIds = new HashSet<>();
        for (WorkerId worker : workers) {
            if (!workerIds.add(worker.getId())) {
                throw new WorkerAwareDataBufferException("Duplicate worker id error: " + worker.getId());
            }
        }
        
        // Combine all worker bits with OR operation
        long fullStatus = 0;
        for (WorkerId worker : workers) {
            fullStatus |= worker.getIdBit();
        }
        
        this.dataBuffer = new AtomicReference<>(initialData);
        this.notifyingStatus = new AtomicLong(0);
        this.readingStatus = new AtomicLong(0);
        this.fullStatus = fullStatus;
    }
    
    /**
     * Access mutable data (only when workers are not reading)
     * 
     * @return Modifiable data or null (when other workers are reading)
     */
    public Optional<T> getMut() {
        if (readyToUpdate()) {
            return Optional.of(dataBuffer.get());
        }
        return Optional.empty();
    }
    
    /**
     * Blocking mutable access (wait until safe)
     * 
     * @return Modifiable data
     */
    public T blockingGetMut() {
        while (true) {
            Optional<T> data = getMut();
            if (data.isPresent()) {
                return data.get();
            }
            // Short wait to prevent CPU overload
            Thread.yield();
        }
    }
    
    /**
     * Worker attempts to read data
     * 
     * @param workerId ID of worker attempting to read
     * @return Readable data or null
     */
    public Optional<T> read(WorkerId workerId) {
        if (readyToRead(workerId)) {
            // Mark as reading state
            readingStatus.getAndUpdate(status -> status | workerId.getIdBit());
            return Optional.of(dataBuffer.get());
        }
        return Optional.empty();
    }
    
    /**
     * Blocking read (wait until data is ready)
     * 
     * @param workerId Worker ID
     * @return Data
     */
    public T blockingRead(WorkerId workerId) {
        while (true) {
            Optional<T> data = read(workerId);
            if (data.isPresent()) {
                return data.get();
            }
            Thread.yield();
        }
    }
    
    /**
     * Notify all workers that data is ready
     */
    public void notifyAllWorkers() {
        notifyingStatus.set(fullStatus);
    }
    
    /**
     * Mark worker read completion
     * 
     * @param workerId ID of worker that completed reading
     */
    public void readDone(WorkerId workerId) {
        long mask = workerId.workDoneMask();
        readingStatus.getAndUpdate(status -> status & mask);
        notifyingStatus.getAndUpdate(status -> status & mask);
    }
    
    /**
     * Check if ready for update
     * 
     * @return Whether all workers have completed reading
     */
    public boolean readyToUpdate() {
        return readingStatus.get() == 0;
    }
    
    /**
     * Check specific worker's readiness to read
     * 
     * @param workerId Worker ID
     * @return Whether the worker is ready to read
     */
    public boolean readyToRead(WorkerId workerId) {
        return (notifyingStatus.get() & workerId.getIdBit()) > 0;
    }
    
    /**
     * Returns current notification status
     */
    public long getNotifyingStatus() {
        return notifyingStatus.get();
    }
    
    /**
     * Returns current reading status
     */
    public long getReadingStatus() {
        return readingStatus.get();
    }
    
    /**
     * Display status for debugging
     */
    public void showStatus() {
        System.out.printf("Full Status: %64s%n", 
            Long.toBinaryString(fullStatus));
        System.out.printf("Notifying Status: %64s%n", 
            Long.toBinaryString(notifyingStatus.get()));
        System.out.printf("Reading Status: %64s%n", 
            Long.toBinaryString(readingStatus.get()));
    }
    
    /**
     * Direct data access (debugging only - use with caution)
     * 
     * @return Current data
     */
    public T unsafeRead() {
        return dataBuffer.get();
    }
}