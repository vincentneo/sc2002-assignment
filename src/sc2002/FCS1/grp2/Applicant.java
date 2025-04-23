package sc2002.FCS1.grp2;

import java.util.*;

/**
 * Represents an Applicant user.
 */
public class Applicant extends User {

    /**
     * Constructor for {@code CSVParser} class
     * This constructor constructs values based on CSV encoded rows representing this object.
     * @param cells
     */
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

   @Override
   ArrayList<String> getMenu() {
       return super.getMenuWithScopedOptions(Menu.allMenuOptions);
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
       VIEW_PROJECTS("View Available Projects"),
       VIEW_ENQUIRIES("Enter Enquiries System"),
       VIEW_APPLICATIONS("View My Applications"),
       WITHDRAW_APPLICATION("Withdraw My Application");
       
       private final String optionName;
       
       Menu(String optionName) {
           this.optionName = optionName;
       }
       
       @Override
       public String getOptionName() {
           return optionName;
       }
       
       public static Menu[] allMenuOptions = Menu.values();
       
       /**
        * Get the menu value based on index. Each enum value starts from 0..size of enum values.
        * @param o Index expected of the option
        * @return the menu enum value.
        */
       public static Menu fromOrdinal(int o) {
           if(o >= allMenuOptions.length) return null;
           return allMenuOptions[o];
       }
   }

	@Override
	boolean canApplyProject() {
		return !getEligibleFlatTypes().isEmpty();
	}
}
