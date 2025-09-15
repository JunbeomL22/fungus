package com.junbeom.common.order;

import java.util.Objects;

/**
 * A plain implementation of OrderId that stores venue and order information
 * as separate Long fields, occupying 16 bytes due to memory alignment.
 * 
 * This class is designed for performance comparison with the optimized OrderId
 * that packs both values into a single 64-bit Long (8 bytes).
 * 
 * <p>Memory Layout:</p>
 * <ul>
 *   <li>venueId: 8 bytes (Long)</li>
 *   <li>orderId: 8 bytes (Long)</li>
 *   <li>Total: 16 bytes (plus object overhead)</li>
 * </ul>
 * 
 * @author junbeom
 * @version 1.0
 * @since 2025-09-12
 */
public class PlainOrderId {
    public static final int dummyVenue = 0;
    public static final int krxGeneral = 1;
    public static final int krxKts = 2;
    public static final int smb = 3;

    private final int venueId;
    private final long orderId;

    public PlainOrderId(int venueId, long orderId) {
        if (venueId < 0 || venueId > 0xFF) {
            throw new IllegalArgumentException("Venue must be between 0 and 255");
        }
        this.venueId = venueId;
        this.orderId = orderId;
    }

    /**
     * Returns the venue ID.
     *
     * @return the venue ID (8-bit value stored as Long)
     */
    public int venueId() {
        return venueId;
    }

    /**
     * Returns the order ID.
     *
     * @return the order ID
     */
    public long orderId() {
        return orderId;
    }

    /**
     * Returns the name of the venue associated with this order ID.
     *
     * @return the venue name as a string
     */
    public String venueName() {
        if (venueId == dummyVenue) {
            return "dummy";
        } else if (venueId == krxGeneral) {
            return "krxGeneral";
        } else if (venueId == krxKts) {
            return "krxKts";
        } else if (venueId == smb) {
            return "smb";
        } else {
            return "unknown";
        }
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        PlainOrderId that = (PlainOrderId) obj;
        return venueId == that.venueId && orderId == that.orderId;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(venueId, orderId);
    }
    
    @Override
    public String toString() {
        return String.format("PlainOrderId{venue=%s, venueId=%d, orderId=%d}", 
                venueName(), venueId, orderId);
    }
}