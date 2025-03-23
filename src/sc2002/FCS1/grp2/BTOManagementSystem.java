package sc2002.FCS1.grp2;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Scanner;

public class BTOManagementSystem {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		CSVParser parser = new CSVParser();
		
		ArrayList<User> users = parser.retrieveAllUsers();
		
		System.out.println("Welcome to Build-To-Order (BTO) Management System!");
		System.out.println("-".repeat(60));
		debugPrintAllUsers(users);

		Scanner scanner = new Scanner(System.in);
		User user = login(scanner, users);
		

		
		System.out.printf("%s, %s!\n", getGreetings(), user.getName());
		
		scanner.close();
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
		System.out.printf("\n\n%s %s %s\n", "-".repeat(20), "Login via SingPass", "-".repeat(20));
		
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
		
		return user;
	}
}
