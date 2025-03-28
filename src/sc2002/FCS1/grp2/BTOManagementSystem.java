package sc2002.FCS1.grp2;

import java.util.ArrayList;

public class BTOManagementSystem {
	private ArrayList<User> users;
	private ArrayList<BTOProject> projects;
	
	private User activeUser = null;
	
	public BTOManagementSystem() {
		CSVParser parser = new CSVParser();
		
		ArrayList<User> users = new ArrayList<>();
		ArrayList<Applicant> applicants = parser.parseApplicants();
		ArrayList<HDBManager> managers = parser.parseManagers();
		ArrayList<HDBOfficer> officers = parser.parseOfficer();
		users.addAll(applicants);
		users.addAll(managers);
		users.addAll(officers);
		
		ArrayList<BTOProject> projects = parser.parseProjects();
		
		for (BTOProject project : projects) {
			project.retrieveConnectedUsers(officers);
		}
		
		this.users = users;
		this.projects = projects;
	}
	
	public User findUserByNRIC(String nric) {
		for (User user : users) {
			if (user.getNric().equalsIgnoreCase(nric)) {
				return user;
			}
		}
		return null;
	}
	
	public boolean attemptLogin(User user, String password) {
		boolean result = user.checkPassword(password);
		
		if (result) {
			this.activeUser = user;
		}
		
		return result;
	}
	
	public User getActiveUser() {
		return activeUser;
	}
	
	public ArrayList<BTOProject> getProjects() {
		return projects;
	}

	// TODO: Delete once done. This method is only intended for testing.
	void debugPrintAllUsers() {		
		// del later
		for (User user : users) {
			System.out.printf("%s: ", user.getClass());
			user.print();
		}
		
	}
	
}
