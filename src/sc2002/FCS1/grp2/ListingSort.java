package sc2002.FCS1.grp2;

/**
 * This class is used to sort the listings based on certain criteria.
 */
public enum ListingSort {
    DEFAULT("A-Z (default)"), // Alphabetical order
    REVERSE_DEFAULT("Z-A"), // Reverse alphabetical order
    TWO_ROOM_PRICE_DESCENDING("2-Room Price, High to Low"),
    TWO_ROOM_PRICE_ASCENDING("2-Room Price, Low to High"),
    THREE_ROOM_PRICE_DESCENDING("3-Room Price, High to Low"),
    THREE_ROOM_PRICE_ASCENDING("3-Room Price, Low to High"),
    OPENING_DATE_ASCENDING("Opening Date, Earliest to Latest"),
    OPENING_DATE_DESCENDING("Opening Date, Latest to Earliest"),
    CLOSING_DATE_ASCENDING("Closing Date, Earliest to Latest"),
    CLOSING_DATE_DESCENDING("Closing Date, Latest to Earliest");

    // Add more filters as needed

    String title;

    ListingSort(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return title;
    }
    
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
