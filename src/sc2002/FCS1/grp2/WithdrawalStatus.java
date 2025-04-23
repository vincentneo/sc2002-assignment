package sc2002.FCS1.grp2;

/**
 * Status for application withdrawal.
 */
public enum WithdrawalStatus {
	/**
	 * Not Withdrawn, application expected to go as expected.
	 */
	NOT("Not Withdrawn"),

	/**
	 * Withdrawal is requested by authority. Consider application voided unless personnel overturn status.
	 */
	WITHDRAWN("Withdrawn"),
	
	/**
	 * Rejected Withdrawal.
	 */
	REJECTED("Rejected");
	
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
