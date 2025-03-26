package sc2002.FCS1.grp2;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Scanner;

public class BTOManagementSystem {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		CSVParser parser = new CSVParser();
		
		ArrayList<User> users = parser.retrieveAllUsers();
		
		debugPrintAllUsers(users);

		Scanner scanner = new Scanner(System.in);
		User user = login(scanner, users);
		

		System.out.println("\n\nWelcome to Build-To-Order (BTO) Management System!");
		System.out.printf("%s, %s!\n", getGreetings(), user.getName());
		System.out.printf("You are signed in as a %s.\n", user.getReadableTypeName());
		
		System.out.print(generateMenu(user));
		
		startResponseLoop(scanner, user);
		
		System.out.println("Thanks for using the BTO system. Goodbye!");
		
		scanner.close();
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
	
	private static String generateMenu(User user) {
		ArrayList<String> menuList = user.getMenu();
		String result = prepareHeader("Menu");
		
		for (int i = 0; i < menuList.size(); i++) {
			result += String.format("%d. %s\n", i, menuList.get(i));
		}
		
		result += "To exit, type \"exit\"\n";
		result += "-".repeat(60);
		result += "\n";
		
		return result;
	}
	
	private static void startResponseLoop(Scanner scanner, User user) {
		String response = "";
		
		while (true) {
			System.out.print("Your Option: ");
			response = scanner.nextLine();
			if (response.equalsIgnoreCase("exit")) {
				break;
			}
			
			handleUserResponse(response, scanner, user);
		}
	}
	
	private static void handleUserResponse(String response, Scanner scanner, User user) {
		try {
			int index = Integer.parseInt(response);
			handleAction(index, user);
		}
		catch (Exception e) {
			System.out.println("Invalid option.");
		}
	}
	
	private static void handleAction(int index, User user) {
		
		if (index == 0) {
			// TODO: Change password flow
		}
		
		// cast user out to respective type
		if (user instanceof HDBOfficer) {
			HDBOfficer officer = (HDBOfficer) user;
		}
		else if (user instanceof HDBManager) {
			HDBManager manager = (HDBManager) user;
		}
		else if (user instanceof Applicant) {
			Applicant applicant = (Applicant) user;
		}
		else {
			System.out.println("Logged in user appears to be of an undefined type. Unable to proceed further.");
			throw new IllegalArgumentException();
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
	
	// TODO: Delete once done. This method is only intended for testing.
	private static void debugPrintAllUsers(ArrayList<User> users) {		
		// del later
		for (User user : users) {
			System.out.printf("%s: ", user.getClass());
			user.print();
		}
		
	}

	private static User findUserByNRIC(String nric, ArrayList<User> users) {
		for (User user : users) {
			if (user.getNric().equalsIgnoreCase(nric)) {
				return user;
			}
		}
		return null;
	}
	
	private static User login(Scanner scanner, ArrayList<User> users) {
		System.out.print(prepareHeader("Login via SingPass"));

		System.out.print("NRIC Number: ");
		String nric = scanner.next();
		User user = findUserByNRIC(nric, users);
		while (user == null) {
			System.out.println("Invalid NRIC Number. Please try again.");
			System.out.print("NRIC Number: ");
			nric = scanner.next();
			user = findUserByNRIC(nric, users);
		}
		
		System.out.print("Password: ");
		String password = scanner.next();
		
		int remainingTries = 2;
		while (!user.checkPassword(password)) {
			if (remainingTries == 0) {
				System.out.println("Too many failed login attempts. Please try again later.");
				return login(scanner, users);
			}
			System.out.println("Invalid password.");
			System.out.print("Password: ");
			password = scanner.next();
			remainingTries--;
		}
		
		if (scanner.hasNextLine()) {
			scanner.nextLine();
		}
		
		return user;
	}
}
