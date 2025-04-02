package sc2002.FCS1.grp2;

import java.util.ArrayList;
import java.util.Scanner;

public class HDBManagerActions {
	private static BTOManagementSystem system;
	
	public static void setSystem(BTOManagementSystem system) {
		HDBManagerActions.system = system;
	}
	
	public static void handleAction(HDBManager.Menu option, HDBManager user) {
		switch (option) {
		case CREATE_PROJECT:
			createProject();
			break;
		}
	}
	
	private static void createProject() {
		Scanner scanner = system.getScanner();
		
		System.out.println("Enter Project Name: ");
		String projectName = scanner.nextLine();
//		System.out.println("Enter Neighbourhood: ");
//		String Neighborhood = scanner.toString();
//		System.out.println("Enter Maximum two room units: ");
//		int maxTwoRoomUnits = scanner.nextInt();
//		System.out.println("Enter Maximum three room units: ");
//		int maxThreeRoomUnits = scanner.nextInt();
//		System.out.println("Enter Opening Date: ");
//		String openingDate = scanner.toString();
//		System.out.println("Enter Closing Date: ");
//		String closingDate = scanner.toString();
//		System.out.println("Enter number of officer slots: ");
//		int officerSlots = scanner.nextInt();
//		ArrayList<HDBOfficer> officers; //;
		// to do officers
		
		
//		BTOProject project = new BTOProject(ProjectName, Neighborhood, maxTwoRoomUnits, maxThreeRoomUnits, openingDate, closingDate,  this, officerSlots, officers);
		
	}
}
