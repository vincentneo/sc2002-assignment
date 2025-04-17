package sc2002.FCS1.grp2;

import java.util.*;

public class Applicant extends User {

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

	// TODO: Similar to HDBManager implementation, list each scoped feature by
	// creating an enum for it
	@Override
	ArrayList<String> getMenu() {
		return super.getMenuWithScopedOptions(Menu.allMenuOptions);
	}

	@Override
	public CSVFileTypes sourceFileType() {
		// TODO Auto-generated method stub
		return CSVFileTypes.APPLICANT_LIST;
	}

	@Override
	String getReadableTypeName() {
		// TODO Auto-generated method stub
		return "Applicant";
	}
	
	
	// TODO: Change this
	/**
	 * Possible menu options for a applicant role
	 * 
	 * Each option listed here are options that only a HDB Manager can interact with. 
	 */
	enum Menu implements ScopedOption {
		VIEW_PROJECTS,
		VIEW_ENQUIRIES,
		SEND_ENQUIRY;
		
		public String getOptionName() {
			switch (this) {
			case VIEW_PROJECTS: 
				return "View Projects";
			case VIEW_ENQUIRIES:
				return "View My Enquiries";
			case SEND_ENQUIRY: 
				return "Submit New Enquiry";
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
