package sc2002.FCS1.grp2;

import java.util.ArrayList;
import java.util.Scanner;

public class ApplicantActions {
	private static BTOManagementSystem system;
	
	public static void setSystem(BTOManagementSystem system) {
		ApplicantActions.system = system;
	}
	
	public static void handleAction(Applicant.Menu option, Applicant user) throws Exception {
		switch (option) {
		case VIEW_PROJECTS:
			System.out.println("Work in progress");
			break;
		case VIEW_ENQUIRIES:
			showEnquiries(user);
			break;
		case SEND_ENQUIRY:
			enquiryFlow(user);
			break;
		}
	}
	
	private static void showEnquiries(Applicant applicant) throws Exception {
		applicant.getEnquiriesSystem().displayEnquiriesMenu();
	}
	
	private static void enquiryFlow(Applicant applicant) throws Exception {
		Scanner scanner = system.getScanner();
		
		// TODO: print out projects for user to select first
		
		System.out.println("Ask any question regarding our project.");
		System.out.print("Question: ");
		String query = scanner.nextLine();
		
		Message question = new Message(applicant, query);
		
		// TODO: Link with a project.
		Enquiry enquiry = new Enquiry(question, null);
		
		applicant.getEnquiriesSystem().addEnquiry(enquiry);
		
		System.out.println("Thank you. We will reply back in 3-5 business days.");
	}
}
