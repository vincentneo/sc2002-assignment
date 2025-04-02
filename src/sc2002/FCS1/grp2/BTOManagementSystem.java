package sc2002.FCS1.grp2;

import java.util.ArrayList;
import java.util.Scanner;

public class BTOManagementSystem {
	private ArrayList<Applicant> applicants;
	private ArrayList<HDBManager> managers;
	private ArrayList<HDBOfficer> officers;
	private ArrayList<BTOProject> projects;
	
	private User activeUser = null;
	
	private Scanner scanner;
	
	public BTOManagementSystem() {
		this.scanner = new Scanner(System.in);
		
		CSVParser parser = new CSVParser();
		
		ArrayList<Applicant> applicants = parser.parseApplicants();
		ArrayList<HDBManager> managers = parser.parseManagers();
		ArrayList<HDBOfficer> officers = parser.parseOfficer();
		ArrayList<BTOProject> projects = parser.parseProjects();
		
		for (BTOProject project : projects) {
			project.retrieveConnectedUsers(officers);
		}
		
		this.applicants = applicants;
		this.managers = managers;
		this.officers = officers;
		this.projects = projects;
	}
	
	private ArrayList<User> allUsers() {
		ArrayList<User> users = new ArrayList<>();
		users.addAll(applicants);
		users.addAll(managers);
		users.addAll(officers);
		return users;
	}
	
	public User findUserByNRIC(String nric) {
		for (User user : allUsers()) {
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
	
	public void saveChanges(CSVFileTypes type) {
		ArrayList<CSVEncodable> encodables = new ArrayList<>();
		
		switch (type) {
			case APPLICANT_LIST: {
				encodables.addAll(applicants);
				break;
			}
			case MANAGER_LIST: {
				encodables.addAll(managers);
				break;
			}
			case OFFICER_LIST: {
				encodables.addAll(officers);
				break;
			}
			case PROJECT_LIST: {
				// TODO: implement for BTOProject class
				//encodables.addAll(projects);
				break;
			}
		}
		try {
			CSVEncoder encoder = new CSVEncoder(type.getFileName(), encodables);
			encoder.encode();
		}
		catch (Exception e) {
			// TODO: handle properly.
			e.printStackTrace();
		}
	}
	
	public User getActiveUser() {
		return activeUser;
	}
	
	public ArrayList<BTOProject> getProjects() {
		return projects;
	}
	
	public void addProject(BTOProject project) {
		projects.add(project);
//		project.retrieveConnectedUsers(officers);
		System.out.println(projects);
	}
	
	public Scanner getScanner() {
		return scanner;
	}
	
	/**
	 * call when finished
	 */
	public void cleanup() {
		scanner.close();
	}

	// TODO: Delete once done. This method is only intended for testing.
	void debugPrintAllUsers() {		
		// del later
		for (User user : allUsers()) {
			System.out.printf("%s: ", user.getClass());
			user.print();
		}
		
	}
	
}
