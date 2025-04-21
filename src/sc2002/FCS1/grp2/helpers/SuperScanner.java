package sc2002.FCS1.grp2.helpers;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

import sc2002.FCS1.grp2.builders.Style;
import sc2002.FCS1.grp2.builders.Style.Builder;
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
			Utilities.getInstance().printYellow("Date input is incorrect. Please double check the date and format of d/m/yy");
			System.out.println();
			return nextDateUntilCorrect(prompt);
		}
	}
	
	/**
	 * Get user input that is binary (either true or false)
	 * 
	 * This method accepts a few forms to represent truthness.
	 * <ul>
	 * 		<li>true/false</li>
	 * 		<li>Yes/No</li>
	 * 		<li>y/n<li>
	 * </ul>
	 * @param prompt
	 * @return
	 */
	public Boolean nextBoolUntilCorrect(String prompt) {
		try {
			return nextBool(prompt);
		}
		catch (IllegalArgumentException e) {
			Utilities.getInstance().printYellow("Invalid input.");
			System.out.println();
			return nextBoolUntilCorrect(prompt);
		}
	}
	
	private int nextInt(String prompt) {
		System.out.print(prompt);
		String userInput = scanner.nextLine();
		return Integer.parseInt(userInput);
	}
	
	private LocalDate nextDate(String prompt) {
		System.out.print(prompt);
		String userInput = scanner.nextLine();
		return Utilities.getInstance().parseDate(userInput);
	}
	
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
