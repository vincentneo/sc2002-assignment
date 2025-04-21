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
 * It acts as a single source of truth by design, to facilitate two things: -
 * Consistency for persisting data - Ensure proper access control
 * 
 * The idea is that this class shall hold the entire pool of data across any
 * persistable objects, including but not limited to users, projects, and other
 * related objects.
 * 
 * Other classes shall take great care by only referencing objects provided by
 * this class. This ensures that, when an attempt is made to persist data by
 * encoding into CSV files, data remains consistent, for that, any changes made,
 * shall be made to the same object, and not a copy of the object.
 * 
 * Other classes that require the data, should take great care in ensuring that
 * it is retrieved in a manner, which User classes cannot be passed around to
 * get the data, rather the `activeUser` property of this class, shall be the
 * only truth towards the current logged in user, ensuring proper access
 * control.
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

		this.applicants = applicants;
		this.managers = managers;
		this.officers = officers;
		this.projects = projects;
		this.enquiries = enquiries;
		this.applications = applications;

		crosslinking();
		automatedChecks();
	}

	public static BTOManagementSystem common() {
		return commonInstance;
	}

	private void crosslinking() {
		for (BTOProject project : projects) {
			project.retrieveConnectedUsers(officers, managers);
		}

		ArrayList<User> all = allUsers();
		for (Enquiry enquiry : enquiries) {
			enquiry.linkUsers(all);
			enquiry.linkProject(projects);
		}

		for (Application application : applications) {
			application.linkApplicant(applicants);
			application.linkProject(projects);
		}
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
			login(user);
		}

		return result;
	}
	
	private void login(User user) {
		this.activeUser = user;
		
		if (user.getEnquiriesSystem() == null) {
			EnquiriesSystem eSystem = new EnquiriesSystem(this);
			
			user.setEnquiriesSystem(eSystem);
		}
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
		} catch (Exception e) {
			// TODO: handle properly.
			e.printStackTrace();
		}
	}

	// region Getters & Setters
	private ArrayList<User> allUsers() {
		ArrayList<User> users = new ArrayList<>();
		users.addAll(applicants);
		users.addAll(managers);
		users.addAll(officers);
		return users;
	}

	public User getActiveUser() {
		return activeUser;
	}

	/**
	 * Retrieve all projects of the system.
	 * 
	 * Great care must be exercised when using this getter. Do not use unless
	 * necessary.
	 * 
	 * @return all projects, regardless of visibility, applicability, intended for
	 *         front-end filtering.
	 */
	public ArrayList<BTOProject> getProjects() throws Exception {
		if (!(isActiveUserPermitted(HDBManager.class) || isActiveUserPermitted(HDBOfficer.class)))
			throw new InsufficientAccessRightsException();
		return projects;
	}

	/**
	 * Retrieve projects applicable for the current user.
	 * 
	 * @return For applicants, it will return projects with public visibility,
	 *         subject to eligibility criteria; For officers, it will return
	 *         projects that the officer is involved in; For managers, it will
	 *         return projects whereby the manager is in charge of.
	 */

	public ArrayList<BTOProject> getApplicableProjects() {
		if (activeUser instanceof HDBManager) {
			return projects.stream().filter(p -> p.getManagerInCharge().equals(activeUser))
					.collect(Collectors.toCollection(ArrayList::new));
		} else if (activeUser instanceof HDBOfficer) {
			return projects.stream().filter(p -> p.getOfficers().contains(activeUser))
					.collect(Collectors.toCollection(ArrayList::new));
		} else if (activeUser instanceof Applicant) {
			return getVisibleProjects();
		}

		return new ArrayList<>();
	}

	private ArrayList<BTOProject> getVisibleProjects() {
		Applicant ap = (Applicant) activeUser;
		Set<FlatType> types = ap.getEligibleFlatTypes();

		return projects.stream().filter(p -> {
			boolean condition = p.getVisibility() && p.isEligible(types);
			if (ap instanceof HDBOfficer) {
				condition = condition && !p.getOfficers().contains(ap);
			}
			return condition;
		}).collect(Collectors.toCollection(ArrayList::new));
	}

	public ArrayList<BTOProject> getApplicableProjectsAsApplicant() throws Exception {
		if (!isActiveUserPermitted(HDBOfficer.class))
			throw new InsufficientAccessRightsException();

		return getVisibleProjects();
	}

	/**
	 * Use this scanner to get user input.
	 * 
	 * @return {@code Scanner} object.
	 */
	public Scanner getScanner() {
		return scanner;
	}

	@Override
	public List<Enquiry> getOwnEnquiries() {
//		if (!(isActiveUserPermitted(HDBOfficer.class) || isActiveUserPermitted(Applicant.class))) return new ArrayList<>();
		
		return enquiries.stream().filter(e -> e.isUserInvolved(activeUser)).collect(Collectors.toCollection(ArrayList::new));
	}
	
	@Override
	public List<Enquiry> getEnquiries() {
		if (activeUser instanceof HDBManager) {
			return enquiries;
		}
		else if (activeUser instanceof HDBOfficer) {
			return enquiries.stream()
					.filter(e -> e.getProject().getOfficers().contains(activeUser))
					.collect(Collectors.toCollection(ArrayList::new));

		}
		
		return new ArrayList<>();
	}

	@Override
	public void addEnquiry(Enquiry enquiry) throws Exception {
		if (!isActiveUserPermitted(Applicant.class))
			throw new InsufficientAccessRightsException();
		this.enquiries.add(enquiry);
		this.saveChanges(CSVFileTypes.ENQUIRIES_LIST);
	}

	/**
	 * Get the projects that are filtered by the active user's filter.
	 * 
	 * @param filteredProjects The list of projects to be filtered.
	 * @return The filtered list of projects.
	 */
	public ArrayList<BTOProject> filterProjects(ArrayList<BTOProject> filteredProjects) {
		ListingSort sort = activeUser.getListingSort();
		ListingFilter filter = activeUser.getListingFilter();

		switch (sort) {
		case DEFAULT: // Alphabetical order by project name
			filteredProjects = filteredProjects.stream()
					.sorted(Comparator.comparing(p -> p.getProjectName().toLowerCase()))
					.collect(Collectors.toCollection(ArrayList::new));
			break;
		case REVERSE_DEFAULT: // Reverse alphabetical order by project name
			filteredProjects = filteredProjects.stream()
					.sorted(Comparator.comparing((BTOProject p) -> p.getProjectName().toLowerCase()).reversed())
					.collect(Collectors.toCollection(ArrayList::new));
			break;
		case TWO_ROOM_PRICE_DESCENDING:
			filteredProjects = filteredProjects.stream()
					.sorted(Comparator.comparing(BTOProject::getTwoRoomPrice).reversed())
					.collect(Collectors.toCollection(ArrayList::new));
			break;
		case TWO_ROOM_PRICE_ASCENDING:
			filteredProjects = filteredProjects.stream().sorted(Comparator.comparing(BTOProject::getTwoRoomPrice))
					.collect(Collectors.toCollection(ArrayList::new));
			break;
		case THREE_ROOM_PRICE_DESCENDING:
			filteredProjects = filteredProjects.stream()
					.sorted(Comparator.comparing(BTOProject::getThreeRoomPrice).reversed())
					.collect(Collectors.toCollection(ArrayList::new));
			break;
		case THREE_ROOM_PRICE_ASCENDING:
			filteredProjects = filteredProjects.stream().sorted(Comparator.comparing(BTOProject::getThreeRoomPrice))
					.collect(Collectors.toCollection(ArrayList::new));
			break;
		case OPENING_DATE_ASCENDING:
			filteredProjects = filteredProjects.stream().sorted(Comparator.comparing(BTOProject::getOpeningDate))
					.collect(Collectors.toCollection(ArrayList::new));
			break;
		case OPENING_DATE_DESCENDING:
			filteredProjects = filteredProjects.stream()
					.sorted(Comparator.comparing(BTOProject::getOpeningDate).reversed())
					.collect(Collectors.toCollection(ArrayList::new));
			break;
		case CLOSING_DATE_ASCENDING:
			filteredProjects = filteredProjects.stream().sorted(Comparator.comparing(BTOProject::getClosingDate))
					.collect(Collectors.toCollection(ArrayList::new));
			break;
		case CLOSING_DATE_DESCENDING:
			filteredProjects = filteredProjects.stream()
					.sorted(Comparator.comparing(BTOProject::getClosingDate).reversed())
					.collect(Collectors.toCollection(ArrayList::new));
			break;
		case null:
			// No sort applied, keep the original list
			break;
		}

		switch (filter) {
		case DEFAULT:
			// No filter applied, keep the original list
			break;
		case TWO_ROOM:
			filteredProjects = filteredProjects.stream().filter(p -> p.getTwoRoomUnits() > 0)
					.collect(Collectors.toCollection(ArrayList::new));
			break;
		case THREE_ROOM:
			filteredProjects = filteredProjects.stream().filter(p -> p.getThreeRoomUnits() > 0)
					.collect(Collectors.toCollection(ArrayList::new));
			break;
		case NAME:
			filteredProjects = filteredProjects.stream()
					.filter(p -> p.getProjectName().toLowerCase().contains(filter.getKeyword().toLowerCase()))
					.collect(Collectors.toCollection(ArrayList::new));
			break;
		case NEIGHBORHOOD:
			filteredProjects = filteredProjects.stream()
					.filter(p -> p.getNeighborhood().toLowerCase().contains(filter.getKeyword().toLowerCase()))
					.collect(Collectors.toCollection(ArrayList::new));
			break;
		case null:
			// No filter applied, keep the original list
			break;
		}

		return filteredProjects;
	}

	// region Access Control
	/**
	 * Decide if the current active user should be permitted to do a certain task,
	 * based on access rights by type of user.
	 * 
	 * @param <U>          Type of User
	 * @param expectedType Provide the expected user class type here.
	 * @return true if active user is of given user type, hence permitted, false if
	 *         otherwise.
	 */
	public <U extends User> boolean isActiveUserPermitted(Class<U> expectedType) {
		if (activeUser == null)
			return false;

		return expectedType.isInstance(activeUser);
	}

	@SuppressWarnings("unchecked")
	public <U extends User> U getActiveUserForPermittedTask(Class<U> expectedType)
			throws InsufficientAccessRightsException {
		if (isActiveUserPermitted(expectedType))
			return (U) activeUser;
		else
			throw new InsufficientAccessRightsException();
	}
	// endregion

	public void addProject(BTOProject project) throws Exception {
		// always check if logged-in user is permitted for activity, else return;
		if (!isActiveUserPermitted(HDBManager.class))
			throw new InsufficientAccessRightsException();

		projects.add(project);
		saveChanges(CSVFileTypes.PROJECT_LIST);
//		project.retrieveConnectedUsers(officers);
		System.out.println("\nProject added successfully : ");
		System.out.println(project.toString());
	}

	public void deleteProject(BTOProject project) throws Exception {
		if (!isActiveUserPermitted(HDBManager.class))
			throw new InsufficientAccessRightsException();
		if (project.getManagerInCharge() != activeUser)
			throw new InsufficientAccessRightsException();
		if (projects.contains(project) == false)
			throw new Exception("Project not found in system.");

		projects.remove(project);
		saveChanges(CSVFileTypes.PROJECT_LIST);
	}

	public void addApplication(Application application) throws Exception {
		if (!(isActiveUserPermitted(Applicant.class) || isActiveUserPermitted(HDBOfficer.class)))
			throw new InsufficientAccessRightsException();
		applications.add(application);
		saveChanges(CSVFileTypes.APPLICATIONS_LIST);
	}

	public ArrayList<Application> getApplications() throws Exception {
		if (!(isActiveUserPermitted(HDBManager.class) || isActiveUserPermitted(Applicant.class)))
			throw new InsufficientAccessRightsException();

		if (activeUser instanceof HDBManager) {
			return applications.stream().filter(a -> a.getProject().getManagerInCharge() == activeUser)
					.collect(Collectors.toCollection(ArrayList::new));
		}

		if (activeUser instanceof Applicant) {
			return applications.stream().filter(a -> a.getApplicant() == activeUser)
					.collect(Collectors.toCollection(ArrayList::new));
		}

		return null;
	}
	/**
	 * Get withdrawal applications made by applicant.
	 * Applicant can only view his/her own withdrawal applications.
	 * Manager can view all withdrawal Applications of projects that he/her is in charge of.
	 * Officer is unable to access this method. 
	 * @return
	 * @throws Exception
	 */

	public ArrayList<Application> getWithdrawalApplications() throws Exception {
		if(!(isActiveUserPermitted(HDBManager.class) || isActiveUserPermitted(Applicant.class))) throw new InsufficientAccessRightsException();
		
		if(activeUser instanceof HDBManager) {
			ArrayList<Application> Withdrawn = getApplications();
			Withdrawn.stream().filter((a -> a.getWithdrawalStatus().equals(WithdrawalStatus.WITHDRAWN)))
					.collect(Collectors.toCollection(ArrayList::new));
		}
		if(activeUser instanceof Applicant) {
			ArrayList<Application> withdrawalApplications = applications.stream().filter(a -> a.getApplicant() == activeUser)
										.filter(a -> !a.getWithdrawalStatus().equals(WithdrawalStatus.NOT))
										.collect(Collectors.toCollection(ArrayList::new));
			if  (withdrawalApplications.isEmpty()) {
				System.out.println("No withdrawal request have been made");
				return null;
			}
			else {
				return withdrawalApplications;
			}
		}
		return null;
	}

	/**
	 * Checks if there is already a project with a similar name.
	 * 
	 * Names must not be the same, regardless of caoitalisation or spaces. For
	 * example, if a project of name: "Woodlands UrbanVille" exists, a new project
	 * name of "wOoDLanDs URBANVille" or "woodlandsurbanville" will not be allowed.
	 * 
	 * @param name            Name of new project to be checked against.
	 * @param excludedProject Pass a {@code BTOProject} if this project should not
	 *                        be checked against. Pass in {@code null} if otherwise.
	 * @return true if such a project name already exists, false if such a name is
	 *         considered new by system.
	 */
	public boolean doesProjectExist(String name, BTOProject excludedProject) {
		for (BTOProject project : projects) {
			if (excludedProject != null && project == excludedProject)
				continue;
			String regex = "\\s+";
			String projectNameWithoutSpaces = project.getProjectName().replaceAll(regex, "");
			String comparableName = name.replaceAll(regex, "");
			if (projectNameWithoutSpaces.equalsIgnoreCase(comparableName)) {
				return true;
			}
		}

		return false;
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
