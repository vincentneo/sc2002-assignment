package sc2002.FCS1.grp2;

public class ApplicantActions {
	private static BTOManagementSystem system;
	
	public static void setSystem(BTOManagementSystem system) {
		ApplicantActions.system = system;
	}
	
	public static void handleAction(Applicant.Menu option, Applicant user) {
		switch (option) {
		case VIEW_PROJECTS:
			System.out.println("Work in progress");
		case SEND_ENQUIRY:
			enquiryFlow(user);
			break;
		}
	}
	
	private static void enquiryFlow(Applicant applicant) {
		
	}
}
