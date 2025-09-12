package com.junbeom.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive unit tests for UniqueId class.
 * Tests cover functionality, performance characteristics, thread safety, and edge cases.
 */
@DisplayName("UniqueId Tests")
class UniqueIdTest {
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Nested
    @DisplayName("Serialization/Deserialization Tests")
    class SerializationTests {
        @Test
        @DisplayName("Should serialize UniqueId to JSON")
        void shouldSerializeToJson() throws Exception {
            UniqueId id = UniqueId.fromString("test");
            String json = objectMapper.writeValueAsString(id);
            assertEquals("\"test\"", json, "Serialized JSON: " + json);
        }
    }

    @Nested
    @DisplayName("Basic Functionality Tests")
    class BasicFunctionalityTests {

        @Test
        @DisplayName("Should create UniqueId from string")
        void shouldCreateFromString() {
            UniqueId id = UniqueId.fromString("test");
            assertNotNull(id);
            assertEquals("test", id.toString());
        }

        @Test
        @DisplayName("Should handle empty string")
        void shouldHandleEmptyString() {
            UniqueId id = UniqueId.fromString("");
            assertNotNull(id);
            assertEquals("", id.toString());
        }

        @Test
        @DisplayName("Should provide default value")
        void shouldProvideDefaultValue() {
            UniqueId defaultId = UniqueId.defaultValue();
            assertNotNull(defaultId);
            assertEquals("", defaultId.toString());
        }

        @Test
        @DisplayName("Should throw NPE for null string")
        void shouldThrowNPEForNullString() {
            assertThrows(NullPointerException.class, () -> {
                UniqueId.fromString(null);
            });
        }

        @Test
        @DisplayName("Should handle special characters")
        void shouldHandleSpecialCharacters() {
            String specialString = "test\nline\ttab\rreturn\"quotes'apostrophe";
            UniqueId id = UniqueId.fromString(specialString);
            assertEquals(specialString, id.toString());
        }

        @Test
        @DisplayName("Should handle Unicode characters")
        void shouldHandleUnicodeCharacters() {
            String unicodeString = "测试字符串 🚀 émoji ñoñó";
            UniqueId id = UniqueId.fromString(unicodeString);
            assertEquals(unicodeString, id.toString());
        }

        @ParameterizedTest
        @ValueSource(strings = {"", "a", "short", "medium_length_string", 
                                "very_long_string_that_exceeds_normal_identifier_length_and_keeps_going_with_more_text"})
        @DisplayName("Should handle strings of various lengths")
        void shouldHandleVariousLengths(String input) {
            UniqueId id = UniqueId.fromString(input);
            assertEquals(input, id.toString());
        }
    }

    @Nested
    @DisplayName("Equality and Hashing Tests")
    class EqualityAndHashingTests {

        @Test
        @DisplayName("Should be equal for same string")
        void shouldBeEqualForSameString() {
            UniqueId id1 = UniqueId.fromString("test");
            UniqueId id2 = UniqueId.fromString("test");
            
            assertEquals(id1, id2);
            assertTrue(id1.equals(id2));
            assertTrue(id2.equals(id1));
        }

        @Test
        @DisplayName("Should not be equal for different strings")
        void shouldNotBeEqualForDifferentStrings() {
            UniqueId id1 = UniqueId.fromString("test1");
            UniqueId id2 = UniqueId.fromString("test2");
            
            assertNotEquals(id1, id2);
            assertFalse(id1.equals(id2));
            assertFalse(id2.equals(id1));
        }

        @Test
        @DisplayName("Should have same hash code for equal objects")
        void shouldHaveSameHashCodeForEqualObjects() {
            UniqueId id1 = UniqueId.fromString("test");
            UniqueId id2 = UniqueId.fromString("test");
            
            assertEquals(id1.hashCode(), id2.hashCode());
        }

        @Test
        @DisplayName("Should work correctly in HashSet")
        void shouldWorkInHashSet() {
            Set<UniqueId> set = new HashSet<>();
            
            UniqueId id1 = UniqueId.fromString("test");
            UniqueId id2 = UniqueId.fromString("test"); // Same string
            UniqueId id3 = UniqueId.fromString("different");
            
            set.add(id1);
            set.add(id2); // Should not add duplicate
            set.add(id3);
            
            assertEquals(2, set.size());
            assertTrue(set.contains(id1));
            assertTrue(set.contains(id2));
            assertTrue(set.contains(id3));
        }

        @Test
        @DisplayName("Should work correctly in HashMap")
        void shouldWorkInHashMap() {
            Map<UniqueId, String> map = new HashMap<>();
            
            UniqueId id1 = UniqueId.fromString("key1");
            UniqueId id2 = UniqueId.fromString("key1"); // Same string
            UniqueId id3 = UniqueId.fromString("key2");
            
            map.put(id1, "value1");
            map.put(id2, "value2"); // Should overwrite
            map.put(id3, "value3");
            
            assertEquals(2, map.size());
            assertEquals("value2", map.get(id1));
            assertEquals("value2", map.get(id2));
            assertEquals("value3", map.get(id3));
        }

        @Test
        @DisplayName("Should handle equals with null and different types")
        void shouldHandleEqualsEdgeCases() {
            UniqueId id = UniqueId.fromString("test");
            
            assertFalse(id.equals(null));
            assertFalse(id.equals("test")); // Different type
            assertFalse(id.equals(42)); // Different type
            assertTrue(id.equals(id)); // Self equality
        }
    }

    @Nested
    @DisplayName("Caching and Deduplication Tests")
    class CachingTests {

        @Test
        @DisplayName("Should reuse existing IDs for same strings")
        void shouldReuseExistingIds() {
            String testString = "cache_test_" + System.currentTimeMillis();
            
            UniqueId id1 = UniqueId.fromString(testString);
            UniqueId id2 = UniqueId.fromString(testString);
            
            assertEquals(id1.getId(), id2.getId());
            assertSame(id1.toString(), id2.toString()); // Should be same string reference
        }

        @Test
        @DisplayName("Should increment count for new unique strings")
        void shouldIncrementCountForNewStrings() {
            int initialCount = UniqueId.count();
            
            String uniqueString = "unique_" + System.currentTimeMillis() + "_" + Math.random();
            UniqueId.fromString(uniqueString);
            
            assertTrue(UniqueId.count() > initialCount);
        }

        @Test
        @DisplayName("Should not increment count for existing strings")
        void shouldNotIncrementCountForExistingStrings() {
            String testString = "existing_" + System.currentTimeMillis();
            UniqueId.fromString(testString); // First creation
            
            int countAfterFirst = UniqueId.count();
            UniqueId.fromString(testString); // Second creation
            int countAfterSecond = UniqueId.count();
            
            assertEquals(countAfterFirst, countAfterSecond);
        }
    }

    @Nested
    @DisplayName("Concatenation Tests")
    class ConcatenationTests {

        @Test
        @DisplayName("Should concatenate with single object")
        void shouldConcatenateWithSingleObject() {
            UniqueId base = UniqueId.fromString("base");
            UniqueId result = base.add("_suffix");
            
            assertEquals("base_suffix", result.toString());
        }

        @Test
        @DisplayName("Should concatenate with multiple objects")
        void shouldConcatenateWithMultipleObjects() {
            UniqueId base = UniqueId.fromString("base");
            UniqueId result = base.add("_middle", "_end", 123);
            
            assertEquals("base_middle_end123", result.toString());
        }

        @Test
        @DisplayName("Should concatenate with various types")
        void shouldConcatenateWithVariousTypes() {
            UniqueId base = UniqueId.fromString("prefix");
            UniqueId result = base.add(42, true, 3.14, 'X');
            
            assertEquals("prefix42true3.14X", result.toString());
        }

        @Test
        @DisplayName("Should handle empty concatenation")
        void shouldHandleEmptyConcatenation() {
            UniqueId base = UniqueId.fromString("base");
            UniqueId result = base.add();
            
            assertEquals("base", result.toString());
        }

        @Test
        @DisplayName("Should handle null values in concatenation")
        void shouldHandleNullInConcatenation() {
            UniqueId base = UniqueId.fromString("base");
            UniqueId result = base.add((Object) null);
            
            assertEquals("basenull", result.toString());
        }
    }

    @Nested
    @DisplayName("Merge Tests")
    class MergeTests {

        @Test
        @DisplayName("Should merge multiple elements")
        void shouldMergeMultipleElements() {
            UniqueId result = UniqueId.merged("part1", "part2", "part3");
            assertEquals("part1part2part3", result.toString());
        }

        @Test
        @DisplayName("Should merge with various types")
        void shouldMergeWithVariousTypes() {
            UniqueId result = UniqueId.merged("str", 42, true, 3.14);
            assertEquals("str42true3.14", result.toString());
        }

        @Test
        @DisplayName("Should handle empty merge")
        void shouldHandleEmptyMerge() {
            UniqueId result = UniqueId.merged();
            assertEquals("", result.toString());
        }

        @Test
        @DisplayName("Should handle single element merge")
        void shouldHandleSingleElementMerge() {
            UniqueId result = UniqueId.merged("single");
            assertEquals("single", result.toString());
        }
    }

    @Nested
    @DisplayName("Thread Safety Tests")
    class ThreadSafetyTests {

        @Test
        @DisplayName("Should be thread-safe for concurrent creation")
        void shouldBeThreadSafeForConcurrentCreation() throws InterruptedException {
            int threadCount = 10;
            int iterationsPerThread = 100;
            String baseString = "concurrent_test_";
            
            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
            CountDownLatch startLatch = new CountDownLatch(1);
            CountDownLatch endLatch = new CountDownLatch(threadCount);
            
            Map<Integer, UniqueId> results = new ConcurrentHashMap<>();
            
            // Create threads that will all try to create UniqueIds concurrently
            for (int t = 0; t < threadCount; t++) {
                final int threadId = t;
                executor.submit(() -> {
                    try {
                        startLatch.await(); // Wait for signal to start
                        
                        for (int i = 0; i < iterationsPerThread; i++) {
                            String str = baseString + (i % 10); // Reuse some strings
                            UniqueId id = UniqueId.fromString(str);
                            results.put(threadId * iterationsPerThread + i, id);
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        endLatch.countDown();
                    }
                });
            }
            
            startLatch.countDown(); // Start all threads
            assertTrue(endLatch.await(10, TimeUnit.SECONDS)); // Wait for completion
            executor.shutdown();
            
            // Verify results
            assertEquals(threadCount * iterationsPerThread, results.size());
            
            // Verify that same strings have same IDs across threads
            for (int i = 0; i < 10; i++) {
                String testStr = baseString + i;
                Set<Long> idsForString = new HashSet<>();
                
                results.values().stream()
                    .filter(id -> id.toString().equals(testStr))
                    .forEach(id -> idsForString.add(id.getId()));
                
                assertEquals(1, idsForString.size(), 
                    "All instances of '" + testStr + "' should have the same ID");
            }
        }

        @RepeatedTest(5)
        @DisplayName("Should maintain consistency under concurrent access")
        void shouldMaintainConsistencyUnderConcurrentAccess() throws InterruptedException {
            String testString = "consistency_test_" + System.currentTimeMillis();
            int threadCount = 20;
            
            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
            CountDownLatch latch = new CountDownLatch(threadCount);
            List<UniqueId> results = Collections.synchronizedList(new ArrayList<>());
            
            for (int i = 0; i < threadCount; i++) {
                executor.submit(() -> {
                    try {
                        UniqueId id = UniqueId.fromString(testString);
                        results.add(id);
                    } finally {
                        latch.countDown();
                    }
                });
            }
            
            assertTrue(latch.await(5, TimeUnit.SECONDS));
            executor.shutdown();
            
            assertEquals(threadCount, results.size());
            
            // All results should be equal and have the same internal ID
            UniqueId first = results.get(0);
            for (UniqueId id : results) {
                assertEquals(first, id);
                assertEquals(first.getId(), id.getId());
                assertEquals(testString, id.toString());
            }
        }
    }

    @Nested
    @DisplayName("Performance and Edge Cases")
    class PerformanceAndEdgeCaseTests {

        @Test
        @DisplayName("Should handle very long strings")
        void shouldHandleVeryLongStrings() {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 10000; i++) {
                sb.append("x");
            }
            String longString = sb.toString();
            
            UniqueId id = UniqueId.fromString(longString);
            assertEquals(longString, id.toString());
        }

        @Test
        @DisplayName("Should be efficient for repeated access")
        void shouldBeEfficientForRepeatedAccess() {
            String testString = "performance_test";
            UniqueId id = UniqueId.fromString(testString);
            
            // Multiple toString calls should be fast
            long startTime = System.nanoTime();
            for (int i = 0; i < 1000; i++) {
                assertEquals(testString, id.toString());
            }
            long endTime = System.nanoTime();
            
            // Should complete very quickly (arbitrary threshold)
            assertTrue((endTime - startTime) < 10_000_000, // 10ms
                "Repeated toString() calls should be very fast");
        }

        @Test
        @DisplayName("Should handle many unique strings")
        void shouldHandleManyUniqueStrings() {
            int count = 1000;
            List<UniqueId> ids = new ArrayList<>();
            
            for (int i = 0; i < count; i++) {
                ids.add(UniqueId.fromString("unique_string_" + i));
            }
            
            // Verify all are unique
            Set<UniqueId> uniqueIds = new HashSet<>(ids);
            assertEquals(count, uniqueIds.size());
            
            // Verify all have correct string representation
            for (int i = 0; i < count; i++) {
                assertEquals("unique_string_" + i, ids.get(i).toString());
            }
        }

        @Test
        @DisplayName("Should handle rapid creation and equality checks")
        void shouldHandleRapidOperations() {
            List<UniqueId> batch1 = IntStream.range(0, 100)
                .mapToObj(i -> UniqueId.fromString("batch_" + (i % 20)))
                .toList();
                
            List<UniqueId> batch2 = IntStream.range(0, 100)
                .mapToObj(i -> UniqueId.fromString("batch_" + (i % 20)))
                .toList();
            
            // Verify equality
            for (int i = 0; i < 100; i++) {
                assertEquals(batch1.get(i), batch2.get(i));
                assertEquals(batch1.get(i).hashCode(), batch2.get(i).hashCode());
            }
        }

        @Test
        @DisplayName("Should maintain internal consistency")
        void shouldMaintainInternalConsistency() {
            UniqueId id1 = UniqueId.fromString("consistency_check");
            UniqueId id2 = UniqueId.fromString("consistency_check");
            
            // Should have same internal ID
            assertEquals(id1.getId(), id2.getId());
            
            // Should be equal
            assertEquals(id1, id2);
            
            // Should have same hash code
            assertEquals(id1.hashCode(), id2.hashCode());
            
            // Should have same string representation
            assertEquals(id1.toString(), id2.toString());
        }
    }
}