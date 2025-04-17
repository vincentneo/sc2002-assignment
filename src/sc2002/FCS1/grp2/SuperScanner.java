package sc2002.FCS1.grp2;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

import sc2002.FCS1.grp2.Style.Code;

/**
 * An extension of features for the {@code Scanner} class.
 * 
 * This class allows for more convenient input gathering from users, for types that may be error prone.
 * For example, when expecting an integer, the user may input something else, such as text. 
 * Here, it will automatically and recursively ask the user until the input given is in correct format.
 */
public class SuperScanner {
	private Scanner scanner;
	
	public SuperScanner(Scanner scanner) {
		this.scanner = scanner;
	}
	
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
	
	public int nextIntUntilCorrect(String prompt, int min, int max) {
		int value = nextIntUntilCorrect(prompt);
		if (value < min || value > max) {
			new Style.Builder().text("Invalid option. Please try again.\n").code(Code.TEXT_YELLOW).print();
			return nextIntUntilCorrect(prompt);
		}
		return value;
	}
	
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
