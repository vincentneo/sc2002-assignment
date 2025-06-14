package sc2002.FCS1.grp2;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import sc2002.FCS1.grp2.BTOProject.TableColumnOption;
import sc2002.FCS1.grp2.builders.DisplayMenu;
import sc2002.FCS1.grp2.builders.Style;
import sc2002.FCS1.grp2.builders.Style.Code;
import sc2002.FCS1.grp2.helpers.SuperScanner;

/**
 * User interactable actions for when logged in user is of HDB Officer role
 */
public class HDBOfficerActions implements IUserActions {
	private static BTOManagementSystem system = BTOManagementSystem.common();

	/** Handles user input selection from {@code BTOManagementApplication} */
	public static void handleAction(HDBOfficer.Menu option, HDBOfficer user) throws Exception {
        switch (option) {
            case VIEW_PROJECTS:
            	viewProjects(user);
                break;
            case JOIN_PROJECT:
                joinProject(user); 
                break;
            case ENTER_ENQUIRIES_SYSTEM:
            	enquiriesSystemFlow(user);
            	break;
            case CHECK_PENDING_PROJECT:
            	checkProjectApplicationStatus(user);
            	break;
			case BOOK_APPLICATION:
				bookApplication(user);
				break;
			case VIEW_MY_APPLICATION:
				ApplicantActions.viewApplication(user);
				break;
			case WITHDRAW_APPLICATION:
				ApplicantActions.withdrawalApplicationFlow(user);
				break;
            default:
                System.out.println("Option not implemented.");
        }
    }

	/**
	 * Method to help applicants to book an application for a flat 
	 * @param officer HDB Officer
	 * @throws Exception if any error occurs during the booking process
	 */
	private static void bookApplication(HDBOfficer officer) throws Exception {
		Scanner scanner = system.getScanner();
		SuperScanner superScanner = new SuperScanner(scanner);
		ArrayList<Application> applications = system.getSuccessfulApplications();
		int applicationsCount = applications.size();

		if (applications.isEmpty()) {
			System.out.println("There are currently no applications available for booking.");
			return;
		}

		var table = new DisplayMenu.Builder();
		table.addContent(String.format("%-5s │ %-15s │ %-15s │ %-10s", "No.", "Applicant", "Project", "Size")).addDivider();


		for (int i = 0; i < applicationsCount; i++) {
			var application = applications.get(i);
			var index = (i + 1) + ".";

			var applicantName = application.getApplicant().getName();
			var projectName = application.getProject().getProjectName();
			var size = application.getFlatType();
			table.addContent(String.format("%-5s │ %-15s │ %-15s │ %-10s", index, applicantName, projectName, size));
		}

		table.build().display();

		int option = superScanner.nextIntUntilCorrect("Choose application for booking (0 to go back): ", 0, applicationsCount);

		if (option == 0) return;

		var application = applications.get(option - 1);
		var project = application.getProject();

		var bookingResult = project.bookFlat(application.getFlatType());

		if (!bookingResult) {
			new Style.Builder()
			.text("Not enough available units to book a flat.")
			.code(Code.TEXT_RED)
			.newLine()
			.print();
			return;
		}

		application.setStatus(ApplicationStatus.BOOKED);
		system.saveChanges(CSVFileTypes.APPLICATIONS_LIST);
		system.saveChanges(CSVFileTypes.PROJECT_LIST);

		new Style.Builder()
			.text("Flat successfully booked for applicant.")
			.code(Code.TEXT_GREEN)
			.newLine()
			.print();
	}
	
	/**
	 * Method to handle the enquiries system flow for HDB Officer.
	 * This method allows the officer to view the enquiries list, send an enquiry as an applicant, or go back to the main menu.
	 * @param officer HDB Officer
	 * @throws Exception if any error occurs during the enquiries system flow
	 */
	private static void enquiriesSystemFlow(HDBOfficer officer) throws Exception {
		Scanner scanner = system.getScanner();
		SuperScanner sscanner = new SuperScanner(scanner);
		EnquiriesSystem enquiriesSystem = officer.getEnquiriesSystem();

		new DisplayMenu.Builder()
			.setTitle("Options")
			.addContent("1. View Enquiries List")
			.addContent("2. Send Enquiry as Applicant")
			.addContent("3. Back")
			.build()
			.display();

		int option = sscanner.nextIntUntilCorrect("Choose option: ", 1, 3);

		if (option == 3) return;

		if (option == 2) {
			ApplicantActions.sendEnquiryFlow(officer);
			return;
		}

		enquiriesSystem.displayEnquiriesMenu();
		List<Enquiry> enquiries = enquiriesSystem.getEnquiries();

		if (enquiries.isEmpty()) return;

		int selectionOption = sscanner.nextIntUntilCorrect("Choose enquiry (0 to exit): ", 0, enquiries.size());

		if (selectionOption == 0) return;

		Enquiry selectedEnquiry = enquiries.get(selectionOption - 1);
		selectedEnquiry.display();
		
		if (selectedEnquiry.getQuestion().getUser() == officer) {
			new DisplayMenu.Builder()
				.setTitle("Applicant Actions")
				.addContent("1. Edit My Question")
				.addContent("2. Delete")
				.addContent("3. Back")
				.build()
				.display();

			int applicantOption = sscanner.nextIntUntilCorrect("Choose action: ", 1, 3);

			if (applicantOption == 3) return;
			
			if (applicantOption == 2) {
				ApplicantActions.removeEnquiry(selectedEnquiry, enquiriesSystem);
			}

			if (applicantOption == 1) {
				ApplicantActions.editEnquiry(sscanner, enquiriesSystem, selectedEnquiry);
			}
			
		}
		else if (!selectedEnquiry.hasResponded()) {
			Boolean wantsReply = sscanner.nextBoolUntilCorrect("Would you like to reply? (Y/N): ");
			
			if (wantsReply) {
				System.out.print("Your reply: ");
				String reply = scanner.nextLine();
				selectedEnquiry.setResponse(new Message(officer, reply));
				system.saveChanges(CSVFileTypes.ENQUIRIES_LIST);
				
				System.out.println("Your response has been sent.");
			}
		}
	}
	
	/**
	 * Method to check the project application status for HDB Officer as an officer.
	 * This method displays the approved and pending projects for the officer.
	 * @param officer HDB Officer
	 * @throws Exception if any error occurs during the project application status check
	 */
	private static void checkProjectApplicationStatus(HDBOfficer officer) throws Exception {
		List<TableColumnOption> displayOptions = new ArrayList<>();
		displayOptions.add(TableColumnOption.MANAGER);
		displayOptions.add(TableColumnOption.OPENING_DATE);
		displayOptions.add(TableColumnOption.CLOSING_DATE);
		displayOptions.add(TableColumnOption.ROOM_ONE_UNITS);
		displayOptions.add(TableColumnOption.ROOM_ONE_PRICE);
		displayOptions.add(TableColumnOption.ROOM_TWO_UNITS);
		displayOptions.add(TableColumnOption.ROOM_TWO_PRICE);
		displayOptions.add(TableColumnOption.OFFICER_SLOTS);
		displayOptions.add(TableColumnOption.OFFICERS);
		displayOptions.add(TableColumnOption.VISIBILITY);
		
		ArrayList<BTOProject> projects = system.getProjects();
		
		new Style.Builder()
			.text("Approved Projects:")
			.bold().newLine()
			.print();
		
		ArrayList<BTOProject> approved = projects.stream()
				.filter(p -> p.getOfficers().contains(officer))
				.collect(Collectors.toCollection(ArrayList::new));
		
		if (approved.isEmpty()) {
			System.out.println("You have no approved projects right now.");
		}
		else {
			BTOProject.display(approved, displayOptions);
		}
		

		
		new Style.Builder()
		.text("Pending Projects:")
		.bold().newLine()
		.print();
		
		ArrayList<BTOProject> pending = projects.stream()
				.filter(p -> p.getPendingOfficers().contains(officer))
				.collect(Collectors.toCollection(ArrayList::new));
		
		if (pending.isEmpty()) {
			System.out.println("You have no projects that require manager approval right now.");
		}
		else {
			BTOProject.display(pending, displayOptions);
		}
		
		System.out.println("");
	}
    
	/**
	 * Method to view available projects for HDB Officer as an applicant to join
	 * @param officer HDB Officer
	 * @throws Exception if any error occurs during the project viewing process
	 */
    private static void viewProjects(HDBOfficer officer) throws Exception {
    	var sscanner = new SuperScanner(system.getScanner());
    	
		List<BTOProject.TableColumnOption> options = new ArrayList<>();
		options.add(TableColumnOption.MANAGER);
		options.add(TableColumnOption.OPENING_DATE);
		options.add(TableColumnOption.CLOSING_DATE);
		
		ArrayList<BTOProject> projects = system.getApplicableProjectsAsApplicant();
		
		if (!officer.canApplyProject()) {
			new Style.Builder()
				.text("It appears that you are currently not elligible to apply for a BTO.")
				.add256Colour(256, false)
				.newLine()
				.print();
			return;
		}
		
		if (projects.isEmpty()) {
			System.out.println("There are no projects available for you right now.");
			return;
		}
		
		projects = system.filterProjects(projects);
		
		if (projects.isEmpty()) {
			System.out.println("There is currently no BTO projects suitable for your filter citeria.");
			return;
		}

		BTOProject.display(projects, options);


		
		new DisplayMenu.Builder()
			.setTitle("Options")
			.addContent("1. Apply for BTO")
			.addContent("2. Back to Main Menu")
			.build()
			.display();
		
		int option = sscanner.nextIntUntilCorrect("Choose an option: ", 1, 2);
		
		if (option == 1) {
			ApplicantActions.applyBTOFlow(officer, projects, sscanner);
		}
    }
    /**
	 * Method which checks pre-req before letting HDB Officer submit their name for approval for a project.
	 * @param user HDB Officer
	 */ 
    public static void joinProject(HDBOfficer user) throws Exception {
		// Ensure system is properly initialized
		if (system == null) {
			System.out.println("Error: BTOManagementSystem is not initialized.");
			return;
		}
	
		// Display available projects
		System.out.println("Available Projects to Join:");
		ArrayList<BTOProject> projects = system.getProjects();
		ArrayList<String> headers = new ArrayList<>();
		ArrayList<String> projectDetails = new ArrayList<>();

		// Adding the header for the table
		String header = String.format("%-5s │ %-30s │ %-30s", "No.", "Project Name", "Neighborhood");
		headers.add(header);

		// Add the project rows to the table
		for (int i = 0; i < projects.size(); i++) {
			BTOProject project = projects.get(i);
			String row = String.format("%-5d │ %-30s │ %-30s", i + 1, project.getProjectName(), project.getNeighborhood());
			projectDetails.add(row);
		}

		// Build and display the table using DisplayMenu
		new DisplayMenu.Builder()
			.setTitle("Available Projects")
			.addContents(headers)
			.addDivider()
			.addContents(projectDetails)
			.build()
			.display();
	
		// Officer selects a project
		Scanner scanner = system.getScanner();
		System.out.print("Select a project to join (enter number): ");
		
		String response = scanner.nextLine();
		
		// Check if the response is not empty and is a valid integer
		if (response.trim().isEmpty()) {
			System.out.println("Invalid input. Please enter a valid project number.");
			return;
		}
	
		int projectIndex = -1;
		try {
			projectIndex = Integer.parseInt(response);
		} catch (NumberFormatException e) {
			System.out.println("Invalid input. Please enter a valid project number.");
			return;
		}
	
		if (projectIndex < 1 || projectIndex > projects.size()) {
			System.out.println("Invalid selection. Please try again.");
			return;
		}
	
		BTOProject selectedProject = projects.get(projectIndex - 1);
	
		// Check if the officer is already registered for this project
		if (user.isOfficerForProject(selectedProject)) {
			System.out.printf("You are already an officer for the project %s.\n", selectedProject.getProjectName());
			return;
		}
	
		// Check if the officer has already applied as an applicant
		var applications = system.getApplications();

		for (var app : applications) {
			if (app.getProject() == selectedProject) {
				System.out.println("You cannot join the project %s as an officer because you have already applied for it as an applicant.");
				break;
			}
		}
	
		// Check if the project has room for more officers
		if (selectedProject.getOfficers().size() >= selectedProject.getTotalOfficerSlots()) {
			System.out.println("The project already has the maximum number of officers assigned.");
			return;
		}
	
		// Ensure the officer is not applying for a project during the application period of a project they are already handling
		for (BTOProject currentProject : system.getApplicableProjects()) {
			// Skip the check if this is the selected project itself
			if (currentProject.equals(selectedProject)) continue;
	
			// Get the officer's assigned projects' start and end dates
			LocalDate currentProjectOpenDate = currentProject.getOpeningDate();
			LocalDate currentProjectCloseDate = currentProject.getClosingDate();
			LocalDate selectedProjectOpenDate = selectedProject.getOpeningDate();
			LocalDate selectedProjectCloseDate = selectedProject.getClosingDate();
	
			if (datesOverlap(currentProjectOpenDate, currentProjectCloseDate, selectedProjectOpenDate, selectedProjectCloseDate)) {
				System.out.println("You cannot apply for this project as its application period overlaps with another project you are handling.");
				return;
			}
		}
	
		// Create an enquiry for manager's approval
		HDBManager manager = selectedProject.getManagerInCharge();
		if (manager == null) {
			System.out.println("You cannot be registered as an officer for this project as the logged-in user is not the manager of the project.");
			return;
		}
		
		try {
			selectedProject.addCurrentUserToPendingList();
		}
		catch (Exception e) {
			new Style.Builder()
			.text(e.getMessage())
			.code(Code.TEXT_RED)
			.bold()
			.print();
		}
		
		system.saveChanges(CSVFileTypes.PROJECT_LIST);
	
		System.out.println("Your application status is now PENDING. Please wait for manager's response...");
	}
    
      
    
	/**
	 * helper function to check with date overlaps
	 * @param start1 start date of project officer is handling
	 * @param end1 end date of project officer is handling
	 * @param start2 start date of project officer is applying for
	 * @param end2 end date of project officer is applying for
	 * @return true if the two projects overlap, false otherwise
	 */
	private static boolean datesOverlap(LocalDate start1, LocalDate end1, LocalDate start2, LocalDate end2) {
		// Check if project 1's dates overlap with project 2's dates
		return (start1.isBefore(end2) && end1.isAfter(start2));
	}
	
}
