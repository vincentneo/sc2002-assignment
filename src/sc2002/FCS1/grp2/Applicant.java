package sc2002.FCS1.grp2;

import java.util.HashSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Applicant<Project> extends User {
	private Project appliedProject;
	private boolean hasBookedFlat = false;
	private List<String> enquiries = new ArrayList<>();

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
	
	public List<Project> viewApplicableProjects(List<Project> allProjects){
		 List<Project> applicableProjects = new ArrayList<>();
	        for (Project project : allProjects) {
	            if (project.isVisible() && ((Object) project).isEligible(this)) {
	                applicableProjects.add(project);
	            }
	        }
	        return applicableProjects;
	}

	  public boolean applyForProject(Project project) {
	        if (appliedProject != null) {
	            System.out.println("You have already applied for a project.");
	            return false;
	        }
	        if (!project.isEligible(this)) {
	            System.out.println("You are not eligible for this project.");
	            return false;
	        }
	        appliedProject = project;
	        project.addApplicant(this);
	        System.out.println("Application successful.");
	        return true;
	    }
	  public Project getAppliedProject() {
	        return appliedProject;
	    }

	    public void withdrawApplication() {
	        if (appliedProject == null) {
	            System.out.println("No active application to withdraw.");
	            return;
	        }
	        appliedProject.removeApplicant(this);
	        appliedProject = null;
	        hasBookedFlat = false;
	        System.out.println("Application withdrawn successfully.");
	    }
	    public void bookFlat() {
	        if (appliedProject == null || appliedProject.getApplicationStatus(this) != ApplicationStatus.SUCCESSFUL) {
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
	}

