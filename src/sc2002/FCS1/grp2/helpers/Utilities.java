package sc2002.FCS1.grp2.helpers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

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
	private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("d'/'M'/'yy");
	
	/**
	 * Escape code to tint text to yellow.
	 * Make sure to reset after text completion.
	 */
	private String ANSI_YELLOW = "\u001B[33m";
	
	/**
	 * Escape code to reset colour tint. 
	 * Make sure to add this to the back of any tinted text content before return.
	 */
	private String ANSI_RESET = "\u001B[0m";
	
	
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
		return LocalDate.parse(dateString, dateFormatter);
	}
	
	/**
	 * Formats a {@code LocalDate} object into the CSV file compliant format of d/M/yy (e.g. 12/4/25)
	 * @param date The date object that you wish to convert to text.
	 * @return A string that is formatted based on d/M/yy.
	 */
	public String formatDate(LocalDate date) {
		return date.format(dateFormatter);
	}
	
	/**
	 * Format date into text of format MMM dd, yyyy (Example: Jan 1, 2025)
	 * @param date The date to be formatted.
	 * @return date formatted to text as described.
	 */
	public String formatUserReadableDate(LocalDate date) {
		return date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM));
	}
	
	/**
	 * Standardised format to be used to encode date time objects in CSV file.
	 * 
	 * For example: 2025-04-23T17:25:30'
	 * @return formatter object to be used in parse or format methods of {@code LocalDateTime}.
	 */
	private DateTimeFormatter formatterForCSVDateTime() {
		return DateTimeFormatter.ISO_LOCAL_DATE_TIME;
	}
	
	/**
	 * Parse date and time strings from CSV fields.
	 * 
	 * Intended for CSV date time fields parsing.
	 * @param string date and time, formatted as ISO 8601, but as local time, without timezone offset.
	 * @return an object representing local date and time.
	 */
	public LocalDateTime parseDateTime(String string) {
		return LocalDateTime.parse(string, formatterForCSVDateTime());
	}

	/**
	 * Format date and time object as string, in preparation for encoding into a CSV field.
	 * @param dateTime local date and time object
	 * @return a formatted date and time string, formatted as ISO 8601, as local time, without timezone offset.
	 */
	public String formatDateTime(LocalDateTime dateTime) {
		return dateTime.format(formatterForCSVDateTime());
	}

	/**
	 * Formats a date and time object as human-readable date text.
	 * 
	 * For example: 22 Apr 2025.
	 * @param dateTime the local date time object.
	 * @return formatted date only. 
	 */
	public String formatDateFromDateTime(LocalDateTime dateTime) {
		return dateTime.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM));
	}

	/**
	 * Formats a date and time object as human-readable time text.
	 * 
	 * For example: 8:43:39 pm.
	 * @param dateTime the local date time object.
	 * @return formatted time only. 
	 */
	public String formatTimeFromDateTime(LocalDateTime dateTime) {
		return dateTime.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM));
	}
	
	/**
	 * Formats a date and time object as human-readable date and time text.
	 * 
	 * For example: 22 Apr 2025, 4:59:18 pm.
	 * @param dateTime the local date time object.
	 * @return formatted date and time, seperated by comma.
	 */
	public String formatUserReadableDateTime(LocalDateTime dateTime) {
		return dateTime.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM));
	}
}
