package com.junbeom.data;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("WorkerAwareDataBuffer Tests")
public class WorkerAwareDataBufferTest {

    @Nested
    @DisplayName("WorkerId Tests")
    class WorkerIdTests {

        @Test
        @DisplayName("WorkerId creation with valid ID")
        void testWorkerIdCreation() throws WorkerAwareDataBufferException {
            WorkerId workerId = new WorkerId(5);
            assertEquals(5, workerId.getId());
            assertEquals(1L << 5, workerId.getIdBit());
            assertEquals(~(1L << 5), workerId.workDoneMask());
        }

        @ParameterizedTest
        @ValueSource(ints = {0, 1, 32, 63})
        @DisplayName("WorkerId creation with boundary values")
        void testWorkerIdBoundaryValues(int id) throws WorkerAwareDataBufferException {
            WorkerId workerId = new WorkerId(id);
            assertEquals(id, workerId.getId());
            assertEquals(1L << id, workerId.getIdBit());
        }

        @Test
        @DisplayName("WorkerId creation with invalid ID throws exception")
        void testWorkerIdInvalidId() {
            assertThrows(WorkerAwareDataBufferException.class, () -> new WorkerId(64));
            assertThrows(WorkerAwareDataBufferException.class, () -> new WorkerId(100));
        }

        @Test
        @DisplayName("WorkerId equality and hashCode")
        void testWorkerIdEquality() throws WorkerAwareDataBufferException {
            WorkerId worker1 = new WorkerId(5);
            WorkerId worker2 = new WorkerId(5);
            WorkerId worker3 = new WorkerId(6);

            assertEquals(worker1, worker2);
            assertNotEquals(worker1, worker3);
            assertEquals(worker1.hashCode(), worker2.hashCode());
            assertNotEquals(worker1.hashCode(), worker3.hashCode());
        }

        @Test
        @DisplayName("WorkerId toString")
        void testWorkerIdToString() throws WorkerAwareDataBufferException {
            WorkerId workerId = new WorkerId(42);
            assertEquals("42", workerId.toString());
        }
    }

    @Nested
    @DisplayName("WorkerAwareDataBuffer Basic Functionality")
    class BasicFunctionalityTests {

        private WorkerAwareDataBuffer<String> buffer;
        private List<WorkerId> workers;

        @BeforeEach
        void setUp() throws WorkerAwareDataBufferException {
            workers = List.of(
                new WorkerId(0),
                new WorkerId(1),
                new WorkerId(2)
            );
            buffer = new WorkerAwareDataBuffer<>("initial", workers);
        }

        @Test
        @DisplayName("Initial state allows mutable access")
        void testInitialState() {
            assertTrue(buffer.readyToUpdate());
            assertEquals(0, buffer.getReadingStatus());
            assertEquals(0, buffer.getNotifyingStatus());
            
            Optional<String> data = buffer.getMut();
            assertTrue(data.isPresent());
            assertEquals("initial", data.get());
        }

        @Test
        @DisplayName("Workers cannot read before notification")
        void testWorkersCannotReadBeforeNotification() throws WorkerAwareDataBufferException {
            WorkerId worker = new WorkerId(0);
            assertFalse(buffer.readyToRead(worker));
            
            Optional<String> data = buffer.read(worker);
            assertTrue(data.isEmpty());
        }

        @Test
        @DisplayName("Workers can read after notification")
        void testWorkersCanReadAfterNotification() throws WorkerAwareDataBufferException {
            buffer.notifyAllWorkers();
            
            for (WorkerId worker : workers) {
                assertTrue(buffer.readyToRead(worker));
                Optional<String> data = buffer.read(worker);
                assertTrue(data.isPresent());
                assertEquals("initial", data.get());
            }
        }

        @Test
        @DisplayName("Cannot update while workers are reading")
        void testCannotUpdateWhileReading() throws WorkerAwareDataBufferException {
            buffer.notifyAllWorkers();
            
            // One worker starts reading
            WorkerId worker = new WorkerId(0);
            buffer.read(worker);
            
            assertFalse(buffer.readyToUpdate());
            assertTrue(buffer.getMut().isEmpty());
        }

        @Test
        @DisplayName("Can update after all workers finish reading")
        void testCanUpdateAfterAllWorkersFinish() throws WorkerAwareDataBufferException {
            buffer.notifyAllWorkers();
            
            // All workers read and finish
            for (WorkerId worker : workers) {
                buffer.read(worker);
                buffer.readDone(worker);
            }
            
            assertTrue(buffer.readyToUpdate());
            Optional<String> data = buffer.getMut();
            assertTrue(data.isPresent());
            assertEquals("initial", data.get());
        }

        @Test
        @DisplayName("Blocking getMut waits for workers to finish")
        void testBlockingGetMut() throws WorkerAwareDataBufferException, InterruptedException {
            buffer.notifyAllWorkers();
            
            // One worker starts reading
            WorkerId worker = new WorkerId(0);
            buffer.read(worker);
            
            AtomicBoolean gotData = new AtomicBoolean(false);
            Thread thread = new Thread(() -> {
                String data = buffer.blockingGetMut();
                assertEquals("initial", data);
                gotData.set(true);
            });
            
            thread.start();
            Thread.sleep(100); // Give time for thread to start
            assertFalse(gotData.get());
            
            // Finish reading
            buffer.readDone(worker);
            thread.join(1000);
            assertTrue(gotData.get());
        }

        @Test
        @DisplayName("Blocking read waits for notification")
        void testBlockingRead() throws WorkerAwareDataBufferException, InterruptedException {
            WorkerId worker = new WorkerId(0);
            
            AtomicBoolean gotData = new AtomicBoolean(false);
            Thread thread = new Thread(() -> {
                String data = buffer.blockingRead(worker);
                assertEquals("initial", data);
                gotData.set(true);
            });
            
            thread.start();
            Thread.sleep(100); // Give time for thread to start
            assertFalse(gotData.get());
            
            // Notify workers
            buffer.notifyAllWorkers();
            thread.join(1000);
            assertTrue(gotData.get());
        }

        @Test
        @DisplayName("Unsafe read always returns data")
        void testUnsafeRead() {
            assertEquals("initial", buffer.unsafeRead());
            
            buffer.notifyAllWorkers();
            assertEquals("initial", buffer.unsafeRead());
        }
    }

    @Nested
    @DisplayName("Exception Handling Tests")
    class ExceptionHandlingTests {

        @Test
        @DisplayName("Duplicate worker IDs throw exception")
        void testDuplicateWorkerIds() throws WorkerAwareDataBufferException {
            List<WorkerId> duplicateWorkers = List.of(
                new WorkerId(0),
                new WorkerId(1),
                new WorkerId(0) // Duplicate
            );
            
            assertThrows(WorkerAwareDataBufferException.class, 
                () -> new WorkerAwareDataBuffer<>("test", duplicateWorkers));
        }

        @Test
        @DisplayName("Empty worker list is allowed")
        void testEmptyWorkerList() throws WorkerAwareDataBufferException {
            WorkerAwareDataBuffer<String> buffer = new WorkerAwareDataBuffer<>("test", List.of());
            
            assertTrue(buffer.readyToUpdate());
            assertEquals("test", buffer.getMut().get());
        }
    }

    @Nested
    @DisplayName("Thread Safety Tests")
    class ThreadSafetyTests {

        @Test
        @DisplayName("Concurrent worker operations are thread-safe")
        void testConcurrentWorkerOperations() throws Exception {
            List<WorkerId> workers = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                workers.add(new WorkerId(i));
            }
            
            WorkerAwareDataBuffer<Integer> buffer = new WorkerAwareDataBuffer<>(0, workers);
            ExecutorService executor = Executors.newFixedThreadPool(10);
            CountDownLatch startLatch = new CountDownLatch(1);
            CountDownLatch doneLatch = new CountDownLatch(10);
            AtomicInteger readCount = new AtomicInteger(0);

            // Start all workers
            for (int i = 0; i < 10; i++) {
                final int workerId = i;
                executor.submit(() -> {
                    try {
                        startLatch.await();
                        WorkerId worker = new WorkerId(workerId);
                        
                        // Try to read multiple times
                        for (int j = 0; j < 100; j++) {
                            buffer.notifyAllWorkers();
                            Optional<Integer> data = buffer.read(worker);
                            if (data.isPresent()) {
                                readCount.incrementAndGet();
                                buffer.readDone(worker);
                            }
                            Thread.yield();
                        }
                    } catch (Exception e) {
                        fail("Exception in worker thread: " + e.getMessage());
                    } finally {
                        doneLatch.countDown();
                    }
                });
            }

            startLatch.countDown();
            assertTrue(doneLatch.await(10, TimeUnit.SECONDS));
            assertTrue(readCount.get() > 0);
            
            executor.shutdown();
        }

        @Test
        @DisplayName("Sequential producer-consumer pattern works correctly")
        void testProducerConsumerPattern() throws Exception {
            List<WorkerId> workers = List.of(new WorkerId(0));
            WorkerAwareDataBuffer<Integer> buffer = new WorkerAwareDataBuffer<>(42, workers);
            WorkerId worker = new WorkerId(0);
            
            // Simulate producer-consumer cycle
            for (int i = 0; i < 5; i++) {
                // Producer phase: update data
                Optional<Integer> mutData = buffer.getMut();
                assertTrue(mutData.isPresent());
                assertEquals(42, mutData.get());
                
                // Notify consumer
                buffer.notifyAllWorkers();
                
                // Consumer phase: read data
                assertTrue(buffer.readyToRead(worker));
                Optional<Integer> data = buffer.read(worker);
                assertTrue(data.isPresent());
                assertEquals(42, data.get());
                
                // Consumer finishes
                buffer.readDone(worker);
                
                // Verify back to initial state
                assertTrue(buffer.readyToUpdate());
                assertFalse(buffer.readyToRead(worker));
            }
        }
    }

    @Nested
    @DisplayName("Status and Debugging Tests")
    class StatusTests {

        @Test
        @DisplayName("Status methods return correct values")
        void testStatusMethods() throws WorkerAwareDataBufferException {
            List<WorkerId> workers = List.of(new WorkerId(0), new WorkerId(2));
            WorkerAwareDataBuffer<String> buffer = new WorkerAwareDataBuffer<>("test", workers);

            // Initial state
            assertEquals(0, buffer.getNotifyingStatus());
            assertEquals(0, buffer.getReadingStatus());

            // After notification
            buffer.notifyAllWorkers();
            assertEquals(0b101L, buffer.getNotifyingStatus()); // bits 0 and 2 set

            // After one worker reads
            buffer.read(new WorkerId(0));
            assertEquals(0b001L, buffer.getReadingStatus()); // bit 0 set

            // After worker finishes
            buffer.readDone(new WorkerId(0));
            assertEquals(0, buffer.getReadingStatus());
            assertEquals(0b100L, buffer.getNotifyingStatus()); // only bit 2 set
        }

        @Test
        @DisplayName("showStatus does not throw exception")
        void testShowStatus() throws WorkerAwareDataBufferException {
            List<WorkerId> workers = List.of(new WorkerId(0));
            WorkerAwareDataBuffer<String> buffer = new WorkerAwareDataBuffer<>("test", workers);
            
            assertDoesNotThrow(() -> buffer.showStatus());
        }
    }
}