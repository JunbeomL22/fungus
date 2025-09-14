package com.junbeom.data;

/**
 * Unique identifier for worker threads
 *
 * Designed for efficient state tracking and synchronization through bit manipulation
 * Each worker has a unique ID (0-63) and corresponding bit flag
 */
public class WorkerId {
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