package sc2002.FCS1.grp2;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

import sc2002.FCS1.grp2.BTOProject.TableColumnOption;
import sc2002.FCS1.grp2.builders.DisplayMenu;
import sc2002.FCS1.grp2.builders.Style;
import sc2002.FCS1.grp2.builders.Style.Code;
import sc2002.FCS1.grp2.helpers.ReceiptPrinter;
import sc2002.FCS1.grp2.helpers.SuperScanner;

/**
 * Handles all actions specific to users logged in with applicant role.
 */
public class ApplicantActions {
	private static BTOManagementSystem system = BTOManagementSystem.common();
	
	/**
	 * Point of entry, to be handed over from {@code BTOManagementApplication}
	 * @param option Menu option selected by user.
	 * @param user The user involved in the current action.
	 * @throws Exception
	 */
	public static void handleAction(Applicant.Menu option, Applicant user) throws Exception {
		switch (option) {
		case VIEW_PROJECTS:
			viewProjects(user);
			break;
		case VIEW_ENQUIRIES:
			enterEnquiriesSystem(user);
			break;
		case VIEW_APPLICATIONS:
			viewApplication(user);
			break;
		case WITHDRAW_APPLICATION:
			withdrawalApplicationFlow(user);
			break;
		default:
			break;
		}
	}
	/** 
	 * The flow for withdrawing of application
	 * @param applicant
	 * @throws Exception
	 */

	static void withdrawalApplicationFlow(Applicant applicant) throws Exception {
		Scanner scanner = system.getScanner();
		SuperScanner superScanner = new SuperScanner(scanner);

		ArrayList<Application> applications = system.getWithdrawableApplications();
		
		if (applications.isEmpty()) {
			System.out.println("You have no BTO applications that can be withdrawn right now.");
			return;
		}
		
		printApplicationsTable(applications);

		int option = superScanner.nextIntUntilCorrect("Choose application you wish to withdraw (0 to go back): ", 0, applications.size());

		if (option == 0) return;

		Application application = applications.get(option - 1);
		application.withdraw();
		system.saveChanges(CSVFileTypes.APPLICATIONS_LIST);

		new Style.Builder()
		.text("Application withdrawn, subject to agreement by authorised personnel.")
		.code(Code.TEXT_GREEN)
		.newLine()
		.print();
	}
	/** 
	 * Method to print the application that applicant have applied, if any.
	 * @param applications
	 * @return
	 */

	private static boolean printApplicationsTable(List<Application> applications) {
		var builder = new DisplayMenu.Builder()
		.setTitle("Applications")
		.addContent(String.format("%-5s │ %-20s │ %-10s │ %-30s", "No.", "Project", "Size", "Status"))
		.addDivider();
		
		boolean hasBookedApplication = false;

		for (int i = 0; i < applications.size(); i++) {
			var application = applications.get(i);
			var name = application.getProject().getProjectName();
			var flatSize = application.getFlatType();
			var withdrawalStatus = application.getWithdrawalStatus();
			var status = application.getStatus();

			var comboStatusBuilder = new Style.Builder();
			switch (withdrawalStatus) {
				case NOT, REJECTED:
					comboStatusBuilder.text(status.toString());
					if (withdrawalStatus == WithdrawalStatus.REJECTED) {
						comboStatusBuilder.text(" ");
						comboStatusBuilder.text(String.format("(%s)", withdrawalStatus.toString()))
						.bold()
						.code(Code.TEXT_RED);
					}
					break;
				case WITHDRAWN:
					comboStatusBuilder.text(status.toString()).strikethrough();
					comboStatusBuilder.text(" ");
					comboStatusBuilder.text(String.format("(%s)", withdrawalStatus.toString()))
					.bold()
					.code(Code.BACK_RED);
			}

			var comboStatus = comboStatusBuilder.toString();

			var indexValue = (i + 1) + ".";
			builder.addContent(String.format("%-5s │ %-20s │ %-10s │ %-30s", indexValue, name, flatSize, comboStatus));

			if (application.getStatus() == ApplicationStatus.BOOKED) {
				hasBookedApplication = true;
			}
		}
		
		builder.build().display();

		return hasBookedApplication;
	}
	
	/**
	 * Action flow for an applicant to view BTO applications applied.
	 * @param applicant The applicant that is actively using the application at current.
	 * @throws Exception
	 */
	static void viewApplication(Applicant applicant) throws Exception {
		Scanner scanner = system.getScanner();
		SuperScanner superScanner = new SuperScanner(scanner);

		ArrayList<Application> applications = system.getApplications();
		
		if (applications.isEmpty()) {
			System.out.println("You have no BTO applications.");
			return;
		}
		
		boolean hasBookedApplication = printApplicationsTable(applications);

		if (hasBookedApplication) {
			int choice = superScanner.nextIntUntilCorrect("Would you like to print the receipt? Choose application (0 to go back): ", 0, applications.size());

			if (choice == 0) return;

			Application application = applications.get(choice - 1);
			
			if (application.getStatus() == ApplicationStatus.BOOKED) {
				String receiptContents = application.getReceipt();
				ReceiptPrinter.print(receiptContents);
			}
			else {
				new Style.Builder()
				.text("This application is not yet booked and thus has no receipt printable.\n")
				.code(Code.TEXT_YELLOW)
				.print();
			}
		}
	}
	/**
	 * Method for user to view all the available projects for user to apply, filtered based on the user's age and marital status.
	 * @param applicant
	 * @throws Exception
	 */
	private static void viewProjects(Applicant applicant) throws Exception {
		SuperScanner sscanner = new SuperScanner(system.getScanner());
		
		ArrayList<BTOProject> projects = system.getApplicableProjects();
		
		if (projects.isEmpty()) {
			System.out.println("Unfortunately, there is currently no BTO projects suitable for you to apply.");
			return;
		}

		projects = system.filterProjects(projects);

		if (projects.isEmpty()) {
			System.out.println("Unfortunately, there is currently no BTO projects suitable for your filter citeria.");
			return;
		}


		
		repeatableMenuFlow(applicant, projects, sscanner);
	}
	/**
	 * this is the menu display which will be displayed at the home page for applicants.
	 * @param applicant
	 * @param projects
	 * @param sscanner
	 * @throws Exception
	 */
	private static void repeatableMenuFlow(Applicant applicant, ArrayList<BTOProject> projects, SuperScanner sscanner) throws Exception {
		List<BTOProject.TableColumnOption> viewingOptions = new ArrayList<>();
		viewingOptions.add(TableColumnOption.CLOSING_DATE);
		viewingOptions.add(TableColumnOption.ROOM_ONE_PRICE);
		viewingOptions.add(TableColumnOption.ROOM_ONE_UNITS);
		viewingOptions.add(TableColumnOption.ROOM_TWO_PRICE);
		viewingOptions.add(TableColumnOption.ROOM_TWO_UNITS);
		BTOProject.display(projects, viewingOptions);
		
		new DisplayMenu.Builder()
			.setTitle("Options")
			.addContent("1. Search by")
			.addContent("2. Apply for BTO")
			.addContent("3. Back to Main Menu")
			.build()
			.display();
		
		int option = sscanner.nextIntUntilCorrect("Choose an option: ", 1, 3);
		
		if (option == 3) return;
		
		if (option == 2) {
			applyBTOFlow(applicant, projects, sscanner);
			return;
		}
		
		searchFlow(applicant, projects, sscanner);
	}
	
	private static void searchFlow(Applicant applicant, ArrayList<BTOProject> projects, SuperScanner sscanner) throws Exception {
		new DisplayMenu.Builder()
		.setTitle("Search By?")
		.addContent("1. Project Name")
		.addContent("2. Neighbourhood")
		.addContent("3. Units Available for Flat Type")
		.addContent("4. Price Range")
		.addContent("5. Back to Projects Menu")
		.build()
		.display();
		
		int option = sscanner.nextIntUntilCorrect("Choose an option: ", 1, 5);
		
		if (option != 5) {
			if (option == 3) {
				int userInputFlatType = sscanner.nextIntUntilCorrect("Which flat type do you wish to see? (2/3, don't key '-Room'): ", 2, 3);
				FlatType flatType = FlatType.fromInt(userInputFlatType);
				projects = projects.stream()
						.filter(project -> {
							return project.getFlatForType(flatType).getRemainingUnits() > 0;
						})
						.collect(Collectors.toCollection(ArrayList::new));
			}
			
			switch (option) {
			case 1,2: {
				System.out.print("Filter for: ");
				String filterCondition = sscanner.getScanner().nextLine().toLowerCase();
				projects = projects.stream()
						.filter(project -> {
							if (option == 1) return project.getProjectName().toLowerCase().contains(filterCondition);
							else if (option == 2) return project.getNeighborhood().toLowerCase().contains(filterCondition);
							else return false;
						})
						.collect(Collectors.toCollection(ArrayList::new));
				break;
			}
			case 4: {
				int min = sscanner.nextIntUntilCorrect("Minimum Price: $", 0, Integer.MAX_VALUE);
				int max = sscanner.nextIntUntilCorrect("Maximum Price: $", min+1, Integer.MAX_VALUE);
				
				projects = projects.stream()
						.filter(project -> {
							return project.containsUnitsThatFitsPriceRange(min, max);
						})
						.collect(Collectors.toCollection(ArrayList::new));
				break;
			}
			default:
				break;
			}
		}
		
		repeatableMenuFlow(applicant, projects, sscanner);
	}
	
	
	public static void applyBTOFlow(Applicant applicant, ArrayList<BTOProject> projects, SuperScanner sscanner) throws Exception {
		
		ArrayList<Application> applications = system.getApplications();
		for (Application apps : applications) {
			if((apps.getStatus() == ApplicationStatus.BOOKED || 
					apps.getStatus() == ApplicationStatus.PENDING 
					|| apps.getStatus() == ApplicationStatus.SUCCESSFUL) && (apps.getWithdrawalStatus() != WithdrawalStatus.WITHDRAWN)) {
				new Style.Builder()
					.text("Only can apply for maximum of one project\n")
					.code(Code.TEXT_YELLOW)
					.print();
				return;
			}
		}
		
		List<BTOProject.TableColumnOption> listingOptions = new ArrayList<>();
		listingOptions.add(TableColumnOption.INDEX_NUMBER);
		BTOProject.display(projects, listingOptions);
		
		int choice = sscanner.nextIntUntilCorrect("Select the number representing the project that you are interested in: ", 1, projects.size());
		
		BTOProject selectedProject = projects.get(choice - 1);
		
		Set<FlatType> types = applicant.getEligibleFlatTypes();
		
		DisplayMenu.Builder typeMenu = new DisplayMenu.Builder();
		typeMenu.addContent(String.format("%3s │ %-15s │ %-10s │ %-16s", "No.", "Type", "Price", "Units Remaining"));
		typeMenu.addDivider();
		List<FlatInfo> flats = selectedProject.getApplicableFlats(types);
		
		for (int i = 0; i < flats.size(); i++) {
			FlatInfo flat = flats.get(i);
			typeMenu.addContent(String.format("%3d │ %-15s │ $%-9s │ %-16d", i+1, flat.getType(), flat.getPrice(), flat.getRemainingUnits()));
		}
		
		typeMenu.build().display();
		
		FlatInfo flat;
		
		if (types.size() > 1) {
			int typeIndex = sscanner.nextIntUntilCorrect("Which size are you interested in applying? (Select the number): ");
			
			while (typeIndex <= 0 || typeIndex > flats.size()) {
				new Style.Builder()
					.text(String.format("No such project at position %d.\n", choice))
					.code(Code.TEXT_YELLOW)
					.print();
		
				typeIndex = sscanner.nextIntUntilCorrect("Which size are you interested in applying? (Select the number): ");
			}
			flat = flats.get(typeIndex - 1);
			
			
		}
		else {
			FlatType type = types.stream().findFirst().orElse(null);
			System.out.printf("You are only eligible for %s flats.\n", type);
			boolean shouldApply = sscanner.nextBoolUntilCorrect("Would you like to apply? (Y/N): ");
			
			if (!shouldApply) return;
			
			flat = selectedProject.getFlatForType(type);
		}
		
		Application application = new Application(selectedProject, flat.getType(), applicant);
		system.addApplication(application);
		System.out.println("Application successfully submitted");
	}
	
	private static void enterEnquiriesSystem(Applicant applicant) throws Exception {
		Scanner scanner = system.getScanner();
		SuperScanner superScanner = new SuperScanner(scanner);

		var enquiriesSystem = applicant.getEnquiriesSystem();
		
		enquiriesSystem.displayEnquiriesMenu();

		new DisplayMenu.Builder()
		.setTitle("Options")
		.addContent("1. View More About Enquiry")
		.addContent("2. Send Enquiry")
		.addContent("3. Delete Enquiry")
		.addContent("4. Edit Enquiry")
		.addContent("5. Back")
		.build()
		.display();

		int option = superScanner.nextIntUntilCorrect("Choose option: ", 1, 5);

		if (option == 5) return;

		if (option == 2) {
			sendEnquiryFlow(applicant);
			return;
		}

		Enquiry selectedEnquiry = retrieveEnquiryFromUser(superScanner, enquiriesSystem);

		switch (option) {
			case 1: { // view more info
				selectedEnquiry.display();
				break;
			}
			case 3: { // delete
				removeEnquiry(selectedEnquiry, enquiriesSystem);
				break;
			}
			case 4: { // edit
				editEnquiry(superScanner, enquiriesSystem, selectedEnquiry);
				break;
			}
			default: {
				throw new IllegalArgumentException("An unknown error has occurred");
			}
		}
	}

	private static Enquiry retrieveEnquiryFromUser(SuperScanner superScanner, EnquiriesSystem enquiriesSystem) {
		int size = enquiriesSystem.ownEnquiriesSize();
		enquiriesSystem.displayEnquiriesMenu();
		int enquiryIndex = superScanner.nextIntUntilCorrect("Choose enquiry: ", 1, size) - 1;
		Enquiry selectedEnquiry = enquiriesSystem.getEnquiries().get(enquiryIndex);
		return selectedEnquiry;
	}

	static void removeEnquiry(Enquiry selectedEnquiry, EnquiriesSystem enquiriesSystem) throws Exception {
		enquiriesSystem.removeEnquiry(selectedEnquiry);
		new Style.Builder()
			.text("Enquiry successfully deleted.")
			.code(Style.Code.TEXT_GREEN)
			.newLine()
			.print();
	}

	static void editEnquiry(SuperScanner superScanner, EnquiriesSystem enquiriesSystem, Enquiry selectedEnquiry) throws Exception {
		if (selectedEnquiry.hasResponded()) throw new IllegalStateException("You cannot change your message after an officer or manager has responded to you.");

		System.out.println("Current question: " + selectedEnquiry.getQuestion().getContent());
		System.out.print("    New question: ");
		String newQuestion = superScanner.getScanner().nextLine();
		enquiriesSystem.updateEnquiry(selectedEnquiry, newQuestion);
		new Style.Builder()
			.text("Enquiry successfully updated.")
			.code(Style.Code.TEXT_GREEN)
			.newLine()
			.print();
	}
	
	static void sendEnquiryFlow(Applicant applicant) throws Exception {
		Scanner scanner = system.getScanner();
		SuperScanner sscanner = new SuperScanner(scanner);
		
		ArrayList<BTOProject> projects;
		
		if (applicant instanceof HDBOfficer) {
			projects = system.getApplicableProjectsAsApplicant();
		}
		else {
			projects = system.getApplicableProjects();
		}
		
		if (projects.isEmpty()) {
			System.out.println("As you are not eligible to apply a BTO flat at this moment, you cannot enquire about current BTO projects.");
			return;
		}

		projects = system.filterProjects(projects);

		if (projects.isEmpty()) {
			System.out.println("There is currently no BTO projects suitable for your filter citeria.");
			return;
		}

		List<BTOProject.TableColumnOption> options = new ArrayList<>();
		options.add(TableColumnOption.INDEX_NUMBER);
		BTOProject.display(projects, options);
		
		int projectOption = sscanner.nextIntUntilCorrect("Which project would you like to enquire about? Choose a number: ", 1, projects.size());
		
		BTOProject project = projects.get(projectOption - 1);
		
		System.out.println("Ask any question regarding our project.");
		System.out.print("Question: ");
		String query = scanner.nextLine();
		
		Message question = new Message(applicant, query);
		
		// TODO: Link with a project.
		Enquiry enquiry = new Enquiry(question, project);
		
		applicant.getEnquiriesSystem().addEnquiry(enquiry);
		
		System.out.println("Thank you. We will reply back in 3-5 business days.");
	}
}
