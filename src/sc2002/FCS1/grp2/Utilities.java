package sc2002.FCS1.grp2;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Singleton class that handles certain common utility features such as date parsing.
 */
public class Utilities {
	
	/**
	 * The static instance of this class.
	 */
	private static Utilities instance = new Utilities();
	
	/**
	 * Common Date formatter, based on provided CSV file's date format of d/M/yy.
	 */
	private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d'/'M'/'uu");
	
	/**
	 * Constructor that should not be used outside this class.
	 */
	private Utilities() {}

	/**
	 * Get Singleton instance of {@code Utilities}.
	 * @return the singleton instance of {@code Utilities}.
	 */
	public static Utilities getInstance() {
		return instance;
	}
	
	/**
	 * Parse a date from text.
	 * @param dateString The text that should be of format d/M/yy (e.g. 12/4/25)
	 * @return LocalDate object representing the provided date.
	 * @throws java.time.format.DateTimeParseException if the text is not of expected date format, or isn't a date.
	 */
	public LocalDate parseDate(String dateString) {
		return LocalDate.parse(dateString, formatter);
	}
	
	/**
	 * Formats a {@code LocalDate} object into the CSV file compliant format of d/M/yy (e.g. 12/4/25)
	 * @param date The date object that you wish to convert to text.
	 * @return A string that is formatted based on d/M/yy.
	 */
	public String formatDate(LocalDate date) {
		return date.format(formatter);
	}
}
