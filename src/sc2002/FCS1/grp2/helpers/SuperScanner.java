package sc2002.FCS1.grp2.helpers;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

import sc2002.FCS1.grp2.builders.Style;
import sc2002.FCS1.grp2.builders.Style.Code;

/**
 * An extension of features for the {@code Scanner} class.
 * 
 * This class allows for more convenient input gathering from users, for types that may be error prone.
 * For example, when expecting an integer, the user may input something else, such as text. 
 * Here, it will automatically and recursively ask the user until the input given is in correct format.
 */
public class SuperScanner {
	/**
	 * Scanner object to be referenced in class.
	 */
	private Scanner scanner;
	
	/**
	 * Construct a SuperScanner to use convenient user input abilities
	 * @param scanner Scanner to be used to get user input.
	 */
	public SuperScanner(Scanner scanner) {
		this.scanner = scanner;
	}
	
	/**
	 * Where necessary, you can access the associated {@code Scanner} object where necessary.
	 * @return Scanner object.
	 */
	public Scanner getScanner() {
		return scanner;
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
			new Style.Builder().text("Input must be a number. Please try again.\n").code(Code.TEXT_YELLOW).print();
			return nextIntUntilCorrect(prompt);
		}
	}
	
	/**
	 * Get input from user until input is a number, and falls within boundaries provided.
	 * 
	 * Both min and max parameters are inclusive; All values in between are also treated as accepted.
	 * @param prompt The prompt to be shown to the user.
	 * @param min The minimum value to be accepted. (inclusive)
	 * @param max The maximum value to be accepted. (inclusive)
	 * @return The integer that is provided by the user's input.
	 */
	public int nextIntUntilCorrect(String prompt, int min, int max) {
		int value = nextIntUntilCorrect(prompt);
		if (value < min || value > max) {
			new Style.Builder().text("Invalid option. Please try again.\n").code(Code.TEXT_YELLOW).print();
			return nextIntUntilCorrect(prompt);
		}
		return value;
	}
	
	/**
	 * Get user input of date type, while ensuring format is correct.
	 * 
	 * Date format expected should be d/M/yy, where d is day, M is month, and yy is year (2 digits)
	 * @param prompt
	 * @return Date provided by the user.
	 */
	public LocalDate nextDateUntilCorrect(String prompt) {
		try {
			return nextDate(prompt);
		}
		catch (DateTimeParseException e) {
			new Style.Builder()
				.text("Date input is incorrect. Please double check the date and format of d/m/yy")
				.code(Code.TEXT_YELLOW)
				.newLine()
				.print();
			return nextDateUntilCorrect(prompt);
		}
	}
	
	/**
	 * Get user input that is binary (either true or false)
	 * 
	 * This method accepts a few forms to represent truthness.
		<ul>
			<li>{@code true}:
				<ul>
					<li>T</li>
					<li>True</li>
					<li>Yes</li>
					<li>Y</li>
				</ul>
			</li>
			<li>{@code false}:
				<ul>
					<li>F</li>
					<li>False</li>
					<li>No</li>
					<li>N</li>
				</ul>
			</li>
		</ul>
	 * @param prompt
	 * @return
	 */
	public Boolean nextBoolUntilCorrect(String prompt) {
		try {
			return nextBool(prompt);
		}
		catch (IllegalArgumentException e) {
			new Style.Builder()
				.text("Invalid input.")
				.code(Code.TEXT_YELLOW)
				.newLine()
				.print();
			return nextBoolUntilCorrect(prompt);
		}
	}
	
	/**
	 * Prints the prompt, and then wait for user's input, on same line, expecting a number.
	 * @param prompt The prompt associated with the input.
	 * @return User's input as an integer.
	 * @throws NumberFormatException When user's input is not an integer.
	 */
	private int nextInt(String prompt) throws NumberFormatException {
		System.out.print(prompt);
		String userInput = scanner.nextLine();
		return Integer.parseInt(userInput);
	}
	

	/**
	 * Prints the prompt, and then wait for user's input, on same line, expecting a date, of d/M/yy.
	 * 
	 * d/M/yy format is used as that is the date format that the spreadsheet samples given to us are using.
	 * @param prompt The prompt associated with the input.
	 * @return User's input as an date (format: d/M/yy).
	 * @throws java.time.format.DateTimeParseException When user's input is not a date or of incorrect formatting.
	 */
	private LocalDate nextDate(String prompt) {
		System.out.print(prompt);
		String userInput = scanner.nextLine();
		return Utilities.getInstance().parseDate(userInput);
	}
	
	/**
	 * Prints the prompt, and then wait for user's input, on same line, expecting a boolean.
	 * 
	 * This method employs a very relaxed way of detecting boolean state.
	 * Users can input the following, irrespective of case:
		<ul>
			<li>{@code true}:
				<ul>
					<li>T</li>
					<li>True</li>
					<li>Yes</li>
					<li>Y</li>
				</ul>
			</li>
			<li>{@code false}:
				<ul>
					<li>F</li>
					<li>False</li>
					<li>No</li>
					<li>N</li>
				</ul>
			</li>
		</ul>
	 * 
	 * @param prompt The prompt associated with the input.
	 * @return A boolean according to user's input
	 * @throws IllegalArgumentException When user's input does not fit detectable boolean state.
	 */
	private Boolean nextBool(String prompt) throws IllegalArgumentException {
		System.out.print(prompt);
		String userInput = scanner.nextLine();
		
		if (userInput.equals("back")) {
			return null;
		}
		if (userInput.equalsIgnoreCase("false") || userInput.equalsIgnoreCase("f") || userInput.equalsIgnoreCase("no") || userInput.equalsIgnoreCase("n")) {
			return false;
		} 
		if (userInput.equalsIgnoreCase("true") || userInput.equalsIgnoreCase("t") || userInput.equalsIgnoreCase("yes") || userInput.equalsIgnoreCase("y")) {
			return true;
		}
		
		throw new IllegalArgumentException();
	}
}
