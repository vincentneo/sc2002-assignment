package sc2002.FCS1.grp2;

/**
 * Represents the marital status of each user.
 */
public enum MaritalStatus {
	SINGLE("Single"),
	MARRIED("Married");
	
	private String value;
	
	private MaritalStatus(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return value;
	}
	
	public static MaritalStatus fromString(String value) {
		for (MaritalStatus status : MaritalStatus.values()) {
			if (status.value.equalsIgnoreCase(value)) {
				return status;
			}
		}
		
		return null;
	}
}
