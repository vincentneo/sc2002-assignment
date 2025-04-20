package sc2002.FCS1.grp2;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

import sc2002.FCS1.grp2.BTOProject.TableColumnOption;
import sc2002.FCS1.grp2.Style.Code;

public class ApplicantActions {
	private static BTOManagementSystem system = BTOManagementSystem.common();
	
	public static void handleAction(Applicant.Menu option, Applicant user) throws Exception {
		switch (option) {
		case VIEW_PROJECTS:
			viewProjects(user);
			break;
		case VIEW_ENQUIRIES:
			showEnquiries(user);
			break;
		case SEND_ENQUIRY:
			enquiryFlow(user);
			break;
		case VIEW_APPLICATIONS:
			viewApplication(user);
			break;
		default:
			break;
		}
		/*
		 * TODO: move them else where
		 *            menu.add("View Applied Project");
           menu.add("Withdraw Application");
           if(applicationStatus == ApplicationStatus.SUCCESSFUL && !hasBookedFlat) {
               menu.add("Book Flat");
           }
		 */
	}
	
	private static void viewApplication(Applicant applicant) throws Exception {
		ArrayList<Application> applications = system.getApplications();
		
		if (applications.isEmpty()) {
			System.out.println("You have no BTO applications.");
			return;
		}
		
		var builder = new DisplayMenu.Builder()
		.setTitle("Applications")
		.addContent(String.format("%-5s │ %-20s │ %-20s", "No.", "Project", "Status"))
		.addDivider();
		
		for (int i = 0; i < applications.size(); i++) {
			var application = applications.get(i);
			var name = application.getProject().getProjectName();
			var status = application.getStatus();
			builder.addContent(String.format("%-5s │ %-20s │ %-20s", (i+1) + ".", name, status.toString()));
		}
		
		builder.build().display();
	}
	
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
		
		// TODO: investigate why on return of this method will cause "invalid option" in subsequent cycle. Wheres the uncaptured \n coming from?
		
	}
	
	private static void showEnquiries(Applicant applicant) throws Exception {
		applicant.getEnquiriesSystem().displayEnquiriesMenu();
	}
	
	private static void enquiryFlow(Applicant applicant) throws Exception {
		Scanner scanner = system.getScanner();
		SuperScanner sscanner = new SuperScanner(scanner);
		
		// TODO: print out projects for user to select first
		ArrayList<BTOProject> projects = system.getApplicableProjects();
		
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
