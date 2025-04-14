package sc2002.FCS1.grp2;

import java.util.ArrayList;
import java.util.List;

import sc2002.FCS1.grp2.HDBManager.Menu;

public class HDBOfficer extends Applicant {

	public HDBOfficer(List<CSVCell> cells) {
		super(cells);
		// TODO Auto-generated constructor stub
	}
	public boolean isOfficerForProject(BTOProject project)
	{
		return project.getOfficers().contains(getNric());
	}

	public void registerAsOfficer(BTOProject project, HDBManager manager) {
        // 1. Ensure we actually have a manager to approve
        if (manager == null) {
            System.out.println("Error: A valid HDB Manager is required for approval. Officer not assigned.");
            return;
        }

        // 2. Ask the manager for approval. approveOffcierAssignment yet to be done. Passing entire offcier obj via "this".
        // TODO: fix missing 
//        boolean approved = manager.approveOfficerAssignment(this, project); 

		// need to decide if need to check if manager is acutally manager for the specific project
	
//        if (!approved) {
//            System.out.printf("Manager %s did not approve assignment for Officer %s.\n",
//                              manager.getName(), this.getName());
//            return;
//        }

        // 3. If approved, proceed to register
        if (isOfficerForProject(project)) {
            System.out.printf("Officer %s is already assigned to %s.\n",
                              this.getName(), project.getProjectName());
            return;
        }

        // Attempt to add the officer’s name to the project’s officer list
        // TODO addOfficer does not exist
//        project.addOfficer(this.getName());
        System.out.println("Registered as HDB Officer for project: " + project.getProjectName());
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
		VIEW_PROJECTS;
		
		
		public String getOptionName() {
			switch (this) {
			case VIEW_PROJECTS: 
				return "View Projects";
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

