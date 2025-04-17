package sc2002.FCS1.grp2;

/**
 * This class is used to filter the listings based on certain criteria.
 */
public enum ListingFilter {
    DEFAULT, // Alphabetical order
    TWO_ROOM,
    THREE_ROOM,
    TWO_ROOM_PRICE_DESCENDING,
    TWO_ROOM_PRICE_ASCENDING,
    THREE_ROOM_PRICE_DESCENDING,
    THREE_ROOM_PRICE_ASCENDING,
    OPENING_DATE_ASCENDING,
    OPENING_DATE_DESCENDING,
    CLOSING_DATE_ASCENDING,
    CLOSING_DATE_DESCENDING,
    NAME,
    NEIGHBORHOOD;

    // Add more filters as needed

    /**
     * The keyword used for filtering.
     * This is used to filter the listings based on the keyword.
     * Eg): SEARCH BY NAME, SEARCH BY NEIGHBORHOOD
     */
    private String keyword;

    /**
     * Default constructor for the ListingFilter enum.
     * This constructor is used to create a default filter with no keyword.
     */
    private ListingFilter() {
        this.keyword = null;
    }

    /**
     * Constructor for the ListingFilter enum with a keyword.
     * This constructor is used to create a filter with a specific keyword.
     *
     * @param keyword The keyword used for filtering.
     */
    private ListingFilter(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Gets the keyword used for filtering.
     *
     * @return The keyword used for filtering.
     */
    public String getKeyword() {
        return keyword;
    }

    /**
     * Sets the keyword used for filtering.
     *
     * @param keyword The keyword used for filtering.
     */
    public static ListingFilter fromString(String value) {
        for (ListingFilter filter : ListingFilter.values()) {
            if (filter.name().equalsIgnoreCase(value)) {
                return filter;
            }
        }
        return null;
    }

}
