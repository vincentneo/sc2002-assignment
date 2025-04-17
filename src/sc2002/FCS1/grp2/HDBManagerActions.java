package sc2002.FCS1.grp2;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import sc2002.FCS1.grp2.BTOProject.TableColumnOption;
import sc2002.FCS1.grp2.Style.Code;

public class HDBManagerActions {
	private static BTOManagementSystem system;
	
	public static void setSystem(BTOManagementSystem system) {
		HDBManagerActions.system = system;
	}
	
	public static void handleAction(HDBManager.Menu option, HDBManager user) throws Exception {
		switch (option) {
		case VIEW_ALL_PROJECTS:
			viewAllProjects(user);
			break;
		case VIEW_CREATED_PROJECTS:
			viewCreatedProjects(user);
			break;
		case CREATE_PROJECT:
			createProject(user);
			break;
		case EDIT_PROJECT:
			editProject(user);
			break;
		case DELETE_PROJECT:
			deleteProject(user);
			break;
		// case FILTER_PROJECT:
		// 	filterProjects(user);
		// 	break;
		case VIEW_PENDING_OFFICER_REQUESTS:
			viewPendingOfficerRequests(user);
			break;
		case VIEW_ALL_ENQUIRIES:
			viewAllEnquiries(user);
			break;
		}
	}
	

	/**
	 * Check if the project creation is eligible based on the opening and closing dates.
	 * The project can only be created if there are no other projects that are on the same period (inclusive).
	 * @param openingDate Opening date of the new project.
	 * @param closingDate Closing date of the new project.
	 * @return true if the project can be created, false otherwise.
	 */
	private static boolean checkProjectCreationEligibility(LocalDate openingDate, LocalDate closingDate) {
		// ArrayList<BTOProject> applicableProjects = system.getApplicableProjects();
		
		// LocalDate today = LocalDate.now();
		// long count = applicableProjects.stream()
		// 		.filter(p -> {
		// 			if (today.isEqual(p.getOpeningDate())) {
		// 				return true;
		// 			}
					
		// 			if (today.isEqual(p.getClosingDate())) {
		// 				return true;
		// 			}
						
		// 			return today.isAfter(p.getOpeningDate()) && today.isBefore(p.getClosingDate());
		// 			})
		// 		.count();
		// return count < 1;

		ArrayList<BTOProject> applicableProjects = system.getApplicableProjects();

		for(BTOProject project : applicableProjects) {
			if (project.isDateWithin(closingDate) || project.isDateWithin(openingDate)) {
				System.out.println("The new project's dates overlap with an existing project.");
				System.out.println("Existing Project: " + project.getProjectName() + " (" + project.getOpeningDate() + " - " + project.getClosingDate() + ")");
				return false; // Dates overlap with an existing project
			}
		}

		return true;
	}
	

	/**
	 * Create a new project and add it to the system.
	 * @param manager The manager creating the project.
	 * @throws Exception If there is an error during project creation or adding the new project to the system.
	 */
	private static void createProject(HDBManager manager) throws Exception {
		// Managers are able to create projects if the dates of the project are not within the range of any other projects they are in charge regardless of current time.
		
		// if (!checkProjectCreationEligibility()) {
		// 	System.out.println("You can only have one active project on hand. Hence you are not allowed to create a new project at this time.");
		// 	return;
		// } 

	
		
		Scanner scanner = system.getScanner();
		SuperScanner superScanner = new SuperScanner(scanner);
		
		System.out.print("Enter Project Name: ");
		String projectName = scanner.nextLine();
		System.out.print("Enter Neighbourhood: ");
		String neighbourhood = scanner.nextLine();
		int maxTwoRoom = superScanner.nextIntUntilCorrect("Enter maxmium number of 2-Room units: ");
		int priceTwoRoom = superScanner.nextIntUntilCorrect("Enter price of 2-Room unit: $");
		
		int maxThreeRoom = superScanner.nextIntUntilCorrect("Enter maxmium number of 3-Room units: ");
		int priceThreeRoom = superScanner.nextIntUntilCorrect("Enter price of 3-Room unit: $");
		
		LocalDate openingDate = superScanner.nextDateUntilCorrect("Enter Opening Date (d/m/yy): ");
		LocalDate closingDate = superScanner.nextDateUntilCorrect("Enter Closing Date (d/m/yy): ");

		if (!checkProjectCreationEligibility(openingDate, closingDate)) {
			System.out.println("You can only have one active project on hand on within an application period. Hence you are not allowed to create a new project at that time.");
			return;
		}

		int officerSlots = superScanner.nextIntUntilCorrect("Enter number of officer slots: ");

		BTOProject newProject;
		try {
			newProject = new BTOProject(projectName, neighbourhood,
				maxTwoRoom, maxThreeRoom, priceTwoRoom, priceThreeRoom,
				openingDate, closingDate, manager, officerSlots);
		}
		catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
			System.out.println("Please try again.");
			return;
		}

		system.addProject(newProject);
//		BTOProject project = new BTOProject(projectName, Neighborhood, maxTwoRoomUnits, maxThreeRoomUnits, openingDate, closingDate,  this, officerSlots, officers);
	}
	

	/**
	 * Edit an existing project.
	 * @param manager The manager editing the project.
	 */
	private static void editProject(HDBManager manager) {
		Scanner scanner = system.getScanner();
		SuperScanner superScanner = new SuperScanner(scanner);
		
		
		ArrayList<BTOProject> projects = system.getApplicableProjects();

		if(projects == null || projects.isEmpty()) {
            System.out.println("You don't have any projects to in charge.");
            return;
        }

		projects = system.filterProjects(projects);
		if (projects.isEmpty()) {
			System.out.println("There is currently no BTO projects suitable for your filter citeria.");
			return;
		}

		List<BTOProject.TableColumnOption> options = new ArrayList<>();
		options.add(TableColumnOption.INDEX_NUMBER);
		options.add(TableColumnOption.ROOM_ONE_UNITS);
		options.add(TableColumnOption.ROOM_ONE_PRICE);
		options.add(TableColumnOption.ROOM_TWO_UNITS);
		options.add(TableColumnOption.ROOM_TWO_PRICE);
		options.add(TableColumnOption.OPENING_DATE);
		options.add(TableColumnOption.CLOSING_DATE);
		options.add(TableColumnOption.OFFICERS);
		options.add(TableColumnOption.OFFICER_SLOTS);
		options.add(TableColumnOption.VISIBILITY);

		BTOProject.display(projects, options);
		
		int choose = superScanner.nextIntUntilCorrect("Which Project would you like to edit? (enter the corresponding number): ", 1, projects.size());
		BTOProject selectedProject = projects.get(choose-1);
		
//		ArrayList<BTOProject> projects = system.getApplicableProjects(); 
//		ArrayList<BTOProject> ProjectsFinder = projects.stream()
//				.filter(p -> p.getProjectName().equalsIgnoreCase(projectName))
//				.collect(Collectors.toCollection(ArrayList::new));
//		
//		if(ProjectsFinder.isEmpty()) {
//			System.out.println("Project not found");
//			return;
//		}
//		else {
//		System.out.println("Matching Projects: ");
//		for(BTOProject p: ProjectsFinder) {
//			System.out.println(p);
//			}
//		}

		System.out.println("What would you like to edit?");
		
		new DisplayMenu.Builder()
		.setTitle("Options")
		.addContent("1. Project Name")
		.addContent("2. Project Neighbourhood")
		.addContent(String.format("3. %s Remaining Slots", FlatType.TWO_ROOM))
		.addContent(String.format("4. %s Price", FlatType.TWO_ROOM))
		.addContent(String.format("5. %s Remaining Slots", FlatType.THREE_ROOM))
		.addContent(String.format("6. %s Price", FlatType.THREE_ROOM))
		.addContent("7. Opening Date")
		.addContent("8. Closing Date")
		.addContent("9. Officer Slots")
		.addContent("10. Visibility")
		.build()
		.display();
		
		int choice = superScanner.nextIntUntilCorrect("Choose an option: ", 1, 10);
		
		try {
			switch(choice) {
				case 1:
					System.out.println("What would you like to change Project name to?"); //need unique project name
					String newName = scanner.next();
					selectedProject.setProjectName(newName);
					System.out.println("Project name have been updated");
					System.out.println(selectedProject);
					break;
				
				case 2:
					System.out.println("What would you like to change Neighbourhood to?");
					String newNeighbourhood = scanner.next();
					selectedProject.setNeighborhood(newNeighbourhood);
					break;
					
				case 3:
					int newMax = superScanner.nextIntUntilCorrect("What would you like to change maxRoomOne to? : ");
					selectedProject.setTwoRoomUnits(newMax);
					break;
				case 4:
					int newMaxprice = superScanner.nextIntUntilCorrect("What would you like to change priceRoomOne to? : ");
					selectedProject.setTwoRoomPrice(newMaxprice);
					break;
				case 5:
					int newMax2 = superScanner.nextIntUntilCorrect("What would you like to change maxRoomTwo to? : ");
					selectedProject.setThreeRoomUnits(newMax2);
					break;
				case 6:
					int newMaxprice2 = superScanner.nextIntUntilCorrect("What would you like to change priceRoomTwo to? : ");
					selectedProject.setThreeRoomPrice(newMaxprice2);
					break;
				case 7:
					System.out.println("What would you like to change openingDate to?");
					LocalDate newOpening = superScanner.nextDateUntilCorrect("Enter Opening Date (d/m/yy): ");
					selectedProject.setOpeningDate(newOpening);
					break;
				case 8:
					System.out.println("What would you like to change closingDate to?");
					LocalDate newClosing = superScanner.nextDateUntilCorrect("Enter Closing Date (d/m/yy): ");
					selectedProject.setClosingDate(newClosing);
					break;
				case 9:
					int newOfficerSlots = superScanner.nextIntUntilCorrect("What would you like to change officerSlots to? : ");
					selectedProject.setTotalOfficerSlots(newOfficerSlots);
					break;
				case 10:
					System.out.println("Would you like to toggle visibility? (Y/N) : ");
					String visibility = scanner.nextLine();
					if(visibility.equalsIgnoreCase("Y")) {
						selectedProject.toggleVisibility();
						System.out.println("Visibility has been toggled");
					}
					else if(visibility.equalsIgnoreCase("N")) {
						System.out.println("Visibility remains unchanged");
					}
					else {
						System.out.println("Invalid input, visibility remains unchanged");
					}
					break;
				default:
					// technically this wont even hit due to nextint min max.
					System.out.println("Invalid option. Please try again.");
					break;
			}
		}
		catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
			System.out.println("Please try again.");
			return;
		}

		system.saveChanges(CSVFileTypes.PROJECT_LIST);
	}
	
	
	/**
	 * Delete a project from the system.
	 * @param manager The manager deleting the project.
	 */
	private static void deleteProject(HDBManager manager) {
		Scanner scanner = system.getScanner();
		SuperScanner superScanner = new SuperScanner(scanner);
		
		ArrayList<BTOProject> projects = system.getApplicableProjects();

		if(projects == null || projects.isEmpty()) {
            System.out.println("You don't have any projects to in charge.");
            return;
        }

		projects = system.filterProjects(projects);
		if (projects.isEmpty()) {
			System.out.println("There is currently no BTO projects suitable for your filter citeria.");
			return;
		}

		List<BTOProject.TableColumnOption> options = new ArrayList<>();
		options.add(TableColumnOption.INDEX_NUMBER);
		options.add(TableColumnOption.ROOM_ONE_UNITS);
		options.add(TableColumnOption.ROOM_TWO_UNITS);
		options.add(TableColumnOption.OPENING_DATE);
		options.add(TableColumnOption.CLOSING_DATE);

		BTOProject.display(projects, options);
		
		int choose = superScanner.nextIntUntilCorrect("Which Project would you like to delete? (enter the corresponding number): ", 1, projects.size());
		BTOProject selectedProject = projects.get(choose-1);

		System.out.println("Would you delete project " + selectedProject.getProjectName() + "? (Y/N) : ");
		String visibility = scanner.nextLine();
		if(visibility.equalsIgnoreCase("Y")) {
			try {
				system.deleteProject(selectedProject);
			}
			catch (Exception e) {
				System.out.println("Error: " + e.getMessage());
				System.out.println("Please try again.");
				return;
			}

			System.out.println("Project " + selectedProject.getProjectName() + " has been deleted");
		}
		else if(visibility.equalsIgnoreCase("N")) {
			System.out.println("Project remains undeleted");
		}
		else {
			System.out.println("Invalid input, project remains undeleted");
		}	
	}
	
/* 	private static void toggleVisibility(HDBManager manager) {
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
		
	} */
	
	/**
	 * View all projects in the system.
	 * @param manager The manager viewing the projects.
	 */
	private static void viewAllProjects(HDBManager manager) {
		ArrayList<BTOProject> projects = system.getProjects();

		if(projects == null || projects.isEmpty()) {
            System.out.println("No projects to display.");
            return;
        }

		projects = system.filterProjects(projects);
		if (projects.isEmpty()) {
			System.out.println("There is currently no BTO projects suitable for your filter citeria.");
			return;
		}

		List<BTOProject.TableColumnOption> options = new ArrayList<>();
		options.add(TableColumnOption.INDEX_NUMBER);
		options.add(TableColumnOption.ROOM_ONE_UNITS);
		options.add(TableColumnOption.ROOM_ONE_PRICE);
		options.add(TableColumnOption.ROOM_TWO_UNITS);
		options.add(TableColumnOption.ROOM_TWO_PRICE);
		options.add(TableColumnOption.OPENING_DATE);
		options.add(TableColumnOption.CLOSING_DATE);
		options.add(TableColumnOption.OFFICERS);
		options.add(TableColumnOption.OFFICER_SLOTS);
		options.add(TableColumnOption.VISIBILITY);

		BTOProject.display(projects, options);
	}
	
	/**
	 * View all projects created by the manager.
	 * @param manager The manager viewing the projects.
	 */
	private static void viewCreatedProjects(HDBManager manager) {
		ArrayList<BTOProject> projects = system.getApplicableProjects();
		if(projects == null || projects.isEmpty()) {
			System.out.println("No projects to display.");
			return;
		}

		projects = system.filterProjects(projects);
		if (projects.isEmpty()) {
			System.out.println("There is currently no BTO projects suitable for your filter citeria.");
			return;
		}

		List<BTOProject.TableColumnOption> options = new ArrayList<>();
		options.add(TableColumnOption.INDEX_NUMBER);
		options.add(TableColumnOption.ROOM_ONE_UNITS);
		options.add(TableColumnOption.ROOM_ONE_PRICE);
		options.add(TableColumnOption.ROOM_TWO_UNITS);
		options.add(TableColumnOption.ROOM_TWO_PRICE);
		options.add(TableColumnOption.OPENING_DATE);
		options.add(TableColumnOption.CLOSING_DATE);
		options.add(TableColumnOption.OFFICERS);
		options.add(TableColumnOption.OFFICER_SLOTS);
		options.add(TableColumnOption.VISIBILITY);

		BTOProject.display(projects, options);
	}


// 	private static void filterProjects(HDBManager manager) {
// 		Scanner scanner = system.getScanner();
// 		System.out.println("Filter Project Listings: ");
		
// 		System.out.println("Enter Project Name (or part of it): ");
// 		String projectName = scanner.nextLine().trim();
		
// 		System.out.print("Enter Neighborhood (or part of it): ");
// 	    String neighborhood = scanner.nextLine().trim();
	    
// 	    System.out.print("Enter Manager Name: ");
// 	    String Manager = scanner.nextLine().trim();
	    
// 	    System.out.println("Enter number of Two-Room flats: ");
// 	    int num1 = scanner.nextInt();
	    
// 	    //to do
// //	    System.out.println("Enter Two-Room flat price: ");
// //	    int price1 = scanner.nextInt();
	   
// 	    System.out.println("Enter number of Three-Room flats: ");
// 	    int num2 = scanner.nextInt();
	    
// 	 // to do
// //	    System.out.println("Enter Three-Room flat price: ");
// //	    int price2 = scanner.nextInt();
	    
// 	    ArrayList<BTOProject> projects = system.getProjects();
// 	    if(projects == null) {
// 	    	System.out.println("Error, no ongoing projects");
// 	    	return;
// 	    }
	   
// 	    ArrayList<BTOProject> filteredProjects = projects.stream()
// 	    		.filter(p-> projectName.isEmpty() || p.getProjectName().toLowerCase().contains(projectName.toLowerCase()))
// 	    		.filter(p-> neighborhood.isEmpty() || p.getNeighborhood().toLowerCase().contains(neighborhood.toLowerCase()))
// //	    		.filter(p -> Manager.isEmpty() || p.getManagerInCharge().toLowerCase().contains(Manager.toLowerCase())) // TODO: @jiahao, getManagerInCharge() (HDBManager object) is not a string! 
// //	    		.filter(p -> num1.isEmpty() || p.getMaxTwoRoomUnits.equals(num1))
// //	    		.filter (p-> price1.isEmpty() || p.getTwoRoomPrice.equals(price1))
// //	    		.filter(p -> num2.isEmpty() || p.getMaxThreeRoomUnits.equals(num2))
// //	    		.filter (p-> price2.isEmpty() || p.getThreeRoomPrice.equals(price2))
// 	    		.collect(Collectors.toCollection(ArrayList::new));
// 	    System.out.println("Filtered Projects: ");
// 	    if(filteredProjects.isEmpty()) {
// 	    	System.out.println("No Projects match the given criteria");
// 	    	}
// 	    else {
// 	    	for (int i = 0; i < filteredProjects.size(); i++) {
// 	            System.out.println((i + 1) + ". " + filteredProjects.get(i));
// 	        }
// 	    }
	    		
// 	}
	
	private static void viewPendingOfficerRequests(HDBManager manager) throws Exception {
		Scanner scanner = system.getScanner();
		SuperScanner superScanner = new SuperScanner(scanner);
		
		List<BTOProject> projects = system.getApplicableProjects().stream()
				.filter(p -> p.getPendingOfficers().size() > 0)
				.collect(Collectors.toCollection(ArrayList::new));
		
		if (projects.isEmpty()) {
			new Style.Builder()
			.text("There are no project join requests applicable for your approval right now.")
			.newLine()
			.code(Code.TEXT_YELLOW)
			.print();
			return;
		}
		
		List<TableColumnOption> options = new ArrayList<>();
		options.add(TableColumnOption.INDEX_NUMBER);
		options.add(TableColumnOption.OFFICER_SLOTS);
		options.add(TableColumnOption.OFFICERS);
		options.add(TableColumnOption.PENDING_OFFICERS);
		BTOProject.display(projects, options);
		
		int option = superScanner.nextIntUntilCorrect("Which project's pending officers would you like to review? (0 to exit): ", 0, projects.size());
		
		if (option == 0) return;
		
		BTOProject project = projects.get(option - 1);
		
		List<HDBOfficer> settledPendingOfficers = new ArrayList<>();
		
		for (HDBOfficer officer : project.getPendingOfficers()) {
			new DisplayMenu.Builder()
			.setTitle("Officer Request")
			.addContent(new Style.Builder().text("Information").bold().toString())
			.addContent("Name: " + officer.getName())
			.addContent("Age: " + officer.getAge())
			.addContent("NRIC: " + officer.getNric())
			.addDivider()
			.addContent("Remaining Slots: " + (project.getTotalOfficerSlots() - project.getOfficers().size()))
			.build()
			.display();
			
			String decisionPrompt = String.format("Would like to approve this officer's request to join project <%s>? (Y/N): ", project.getProjectName());
			boolean decision = superScanner.nextBoolUntilCorrect(decisionPrompt);
			
			if (decision) {
				project.addOfficer(officer);
				settledPendingOfficers.add(officer);
			}
			else {
				settledPendingOfficers.add(officer);
			}
		}
		
		for (HDBOfficer officer : settledPendingOfficers) {
			project.removeOfficerFromPendingList(officer);
		}
		
		system.saveChanges(CSVFileTypes.PROJECT_LIST);
	}
	
	private static void viewAllEnquiries(HDBManager manager) throws Exception {
		EnquiriesSystem eSystem = manager.getEnquiriesSystem();
		eSystem.displayEnquiriesMenu();
		
		if (eSystem.isEmpty()) return;
		
		Scanner scanner = system.getScanner();
		SuperScanner superScanner = new SuperScanner(scanner);
		
		new DisplayMenu.Builder()
				.addContent("Which enquiry would you like to view more details or reply?")
				.addContent("Choose based on the number option shown above.")
				.addDivider()
				.addContent("To go back, type \"back\"")
				.build()
				.display();
		
		boolean shouldExit = false;
		int userOption = -1;
		
		while (true) {
			System.out.print("Choose option: ");
			String userInput = scanner.nextLine();
			
			if (userInput.equalsIgnoreCase("back")) {
				shouldExit = true;
				break;
			}
			
			try {
				userOption = Integer.parseInt(userInput);
				if (userOption > eSystem.size() || userOption <= 0) throw new IllegalArgumentException();
				break;
			}
			catch (Exception e) {
				Utilities.getInstance().printYellow("Invalid option.");
				continue;
			}
			
		}
		
		if (shouldExit) return;
		
		Enquiry enquiry = eSystem.getEnquiries().get(userOption - 1);
		int width = 100;
		
		String userRow = enquiry.getQuestion().getUser().getName() + " asks:";
		ArrayList<String> rows = new ArrayList<>();

		String questionDate = Utilities.getInstance().formatUserReadableDateTime(enquiry.getQuestion().getTimestamp());
		rows.add(String.format("\u001B[1;30;46m %s \u001B[0m", questionDate));
		rows.add(String.format("\u001B[1m%-" + (width - userRow.length()) + "s\u001B[0m", userRow));
		rows.add(enquiry.getQuestion().getContent());
		
		if (enquiry.getResponse() != null) {
			rows.add("");
			Message response = enquiry.getResponse();
			
			String responseDate = Utilities.getInstance().formatUserReadableDateTime(response.getTimestamp());
			rows.add(String.format("\u001B[1;30;46m %s \u001B[0m", responseDate));
			
			String responseInfo = String.format("%s (%s) replied:", response.getUser().getName(), response.getUser().getReadableTypeName());
			rows.add(String.format("\u001B[1m%-" + (width - responseInfo.length()) + "s\u001B[0m", responseInfo));
			rows.add(String.format("%-" + (width - response.getContent().length()) + "s", response.getContent()));
		}
		
		new DisplayMenu.Builder()
			.addContents(rows)
			.build()
			.display();
		
		if (!enquiry.hasResponded()) {
			Boolean wantsReply = superScanner.nextBoolUntilCorrect("Would you like to reply? (Y/N): ");
			
			if (wantsReply) {
				System.out.print("Your reply: ");
				String reply = scanner.nextLine();
				enquiry.setResponse(new Message(manager, reply));
				system.saveChanges(CSVFileTypes.ENQUIRIES_LIST);
				
				System.out.println("Your response has been sent.");
			}
		}
	}
}
