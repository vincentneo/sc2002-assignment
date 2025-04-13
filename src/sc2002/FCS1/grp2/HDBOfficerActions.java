package sc2002.FCS1.grp2;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;


public class HDBOfficerActions {
	
	
	private static BTOManagementSystem system;
	
	public static void setSystem(BTOManagementSystem system) {
		HDBOfficerActions.system = system;
	}
	
	public static void handleAction(HDBOfficer.Menu option, HDBOfficer user) throws Exception {
		switch (option) {
		case CREATE_PROJECT:
			createProject(user);
			break;
		}
	}

}
