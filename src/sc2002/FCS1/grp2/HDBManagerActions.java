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
		case CREATE_PROJECT:
			createProject(user);
			break;
		case EDIT_PROJECT:
			editProject(user);
			break;
		case DELETE_PROJECT:
			deleteProject(user);
			break;
		case VIEW_ALL_PROJECTS:
			viewAllProjects(user);
			break;
		case APPROVE_OR_REJECT_APPLICATIONS:
			approveRejectApplication(user);
			break;
		case FILTER_PROJECT:
			filterProjects(user);
			break;
		case VIEW_PENDING_OFFICER_REQUESTS:
			viewPendingOfficerRequests(user);
			break;
		case VIEW_ALL_ENQUIRIES:
			viewAllEnquiries(user);
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
		
		
		ArrayList<BTOProject> projects = system.getApplicableProjects();

		if(projects == null || projects.isEmpty()) {
            System.out.println("No projects to display.");
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
	

/*	private static void generateReport(HDBManager manager) {
		//filter based on category
	}*/
	
	private static void approveRejectApplication(HDBManager manager) {
		Scanner scanner = system.getScanner();
		SuperScanner superScanner = new SuperScanner(scanner);
		
		ArrayList<Application> applications = system.getApplications();
		
		if(applications.isEmpty()) {
			System.out.println("No Application needed for approval");
			return;
		}
		 
		for (int i = 0; i < applications.size(); i++) {
	            System.out.println((i + 1) + ". " + applications.get(i));
		}
		int choice = superScanner.nextIntUntilCorrect("Which Application would you like to approve or reject?");
		
		Application selectedApplication = applications.get(choice - 1);
		boolean choose = superScanner.nextBoolUntilCorrect("Would you like to approve or reject this application? (input Y if approve, N if reject");
		
		if (choose == true) {
			selectedApplication.setStatus(ApplicationStatus.SUCCESSFUL);
		}
		else {
			selectedApplication.setStatus(ApplicationStatus.UNSUCCESSFUL);
		}
	}
	
	//to do
		
		private static void approveRejectWithdrawalApplication(HDBManager manager) {
		Scanner scanner = system.getScanner();
		SuperScanner superScanner = new SuperScanner(scanner);
		
		ArrayList<application> withdrawalApplications = system.getwithdrawalApplications();
		
		for (int i = 0; i < withdrawalApplications.size(); i++) {
            System.out.println((i + 1) + ". " + withdrawalApplications.get(i));
		}
		int choice = superScanner.nextIntUntilCorrect("Which Withdrawal Application would you like to approve or reject?");
		
		
		
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
//	    		.filter(p -> Manager.isEmpty() || p.getManagerInCharge().toLowerCase().contains(Manager.toLowerCase())) // TODO: @jiahao, getManagerInCharge() (HDBManager object) is not a string! 
//	    		.filter(p -> num1.isEmpty() || p.getMaxTwoRoomUnits.equals(num1))
//	    		.filter (p-> price1.isEmpty() || p.getTwoRoomPrice.equals(price1))
//	    		.filter(p -> num2.isEmpty() || p.getMaxThreeRoomUnits.equals(num2))
//	    		.filter (p-> price2.isEmpty() || p.getThreeRoomPrice.equals(price2))
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
