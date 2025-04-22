package sc2002.FCS1.grp2;

import java.util.ArrayList;
import java.util.List;

public class HDBOfficer extends Applicant {

    public HDBOfficer(List<CSVCell> cells) {
        super(cells);
    }

    public boolean isOfficerForProject(BTOProject project) {
        return project.getOfficers().contains(this);
    }

	@Override
	String getReadableTypeName() {
		return "HDB Officer";
	}
	
	public CSVFileTypes sourceFileType() {
		return CSVFileTypes.OFFICER_LIST;
	}
	
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
		ENTER_ENQUIRIES_SYSTEM,
		CHECK_PENDING_PROJECT;
		
		public String getOptionName() {
			switch (this) {
			case VIEW_PROJECTS: 
				return "View Projects (as Applicant)";
			case JOIN_PROJECT:
				return "Join Project";
			case ENTER_ENQUIRIES_SYSTEM:
				return "Enter Enquiries System";
			case CHECK_PENDING_PROJECT:
				return "Check Officer Project Application Status";
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

