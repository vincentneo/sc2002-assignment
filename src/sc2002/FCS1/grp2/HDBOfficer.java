package sc2002.FCS1.grp2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HDBOfficer extends Applicant {

    // Field to store enquiries per project application
    private Map<BTOProject, Enquiry> applicationEnquiries = new HashMap<>();
    
    public HDBOfficer(List<CSVCell> cells) {
        super(cells);
    }

    public boolean isOfficerForProject(BTOProject project) {
        return project.getOfficers().contains(this);
    }

	public Map<BTOProject, Enquiry> getApplicationEnquiries() {
		return applicationEnquiries;
	}
	
	public void viewProjectDetails(BTOProject project) {
        if (!isOfficerForProject(project)) {
            System.out.println("You are not an officer for this project and cannot view its details.");
            return;
        }
        // Display project info (including invisible ones)
        System.out.println(project.toString());
    }

	@Override
	String getReadableTypeName() {
		return "HDB Officer";
	}
	
	public CSVFileTypes sourceFileType() {
		return CSVFileTypes.OFFICER_LIST;
	}
	
    // TODO: Similar to HDBManager implementation, list each scoped feature by creating an enum for it
    @Override
	ArrayList<String> getMenu() {
		return super.getMenuWithScopedOptions(Menu.allMenuOptions);
	}
    
	/**
	 * Possible menu options for a HDB Manager role
	 * 
	 * Each option listed here are options that only a HDB Manager can interact with. 
	 */
	enum Menu implements ScopedOption {
		VIEW_PROJECTS,
		JOIN_PROJECT,
		CHECK_APPLICATION_STATUS;
		
		public String getOptionName() {
			switch (this) {
			case VIEW_PROJECTS: 
				return "View Projects";
			case JOIN_PROJECT:
				return "Join Project";
			case CHECK_APPLICATION_STATUS:
				return "Check Application Status";
			default:
				return null;
			}
		}
		
		public static Menu[] allMenuOptions = Menu.values();
		
		public static Menu fromOrdinal(int o) {
			if (o >= allMenuOptions.length) return null;
			return allMenuOptions[o];
		}
	}

}

