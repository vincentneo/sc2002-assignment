package sc2002.FCS1.grp2;

public enum ApplicationStatus {
	PENDING("Pending"),
	SUCCESSFUL("Successful"),
	UNSUCCESSFUL("Unsuccessful"),
	BOOKED("Booked");
	
	private String value;
	
	private ApplicationStatus(String value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return value;
	}
	
	public static ApplicationStatus fromString(String value) {
		for (ApplicationStatus type : ApplicationStatus.values()) {
			if (type.value.equalsIgnoreCase(value)) {
				return type;
			}
		}
		
		return null;
	}
}
