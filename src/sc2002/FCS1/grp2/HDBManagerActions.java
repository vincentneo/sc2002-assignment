package sc2002.FCS1.grp2;

import java.util.ArrayList;
import java.util.Scanner;

public class HDBManagerActions {
	private static BTOManagementSystem system;
	
	public static void setSystem(BTOManagementSystem system) {
		HDBManagerActions.system = system;
	}
	
	public static void handleAction(HDBManager.Menu option, HDBManager user) throws Exception {
		switch (option) {
		case CREATE_PROJECT:
			createProject();
			break;
		}
	}
	
	private static void createProject() throws Exception {
		Scanner scanner = system.getScanner();
		
		System.out.print("Enter Project Name: ");
		String projectName = scanner.next();
		System.out.print("Enter Neighbourhood: ");
		String neighbourhood = scanner.next();
//		System.out.print("Enter maximum number of two room units: ");
//		String maxTwoRoomUnits = scanner.next();
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
		
		BTOProject project = new BTOProject(projectName, neighbourhood, 0, 0, 0, 0, "", "", "", 0, new ArrayList<>());
		system.addProject(project);
//		BTOProject project = new BTOProject(projectName, Neighborhood, maxTwoRoomUnits, maxThreeRoomUnits, openingDate, closingDate,  this, officerSlots, officers);
		
	}
}
