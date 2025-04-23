package sc2002.FCS1.grp2;

import java.util.ArrayList;
import java.util.List;

public class HDBOfficer extends Applicant {

	/**
     * Constructor for {@code CSVParser} class
     * This constructor constructs values based on CSV encoded rows representing this object.
     * @param cells
     */
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
		CHECK_PENDING_PROJECT,
		BOOK_APPLICATION,
		VIEW_MY_APPLICATION,
		WITHDRAW_APPLICATION;
		
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
			case BOOK_APPLICATION:
				return "Book Flats for Applicants";
			case VIEW_MY_APPLICATION:
				return "View My Applications (as Applicant)";
			case WITHDRAW_APPLICATION:
				return "Withdraw My Application (as Applicant)";
			default:
				return null;
			}
		}
		
		public static Menu[] allMenuOptions = Menu.values();
		
		/**
        * Get the menu value based on index. Each enum value starts from 0..<size of enum values.
        * @param o Index expected of the option
        * @return the menu enum value.
        */
		public static Menu fromOrdinal(int o) {
			if (o >= allMenuOptions.length) return null;
			return allMenuOptions[o];
		}
	}

}

