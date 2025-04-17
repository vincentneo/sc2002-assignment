package sc2002.FCS1.grp2;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;
import java.time.LocalDate;

/**
 * The key class that handles all important data.
 * 
 * It acts as a single source of truth by design, to facilitate two things:
 *  - Consistency for persisting data
 * 	- Ensure proper access control
 * 
 * The idea is that this class shall hold the entire pool of data across any persistable objects,
 * including but not limited to users, projects, and other related objects.
 * 
 * Other classes shall take great care by only referencing objects provided by this class.
 * This ensures that, when an attempt is made to persist data by encoding into CSV files, data remains consistent,
 * for that, any changes made, shall be made to the same object, and not a copy of the object.
 * 
 * Other classes that require the data, should take great care in ensuring that it is retrieved in a manner,
 * which User classes cannot be passed around to get the data, rather the `activeUser` property of this class, 
 * shall be the only truth towards the current logged in user, ensuring proper access control.
 * 
 * @author Vincent Neo
 */
public class BTOManagementSystem implements EnquiriesDelegate {
	
	private static BTOManagementSystem commonInstance = new BTOManagementSystem();
	
	private ArrayList<Applicant> applicants;
	private ArrayList<HDBManager> managers;
	private ArrayList<HDBOfficer> officers;
	private ArrayList<BTOProject> projects;
	private ArrayList<Enquiry> enquiries;
	private ArrayList<Application> applications;
	
	private User activeUser = null;
	
	private Scanner scanner;
	
	private BTOManagementSystem() {
		this.scanner = new Scanner(System.in);
		
		CSVParser parser = new CSVParser();
		
		ArrayList<Applicant> applicants = parser.parseApplicants();
		ArrayList<HDBManager> managers = parser.parseManagers();
		ArrayList<HDBOfficer> officers = parser.parseOfficer();
		ArrayList<BTOProject> projects = parser.parseProjects();
		ArrayList<Enquiry> enquiries = parser.parseEnquiries();
		ArrayList<Application> applications = parser.parseApplications();
		
		for (BTOProject project : projects) {
			project.retrieveConnectedUsers(officers, managers);
		}
		
		this.applicants = applicants;
		this.managers = managers;
		this.officers = officers;
		this.projects = projects;
		this.enquiries = enquiries;
		this.applications = applications;
		
		ArrayList<User> all = allUsers();
		for (Enquiry enquiry : enquiries) {
			enquiry.linkUsers(all);
		}
		
		automatedChecks();
	}
	
	public static BTOManagementSystem common() {
		return commonInstance;
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
				break;
			}
			case APPLICATIONS_LIST: {
				encodables.addAll(applications);
				break;
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
					.filter(p -> p.getManagerInCharge().equals(activeUser)
							)
					.collect(Collectors.toCollection(ArrayList::new));
		}
		else if (activeUser instanceof HDBOfficer) {
			return projects.stream()
					.filter(p -> p.getOfficers().contains(activeUser))
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
	 * Get the projects that are filtered by the active user's filter.
	 * 
	 * @param filteredProjects The list of projects to be filtered.
	 * @return The filtered list of projects.
	 */
	public ArrayList<BTOProject> filterProjects (ArrayList<BTOProject> filteredProjects) {
		ListingFilter filter = activeUser.getListingFilter();
		
		switch (filter) {
            case DEFAULT: // Alphabetical order by project name
				filteredProjects = filteredProjects.stream()
						.sorted(Comparator.comparing(p -> p.getProjectName().toLowerCase()))
						.collect(Collectors.toCollection(ArrayList::new));
                break;
            case TWO_ROOM:
                filteredProjects = filteredProjects.stream()
                        .filter(p -> p.getTwoRoomUnits() > 0)
                        .collect(Collectors.toCollection(ArrayList::new));
                break;
            case THREE_ROOM:   
                filteredProjects = filteredProjects.stream()
                        .filter(p -> p.getThreeRoomUnits() > 0)
                        .collect(Collectors.toCollection(ArrayList::new));
                break;
            case TWO_ROOM_PRICE_DESCENDING:
                filteredProjects = filteredProjects.stream()
                        .sorted(Comparator.comparing(BTOProject::getTwoRoomPrice).reversed())
                        .collect(Collectors.toCollection(ArrayList::new));
                break;
            case TWO_ROOM_PRICE_ASCENDING:
                filteredProjects = filteredProjects.stream()
                        .sorted(Comparator.comparing(BTOProject::getTwoRoomPrice))
                        .collect(Collectors.toCollection(ArrayList::new));
                break;
            case THREE_ROOM_PRICE_DESCENDING:
                filteredProjects = filteredProjects.stream()
                        .sorted(Comparator.comparing(BTOProject::getThreeRoomPrice).reversed())
                        .collect(Collectors.toCollection(ArrayList::new));
                break;
            case THREE_ROOM_PRICE_ASCENDING:
                filteredProjects = filteredProjects.stream()
                        .sorted(Comparator.comparing(BTOProject::getThreeRoomPrice))
                        .collect(Collectors.toCollection(ArrayList::new));
                break;
            case OPENING_DATE_ASCENDING:
                filteredProjects = filteredProjects.stream()
                        .sorted(Comparator.comparing(BTOProject::getOpeningDate))
                        .collect(Collectors.toCollection(ArrayList::new));
                break;
            case OPENING_DATE_DESCENDING:
                filteredProjects = filteredProjects.stream()
                        .sorted(Comparator.comparing(BTOProject::getOpeningDate).reversed())
                        .collect(Collectors.toCollection(ArrayList::new));
                break;
            case CLOSING_DATE_ASCENDING:
                filteredProjects = filteredProjects.stream()
                        .sorted(Comparator.comparing(BTOProject::getClosingDate))
                        .collect(Collectors.toCollection(ArrayList::new));
                break;
            case CLOSING_DATE_DESCENDING:
                filteredProjects = filteredProjects.stream()
                        .sorted(Comparator.comparing(BTOProject::getClosingDate).reversed())
                        .collect(Collectors.toCollection(ArrayList::new));
                break;
            case NAME: // Search based on keyword in filter
                String nameKeyword = filter.getKeyword().trim();
                filteredProjects = filteredProjects.stream()
                        .filter(p -> p.getProjectName().toLowerCase().contains(nameKeyword.toLowerCase()))
                        .collect(Collectors.toCollection(ArrayList::new));
                break;
            case NEIGHBORHOOD: // Search based on keyword in filter
                String neighborhoodKeyword = filter.getKeyword().trim();
                filteredProjects = filteredProjects.stream()
                        .filter(p -> p.getNeighborhood().toLowerCase().contains(neighborhoodKeyword.toLowerCase()))
                        .collect(Collectors.toCollection(ArrayList::new));
                break;
            case null:
                // No filtering applied, keep the original list
                break;
        }

		return filteredProjects;
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
	
	@SuppressWarnings("unchecked")
	public <U extends User> U getActiveUserForPermittedTask(Class<U> expectedType) throws InsufficientAccessRightsException {
		if (isActiveUserPermitted(expectedType)) return (U) activeUser;
		else throw new InsufficientAccessRightsException();
	}

	public void addProject(BTOProject project) throws Exception {
		// always check if logged-in user is permitted for activity, else return;
		if (!isActiveUserPermitted(HDBManager.class)) throw new InsufficientAccessRightsException();

		projects.add(project);
		saveChanges(CSVFileTypes.PROJECT_LIST);
//		project.retrieveConnectedUsers(officers);
		System.out.println("\nProject added successfully : ");
		System.out.println(projects);

	}

	public void deleteProject(BTOProject project) throws Exception {
		if (!isActiveUserPermitted(HDBManager.class)) throw new InsufficientAccessRightsException();
		if (project.getManagerInCharge() != activeUser) throw new InsufficientAccessRightsException();
		if (projects.contains(project) == false) throw new Exception("Project not found in system.");

		projects.remove(project);
		saveChanges(CSVFileTypes.PROJECT_LIST);
	}
	
	public void addApplication(Application application) throws Exception {
		if (!(isActiveUserPermitted(Applicant.class) || isActiveUserPermitted(HDBOfficer.class))) throw new InsufficientAccessRightsException();
		applications.add(application);
		saveChanges(CSVFileTypes.APPLICATIONS_LIST);
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
	public List<Enquiry> getApplicableEnquiries() {
		if (activeUser instanceof HDBManager) {
			return enquiries;
		}
		
		if (activeUser instanceof Applicant) {
			return enquiries.stream()
					.filter(e -> e.isUserInvolved(activeUser))
					.collect(Collectors.toList());
		}
		
		return new ArrayList<>();
	}
	
}
