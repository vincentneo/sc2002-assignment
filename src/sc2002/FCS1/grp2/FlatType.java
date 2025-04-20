package sc2002.FCS1.grp2;

/**
 *	Enum that represents the type of the flats (The assignment assumes there are only 2-Room and 3-Room) 
 */
public enum FlatType {
	/**
	 * Represents a 2-Room flat type.
	 */
	TWO_ROOM("2-Room"),

	/**
	 * Represents a 3-Room flat type.
	 */
	THREE_ROOM("3-Room");
	
	private String value;
	
	/**
	 * Constructor for FlatType enum.
	 * @param value The string representation of the FlatType.
	 */
	private FlatType(String value) {
		this.value = value;
	}
	
	/**
	 * Get the string representation of the FlatType.
	 * @return The string representation of the FlatType.
	 */
	@Override
	public String toString() {
		return value;
	}
	

	/**
	 * Convert a string to the respective FlatType enum.
	 * @param value The string to convert.
	 * @return The FlatType enum that matches the string, or null if no match is found.
	 */
	public static FlatType fromString(String value) {
		for (FlatType type : FlatType.values()) {
			if (type.value.equalsIgnoreCase(value)) {
				return type;
			}
		}
		
		return null;
	}
	
	/**
	 * Construct base on integer value that can represent a flat type.
	 * For example, provide number 2 gives you {@code TWO_ROOM}
	 * 
	 * Any unsupported flat type value such as 4 or 5 will result in {@code null}
	 */
	public static FlatType fromInt(int number) {
		switch (number) {
		case 2:
			return TWO_ROOM;
		case 3:
			return THREE_ROOM;
		default:
			return null;
		}
	}
}
