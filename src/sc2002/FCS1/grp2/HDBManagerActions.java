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
			break;
		case DELETE_PROJECT:
			deleteProject(user);
			break;
		case FILTER_PROJECT:
			filterProjects(user);
			break;
			
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

		
		
		BTOProject project = new BTOProject(projectName, neighbourhood,
				maxRoomOne, maxRoomTwo, priceRoomOne, priceRoomTwo,
				openingDate, closingDate, manager, officerSlots, new ArrayList<>());
		system.addProject(project);
//		BTOProject project = new BTOProject(projectName, Neighborhood, maxTwoRoomUnits, maxThreeRoomUnits, openingDate, closingDate,  this, officerSlots, officers);
		
	}
	
	private static void editProject(HDBManager manager) {
		Scanner scanner = system.getScanner();
		SuperScanner superScanner = new SuperScanner(scanner);
		
		
		ArrayList<BTOProject> project = system.getApplicableProjects();
		for(int i = 0; i<project.size(); i++) {
			System.out.println((i+1) + ". " + project.get(i));
		}
		
		int choose = superScanner.nextIntUntilCorrect("Which Project would you like to edit? (enter the corresponding number):");
		String projectName = project.get(choose-1).getProjectName();
		
			
			
		ArrayList<BTOProject>  projects = system.getApplicableProjects(); 
		ArrayList<BTOProject> ProjectsFinder = projects.stream()
				.filter(p -> p.getProjectName().equalsIgnoreCase(projectName))
				.collect(Collectors.toCollection(ArrayList::new));
		
		if(ProjectsFinder.isEmpty()) {
			System.out.println("Project not found");
			return;
		}
		else {
		System.out.println("Matching Projects: ");
		for(BTOProject p: ProjectsFinder) {
			System.out.println(p);
			}
		}
		BTOProject selectedProject = ProjectsFinder.get(0); //need to figure out how to get the object..
		
		System.out.println("What would you like to edit?");
		System.out.println("Options: ProjectName, Neighbourhood, maxRoomOne, priceRoomOne, maxRoomTwo, priceRoomTwo, openingDate, closingDate, officerSlots");
		String choice = scanner.next();

	
		
		switch(choice) {
			case "ProjectName":
				System.out.println("What would you like to change Project name to?"); //need unique project name
				String newName = scanner.next();
				selectedProject.setProjectName(newName);
				System.out.println("Project name have been updated");
				System.out.println(selectedProject);
				break;
			
			case "Neighbourhood":
				System.out.println("What would you like to change Neighbourhood to?");
				String newNeighbourhood = scanner.next();
				selectedProject.setNeighborhood(newNeighbourhood);
				break;
				
			case "maxRoomOne":
				System.out.println("What would you like to change maxRoomOne to?");
				int newMax = scanner.nextInt();
				selectedProject.setMaxTwoRoomUnits(newMax);
				break;
			case "priceRoomOne":
				System.out.println("What would you like to change priceRoomOne to?");
				int newMaxprice = scanner.nextInt();
				selectedProject.setTwoRoomPrice(newMaxprice);
				break;
			case "maxRoomTwo":
				System.out.println("What would you like to change maxRoomTwo to?");
				int newMax2 = scanner.nextInt();
				selectedProject.setMaxThreeRoomUnits(newMax2);
				break;
			case "priceRoomTwo":
				System.out.println("What would you like to change priceRoomTwo to?");
				int newMaxprice2 = scanner.nextInt();
				selectedProject.setThreeRoomPrice(newMaxprice2);
				break;
			case "openingDate":
				System.out.println("What would you like to change openingDate to?");
				LocalDate newOpening = superScanner.nextDateUntilCorrect("Enter Opening Date (d/m/yy): ");
				selectedProject.setOpeningDate(newOpening);
				break;
			case "closingDate":
				System.out.println("What would you like to change closingDate to?");
				LocalDate newClosing = superScanner.nextDateUntilCorrect("Enter Closing Date (d/m/yy): ");
				selectedProject.setClosingDate(newClosing);
				break;
			case "officerSlots":
				System.out.println("What would you like to change officerSlots to?");
				int newOfficerSlots = scanner.nextInt();
				selectedProject.setTotalOfficerSlots(newOfficerSlots);
				break;
			
			default:
				System.out.println("Invalid option. Please try again.");
				break;

				
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
	
	private static void toggleVisibility(HDBManager manager) {
		Scanner scanner = system.getScanner();
		
		System.out.println("All Projects: ");
		ArrayList<BTOProject> projects = system.getProjects();
		for(int i = 0; i<projects.size(); i++) {
			System.out.println((i+1) + ". " + projects.get(i));
		}
		
		System.out.println("Which Project would you like to toggle visibility?");
		int choice = scanner.nextInt();
		
	}
	private static void generateReport(HDBManager manager) {
		//filter based on category
	}
	
	private static void approveApplication(HDBManager manager) {
		
	}
	
	private static void viewAllProjects(HDBManager manager) {
		System.out.println("All Project listings:");
		ArrayList<BTOProject> projects = system.getProjects();
		for(int i = 0; i<projects.size(); i++) {
			System.out.println((i+1) + ". " + projects.get(i));
		}
	}
	
	private static void filterProjects(HDBManager manager) {
		Scanner scanner = system.getScanner();
		System.out.println("Filter Project Listings: ");
		
		System.out.println("Enter Project Name (or part of it): ");
		String projectName = scanner.nextLine().trim();
		
		System.out.print("Enter Neighborhood (or part of it): ");
	    String neighborhood = scanner.nextLine().trim();
	    
	    System.out.print("Enter Manager Name: ");
	    String Manager = scanner.nextLine().trim();
	    
	    System.out.println("Enter number of Two-Room flats: ");
	    int num1 = scanner.nextInt();
	    
	    //to do
//	    System.out.println("Enter Two-Room flat price: ");
//	    int price1 = scanner.nextInt();
	   
	    System.out.println("Enter number of Three-Room flats: ");
	    int num2 = scanner.nextInt();
	    
	 // to do
//	    System.out.println("Enter Three-Room flat price: ");
//	    int price2 = scanner.nextInt();
	    
	    ArrayList<BTOProject> projects = system.getProjects();
	    if(projects == null) {
	    	System.out.println("Error, no ongoing projects");
	    	return;
	    }
	   
	    ArrayList<BTOProject> filteredProjects = projects.stream()
	    		.filter(p-> projectName.isEmpty() || p.getProjectName().toLowerCase().contains(projectName.toLowerCase()))
	    		.filter(p-> neighborhood.isEmpty() || p.getNeighborhood().toLowerCase().contains(neighborhood.toLowerCase()))
	    		.filter(p -> Manager.isEmpty() || p.getManagerInCharge().toLowerCase().contains(Manager.toLowerCase()))
	    		.filter(p -> num1.isEmpty() || p.getMaxTwoRoomUnits.equals(num1))
	    		.filter (p-> price1.isEmpty() || p.getTwoRoomPrice.equals(price1))
	    		.filter(p -> num2.isEmpty() || p.getMaxThreeRoomUnits.equals(num2))
	    		.filter (p-> price2.isEmpty() || p.getThreeRoomPrice.equals(price2))
	    		.collect(Collectors.toCollection(ArrayList::new));
	    System.out.println("Filtered Projects: ");
	    if(filteredProjects.isEmpty()) {
	    	System.out.println("No Projects match the given criteria");
	    	}
	    else {
	    	for (int i = 0; i < filteredProjects.size(); i++) {
	            System.out.println((i + 1) + ". " + filteredProjects.get(i));
	        }
	    }
	    		
	}
	
	
	
}
