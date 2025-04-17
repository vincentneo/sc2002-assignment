package sc2002.FCS1.grp2;

/**
 * This class is used to sort the listings based on certain criteria.
 */
public enum ListingSort {
    DEFAULT, // Alphabetical order
    REVERSE_DEFAULT, // Reverse alphabetical order
    TWO_ROOM_PRICE_DESCENDING,
    TWO_ROOM_PRICE_ASCENDING,
    THREE_ROOM_PRICE_DESCENDING,
    THREE_ROOM_PRICE_ASCENDING,
    OPENING_DATE_ASCENDING,
    OPENING_DATE_DESCENDING,
    CLOSING_DATE_ASCENDING,
    CLOSING_DATE_DESCENDING;

    // Add more filters as needed

    
    /**
     * Converts a string to a ListingSort enum value.
     * @param value
     * @return
     */
    public static ListingSort fromString(String value) {
        for (ListingSort filter : ListingSort.values()) {
            if (filter.name().equalsIgnoreCase(value)) {
                return filter;
            }
        }
        return null;
    }

}
