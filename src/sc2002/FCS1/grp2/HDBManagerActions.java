package sc2002.FCS1.grp2;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.stream.Collectors;

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
		case EDIT_PROJECT:
			editProject(user);
		case DELETE_PROJECT:
			deleteProject(user);
		}
	}
	
	private static boolean checkProjectCreationEligibility() {
		ArrayList<BTOProject> applicableProjects = system.getApplicableProjects();
		
		LocalDate today = LocalDate.now();
		long count = applicableProjects.stream()
				.filter(p -> {
					if (today.isEqual(p.getOpeningDate())) {
						return true;
					}
					
					if (today.isEqual(p.getClosingDate())) {
						return true;
					}
						
					return today.isAfter(p.getOpeningDate()) && today.isBefore(p.getClosingDate());
					})
				.count();
		return count < 1;
	}
	
	private static void createProject(HDBManager manager) throws Exception {
		if (!checkProjectCreationEligibility()) {
			System.out.println("You can only have one active project on hand. Hence you are not allowed to create a new project at this time.");
			return;
		}
		
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
		SuperScanner superScanner = new SuperScanner(scanner);
		
		System.out.println(system.getApplicableProjects());
		System.out.println("Enter Project Name: ");
		String projectName = scanner.next();
		ArrayList<BTOProject>  projects = system.getApplicableProjects(); 
		ArrayList<BTOProject> ProjectsFinder = projects.stream()
				.filter(p -> p.getProjectName().equals(projectName))
				.collect(Collectors.toCollection(ArrayList::new));
		
		if(projects.isEmpty()) {
			System.out.println("Project not found");
		}
		else {
		System.out.println("Matching Projects: ");
		for(BTOProject p: ProjectsFinder) {
			System.out.println(p);
			}
		BTOProject s = ProjectsFinder.get(0); //need to figure out how to get the object...
		}
		System.out.println("What would you like to edit?");
		String choice = scanner.next();
		switch(choice) {
			case "ProjectName":
				System.out.println("What would you like to change Project name to?");
				String newName = scanner.next();
				s.setProjectName(newName);
				break;
			
			case "Neighbourhood":
				System.out.println("What would you like to change Neighbourhood to?");
				String newNeighbourhood = scanner.next();
				p.setNeighbourhood(newNeighbourhood);
				break;
				
			case "maxRoomOne":
				System.out.println("What would you like to change maxRoomOne to?");
				int newMax = scanner.nextInt();
				p.setmaxRoomOne(newMax);
				break;
			case "priceRoomOne":
				System.out.println("What would you like to change priceRoomOne to?");
				int newMaxprice = scanner.nextInt();
				p.setpriceRoomOne(newMaxprice);
				break;
			case "maxRoomTwo":
				System.out.println("What would you like to change maxRoomTwo to?");
				int newMax2 = scanner.nextInt();
				p.setmaxRoomTwo(newMax2);
				break;
			case "priceRoomTwo":
				System.out.println("What would you like to change priceRoomTwo to?");
				int newMaxprice2 = scanner.nextInt();
				p.setpriceRoomTwo(newMaxprice2);
				break;
			case "openingDate":
				System.out.println("What would you like to change openingDate to?");
				LocalDate newOpening = superScanner.nextDateUntilCorrect("Enter Opening Date (d/m/yy): ");
				p.setopeningDate(newOpening);
				break;
			case "closingDate":
				System.out.println("What would you like to change closingDate to?");
				LocalDate newClosing = superScanner.nextDateUntilCorrect("Enter Closing Date (d/m/yy): ");
				p.setclosingDate(newClosing);
				break;
			case "officerSlots":
				System.out.println("What would you like to change officerSlots to?");
				int newOfficerSlots = scanner.nextInt();
				p.setofficerSlots(newOfficerSlots);
				break;
			
			default:
				
		}
		system.saveChanges(null);
		}
	
	
	private static void deleteProject(HDBManager manager) {
		Scanner scanner = system.getScanner();
		SuperScanner superScanner = new SuperScanner(scanner);
		
		System.out.println("All Project listings: ");
		
		ArrayList<BTOProject> projects = system.getProjects();
		for(int i = 0; i<projects.size(); i++) {
			System.out.println((i+1) + ". " + projects.get(i));
		}
		System.out.println("Which Project listing would you like to delete?");
		int choice = scanner.nextInt();
		BTOProject removed = projects.remove(choice-1);
		system.saveChanges(null);
		System.out.println("Project " + removed.getProjectName() + "has been deleted");
		
		
		
		
	}
}
