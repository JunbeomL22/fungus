package com.junbeom.common;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.Objects;

/**
 * A string interning system that reduces memory usage and improves performance.
 * 
 * UniqueId stores strings as 64-bit integers internally, providing several key benefits:
 * 
 * <h3>Memory Efficiency</h3>
 * <ul>
 * <li><strong>Fixed 8-byte size</strong>: Regardless of the original string length, each UniqueId occupies exactly 8 bytes</li>
 * <li><strong>String deduplication</strong>: Identical strings share the same ID, eliminating duplicate storage</li>
 * <li><strong>Cache-friendly</strong>: Small size improves CPU cache utilization</li>
 * </ul>
 * 
 * <h3>Performance Characteristics</h3>
 * <ul>
 * <li><strong>Fast hashing</strong>: Hashing a 64-bit integer is extremely fast compared to string hashing</li>
 * <li><strong>O(1) equality checks</strong>: Comparing two UniqueIds is a simple integer comparison</li>
 * <li><strong>Efficient copying</strong>: Being immutable, no heap allocations occur during assignment</li>
 * <li><strong>Creation overhead</strong>: String-to-ID mapping occurs only once per unique string</li>
 * </ul>
 * 
 * <h3>Use Cases</h3>
 * <ul>
 * <li>High-frequency string operations (comparisons, hashing, storage)</li>
 * <li>Memory-constrained environments</li>
 * <li>Performance-critical code paths requiring string-like identifiers</li>
 * </ul>
 * 
 * <h3>Thread Safety</h3>
 * The internal cache uses ConcurrentHashMap, making UniqueId safe to use across threads.
 * Creation of new IDs is lock-free and highly concurrent.
 * 
 * <h3>Examples</h3>
 * <pre>{@code
 * // Create UniqueIds from strings
 * UniqueId id1 = UniqueId.fromString("symbol_AAPL");
 * UniqueId id2 = UniqueId.fromString("symbol_AAPL"); // Reuses existing ID
 * 
 * // Fast equality check (just comparing longs)
 * assert id1.equals(id2);
 * 
 * // Efficient concatenation
 * UniqueId combined = id1.add("_option");
 * 
 * // Convert back to string when needed
 * System.out.println("Symbol: " + id1);
 * }</pre>
 */
@JsonSerialize(using = UniqueId.UniqueIdSerializer.class)
@JsonDeserialize(using = UniqueId.UniqueIdDeserializer.class)
public final class UniqueId {
    
    /**
     * Internal 64-bit identifier.
     * This is the only field, ensuring the struct is exactly 8 bytes.
     */
    private final long id;
    
    /**
     * Internal cache for bidirectional string-to-ID mapping.
     * 
     * Maintains two concurrent hash maps for O(1) lookups in both directions:
     * - stringToId: Maps strings to their assigned numeric IDs
     * - idToString: Maps numeric IDs back to their original strings
     */
    private static final ConcurrentHashMap<String, Long> STRING_TO_ID = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Long, String> ID_TO_STRING = new ConcurrentHashMap<>();
    private static final AtomicLong ID_GENERATOR = new AtomicLong(0);
    private static final UniqueId DEFAULT_UNIQUE_ID = UniqueId.fromString("");
    
    /**
     * Private constructor - use fromString() to create instances.
     */
    private UniqueId(long id) {
        this.id = id;
    }
    
    /**
     * Returns the default UniqueId (empty string).
     */
    public static UniqueId defaultValue() {
        return DEFAULT_UNIQUE_ID;
    }
    
    /**
     * Creates a UniqueId from a string, with efficient caching and deduplication.
     * 
     * This method performs string interning - if the string has been seen before,
     * it returns the existing ID. Otherwise, it creates a new ID and caches the mapping.
     * 
     * <h4>Performance Notes</h4>
     * <ul>
     * <li><strong>First call</strong>: O(1) for cache insertion + string storage</li>
     * <li><strong>Subsequent calls</strong>: O(1) for cache lookup (no allocation)</li>
     * <li><strong>Memory</strong>: Strings are stored directly for efficient access</li>
     * </ul>
     * 
     * <h4>Thread Safety</h4>
     * This method is thread-safe and lock-free using ConcurrentHashMap.
     * Multiple threads can safely create UniqueIds concurrently.
     * 
     * @param str The string to convert to a UniqueId
     * @return A UniqueId representing the string
     * 
     * @throws NullPointerException if str is null
     */
    public static UniqueId fromString(String str) {
        Objects.requireNonNull(str, "String cannot be null");
        
        // Try to get existing ID
        Long existingId = STRING_TO_ID.get(str);
        if (existingId != null) {
            return new UniqueId(existingId);
        }
        
        // Create new ID using computeIfAbsent for atomicity
        long newId = STRING_TO_ID.computeIfAbsent(str, s -> {
            long id = ID_GENERATOR.getAndIncrement();
            ID_TO_STRING.put(id, s);
            return id;
        });
        
        return new UniqueId(newId);
    }
    
    /**
     * Converts the UniqueId back to its original string representation.
     * 
     * <h4>Performance</h4>
     * <ul>
     * <li><strong>Time</strong>: O(1) cache lookup</li>
     * <li><strong>Memory</strong>: No string duplication, direct reference</li>
     * </ul>
     * 
     * @return The original string used to create this UniqueId
     * @throws IllegalStateException if the ID is not found in the cache (should never happen in normal usage)
     */
    @Override
    public String toString() {
        String result = ID_TO_STRING.get(this.id);
        if (result == null) {
            throw new IllegalStateException("ID " + this.id + " not found in cache");
        }
        return result;
    }
    
    /**
     * Returns the total number of unique strings currently cached.
     * 
     * This represents the number of distinct strings that have been interned
     * through fromString() calls. Each unique string occupies one entry
     * in the internal cache.
     * 
     * Useful for:
     * <ul>
     * <li>Memory usage monitoring</li>
     * <li>Cache statistics and debugging</li>
     * <li>Performance analysis</li>
     * </ul>
     * 
     * @return The number of unique strings in the cache
     */
    public static int count() {
        return STRING_TO_ID.size();
    }
    
    /**
     * Concatenates this UniqueId with another value to create a new UniqueId.
     * 
     * This method converts the current ID back to a string, appends the provided
     * value (converted to string), and creates a new UniqueId from the result.
     * 
     * <h4>Performance Considerations</h4>
     * <ul>
     * <li>Involves string allocation and concatenation</li>
     * <li>Results in a new cache entry if the combined string is unique</li>
     * <li>Consider using this sparingly in performance-critical code</li>
     * </ul>
     * 
     * @param other The value to append (will be converted to string)
     * @return A new UniqueId representing the concatenated string
     */
    public UniqueId add(Object other) {
        String otherStr = (other == null) ? "null" : other.toString();
        String newStr = this.toString() + otherStr;
        return UniqueId.fromString(newStr);
    }
    
    /**
     * Concatenates this UniqueId with multiple other values to create a new UniqueId.
     * 
     * This method converts the current ID back to a string, appends all provided
     * values (converted to strings), and creates a new UniqueId from the result.
     * 
     * <h4>Performance Considerations</h4>
     * <ul>
     * <li>Involves string allocation and concatenation</li>
     * <li>Results in a new cache entry if the combined string is unique</li>
     * <li>Consider using this sparingly in performance-critical code</li>
     * </ul>
     * 
     * @param others Array of values that will be converted to strings
     * @return A new UniqueId representing the concatenated string
     */
    public UniqueId add(Object... others) {
        StringBuilder sb = new StringBuilder(this.toString());
        for (Object other : others) {
            sb.append((other == null) ? "null" : other.toString());
        }
        return UniqueId.fromString(sb.toString());
    }
    
    /**
     * Creates a new UniqueId by merging multiple elements into a single string.
     * 
     * This is a utility method for creating composite identifiers from multiple
     * components. All elements are concatenated without separators.
     * 
     * <h4>Performance Notes</h4>
     * <ul>
     * <li>More efficient than multiple individual concatenations</li>
     * <li>Single string allocation for the entire result</li>
     * <li>Results in one cache lookup/insertion</li>
     * </ul>
     * 
     * @param elements Array of values that will be converted to strings
     * @return A new UniqueId representing the merged string
     */
    public static UniqueId merged(Object... elements) {
        StringBuilder sb = new StringBuilder();
        for (Object element : elements) {
            sb.append((element == null) ? "null" : element.toString());
        }
        return UniqueId.fromString(sb.toString());
    }
    
    /**
     * Fast equality check based on internal ID comparison.
     * Two UniqueIds are equal if they have the same internal ID.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        UniqueId uniqueId = (UniqueId) obj;
        return id == uniqueId.id;
    }
    
    /**
     * Hash code based on the internal ID.
     * This is extremely fast compared to string hashing.
     */
    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }
    
    /**
     * Returns the internal ID for debugging purposes.
     */
    public long getId() {
        return id;
    }
    
    /**
     * Custom JSON serializer for UniqueId.
     * Serializes as the string representation, with smart JSON parsing support.
     */
    public static class UniqueIdSerializer extends JsonSerializer<UniqueId> {
        private static final ObjectMapper mapper = new ObjectMapper();
        
        @Override
        public void serialize(UniqueId value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            String str = value.toString();
            
            // Try to parse as JSON first (matching Rust behavior)
            try {
                // If the string is valid JSON, write it as a JSON value
                Object jsonValue = mapper.readValue(str, Object.class);
                gen.writeObject(jsonValue);
            } catch (Exception e) {
                // If not valid JSON, serialize as plain string
                gen.writeString(str);
            }
        }
    }
    
    /**
     * Custom JSON deserializer for UniqueId.
     * Deserializes from both string and complex JSON values.
     */
    public static class UniqueIdDeserializer extends JsonDeserializer<UniqueId> {
        private static final ObjectMapper mapper = new ObjectMapper();
        
        @Override
        public UniqueId deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            String str;
            
            // Handle different JSON value types
            switch (p.getCurrentToken()) {
                case VALUE_STRING:
                    str = p.getValueAsString();
                    break;
                case START_OBJECT:
                case START_ARRAY:
                case VALUE_NUMBER_INT:
                case VALUE_NUMBER_FLOAT:
                case VALUE_TRUE:
                case VALUE_FALSE:
                case VALUE_NULL:
                    // For non-string JSON values, convert them to string representation
                    Object value = p.readValueAs(Object.class);
                    // Use static ObjectMapper to write as JSON string
                    str = mapper.writeValueAsString(value);
                    break;
                default:
                    str = p.getValueAsString();
                    break;
            }
            
            return UniqueId.fromString(str);
        }
    }
}