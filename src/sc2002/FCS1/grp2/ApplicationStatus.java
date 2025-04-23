package sc2002.FCS1.grp2;

/**
 * The status of an application.
 */
public enum ApplicationStatus {
	/**
	 * The initial status; When an applicant applies, this is the status, as it awaits approval from authorised personnel.
	 */
	PENDING("Pending"),
	/**
	 * This status occurs when an authorised personnel has approved the application; awaiting booking.
	 */
	SUCCESSFUL("Successful"),
	/**
	 * This status occurs when an authorised personnel decides this application shall be unsuccessful, such as when there are no slots left, over booking.
	 */
	UNSUCCESSFUL("Unsuccessful"),
	/**
	 * When application is confirmed and booked by an officer.
	 * In this state, a receipt can be generated.
	 */
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
