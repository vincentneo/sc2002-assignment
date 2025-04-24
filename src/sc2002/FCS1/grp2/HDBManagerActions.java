package sc2002.FCS1.grp2;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import sc2002.FCS1.grp2.BTOProject.TableColumnOption;
import sc2002.FCS1.grp2.builders.Bar;
import sc2002.FCS1.grp2.builders.DisplayMenu;
import sc2002.FCS1.grp2.builders.Style;
import sc2002.FCS1.grp2.builders.Bar.Type;
import sc2002.FCS1.grp2.builders.Style.Code;
import sc2002.FCS1.grp2.helpers.SuperScanner;

/**
 * User interactable actions for when logged in user is of HDB Manager role
 */
public class HDBManagerActions {
	private static BTOManagementSystem system = BTOManagementSystem.common();
	
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
		case APPROVE_OR_REJECT_APPLICATIONS:
			approveRejectApplication(user);
			break;
		case APPROVE_OR_REJECT_WITHDRAWAL_APPLICATIONS:
			approveRejectWithdrawalApplication(user);
			break;
		case VIEW_PENDING_OFFICER_REQUESTS:
			viewPendingOfficerRequests(user);
			break;
		case ENQUIRIES_SYSTEM:
			enterEnquiriesFlow(user);
			break;
		case REPORTS:
			reportsFlow(user);
			break;
		}
	}
	
	private static void reportsFlow(HDBManager manager) throws Exception {
		SuperScanner superScanner = new SuperScanner(system.getScanner());
		ArrayList<Application> applications = system.getApplications();

		applications = system.filterApplications(applications);
		if (applications.isEmpty()) {
			new Style.Builder()
				.text("There are no applications to display with your filter.")
				.newLine()
				.code(Code.TEXT_YELLOW)
				.print();
			return;
		}

		List<String> menuOptions = new ArrayList<>();

		for (int i = 0; i < ReportType.all.length; i++) {
			menuOptions.add(String.format("%d. By %s", i+1, ReportType.fromOrdinal(i)));
		}

		new DisplayMenu.Builder()
			.setTitle("Report Types")
			.addContents(menuOptions)
			.build()
			.display();

		int option = superScanner.nextIntUntilCorrect("Select option (0 to exit): ", 0, menuOptions.size());

		if (option == 0) return;

		ReportType selectedReportType = ReportType.fromOrdinal(option - 1);

		HashMap<String, Integer> barMap = new HashMap<>();
		HashMap<String, List<Application>> appsMap = new HashMap<>();

		for (Application application : applications) {
			String key;

			switch (selectedReportType) {
				case AGE:
					key = "" + application.getApplicant().getAge();
					break;
				case FLAT_TYPE:
					key = application.getFlatType().toString();
					break;
				case MARITAL_STATUS:
					key = application.getApplicant().getMaritalStatus().toString();
					break;
				case PROJECT:
					key = application.getProject().getProjectName();
					break;
				default:
					continue;
			}

			Integer currentValue = barMap.get(key);
			List<Application> currentApplications = appsMap.get(key);

			if (currentValue == null) {
				currentValue = 0;
				currentApplications = new ArrayList<>();
			}

			currentApplications.add(application);

			barMap.put(key, currentValue + 1);
			appsMap.put(key, currentApplications);
		}

		var builder = new Bar.Builder(30, applications.size());

		boolean dotted = true;

		List<String> keys = new ArrayList<>(barMap.keySet());
		Collections.sort(keys);

		for (var key : keys) {
			int value = barMap.get(key);

			builder.add(dotted ? Type.DOTTED : Type.SOLID, value, key, true);
			dotted = !dotted;
		}

		new Style.Builder()
		.text("Applications Received: ")
		.text(applications.size() + "")
		.bold()
		.newLine()
		.print();

		builder.print();
		System.out.print("\n");

		for (var key : keys) {
			List<Application> sectionApplications = appsMap.get(key);
			var table = new DisplayMenu.Builder();

			new Style.Builder()
				.text(selectedReportType.toString())
				.italic()
				.text(": ")
				.text(key)
				.bold()
				.italic()
				.newLine()
				.print();

			table.addContent(String.format("%-25s │ %-3s │ %-20s │ %15s │ %25s │ %12s", "Applicant", "Age", "Marital Status", "Flat Type", "Project", "Status")).addDivider();
	
			for (Application application : sectionApplications) {
				table.addContent(String.format("%-25s │ %-3d │ %-20s │ %15s │ %25s │ %12s",
				 application.getApplicant().getName(), 
				 application.getApplicant().getAge(), 
				 application.getApplicant().getMaritalStatus(), 
				 application.getFlatType(), 
				 application.getProject().getProjectName(), 
				 application.getStatus()));
			}

			table.build().display();
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
		
		Scanner scanner = system.getScanner();
		SuperScanner superScanner = new SuperScanner(scanner);
		
		System.out.print("Enter Project Name: ");
		String projectName = scanner.nextLine();
		
		while (system.doesProjectExist(projectName, null)) {
			new Style.Builder().text("The project name ")
			.code(Code.TEXT_RED)
			.text(projectName)
			.code(Code.UNDERLINE)
			.bold()
			.text(" is not acceptable as a project of the same or similar name already exists.\n")
			.code(Code.TEXT_RED)
			.print();
			
			System.out.print("Enter Project Name: ");
			projectName = scanner.nextLine();
		}
		
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
					new Style.Builder()
						.text("What would you like to change project name to? (current: ")
						.text(selectedProject.getProjectName())
						.underline()
						.bold()
						.text("): ")
						.print();
					
					String newName = scanner.nextLine();
					while (system.doesProjectExist(newName, selectedProject)) {
						new Style.Builder().text("The project name ")
							.code(Code.TEXT_RED)
							.text(selectedProject.getProjectName())
							.code(Code.UNDERLINE)
							.bold()
							.text(" is not acceptable as a project of the same or similar name already exists.\n")
							.code(Code.TEXT_RED)
							.print();
						
						System.out.print("Type another name: ");
						newName = scanner.nextLine();
					}
					
					selectedProject.setProjectName(newName);
					System.out.println("Project name have been updated.");
					break;
				
				case 2:
					System.out.println("What would you like to change neighbourhood to?");
					String newNeighbourhood = scanner.nextLine();
					selectedProject.setNeighborhood(newNeighbourhood);
					break;
					
				case 3:
					int newMax = superScanner.nextIntUntilCorrect("What would you like to change available units for 2-Room flats to?: ");
					selectedProject.setTwoRoomUnits(newMax);
					break;
				case 4:
					int newMaxprice = superScanner.nextIntUntilCorrect("What would you like to change price for 2-Room flat to?: $");
					selectedProject.setTwoRoomPrice(newMaxprice);
					break;
				case 5:
					int newMax2 = superScanner.nextIntUntilCorrect("What would you like to available units for 3-Room flats to?: ");
					selectedProject.setThreeRoomUnits(newMax2);
					break;
				case 6:
					int newMaxprice2 = superScanner.nextIntUntilCorrect("What would you like to change price for 3-Room flat to?: $");
					selectedProject.setThreeRoomPrice(newMaxprice2);
					break;
				case 7:
					System.out.println("What would you like to change the opening date to?");
					LocalDate newOpening = superScanner.nextDateUntilCorrect("Enter Opening Date (d/m/yy): ");
					selectedProject.setOpeningDate(newOpening);
					break;
				case 8:
					System.out.println("What would you like to change the closing date to?");
					LocalDate newClosing = superScanner.nextDateUntilCorrect("Enter Closing Date (d/m/yy): ");
					selectedProject.setClosingDate(newClosing);
					break;
				case 9:
					int newOfficerSlots = superScanner.nextIntUntilCorrect("What would you like to change officerSlots to? : ");
					selectedProject.setTotalOfficerSlots(newOfficerSlots);
					break;
				case 10:
					if (selectedProject.getVisibility())
					{
						System.out.println("The project is currently visible");
					}
					else
					{
						System.out.println("The project is currently not visible");
					}
					System.out.println("Would you like to toggle visibility? (Y/N): ");
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
		
		int choose = superScanner.nextIntUntilCorrect("Which project would you like to delete? (enter the corresponding number): ", 1, projects.size());
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
	
	/**
	 * Approve / reject applicant's application
	 * @param manager
	 * @throws Exception
	 */
	private static void approveRejectApplication(HDBManager manager) throws Exception {
		Scanner scanner = system.getScanner();
		SuperScanner superScanner = new SuperScanner(scanner);
		
		ArrayList<Application> applications = system.getApplications();
		
		if(applications.isEmpty()) {
			System.out.println("There are currently no applications that require your approval.");
			return;
		}
		 
		for (int i = 0; i < applications.size(); i++) {
	            System.out.println((i + 1) + ". " + applications.get(i));
		}
		int choice = superScanner.nextIntUntilCorrect("Which Application would you like to approve or reject? (0 to go back): ", 0, applications.size() );
		
		if (choice == 0) return;

		Application selectedApplication = applications.get(choice - 1);
		boolean choose = superScanner.nextBoolUntilCorrect("Would you like to approve or reject this application? (input Y if approve, N if reject): ");
		
		if (choose == true) {
			selectedApplication.setStatus(ApplicationStatus.SUCCESSFUL);
			System.out.println("Application have been approved!");
		}
		else {
			selectedApplication.setStatus(ApplicationStatus.UNSUCCESSFUL);
			System.out.println("Application have been rejected!");
		}

		system.saveChanges(CSVFileTypes.APPLICATIONS_LIST);
	}
	
	/** To approve or reject withdrawal application made by applicant
	 * by default if applicant submit a withdrawal application it is successful, unless otherwise rejected manually by manager.
	 * @param manager
	 * @throws Exception
	 */
	private static void approveRejectWithdrawalApplication(HDBManager manager) throws Exception {
		Scanner scanner = system.getScanner();
		SuperScanner superScanner = new SuperScanner(scanner);
		
		
		ArrayList<Application> withdrawalApplications = system.getWithdrawalApplications();
		
		if(withdrawalApplications.isEmpty()) {
			System.out.println("No Withdrawal Application waiting for approval");
			return;
		}
		
		for (int i = 0; i < withdrawalApplications.size(); i++) {
			System.out.println((i + 1) + ". " + withdrawalApplications.get(i));
	}
		int choice = superScanner.nextIntUntilCorrect("Which Withdrawal Application would you like to approve or reject?", 1, withdrawalApplications.size());
		Application chosenApplication = withdrawalApplications.get(choice-1);
		
		boolean choice2 = superScanner.nextBoolUntilCorrect("Enter Y for approve, N for reject");
		if(choice2 == true) {
			chosenApplication.setWithdrawalStatus(WithdrawalStatus.WITHDRAWN);
			System.out.println("Withdrawal Application has been approved");
			
		}
		else {
			chosenApplication.setWithdrawalStatus(WithdrawalStatus.REJECTED);
			System.out.println("Withdrawal Application has been rejected");
		}
		
		system.saveChanges(CSVFileTypes.APPLICATIONS_LIST);
	}
	
	/**
	 * View all projects in the system.
	 * @param manager The manager viewing the projects.
	 */
	private static void viewAllProjects(HDBManager manager) throws Exception {
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


	
	/**
	 * View pending officer requests for a project and review.
	 * @param manager The manager viewing the pending officer requests.
	 * @throws Exception 
	 */
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
	
	private static void enterEnquiriesFlow(HDBManager manager) throws Exception {
		EnquiriesSystem eSystem = manager.getEnquiriesSystem();
		eSystem.displayEnquiriesMenu();
		
		List<Enquiry> enquiries = eSystem.getRespondableEnquiries();

		if (enquiries.isEmpty()) return;
		
		Scanner scanner = system.getScanner();
		SuperScanner superScanner = new SuperScanner(scanner);
		
		new DisplayMenu.Builder()
				.addContent("Which enquiry would you like to view more details or reply?")
				.addContent("Choose based on the number option shown above.")
				.addDivider()
				.addContent("To go back, type 0")
				.build()
				.display();
		
		int userOption = superScanner.nextIntUntilCorrect("Choose option: ", 0, enquiries.size());
		
		if (userOption == 0) return;
		
		Enquiry enquiry = eSystem.getEnquiries().get(userOption - 1);

		enquiry.display();
		
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
