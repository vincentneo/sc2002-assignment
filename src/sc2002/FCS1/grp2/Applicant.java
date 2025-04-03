package sc2002.FCS1.grp2;

import java.util.*;

public class Applicant extends User {
	
	private BTOProject appliedProject;
	private boolean hasBookedFlat;
	private Set<String> enquiries = new HashSet<>();
    private ApplicationStatus applicationStatus;
    private Set<BTOProject> previouslyAppliedProjects = new HashSet<>(); 

	public Applicant(ArrayList<CSVCell> cells) {
		super(cells);
	}

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
	
	public Set<BTOProject> viewEligibleProjects(Set<BTOProject> allProjects){
		 Set<BTOProject> applicableProjects = new HashSet<>();
	        for (BTOProject project : allProjects) {
	            if (project.isVisible() && isEligible(BTOProject) {
	                applicableProjects.add(BTOProject);
	            }
	        }
	        return applicableProjects;
	}

	  public boolean applyForProject(BTOProject project, FlatType flattype) {
	        if (appliedProject != null) {
	            System.out.println("You have already applied for a project.");
	            return false;
	        }
	        if (isEligible(BTOProject) {
	            System.out.println("You are not eligible for this project.");
	            return false;
	        }
	        appliedProject = project;
	        appliedProject.add(project);
	        System.out.println("Application successful.");
	        return true;
	    }
	  public BTOProject getAppliedProject() {
	        return appliedProject;
	    }

	    public void withdrawApplication() {
	        if (appliedProject == null) {
	            System.out.println("No active application to withdraw.");
	            return;
	        }
	        appliedProject.remove(appliedProject);
	        appliedProject = null;
	        hasBookedFlat = false;
	        System.out.println("Application withdrawn successfully.");
	    }
	    public void bookFlat() {
	        if (appliedProject == null || applicationStatus != ApplicationStatus.SUCCESSFUL) {
	            throw new IllegalStateException("You cannot book a flat unless your application is successful.");
	        }
	        if (hasBookedFlat) {
	            throw new IllegalStateException("You have already booked a flat.");
	        }
	        hasBookedFlat = true;
	        System.out.println("Flat booked successfully via HDB Officer.");
	    }

	    public void submitEnquiry(String enquiry) {
	        enquiries.add(enquiry);
	    }

	    public List<String> viewEnquiries() {
	        return enquiries;
	    }

	    public void editEnquiry(int index, String newEnquiry) {
	        if (index < 0 || index >= enquiries.size()) {
	            throw new IndexOutOfBoundsException("Invalid enquiry index.");
	        }
	        enquiries.set(index, newEnquiry);
	    }

	    public void deleteEnquiry(int index) {
	        if (index < 0 || index >= enquiries.size()) {
	            throw new IndexOutOfBoundsException("Invalid enquiry index.");
	        }
	        enquiries.remove(index);
	    }

	    @Override
	    boolean canApplyProject() {
	        return appliedProject == null;
	    }
	    
	    // TODO: Similar to HDBManager implementation, list each scoped feature by creating an enum for it
	    @Override
	    ArrayList<String> getMenu() {
	    	return new ArrayList<>();
	    }
		@Override
		public CSVFileTypes sourceFileType() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		String getReadableTypeName() {
			// TODO Auto-generated method stub
			return null;
		}
	}

