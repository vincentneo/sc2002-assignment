package sc2002.FCS1.grp2;

public enum FlatType {
	TWO_ROOM("2-Room"),
	THREE_ROOM("3-Room");
	
	private String value;
	
	private FlatType(String value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return value;
	}
	
	public static FlatType fromString(String value) {
		for (FlatType type : FlatType.values()) {
			if (type.value.equalsIgnoreCase(value)) {
				return type;
			}
		}
		
		return null;
	}
}
