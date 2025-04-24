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
	
	/**
	 * User readable name thats also used for csv encode/decode.
	 */
	private String value;
	
	/**
	 * Internal constructor for enum
	 * @param value
	 */
	private WithdrawalStatus(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return value;
	}
	
	/**
	 * Decode from string content
	 * @param value Content such as from CSV file
	 * @return this status, or null if invalid.
	 */
	public static WithdrawalStatus fromString(String value) {
		for (WithdrawalStatus status : WithdrawalStatus.values()) {
			if (status.value.equalsIgnoreCase(value)) {
				return status;
			}
		}
		
		return null;
	}
}
