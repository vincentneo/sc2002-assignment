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

public class HDBOfficerActions {
	private static BTOManagementSystem system = BTOManagementSystem.common();

	public static void handleAction(HDBOfficer.Menu option, HDBOfficer user) throws Exception {
        switch (option) {
            case VIEW_PROJECTS:
                // TODO: filtering, access control etc etc
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
//            case CHECK_APPLICATION_STATUS:
//                displayOfficerApplications(user);
//                break;
            default:
                System.out.println("Option not implemented.");
        }
    }
	
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
	
	private static void checkProjectApplicationStatus(HDBOfficer officer) throws Exception {
		List<TableColumnOption> displayOptions = new ArrayList<>();
		displayOptions.add(TableColumnOption.MANAGER);
		displayOptions.add(TableColumnOption.OPENING_DATE);
		displayOptions.add(TableColumnOption.CLOSING_DATE);
		
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
//		if (user.getAppliedProject() != null && user.getAppliedProject().equals(selectedProject)) {
//			System.out.printf("You cannot join the project %s as an officer because you have already applied for it as an applicant.\n", selectedProject.getProjectName());
//			return;
//		}
	
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
	
			// Check if the dates overlap: If selected project overlaps with any other project the officer is handling
			// TODO: not working properly
//			if ((selectedProjectOpenDate.isBefore(currentProjectCloseDate) && selectedProjectCloseDate.isAfter(currentProjectOpenDate))) {
//				System.out.println("You cannot apply for this project as its application period overlaps with another project you are handling.");
//				return;
//			}
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
	
//		// Create an enquiry asking the manager for approval
//		Message question = new Message(user, "Requesting approval to join as officer for project " + selectedProject.getProjectName());
//		Enquiry enquiry = new Enquiry(question, selectedProject);
//	
//		// Add the enquiry to the system
//		try {
//			system.getActiveUser().getEnquiriesSystem().addEnquiry(enquiry);
//		} catch (Exception e) {
//			System.out.println("Error adding enquiry: " + e.getMessage());
//			return;
//		}
//		
//		// Store the enquiry in the officer's map for later reference
//		user.getApplicationEnquiries().put(selectedProject, enquiry);
//	
//		// Mark the officer's status as PENDING for this project
//		user.setApplicationStatus(ApplicationStatus.PENDING);
//	
//		System.out.println("Enquiry sent to manager for approval.");
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
	
	
	
//
//    /**
//     * Update the officer's application status based on the manager's reply.
//     * For each project in the officer's application enquiries, if a reply exists:
//     * - "approve": adds officer to the project's officers list.
//     * - "reject": just continues to the next project.
//     * If no reply is found for an application, nothing happens.
//     */
//    public static void updateApplicationStatus(HDBOfficer officer) {
//		// Iterate through each project enquiry
//		for (Map.Entry<BTOProject, Enquiry> entry : officer.getApplicationEnquiries().entrySet()) {
//			BTOProject project = entry.getKey();
//			Enquiry enquiry = entry.getValue();
//	
//			// Check if there is a response from the manager
//			if (enquiry.getResponse() != null) {
//				String reply = enquiry.getResponse().getContent().trim();
//	
//				if (reply.equalsIgnoreCase("approve")) {
//					// If the manager approves, add the officer to the project's officers list
//					if (!project.getOfficers().contains(officer)) {
//						project.addOfficer(officer);
//						system.saveChanges(CSVFileTypes.PROJECT_LIST);
//					}
//				} else if (reply.equalsIgnoreCase("reject")) {
//					continue;
//				}
//			} else {
//				continue;
//			}
//		}
//	}
//	
//    
//    /**
//     * Display the officer's application statuses for projects in a table format.
//     * This table shows: Project Name, Neighborhood, Manager, and Status.
//     * The Manager's reply (if any) is shown in the last column.
//     */
//	public static void displayOfficerApplications(HDBOfficer officer) {
//		// Retrieve officer's applied projects and associated enquiries.
//		Map<BTOProject, Enquiry> appMap = officer.getApplicationEnquiries();
//		if (appMap.isEmpty()) {
//			System.out.println("No applications found.");
//			return;
//		}
//		
//		List<String> rows = new ArrayList<>();
//		// Header for the table
//		String header = String.format("%-25s │ %-20s │ %-12s │ %-10s │ %-25s", 
//				"Project Name", "Neighborhood", "Manager", "Status", "Manager Reply");
//		rows.add(header);
//		// Divider line
//		rows.add(new String(new char[header.length()]).replace("\0", "-"));
//		
//		// For each project and its enquiry, determine the status individually
//		for (Map.Entry<BTOProject, Enquiry> entry : appMap.entrySet()) {
//			BTOProject project = entry.getKey();
//			Enquiry enquiry = entry.getValue();
//			
//			// Determine status based on the enquiry's response (if any)
//			String statusText = "Pending";  // default
//			if (enquiry.getResponse() != null) {
//				String reply = enquiry.getResponse().getContent().trim();
//				if (reply.equalsIgnoreCase("approve")) {
//					statusText = "Approved";
//				} else if (reply.equalsIgnoreCase("reject")) {
//					statusText = "Denied";
//				} // else leave as Pending
//			}
//			
//			String managerReply = (enquiry.getResponse() != null) ? enquiry.getResponse().getContent() : "";
//			String row = String.format("%-25s │ %-20s │ %-12s │ %-10s │ %-25s", 
//					project.getProjectName(),
//					project.getNeighborhood(),
//					(project.getManagerInCharge() != null ? project.getManagerInCharge().getName() : "N/A"),
//					statusText,
//					managerReply);
//			rows.add(row);
//		}
//		
//		new DisplayMenu.Builder()
//			.addContents(rows)
//			.build()
//			.display();
//	}
}
