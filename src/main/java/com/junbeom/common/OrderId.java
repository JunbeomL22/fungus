package com.junbeom.common;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.IOException;

/**
 * Represents a unique order identifier that can differentiate between multiple venues.
 * The order ID is structured to efficiently encode both the venue and order information
 * within a single 64-bit Long value.
 *
 * <p>Bit Structure:</p>
 * <ul>
 *   <li>Bits 0-55 (56 bits): Order identifier within the venue</li>
 *   <li>Bits 56-63 (8 bits): Venue identifier</li>
 * </ul>
 *
 * <p>This design allows for:</p>
 * <ul>
 *   <li>Up to 256 different venues (8-bit venue ID)</li>
 *   <li>Over 72 quadrillion unique orders per venue (56-bit order ID)</li>
 *   <li>Efficient extraction of both venue and order information</li>
 *   <li>Simple validation and range checking</li>
 * </ul>
 *
 * <p><strong>Memory Alignment and Performance:</strong></p>
 * <p>The primary design goal is optimal memory alignment and cache efficiency. By encoding
 * both venue and order information in a single 64-bit Long (8 bytes), this class ensures:</p>
 * <ul>
 *   <li>Perfect alignment with CPU word boundaries on 64-bit systems</li>
 *   <li>Minimal memory footprint - only 8 bytes regardless of venue/order values</li>
 *   <li>Cache-friendly access patterns with single memory read operations</li>
 *   <li>Optimal performance in high-frequency trading scenarios where memory access speed is critical</li>
 * </ul>
 *
 * <p>Venue identification is performed by extracting the leading 8 bits and mapping
 * them to predefined venue constants.</p>
 *
 * @author junbeom
 * @version 1.0
 * @since 2025-07-17
 */
@JsonSerialize(using = OrderId.OrderIdSerializer.class)
@JsonDeserialize(using = OrderId.OrderIdDeserializer.class)
public class OrderId {
    public static final Long venueShift = 56L;
    public static final Long venueMask = 0xFFL << venueShift; // 8 bits for venue
    public static final Long orderMask = ~venueMask; // 56 bits for order
    
    public static final Long dummyVenue = 0L;
    public static final Long krxGeneral = 1L;
    public static final Long krxKts = 2L;
    public static final Long smb = 3L;

    private final Long combinedId;

    public OrderId(Long venue, Long orderId) {
        if (venue < 0 || venue > 0xFF) {
            throw new IllegalArgumentException("Venue must be between 0 and 255");
        }
        this.combinedId = (venue << venueShift) | (orderId & orderMask);
    }

    /**
     * Returns the order ID without the leading venue indicator.
     *
     * @return the order ID (56-bit value)
     */
    public Long orderId() {
        return combinedId & orderMask;
    }

    /**
     * Returns the raw combined ID containing both venue and order information.
     *
     * @return the combined ID (64-bit value)
     */
    public Long getCombinedId() {
        return combinedId;
    }

    /**
     * Returns the venue ID.
     *
     * @return the venue ID (8-bit value)
     */
    public Long venueId() {
        return (combinedId & venueMask) >> venueShift;
    }

    /**
     * Returns the name of the venue associated with this order ID.
     *
     * @return the venue name as a string
     */
    public String venueName() {
        Long venue = (combinedId & venueMask) >> venueShift;
        if (venue == dummyVenue) {
            return "dummy";
        } else if (venue == krxGeneral) {
            return "krxGeneral";
        } else if (venue == krxKts) {
            return "krxKts";
        } else if (venue == smb) {
            return "smb";
        } else {
            return "unknown";
        }
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        OrderId orderId = (OrderId) obj;
        return combinedId.equals(orderId.combinedId);
    }
    
    @Override
    public int hashCode() {
        return combinedId.hashCode();
    }
    
    @Override
    public String toString() {
        return String.format("OrderId{venue=%s, orderId=%d, combinedId=%d}", 
                venueName(), orderId(), combinedId);
    }

    /**
     * Custom JSON serializer for OrderId.
     * Serializes as an object with venue, orderId, and combinedId fields.
     */
    public static class OrderIdSerializer extends JsonSerializer<OrderId> {
        @Override
        public void serialize(OrderId value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeStartObject();
            gen.writeStringField("venue", value.venueName());
            gen.writeNumberField("venueId", value.venueId());
            gen.writeNumberField("orderId", value.orderId());
            gen.writeNumberField("combinedId", value.getCombinedId());
            gen.writeEndObject();
        }
    }

    /**
     * Custom JSON deserializer for OrderId.
     * Can deserialize from:
     * 1. Object with venue/venueId and orderId fields
     * 2. Single number (treated as combinedId)
     * 3. String representation of a number (treated as combinedId)
     */
    public static class OrderIdDeserializer extends JsonDeserializer<OrderId> {
        @Override
        public OrderId deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            JsonNode node = p.getCodec().readTree(p);
            
            if (node.isObject()) {
                // Deserialize from object format
                Long venueId = null;
                Long orderId = null;
                
                // Try to get venue ID from venueId field first, then from venue name
                if (node.has("venueId")) {
                    venueId = node.get("venueId").asLong();
                } else if (node.has("venue")) {
                    String venueName = node.get("venue").asText();
                    venueId = getVenueIdFromName(venueName);
                }
                
                // Get order ID
                if (node.has("orderId")) {
                    orderId = node.get("orderId").asLong();
                }
                
                // If we have combinedId, use it directly
                if (node.has("combinedId")) {
                    Long combinedId = node.get("combinedId").asLong();
                    return fromCombinedId(combinedId);
                }
                
                // Otherwise construct from venue and order ID
                if (venueId != null && orderId != null) {
                    return new OrderId(venueId, orderId);
                }
                
                throw new IllegalArgumentException("Invalid OrderId JSON: missing required fields");
                
            } else if (node.isNumber()) {
                // Deserialize from single number (combinedId)
                return fromCombinedId(node.asLong());
                
            } else if (node.isTextual()) {
                // Try to parse as number
                try {
                    Long combinedId = Long.parseLong(node.asText());
                    return fromCombinedId(combinedId);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid OrderId string: " + node.asText());
                }
            }
            
            throw new IllegalArgumentException("Invalid OrderId JSON format");
        }
        
        private Long getVenueIdFromName(String venueName) {
            return switch (venueName.toLowerCase()) {
                case "dummy" -> OrderId.dummyVenue;
                case "krxgeneral" -> OrderId.krxGeneral;
                case "krxkts" -> OrderId.krxKts;
                case "smb" -> OrderId.smb;
                default -> throw new IllegalArgumentException("Unknown venue name: " + venueName);
            };
        }
    }
    
    /**
     * Creates an OrderId from a combined ID value.
     * This is useful for deserialization and when you have the full 64-bit value.
     * 
     * @param combinedId The combined 64-bit ID containing both venue and order information
     * @return A new OrderId instance
     */
    public static OrderId fromCombinedId(Long combinedId) {
        Long venue = (combinedId & venueMask) >> venueShift;
        Long orderId = combinedId & orderMask;
        return new OrderId(venue, orderId);
    }
}
