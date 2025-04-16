package sc2002.FCS1.grp2;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import sc2002.FCS1.grp2.BTOProject.TableColumnOption;
import sc2002.FCS1.grp2.Style.Code;

public class ApplicantActions {
	private static BTOManagementSystem system;
	
	public static void setSystem(BTOManagementSystem system) {
		ApplicantActions.system = system;
	}
	
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
		}
	}
	
	private static void viewProjects(Applicant applicant) throws Exception {
		SuperScanner sscanner = new SuperScanner(system.getScanner());
		
		ArrayList<BTOProject> projects = system.getApplicableProjects();
		
		if (projects.isEmpty()) {
			System.out.println("Unfortunately, there is currently no BTO projects suitable for you to apply.");
			return;
		}

		List<BTOProject.TableColumnOption> viewingOptions = new ArrayList<>();
		viewingOptions.add(TableColumnOption.OPENING_DATE);
		viewingOptions.add(TableColumnOption.CLOSING_DATE);
		BTOProject.display(projects, viewingOptions);
		
		boolean applyBTO = sscanner.nextBoolUntilCorrect("Would you like to apply for a BTO? (Y/N): ");
		
		if (!applyBTO) {
			return;
		}

		List<BTOProject.TableColumnOption> listingOptions = new ArrayList<>();
		listingOptions.add(TableColumnOption.INDEX_NUMBER);
		BTOProject.display(projects, listingOptions);

		System.out.println("To return, type \"back\"");
		
		int choice = sscanner.nextIntUntilCorrect("Select the number representing the project that you are interested in: ");
		
		while (choice <= 0 || choice > projects.size()) {
			new Style.Builder()
					.text(String.format("No such project at position %d.\n", choice))
					.code(Code.TEXT_YELLOW)
					.print();
			
			choice = sscanner.nextIntUntilCorrect("Select the number representing the project that you are interested in: ");
		}
		
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
