package sc2002.FCS1.grp2;

public enum WithdrawalStatus {
	/*
	 * Not Withdrawn, application expected to go as expected.
	 */
	NOT("Not Withdrawn"),
	/*
	 * Withdrawal requested by user.
	 */
	PENDING("Pending Withdrawal"),
	/*
	 * Withdrawal is accepted by authority. Assume application voided.
	 */
	WITHDRAWN("Withdrawn");
	
	private String value;
	
	private WithdrawalStatus(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return value;
	}
	
	public static WithdrawalStatus fromString(String value) {
		for (WithdrawalStatus status : WithdrawalStatus.values()) {
			if (status.value.equalsIgnoreCase(value)) {
				return status;
			}
		}
		
		return null;
	}
}
