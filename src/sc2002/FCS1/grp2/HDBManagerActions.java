package sc2002.FCS1.grp2;

import java.time.LocalDate;
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
			createProject(user);
			break;
		}
	}
	
	private static void createProject(HDBManager manager) throws Exception {
		Scanner scanner = system.getScanner();
		SuperScanner superScanner = new SuperScanner(scanner);
		
		System.out.print("Enter Project Name: ");
		String projectName = scanner.next();
		System.out.print("Enter Neighbourhood: ");
		String neighbourhood = scanner.next();
		int maxRoomOne = superScanner.nextIntUntilCorrect("Enter maxmium number of Type 1 units: ");
		int priceRoomOne = superScanner.nextIntUntilCorrect("Enter price of Type 1 unit: $");
		
		int maxRoomTwo = superScanner.nextIntUntilCorrect("Enter maxmium number of Type 2 units: ");
		int priceRoomTwo = superScanner.nextIntUntilCorrect("Enter price of Type 2 unit: $");
		
		LocalDate openingDate = superScanner.nextDateUntilCorrect("Enter Opening Date (d/m/yy): ");
		LocalDate closingDate = superScanner.nextDateUntilCorrect("Enter Closing Date (d/m/yy): ");

		int officerSlots = superScanner.nextIntUntilCorrect("Enter number of officer slots: ");
//		int officerSlots = scanner.nextInt();
//		ArrayList<HDBOfficer> officers; //;
		// TODO: officers
		
		BTOProject project = new BTOProject(projectName, neighbourhood,
				maxRoomOne, maxRoomTwo, priceRoomOne, priceRoomTwo,
				openingDate, closingDate, manager, officerSlots, new ArrayList<>());
		system.addProject(project);
//		BTOProject project = new BTOProject(projectName, Neighborhood, maxTwoRoomUnits, maxThreeRoomUnits, openingDate, closingDate,  this, officerSlots, officers);
		
	}
	
	private static void editProject(HDBManager manager) {
		Scanner scanner = system.getScanner();
		System.out.println("Enter Project Name: ");
		String project = scanner.next();
		ArrayList<BTOProject>  projects = system.getProjects(); 
		ArrayList<BTOProject> ProjectsFinder = projects.stream()
				.filter(p -> projects == project)
				.collect(Collectors.toArrayList);
		
		
		
		
		
	}
}
