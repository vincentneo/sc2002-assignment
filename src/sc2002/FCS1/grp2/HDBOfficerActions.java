package sc2002.FCS1.grp2;

public class HDBOfficerActions {
	
	private static BTOManagementSystem system;
	
	public static void setSystem(BTOManagementSystem system) {
		HDBOfficerActions.system = system;
	}
	
	public static void handleAction(HDBOfficer.Menu option, HDBOfficer user) throws Exception {
		switch (option) {
		case VIEW_PROJECTS:
			 // TODO: filtering, access control etc etc
			BTOProject.display(system.getProjects(), false);
			break;
		}
//		switch (option) {
//		case CREATE_PROJECT:
//			createProject(user);
//			break;
//		}
	}

}
