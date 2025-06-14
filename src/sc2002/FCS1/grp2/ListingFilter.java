package sc2002.FCS1.grp2;
/**
 * Enum representing different filters for listing.
 * Each enum value corresponds to a specific filter.
 */
public enum ListingFilter {
    DEFAULT("None (default)"), // No filter
    TWO_ROOM("2-Room Availability"), // Filter with at least 1 2-room unit
    THREE_ROOM("3-Room Availability"), // Filter with at least 1 3-room unit
    NAME("Name"), // Filter with keywords in the name of the project
    NEIGHBORHOOD("Neighbourhood"); // Filter with keywords in the neighborhood of the project
    
    /**
     * The keyword associated with this enum value.
     */
    private String keyword;

    /**
     * Default constructor for ListingFilter.
     * Initializes the keyword to null.
     */
    ListingFilter() {
        this.keyword = null;
    }

    /**
     * Constructor for ListingFilter with a specific keyword.
     * @param keyword the keyword associated with this enum value
     */
    ListingFilter(String keyword) {
        this.keyword = keyword;
    } 

    /**
     * Sets the keyword for this enum value.
     * @param keyword the keyword to set
     */
    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }


    /**
     * Returns the keyword associated with this enum value.
     * @return the keyword
     */
    public String getKeyword() {
        return keyword;
    }

    @Override
    public String toString() {
        return keyword;
    }

    /**
     * Converts a string to a ListingFilter enum value.
     * @param value
     * @return
     */
    public static ListingFilter fromString(String value) {
        for (ListingFilter keyword : ListingFilter.values()) {
            if (keyword.name().equalsIgnoreCase(value)) {
                return keyword;
            }
        }
        return null;
    }
}