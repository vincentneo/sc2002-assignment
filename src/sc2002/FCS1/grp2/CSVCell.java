package sc2002.FCS1.grp2;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import sc2002.FCS1.grp2.helpers.Utilities;

/**
 * A simple class for representing an individual cell of any specific row and column of a CSV file.
 * 
 * @author Vincent Neo
 */
public class CSVCell {
	/**
	 * Value of each CSV spreadsheet cell, unmodified.
	 */
	private String value = null;
	
	/**
	 * Values of comma separated values within a cell.
	 */
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
	 * @return Value of CSV cell as a {@code String}.
	 */
	public String getValue() {
		return value;
	}
	
	/**
	 * Convenience method to get value as an integer.
	 * @return value of the CSV cell as an {@code int}.
	 * @throws NumberFormatException - if cell content is not a number.
	 */
	public int getIntValue() {
		return Integer.parseInt(value);
	}
	
	/**
	 * Convenience method to get value as an boolean.
	 * 
	 * Note that this method strictly considers "TRUE" and "FALSE" only (Excel style)
	 * 
	 * @return value of the CSV cell as an {@code boolean}.
	 * @throws IllegalArgumentException - if cell content is not either "TRUE" or "FALSE".
	 */
	public boolean getBoolValue() {
		if (value.equals("TRUE")) {
			return true;
		}
		else if (value.equals("FALSE")) {
			return false;
		}
		
		throw new IllegalArgumentException("Boolean value must only be 'TRUE' or 'FALSE'");
	}
	
	/**
	 * Convenience method to get value as date.
	 * @return value of the CSV cell as an {@code LocalDate}.
	 * @throws java.time.format.DateTimeParseException - if cell content is not of d/M/yy date format.
	 */
	public LocalDate getDateValue() {
		return Utilities.getInstance().parseDate(value);
	}
	
	/**
	 * Convenience method to get value as date and time.
	 * @return value of the CSV cell as an {@code LocalDateTime}.
	 * @throws java.time.format.DateTimeParseException - if cell content is not of correct date time format.
	 */
	public LocalDateTime getDateTimeValue() {
		return Utilities.getInstance().parseDateTime(value);
	}
	
	/**
	 * Convenience method to get value as a UUID.
	 * @return value of the CSV cell as an {@code UUID}.
	 * @throws IllegalArgumentException - if cell content is not UUID compliant.
	 */
	public UUID getUUIDValue() {
		return UUID.fromString(value);
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
	 * Provides multiple values of cell where possible, else, the single string value.
	 * 
	 * As this method does not check the contents of cell if singular, it is up to your responsibility to ensure value are correct.
	 * @return values of {@link #getValues()} or {@link #getValue()}
	 */
	public String[] getValuesOrValue() {
		if (hasMultipleValues()) return getValues();
		
		String value = getValue();
		
		if (value.isBlank()) return new String[] {};
		
		return new String[] { value };
	}
	
	/**
	 * Provides a way to tell if there are multiple values retrievable in this cell.
	 * @return {@code true} if >1 values can be retrieved,
	 *  	   {@code false} if cell only contains one value.
	 */
	public boolean hasMultipleValues() {
		return commaSeperatedValues != null;
	}
	
	/**
	 * Returns true if cell is blank. (e.g. not even a blank space)
	 * @return ture if cell blank.
	 */
	public boolean isBlank() {
		return value.isBlank();
	}
}
