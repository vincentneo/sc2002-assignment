package sc2002.FCS1.grp2;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;
import java.time.LocalDate;

public class BTOManagementSystem implements EnquiriesDelegate {
	private ArrayList<Applicant> applicants;
	private ArrayList<HDBManager> managers;
	private ArrayList<HDBOfficer> officers;
	private ArrayList<BTOProject> projects;
	private ArrayList<Enquiry> enquiries;
	
	private User activeUser = null;
	
	private Scanner scanner;
	
	public BTOManagementSystem() {
		this.scanner = new Scanner(System.in);
		
		CSVParser parser = new CSVParser();
		
		ArrayList<Applicant> applicants = parser.parseApplicants();
		ArrayList<HDBManager> managers = parser.parseManagers();
		ArrayList<HDBOfficer> officers = parser.parseOfficer();
		ArrayList<BTOProject> projects = parser.parseProjects();
		ArrayList<Enquiry> enquiries = parser.parseEnquiries();
		
		for (BTOProject project : projects) {
			project.retrieveConnectedUsers(officers, managers);
		}
		
		this.applicants = applicants;
		this.managers = managers;
		this.officers = officers;
		this.projects = projects;
		this.enquiries = enquiries;
		
		ArrayList<User> all = allUsers();
		for (Enquiry enquiry : enquiries) {
			enquiry.linkUsers(all);
		}
		
		automatedChecks();
	}
	
	/**
	 * Call this on every system init for automated checks on expirable content.
	 */
	private void automatedChecks() {
		projectsClosureUponExpiry();
	}
	
	/**
	 * Sets all expired projects as non-visible.
	 */
	private void projectsClosureUponExpiry() {
		LocalDate now = LocalDate.now();
		for (BTOProject project : projects) {
			if (project.getClosingDate().isBefore(now)) {
				project.setVisibility(false);
			}
		}
		
		saveChanges(CSVFileTypes.PROJECT_LIST);
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
	
	public void logout() {
		this.activeUser.setEnquiriesSystem(null);
		this.activeUser = null;
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
				encodables.addAll(projects);
				break;
			}
			case ENQUIRIES_LIST: {
				encodables.addAll(enquiries);
			}
		}
		try {
			CSVEncoder encoder = new CSVEncoder(type.getFileName(), type.getHeader(), encodables);
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
	
	public ArrayList<BTOProject> getApplicableProjects() {
		if (activeUser instanceof HDBManager) {
			return projects.stream()
					.filter(p -> p.getManagerInCharge()
							.getNric()
							.equals(activeUser.getNric())
							)
					.collect(Collectors.toCollection(ArrayList::new));
		}
		else if (activeUser instanceof Applicant) {
			Applicant ap = (Applicant) activeUser;
			Set<FlatType> types = ap.getEligibleFlatTypes();
			
			return projects.stream()
					.filter(p -> { 
						return p.getVisibility() && p.isEligible(types);
					})
					.collect(Collectors.toCollection(ArrayList::new));
		}
		
		return new ArrayList<>();
	}
	
	/**
	 * Decide if the current active user should be permitted to do a certain task, based on access rights by type of user.
	 * 
	 * @param <U> Type of User
	 * @param expectedType Provide the expected user class type here.
	 * @return true if active user is of given user type, hence permitted, false if otherwise.
	 */
	public <U extends User> boolean isActiveUserPermitted(Class<U> expectedType) {
		if (activeUser == null) return false;
		
		return expectedType.isInstance(activeUser);
	}

	public void addProject(BTOProject project) throws Exception {
		// always check if logged-in user is permitted for activity, else return;
		if (!isActiveUserPermitted(HDBManager.class)) throw new InsufficientAccessRightsException();

		projects.add(project);
		saveChanges(CSVFileTypes.PROJECT_LIST);
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

	@Override
	public void addEnquiry(Enquiry enquiry) throws Exception {
		if (!isActiveUserPermitted(Applicant.class)) throw new InsufficientAccessRightsException();
		this.enquiries.add(enquiry);
		this.saveChanges(CSVFileTypes.ENQUIRIES_LIST);
	}

	@Override
	public List<Enquiry> getApplicableEnquiries(User user) {
		if (user instanceof HDBManager) {
			return enquiries;
		}
		
		if (user instanceof Applicant) {
			return enquiries.stream().filter(e -> e.isUserInvolved(user)).collect(Collectors.toList());
		}
		
		return new ArrayList<>();
	}
	
}
