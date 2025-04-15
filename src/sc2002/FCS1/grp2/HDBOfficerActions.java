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
            case VIEW_PROJECTS:
                // TODO: filtering, access control etc etc
                BTOProject.display(system.getProjects(), false);
                break;
            case JOIN_PROJECT:
                joinProject(user); 
                break;
          //  case CHECK_APPLICATION_STATUS:
            //    checkApplicationStatus(user);
              //  break;
            default:
                System.out.println("Option not implemented.");
        }
    }

    /**
	 * Method which checks pre-req before letting HDB Officer to join a project.
	 * @param HDBOfficer object
	 */ 
	// Combined method for joining the project (including officer registration and manager approval)
    public static void joinProject(HDBOfficer user) {
    // Ensure system is properly initialized
    if (system == null) {
        System.out.println("Error: BTOManagementSystem is not initialized.");
        return;
    }

    // Display available projects
    System.out.println("Available Projects to Join:");
    ArrayList<BTOProject> projects = system.getProjects();
    for (int i = 0; i < projects.size(); i++) {
        BTOProject project = projects.get(i);
        System.out.println((i + 1) + ". " + project.getProjectName() + " - " + project.getNeighborhood());
    }

    // Officer selects a project
    Scanner scanner = system.getScanner();
    System.out.print("Select a project to join (enter number): ");
    int projectIndex = scanner.nextInt();

    if (projectIndex < 1 || projectIndex > projects.size()) {
        System.out.println("Invalid selection. Please try again.");
        return;
    }

    BTOProject selectedProject = projects.get(projectIndex - 1);

    // Check if the officer is already registered for this project
    if (user.isOfficerForProject(selectedProject)) {
        System.out.printf("You are already an officer for the project %s.\n", selectedProject.getProjectName());
        return;
    }

    // Check if the officer has already applied as an applicant
    if (user.getAppliedProject() != null && user.getAppliedProject().equals(selectedProject)) {
        System.out.printf("You cannot join the project %s as an officer because you have already applied for it as an applicant.\n", selectedProject.getProjectName());
        return;
    }

    // Check if the project has room for more officers
    if (selectedProject.getOfficers().size() >= selectedProject.getTotalOfficerSlots()) {
        System.out.println("The project already has the maximum number of officers assigned.");
        return;
    }

    // Ensure the officer is not applying for a project during the application period of a project they are already handling
    for (BTOProject currentProject : system.getProjects()) {
        // Skip the check if this is the selected project itself
        if (currentProject.equals(selectedProject)) continue;

        // Get the officer's assigned projects' start and end dates
        LocalDate currentProjectOpenDate = currentProject.getOpeningDate();
        LocalDate currentProjectCloseDate = currentProject.getClosingDate();
        LocalDate selectedProjectOpenDate = selectedProject.getOpeningDate();
        LocalDate selectedProjectCloseDate = selectedProject.getClosingDate();

        // Check if the dates overlap: If selected project overlaps with any other project the officer is handling
        if ((selectedProjectOpenDate.isBefore(currentProjectCloseDate) && selectedProjectCloseDate.isAfter(currentProjectOpenDate))) {
            System.out.println("You cannot apply for this project as its application period overlaps with another project you are handling.");
            return;
        }
    }

    // Create an enquiry for manager's approval
    HDBManager manager = selectedProject.getManagerInCharge();
    if (manager == null) {
        System.out.println("You cannot be registered as an officer for this project as the logged-in user is not the manager of the project.");
        return;
    }

    // Create an enquiry asking the manager for approval
    Message question = new Message(user, "Requesting approval to join as officer for project " + selectedProject.getProjectName());
    Enquiry enquiry = new Enquiry(question, selectedProject);

    // Add the enquiry to the system
    try {
        system.getActiveUser().getEnquiriesSystem().addEnquiry(enquiry);
    } catch (Exception e) {
        System.out.println("Error adding enquiry: " + e.getMessage());
        return;
    }

    // Mark the officer's status as PENDING for this project
    user.setApplicationStatus(ApplicationStatus.PENDING);  // Set application status to pending

    System.out.println("Enquiry sent to manager for approval.");
    System.out.println("Your application status is now PENDING. Please wait for manager's response...");
}

	

    // Method to check the officer's application status/
	//Trying to get this to work but kinda lost for now. feel free to take over...
   // private static void checkApplicationStatus(HDBOfficer user) {
        // Retrieve the officer's pending application status
   //     ApplicationStatus status = user.getApplicationStatus();

   //     if (status == null) {
     //       System.out.println("You have no pending applications.");
       //     return;
     //   }

        // Display the application status
 //       System.out.println("Application Status: " + status);
 //       if (status == ApplicationStatus.PENDING) {
 //           System.out.println("Your application is still pending approval.");
 //       } else if (status == ApplicationStatus.SUCCESSFUL) {
 //           System.out.println("Your application was approved! You are now registered as an officer for project: " + user.getAppliedProject().getProjectName());
 //       } else {
 //           System.out.println("Your application was rejected.");
 //       }
  //  }
	
}
