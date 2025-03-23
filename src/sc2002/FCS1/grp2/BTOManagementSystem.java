package sc2002.FCS1.grp2;

import java.util.ArrayList;
import java.util.Scanner;

public class BTOManagementSystem {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		CSVParser parser = new CSVParser();
		
		ArrayList<User> users = parser.retrieveAllUsers();
		Scanner scanner = new Scanner(System.in);
		
		// del later
		for (User user : users) {
			System.out.printf("%s: ", user.getClass());
			user.print();
		}
		
		System.out.println("Welcome to Build-To-Order (BTO) Management System!");
		System.out.println("-".repeat(30));
		
		System.out.println("\n\nLogin");
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
	}

	private static User findUserByNRIC(String nric, ArrayList<User> users) {
		for (User user : users) {
			if (user.nric.equalsIgnoreCase(nric)) {
				return user;
			}
		}
		return null;
	}
}
