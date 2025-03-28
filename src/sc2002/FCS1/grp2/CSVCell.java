package sc2002.FCS1.grp2;

/**
 * A simple class for representing an individual cell of any specific row and column of a CSV file.
 * 
 * @author Vincent Neo
 */
public class CSVCell {
	private String value = null;
	private String[] commaSeperatedValues = null;
	
	/**
	 * Construct an object based on contents of a CSV cell.
	 * 
	 * It is generally expected that only the {@code CSVParser} class will construct this type of objects.
	 * 
	 * @param value Contents of a CSV cell, either as is, or contents within <pre>"..."</pre>.
	 */
	CSVCell(String value) {
		this.value = value;
		if (value.contains(",")) {
			this.commaSeperatedValues = value.split(",");
		}
	}

	/**
	 * Provides the contents of the cell, as parsed by the CSVParser class.
	 * 
	 * Note that no contents are manipulated here, so even comma separated values are represented as is.
	 * @return value of CSV cell as a {@code String}.
	 */
	public String getValue() {
		return value;
	}
	
	/**
	 * Convenience method to get value as an integer.
	 * @return value of the CSV cell as an {@code int}.
	 * @throws {@code NumberFormatException} if cell content is not a number.
	 */
	public int getIntValue() {
		return Integer.parseInt(value);
	}

	/**
	 * Provides the values of the cell, separated by commas.
	 * 
	 * @return values as a {@code String[]}, if cell contains comma separated values or null, 
	 */
	public String[] getValues() {
		return commaSeperatedValues;
	}
	
	/**
	 * Provides a way to tell if there are multiple values retrievable in this cell.
	 * @return {@code true} if >1 values can be retrieved, {@code false} if cell only contains one value.
	 */
	public boolean hasMultipleValues() {
		return commaSeperatedValues != null;
	}
}
