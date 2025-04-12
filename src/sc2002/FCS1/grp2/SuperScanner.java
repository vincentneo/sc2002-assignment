package sc2002.FCS1.grp2;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/**
 * An extension of features for the {@code Scanner} class.
 * 
 * This class allows for more convenient input gathering from users, for types that may be error prone.
 * For example, when expecting an integer, the user may input something else, such as text. 
 * Here, it will automatically and recursively ask the user until the input given is in correct format.
 */
public class SuperScanner {
	private Scanner scanner;
	
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
	
	public SuperScanner(Scanner scanner) {
		this.scanner = scanner;
	}
	
	/**
	 * Get input from user until input is a number.
	 * @param prompt The prompt to be shown to the user.
	 * @return integer input by user.
	 */
	public int nextIntUntilCorrect(String prompt) {
		try {
			return nextInt(prompt);
		}
		catch (NumberFormatException e) {
			System.out.println(ANSI_YELLOW +  "Input must be a number. Please try again." + ANSI_RESET);
			return nextIntUntilCorrect(prompt);
		}
	}
	
	public LocalDate nextDateUntilCorrect(String prompt) {
		try {
			return nextDate(prompt);
		}
		catch (DateTimeParseException e) {
			System.out.println("Date input is incorrect. Please double check the date and format of d/m/yy");
			return nextDateUntilCorrect(prompt);
		}
	}
	
	private int nextInt(String prompt) {
		System.out.print(prompt);
		String userInput = scanner.next();
		return Integer.parseInt(userInput);
	}
	
	private LocalDate nextDate(String prompt) {
		System.out.print(prompt);
		String userInput = scanner.next();
		return Utilities.getInstance().parseDate(userInput);
	}
}
