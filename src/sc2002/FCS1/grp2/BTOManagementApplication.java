package sc2002.FCS1.grp2;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Scanner;

import sc2002.FCS1.grp2.builders.DisplayMenu;
import sc2002.FCS1.grp2.builders.Style;
import sc2002.FCS1.grp2.helpers.SuperScanner;

/**
 * Main entry for our project's CLI application.
 */
public class BTOManagementApplication {
	/**
	 * System object, for easy access.
	 */
	private static BTOManagementSystem system = BTOManagementSystem.common();
	
	/**
	 * Entry point for CLI application, read and write.
	 * @param args
	 */
	public static void main(String[] args) {
		login();
		
		startResponseLoop();
		
		System.out.println("Thanks for using the BTO system. Goodbye!");
		
		system.cleanup();
	}

	/**
	 * Use this to prepare a string that centers text and places hyphens left and right of it.
	 * @param title The title to be centered.
	 * @return string ready for display.
	 */
	private static String prepareHeader(String title) {
		int max = 60;
		
		int titleSize = title.length() + 2;
		int remaining = max - titleSize;
		int left = remaining / 2;
		int right = remaining / 2;
		
		if (remaining % 2 != 0) {
			left++;
		}
		String headerText = String.format("\n\n%s %s %s\n", "-".repeat(left), title, "-".repeat(right));
		return headerText;
	}
	
	/**
	 * Displays the main menu of the application.
	 */
	private static void displayMenu() {
		ArrayList<String> menuList = system.getActiveUser().getMenu();
		ArrayList<String> menuContents = new ArrayList<>();
		
		for (int i = 1; i <= menuList.size(); i++) {
			menuContents.add(String.format("%d. %s", i, menuList.get(i-1)));
		}
		
		ArrayList<String> additionalDetails = new ArrayList<>();
		additionalDetails.add("To exit, type \"exit\"");
		additionalDetails.add("To logout, type \"logout\"");

		new DisplayMenu.Builder()
				.setTitle("Menu")
				.addContents(menuContents)
				.addContent(new Style.Builder().text("Project Display Mode").italic().toString())
				.addContent("Sorted by: " + system.getActiveUser().getListingSort())
				.addContent("Filtered by: " + system.getActiveUser().getListingFilter())
				.addContents(additionalDetails)
				.build()
				.display();
	}
	
	/**
	 * Once called, this method will always ask the user to do something, again and again, unless requested by the user to exit or logout.
	 */
	private static void startResponseLoop() {
		String response = "";
		Scanner scanner = system.getScanner();
		
		while (true) {
			displayMenu();
			
			System.out.print("Select Menu Option: ");
			while ((response = scanner.nextLine()).isEmpty()); // Since the user actions use mixture of nextLine and nextInt, we need to clear the buffer first.

			if (response.equalsIgnoreCase("exit")) {
				break;
			}
			
			if (response.equalsIgnoreCase("logout")) {
				system.logout();
				login();
				continue;
			}
			
			System.out.println("");
			handleUserResponse(response);
		}
	}
	
	/**
	 * Handle what the user has inputed.
	 * @param response To be converted
	 */
	private static void handleUserResponse(String response) {
		try {
			int index = Integer.parseInt(response);
			if (index < 1) throw new NumberFormatException();
			handleAction(index);
		}
		catch (NumberFormatException e) {
			new Style.Builder()
				.text("Invalid Option.")
				.code(Style.Code.TEXT_YELLOW)
				.bold()
				.newLine()
				.print();
		}
		catch (IllegalStateException e) {
			new Style.Builder()
				.text(e.getMessage())
				.code(Style.Code.TEXT_RED)
				.bold()
				.newLine()
				.print();
		}
		catch (IllegalArgumentException e) {
			new Style.Builder()
				.text(e.getMessage())
				.code(Style.Code.TEXT_YELLOW)
				.newLine()
				.print();
		}
		catch (InsufficientAccessRightsException e) {
			new Style.Builder()
				.text("You have attempted to use a functionality that you have ")
				.bold()
				.add256Colour(196, false)
				.text("NO ACCESS RIGHTS")
				.add256Colour(196, true)
				.bold()
				.text(" to!")
				.bold()
				.add256Colour(196, false)
				.newLine()
				.print();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Handles the option selected by user. Will pass this along to relevant action classes
	 * @param index The index provided by user.
	 * @throws Exception 
	 */
	private static void handleAction(int index) throws Exception {
		User user = system.getActiveUser();

		// this index starts at 0, for each of the specific access control index of specific user types.
		int scopedIndex = index - User.getCommonMenuOptions() - 1;
		
		if (index < 1) {
			throw new IllegalArgumentException();
		}
		
		// common menu option
		if (index == 1) {
			changePassword();
			return;
		} 
		else if (index == 2) {
			changeListingSort();
			return;
		}
		else if (index == 3) {
			changeListingFilter();
			return;
		}

		
		// cast user out to respective type
		if (user instanceof HDBOfficer) {
			HDBOfficer officer = (HDBOfficer) user;
			
			HDBOfficer.Menu selectedOption = HDBOfficer.Menu.fromOrdinal(scopedIndex);
			if (selectedOption == null) throw new NumberFormatException();
			HDBOfficerActions.handleAction(selectedOption, officer);
		}
		else if (user instanceof HDBManager) {
			HDBManager manager = (HDBManager) user;
			
			HDBManager.Menu selectedOption = HDBManager.Menu.fromOrdinal(scopedIndex);
			if (selectedOption == null) throw new NumberFormatException();
			HDBManagerActions.handleAction(selectedOption, manager);
		}
		else if (user instanceof Applicant) {
			Applicant applicant = (Applicant) user;
			
			Applicant.Menu selectedOption = Applicant.Menu.fromOrdinal(scopedIndex);
			if (selectedOption == null) throw new NumberFormatException();
			ApplicantActions.handleAction(selectedOption, applicant);
		}
		else {
			System.out.println("Logged in user appears to be of an undefined type. Unable to proceed further.");
			throw new IllegalArgumentException();
		}
	}
	
	private static void changePassword() {
		User user = system.getActiveUser();
		
		Scanner scanner = system.getScanner();
		
		System.out.print("Please enter your current password for verification: ");
		String currentPassword = scanner.nextLine();
		
		if (user.checkPassword(currentPassword)) {
			System.out.print("New password: ");
			String newPassword = scanner.nextLine();
			System.out.print("Confirm your new password: ");
			String confirmPassword = scanner.nextLine();
			
			if (newPassword.equals(confirmPassword)) {
				user.setPassword(newPassword);
				system.saveChanges(user.sourceFileType());
				System.out.println("Your password has been updated.");
			}
			else {
				System.out.println("We did not change your password, as the passwords you've entered did not match.");
			}
		}
		else {
			System.out.println("Current password is incorrect. Please try again later.");
		}
	}

	/**
	 * Show change listing sort options and ask what user want
	 */
	private static void changeListingSort() {
		User user = system.getActiveUser();
		Scanner scanner = system.getScanner();
		SuperScanner superScanner = new SuperScanner(scanner);

		new DisplayMenu.Builder()
		.setTitle("Options")
		.addContent("1. Default: Alphabetical order")
		.addContent("2. Reverse Default: Reverse alphabetical order")
		.addContent("3. Two Room Price Descending")
		.addContent("4. Two Room Price Ascending")
		.addContent("5. Three Room Price Descending")
		.addContent("6. Three Room Price Ascending")
		.addContent("7. Opening Date Ascending")
		.addContent("8. Opening Date Descending")
		.addContent("9. Closing Date Ascending")
		.addContent("10. Closing Date Descending")
		.build()
		.display();

		int choose = superScanner.nextIntUntilCorrect("Select Option: ", 1, 10);
		
		ListingSort selectedOption = ListingSort.DEFAULT;

		switch (choose) {
			case 1:
				selectedOption = ListingSort.DEFAULT;
				break;
			case 2:
				selectedOption = ListingSort.REVERSE_DEFAULT;
				break;
			case 3:
				selectedOption = ListingSort.TWO_ROOM_PRICE_DESCENDING;
				break;
			case 4:
				selectedOption = ListingSort.TWO_ROOM_PRICE_ASCENDING;
				break;
			case 5:
				selectedOption = ListingSort.THREE_ROOM_PRICE_DESCENDING;
				break;
			case 6:
				selectedOption = ListingSort.THREE_ROOM_PRICE_ASCENDING;
				break;
			case 7:
				selectedOption = ListingSort.OPENING_DATE_ASCENDING;
				break;
			case 8:
				selectedOption = ListingSort.OPENING_DATE_DESCENDING;
				break;
			case 9:
				selectedOption = ListingSort.CLOSING_DATE_ASCENDING;
				break;
			case 10:
				selectedOption = ListingSort.CLOSING_DATE_DESCENDING;
				break;
			default:
				break;
		}

		user.setListingSort(selectedOption);
		System.out.println("Your listing sort has been updated.");
	}

	/**
	 * Show change filter options and ask what user want
	 */
	private static void changeListingFilter() {
		User user = system.getActiveUser();
		Scanner scanner = system.getScanner();
		SuperScanner superScanner = new SuperScanner(scanner);

		new DisplayMenu.Builder()
		.setTitle("Options")
		.addContent("1. Default: No filter")
		.addContent("2. At least 1 available two room")
		.addContent("3. At least 1 available three room")
		.addContent("4. Filter by project name")
		.addContent("5. Filter by project neighbourhood")
		.build()
		.display();

		int choose = superScanner.nextIntUntilCorrect("Select Option: ", 1, 5);
		
		ListingFilter selectedOption = ListingFilter.DEFAULT;

		switch (choose) {
			case 1:
				selectedOption = ListingFilter.DEFAULT;
				break;
			case 2:
				selectedOption = ListingFilter.TWO_ROOM;
				break;
			case 3:
				selectedOption = ListingFilter.THREE_ROOM;
			case 4:
				System.out.print("Enter keyword: ");
				String nameKeyword = scanner.nextLine();
				selectedOption = ListingFilter.NAME;
				selectedOption.setKeyword(nameKeyword);
				break;
			case 5:
				System.out.print("Enter keyword: ");
				String neighbourhoodKeyword = scanner.nextLine();
				selectedOption = ListingFilter.NEIGHBORHOOD;
				selectedOption.setKeyword(neighbourhoodKeyword);
				break;
			default:
				break;
		}

		user.setListingFilter(selectedOption);
		System.out.println("Your listing filter has been updated.");
	}
	
	/**
	 * Different greetings depending on time of day.
	 * @return the greetings.
	 */
	private static String getGreetings() {
		LocalTime time = LocalTime.now();
		int hour = time.getHour();
		
		if (hour < 12) {
			return "Good Morning";
		}
		else if (hour < 18) {
			return "Good Afternoon";
		}
		else {
			return "Good Evening";
		}
	}
	
	/**
	 * User login flow.
	 */
	private static void login() {
		Scanner scanner = system.getScanner();
		
		System.out.print(prepareHeader("Login via SingPass"));

		System.out.print("NRIC Number: ");
		String nric = scanner.nextLine();
		User user = system.findUserByNRIC(nric);
		while (user == null) {
			System.out.println("Invalid NRIC Number. Please try again.");
			System.out.print("NRIC Number: ");
			nric = scanner.nextLine();
			user = system.findUserByNRIC(nric);
		}
		
		System.out.print("Password: ");
		String password = scanner.nextLine();
		
		int remainingTries = 2;
		while (!system.attemptLogin(user, password)) {
			if (remainingTries == 0) {
				System.out.println("Too many failed login attempts. Please try again later.");
				login();
				return;
			}
			System.out.println("Invalid password.");
			System.out.print("Password: ");
			password = scanner.nextLine();
			remainingTries--;
		}
		
		System.out.println("\n\nWelcome to Build-To-Order (BTO) Management System!");
		System.out.printf("%s, %s!\n", getGreetings(), user.getName());
		System.out.printf("You are signed in as a %s.\n", user.getReadableTypeName());
	}
}
