package sc2002.FCS1.grp2;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Scanner;

public class BTOManagementApplication {
	
	private static BTOManagementSystem system = new BTOManagementSystem();
//	private static Scanner scanner = new Scanner(System.in);

	public static void main(String[] args) {

		system.debugPrintAllUsers();
		
		HDBManagerActions.setSystem(system);
		
		// TODO: Delete once finish debugging
		System.out.print("Proj 1 officers: ");
		System.out.println(system.getProjects().getFirst().getOfficers());

		User user = login();
		
		System.out.println("\n\nWelcome to Build-To-Order (BTO) Management System!");
		System.out.printf("%s, %s!\n", getGreetings(), user.getName());
		System.out.printf("You are signed in as a %s.\n", user.getReadableTypeName());
		
		startResponseLoop();
		
		System.out.println("Thanks for using the BTO system. Goodbye!");
		
		system.cleanup();
	}
	
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
	
	private static void generateMenu() {
		
		
		ArrayList<String> menuList = system.getActiveUser().getMenu();
		ArrayList<String> contents = new ArrayList<>();
//		String result = prepareHeader("Menu");
		
		for (int i = 0; i < menuList.size(); i++) {
			contents.add(String.format("%d. %s\n", i, menuList.get(i)));
		}
		
		new DisplayMenu.Builder()
				.setTitle("Menu")
				.setContents(contents)
				.build()
				.display();
		
//		result += "To exit, type \"exit\"\n";
//		result += "-".repeat(60);
//		result += "\n";
//		
//		return result;
	}
	
	private static void startResponseLoop() {
		String response = "";
		Scanner scanner = system.getScanner();
		
		while (true) {
//			System.out.print(generateMenu());
			
			if (scanner.hasNextLine()) {
				scanner.nextLine();
			}
			
			System.out.print("Select Menu Option: ");
			response = scanner.nextLine();
			if (response.equalsIgnoreCase("exit")) {
				break;
			}
			
			handleUserResponse(response);
		}
	}
	
	private static void handleUserResponse(String response) {
		try {
			int index = Integer.parseInt(response);
			handleAction(index);
		}
		catch (NumberFormatException e) {
			System.out.println("Invalid option.");
		}
		catch (InsufficientAccessRightsException e) {
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void handleAction(int index) throws Exception {
		User user = system.getActiveUser();
		
		// this index starts at 0, for each of the specific access control index of specific user types.
		int scopedIndex = index - User.getCommonMenuOptions();
		
		if (index == 0) {
			changePassword();
		}
		
		// cast user out to respective type
		if (user instanceof HDBOfficer) {
			HDBOfficer officer = (HDBOfficer) user;
		}
		else if (user instanceof HDBManager) {
			HDBManager manager = (HDBManager) user;
			
			HDBManager.Menu selectedOption = HDBManager.Menu.fromOrdinal(scopedIndex);
			HDBManagerActions.handleAction(selectedOption, manager);
		}
		else if (user instanceof Applicant) {
			Applicant applicant = (Applicant) user;
			
			Applicant.Menu selectedOption = Applicant.Menu.fromOrdinal(scopedIndex);
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
		String currentPassword = scanner.next();
		
		if (user.checkPassword(currentPassword)) {
			System.out.print("New password: ");
			String newPassword = scanner.next();
			System.out.print("Confirm your new password: ");
			String confirmPassword = scanner.next();
			
			if (newPassword.equals(confirmPassword)) {
				user.setPassword(newPassword);
				system.saveChanges(user.sourceFileType());
				System.out.println("Your password has been updated.");
			}
			else {
				System.out.println("We did not change your password, as your new passwords did not match.");
			}
		}
		else {
			System.out.println("Current password is incorrect. Please try again later.");
		}
	}
	
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
	

	private static User login() {
		Scanner scanner = system.getScanner();
		
		System.out.print(prepareHeader("Login via SingPass"));

		System.out.print("NRIC Number: ");
		String nric = scanner.next();
		User user = system.findUserByNRIC(nric);
		while (user == null) {
			System.out.println("Invalid NRIC Number. Please try again.");
			System.out.print("NRIC Number: ");
			nric = scanner.next();
			user = system.findUserByNRIC(nric);
		}
		
		System.out.print("Password: ");
		String password = scanner.next();
		
		int remainingTries = 2;
		while (!system.attemptLogin(user, password)) {
			if (remainingTries == 0) {
				System.out.println("Too many failed login attempts. Please try again later.");
				return login();
			}
			System.out.println("Invalid password.");
			System.out.print("Password: ");
			password = scanner.next();
			remainingTries--;
		}
		
		return user;
	}
}
