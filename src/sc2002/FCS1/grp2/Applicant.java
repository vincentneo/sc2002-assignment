package sc2002.FCS1.grp2;

import java.util.*;

public class Applicant extends User {

	private BTOProject appliedProject;
	private boolean hasBookedFlat;
	private Set<String> enquiries = new HashSet<>();
	private ApplicationStatus applicationStatus;
	private Set<BTOProject> previouslyAppliedProjects = new HashSet<>();

	public Applicant(List<CSVCell> cells) {
		super(cells);
	}
	/**
	 * This method gives the eligible flat types available to the applicant
	 * @return Eligible Flat Types
	 */
	Set<FlatType> getEligibleFlatTypes() {
		Set<FlatType> set = new HashSet<>();
		int age = getAge();
		switch (getMaritalStatus()) {
		case SINGLE:
			if (age >= 35) {
				set.add(FlatType.TWO_ROOM);
			}
			break;
		case MARRIED:
			if (age >= 21) {
				set.add(FlatType.TWO_ROOM);
				set.add(FlatType.THREE_ROOM);
			}
			break;
		}
		return set;
	}

	/**
     * Views projects applicable to the applicant
     * @param allProjects All available projects
     * @return List of applicable projects
     */
    public List<BTOProject> viewApplicableProjects(List<BTOProject> allProjects) {
        List<BTOProject> applicableProjects = new ArrayList<>();
        Set<FlatType> eligibleTypes = getEligibleFlatTypes();
        
        for(BTOProject project : allProjects) {
            if(project.isVisible() && !Collections.disjoint(project.getAvailableFlatTypes(), eligibleTypes)) {
                applicableProjects.add(project);
            }
        }
        return applicableProjects;
    }
    /**
    * Applies for a BTO project
    * @param project Project to apply for
    * @param flatType Desired flat type
    * @return true if application successful
    */
   public boolean applyForProject(BTOProject project, FlatType flatType) {
       if(appliedProject != null) {
           System.out.println("Error: You already have an active application!");
           return false;
       }
       
       if(!getEligibleFlatTypes().contains(flatType)) {
           System.out.println("Error: You're not eligible for this flat type!");
           return false;
       }
       
       if(!project.getAvailableFlatTypes().contains(flatType)) {
           System.out.println("Error: Selected flat type not available!");
           return false;
       }
       
       appliedProject = project;
       applicationStatus = ApplicationStatus.PENDING;
       System.out.println("Successfully applied for " + project.getProjectName());
       return true;
   }

   /**
    * Views currently applied project details
    */
   public void viewAppliedProject() {
       if(appliedProject == null) {
           System.out.println("No active application");
           return;
       }
       
       System.out.println("\n=== Applied Project Details ===");
       System.out.println("Project: " + appliedProject.getProjectName());
       System.out.println("Location: " + appliedProject.getNeighborhood());
       System.out.println("Status: " + applicationStatus);
       System.out.println("Booked: " + (hasBookedFlat ? "Yes" : "No"));
   }
	
   /**
    * Withdraws current application
    */
   public void withdrawApplication() {
       if(appliedProject == null) {
           System.out.println("No active application to withdraw");
           return;
       }
       
       previouslyAppliedProjects.add(appliedProject);
       appliedProject = null;
       applicationStatus = null;
       hasBookedFlat = false;
       System.out.println("Application withdrawn successfully");
   }

   /**
    * Books a flat via HDB Officer
    * @throws IllegalStateException if conditions not met
    */
   public void bookFlat() throws IllegalStateException {
       if(appliedProject == null) {
           throw new IllegalStateException("No active application");
       }
       if(applicationStatus != ApplicationStatus.SUCCESSFUL) {
           throw new IllegalStateException("Application not successful");
       }
       if(hasBookedFlat) {
           throw new IllegalStateException("Already booked a flat");
       }
       
       hasBookedFlat = true;
       System.out.println("Flat booked successfully via HDB Officer");
   }

// Enquiry Management Methods
   
   /**
    * Submits a new enquiry
    * @param enquiry The enquiry text
    */
   public void submitEnquiry(String enquiry) {
       if(enquiry == null || enquiry.trim().isEmpty()) {
           System.out.println("Error: Enquiry cannot be empty");
           return;
       }
       enquiries.add(enquiry);
       System.out.println("Enquiry submitted successfully");
   }
   
   /**
    * Views all enquiries
    * @return List of enquiries
    */
   public List<String> viewEnquiries() {
       if(enquiries.isEmpty()) {
           System.out.println("No enquiries found");
       }
       return new ArrayList<>(enquiries);
   }
   
   /**
    * Edits an existing enquiry
    * @param index Index of enquiry to edit
    * @param newEnquiry New enquiry text
    * @throws IndexOutOfBoundsException if invalid index
    */
   public void editEnquiry(int index, String newEnquiry) throws IndexOutOfBoundsException {
       if(index < 0 || index >= enquiries.size()) {
           throw new IndexOutOfBoundsException("Invalid enquiry index");
       }
       if(newEnquiry == null || newEnquiry.trim().isEmpty()) {
           System.out.println("Error: Enquiry cannot be empty");
           return;
       }
       enquiries.set(index, newEnquiry);
       System.out.println("Enquiry updated successfully");
   }
   
   /**
    * Deletes an enquiry
    * @param index Index of enquiry to delete
    * @throws IndexOutOfBoundsException if invalid index
    */
   public void deleteEnquiry(int index) throws IndexOutOfBoundsException {
       if(index < 0 || index >= enquiries.size()) {
           throw new IndexOutOfBoundsException("Invalid enquiry index");
       }
       enquiries.remove(index);
       System.out.println("Enquiry deleted successfully");
   }
   
// Getters
   public BTOProject getAppliedProject() { return appliedProject; }
   public ApplicationStatus getApplicationStatus() { return applicationStatus; }
   public boolean hasBookedFlat() { return hasBookedFlat; }
   public Set<BTOProject> getPreviouslyAppliedProjects() { 
       return new HashSet<>(previouslyAppliedProjects); 
   }

   @Override
   boolean canApplyProject() {
       return appliedProject == null;
   }

   @Override
   ArrayList<String> getMenu() {
       ArrayList<String> menu = super.getMenuWithScopedOptions(Menu.allMenuOptions);
       
       // Add dynamic options based on application state
       if(appliedProject != null) {
           menu.add("View Applied Project");
           menu.add("Withdraw Application");
           if(applicationStatus == ApplicationStatus.SUCCESSFUL && !hasBookedFlat) {
               menu.add("Book Flat");
           }
       }
       return menu;
   }

   @Override
   public CSVFileTypes sourceFileType() {
       return CSVFileTypes.APPLICANT_LIST;
   }

   @Override
   String getReadableTypeName() {
       return "Applicant";
   }

	
	
   /**
    * Applicant-specific menu options
    */
   enum Menu implements ScopedOption {
       VIEW_PROJECTS("View All Projects"),
       VIEW_APPLICABLE_PROJECTS("View Applicable Projects"),
       APPLY_PROJECT("Apply for Project"),
       VIEW_ENQUIRIES("View My Enquiries"),
       SEND_ENQUIRY("Send Enquiry");
       
       private final String optionName;
       
       Menu(String optionName) {
           this.optionName = optionName;
       }
       
       @Override
       public String getOptionName() {
           return optionName;
       }
       
       public static Menu[] allMenuOptions = Menu.values();
       
       public static Menu fromOrdinal(int o) {
           if(o >= allMenuOptions.length) return null;
           return allMenuOptions[o];
       }
   }
}
