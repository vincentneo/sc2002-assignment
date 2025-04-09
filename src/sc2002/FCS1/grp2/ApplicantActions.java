package sc2002.FCS1.grp2;

public class ApplicantActions {
	private static BTOManagementSystem system;
	
	public static void setSystem(BTOManagementSystem system) {
		ApplicantActions.system = system;
	}
	
	public static void handleAction(Applicant.Menu option, Applicant user) {
		switch (option) {
		case CREATE_PROJECT:
			System.out.println("Work in progress");
			break;
		}
	}
}
