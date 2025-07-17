package com.junbeom.common;

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
 * <p>Venue identification is performed by extracting the leading 8 bits and mapping
 * them to predefined venue constants.</p>
 *
 * @author junbeom
 * @version 1.0
 * @since 2025-07-17
 */
public class OrderId {
    private static final Long venueShift = 56L;
    private static final Long venueMask = 0xFFL << venueShift; // 8 bits for venue
    private static final Long orderMask = ~venueMask; // 56 bits for orde
    
    public static final Long dummyVenue = 0L;
    public static final Long krxDrv = 1L;
    public static final Long krxKts = 2L;
    public static final Long smb = 3L;

    private final Long rawOrderId;

    public OrderId(Long venue, Long orderId) {
        if (venue < 0 || venue > 0xFF) {
            throw new IllegalArgumentException("Venue must be between 0 and 255");
        }
        this.rawOrderId = (venue << venueShift) | (orderId & orderMask);
    }

    /**
     * Returns the order ID without the leading venue indicator.
     *
     * @return the order ID (56-bit value)
     */
    public Long orderId() {
        return rawOrderId & orderMask;
    }

    /**
     * Returns the name of the venue associated with this order ID.
     *
     * @return the venue name as a string
     */
    public String venueName() {
        Long venue = (rawOrderId & venueMask) >> venueShift;
        if (venue == dummyVenue) {
            return "dummy";
        } else if (venue == krxDrv) {
            return "krxDrv";
        } else if (venue == krxKts) {
            return "krxKts";
        } else if (venue == smb) {
            return "smb";
        } else {
            return "unknown";
        }
    }
}
