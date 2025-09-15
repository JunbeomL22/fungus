# WorkerAwareDataBuffer

**File Location**: [src/main/java/com/junbeom/data/WorkerAwareDataBuffer.java](../../../core/src/main/java/com/junbeom/data/WorkerAwareDataBuffer.java)

## Overview

`WorkerAwareDataBuffer<T>` is a lock-free, high-performance data sharing buffer designed for concurrent access between multiple worker threads, specifically optimized for TradingEngine and MessageBroker thread communication. It ensures thread safety through atomic operations and bit manipulation without traditional synchronization locks.

## Key Features

- **Lock-free design** using atomic operations
- **Bit manipulation** for efficient status tracking (up to 64 workers)
- **Thread-safe** data sharing without blocking
- **Worker-aware** notification system
- **Generic type support** for any data type

## Core Components

### WorkerId

A unique identifier for worker threads with efficient bit manipulation support.

**Constraints:**
- Worker ID range: 0-63 (64 workers maximum)
- Each worker has a unique bit position in status masks

**Key Methods:**
- `getId()`: Returns the worker's numeric ID
- `getIdBit()`: Returns the worker's bit flag (1L << id)
- `workDoneMask()`: Returns completion mask for clearing status bits

### WorkerAwareDataBuffer<T>

The main buffer class that manages data sharing between workers.

## Architecture

### Status Tracking

The buffer uses two atomic status fields:
- **notifyingStatus**: Tracks which workers have pending notifications (0 = idle)
- **readingStatus**: Tracks which workers are currently reading (0 = ready for update)
- **fullStatus**: Combined mask of all registered workers

### Bit Manipulation Strategy

Each worker is assigned a unique bit position (0-63) in 64-bit status masks:
```
Worker 0: bit 0 (0x0000000000000001)
Worker 1: bit 1 (0x0000000000000002)
Worker 2: bit 2 (0x0000000000000004)
...
Worker 63: bit 63 (0x8000000000000000)
```

## API Reference

### Constructor

```java
public WorkerAwareDataBuffer(T initialData, List<WorkerId> workers)
    throws WorkerAwareDataBufferException
```

Creates a new buffer with initial data and worker list. Validates for duplicate worker IDs.

### Data Access Methods

#### Non-blocking Access

```java
public Optional<T> getMut()
```
Returns modifiable data if no workers are reading, otherwise returns empty Optional.

```java
public Optional<T> read(WorkerId workerId)
```
Attempts to read data for specified worker. Marks worker as reading if successful.

#### Blocking Access

```java
public T blockingGetMut()
```
Waits until all workers finish reading, then returns modifiable data.

```java
public T blockingRead(WorkerId workerId)
```
Waits until data is ready for the specified worker, then returns data.

### Notification Methods

```java
public void notifyAllWorkers()
```
Sets notification status for all registered workers.

```java
public void readDone(WorkerId workerId)
```
Marks worker as finished reading, clearing both reading and notification status.

### Status Query Methods

```java
public boolean readyToUpdate()
```
Returns true when no workers are reading (safe for data modification).

```java
public boolean readyToRead(WorkerId workerId)
```
Returns true when the specified worker has a pending notification.

### Debug Methods

```java
public void showStatus()
```
Prints binary representation of all status masks for debugging.

```java
public T unsafeRead()
```
Direct data access bypassing safety checks (debugging only).

## Usage Patterns

### Typical Producer Pattern
```java
// Producer thread
Optional<T> data = buffer.getMut();
if (data.isPresent()) {
    // Modify data safely
    updateData(data.get());
    // Notify all workers
    buffer.notifyAllWorkers();
}
```

### Typical Consumer Pattern
```java
// Worker thread
Optional<T> data = buffer.read(workerId);
if (data.isPresent()) {
    // Process data
    processData(data.get());
    // Mark completion
    buffer.readDone(workerId);
}
```

### Blocking Operations
```java
// When guaranteed access is needed
T data = buffer.blockingRead(workerId);
processData(data);
buffer.readDone(workerId);
```

## Thread Safety Guarantees

1. **Lock-free operations**: All methods use atomic operations without blocking
2. **Consistent state**: Status updates are atomic and consistent
3. **Memory visibility**: AtomicReference ensures proper memory visibility
4. **No data races**: Bit manipulation prevents conflicting status updates

## Characteristics

- **Space Complexity**: O(1) additional overhead per worker
- **Scalability**: Supports up to 64 concurrent workers
- **Memory Model**: Uses atomic operations for memory safety

## Error Handling

### WorkerAwareDataBufferException

Thrown in these scenarios:
- Worker ID exceeds maximum bound (63)
- Duplicate worker IDs in constructor

## Best Practices

1. **Worker ID Management**: Ensure unique IDs within 0-63 range
2. **Completion Tracking**: Always call `readDone()` after reading
3. **Status Checking**: Use non-blocking methods in hot paths
4. **Error Handling**: Handle WorkerAwareDataBufferException during initialization

## Limitations

- Maximum 64 concurrent workers
- No built-in worker lifecycle management
- Assumes cooperative worker behavior (calling readDone())
- Single data buffer per instance (no queue functionality)

## Related Components

- Used by TradingEngine and MessageBroker threads
- Integrates with worker thread pool management
- Part of the high-performance data sharing infrastructure