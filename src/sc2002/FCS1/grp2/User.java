package sc2002.FCS1.grp2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import java.lang.String;

/**
 * This class forms as the base of all user types.
 * All of the above user types must extend this class, as it contains essential fields and common method declarations.
 * <br/>
 * User types includes:
 * <ul>
 * 	<li>Applicant</li>
 * 	<li>HDB Officer</li>
 * 	<li>HDB Manager</li>
 * </ul>
 *  
 * @author Vincent Neo
 * 
 */
public abstract class User extends CSVDecodable implements CSVEncodable {
	/**
	 * User's name.
	 */
	private String name;
	
	/**
	 * User's NRIC number.
	 */
	private String nric;
	
	/**
	 * User's age.
	 */
	private int age;
	
	/**
	 * User's martial status.
	 */
	private MaritalStatus maritalStatus;
	
	/**
	 * User's password.
	 */
	private String password;

	/**
	 * User's listing sort type.
	 * This is used to sort the listings based on the user's preference.
	 */
	private ListingSort listingSort;

	/**
	 * User's listing filter type.
	 * This is used to filter the listings based on the user's preference.
	 */
	private ListingFilter listingFilter;

	private EnquiriesSystem enquiriesSystem;
	
	/**
	 * Options that are common to all user types
	 */
	private static String[] commonMenuOptions = {
			"Change Password",
	};
	
	public static int getCommonMenuOptions() {
		return commonMenuOptions.length;
	}
	
	/**
	 * Constructor that is intended for use by (@code CSVParser} class only.
	 * @param cells Represents a row of a CSV spreadsheet.
	 */
	public User(List<CSVCell> cells) {
		super(cells);
		this.name = cells.get(0).getValue();
		this.nric = cells.get(1).getValue();
		this.age = cells.get(2).getIntValue();
		this.maritalStatus = MaritalStatus.fromString(cells.get(3).getValue());
		this.password = cells.get(4).getValue();
		this.listingSort = ListingSort.DEFAULT;
		this.listingFilter = ListingFilter.DEFAULT;
	}
	
	/**
	 * Getter method returns the name of the user.
	 * @return Name of this user object.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Getter method for retrieving NRIC of a user.
	 * @return NRIC number of this user object.
	 */
	String getNric() {
		return nric;
	}
	
	/**
	 * Getter method for retrieving age of a user.
	 * @return Age of the user associated in this object.
	 */
	int getAge() {
		return age;
	}
	
	/**
	 * Getter method for retrieving marital status of a user.
	 * @return Marital status of the user associated in this object.
	 */
	MaritalStatus getMaritalStatus() {
		return maritalStatus;
	}

	/**
	 * Getter method for retrieving listing sort of a user.
	 * @return Listing sort of the user associated in this object.
	 */
	public ListingSort getListingSort() {
		return listingSort;
	}

	/**
	 * Getter method for retrieving listing filter of a user.
	 * @return Listing filter of the user associated in this object.
	 */	
	public ListingFilter getListingFilter() {
		return listingFilter;
	}

	/**
	 * Check if a user-provided password is correct for user.
	 * 
	 * This method is for password validation purposes when going through a login flow.
	 * The password is not publicly accessible due to access control limits. 
	 * Use this method instead to tell if a user should be allowed entry or not.
	 * 
	 * @param password - The password to be tested.
	 * @return Whether if password is correct or not.
	 */
	boolean checkPassword(String password) {
		return this.password.equals(password);
	}

	/**
	 * For setting a new password.
	 * 
	 * This method is intended to be used for users to set a new password.
	 * 
	 * @param password - The new password to be saved.
	 */
	void setPassword(String password) {
		this.password = password;
	}

	/**
	 * For setting a new listing filter.
	 * 
	 * This method is intended to be used for users to set a new listing filter.
	 * 
	 * @param listingFilter - The new listing filter to be saved.
	 */
	public void setListingSort(ListingSort listingFilter) {
		this.listingSort = listingFilter;
	}

	/**
	 * For setting a new listing filter.
	 * 
	 * This method is intended to be used for users to set a new listing filter.
	 * 
	 * @param listingFilter - The new listing filter to be saved.
	 */
	public void setListingFilter(ListingFilter listingFilter) {
		this.listingFilter = listingFilter;
	}
	
	/**
	 * For checking on whether a user should be allowed to apply for a project, based on eligibility criteria.
	 * @return if user's eligibility criteria matches, returns {@code true}, otherwise {@code false}.
	 */
	abstract boolean canApplyProject();
	
	/**
	 * User-readable name for the type of user.
	 * @return the type of the user, such as "Applicant".
	 */
	abstract String getReadableTypeName();
	
	/**
	 * This method will prepare the list of tasks of which a user can perform.
	 * It can be expected that the contents of the list will be presented to the user to let them know what they can do.
	 * Subclasses must add on to the list by the superclass, rather than creating a new list.
	 * @author Vincent Neo
	 * @return List of tasks that user can do using our BTO system.
	 */
	abstract ArrayList<String> getMenu();
	
	/**
	 * This method facilitates the generation of a menu that includes options specific to a certain user type.
	 * Child classes should call this method when overriding getMenu().
	 * 
	 * @param options Scoped options that are only accessible depending on the user type.
	 * @return List of tasks that user can do using our BTO system.
	 */
	ArrayList<String> getMenuWithScopedOptions(ScopedOption[] options) {
		ArrayList<String> list = new ArrayList<>(Arrays.asList(commonMenuOptions));
		for (ScopedOption option : options) {
			list.add(option.getOptionName());
		}
		return list;
	}
	
	/**
	 * Prints all fields of this user object, in a concise manner.
	 */
	void print() {
		System.out.printf("%s %s %d %s %s \n", this.name, this.nric, this.age, this.maritalStatus.toString(), this.password);
	}
	
	/**
	 * Prepares text for CSV encoding purposes.
	 * @return A string that is intended to represent a spreadsheet row, to be written in a CSV file.
	 */
	public String encode() {
		return String.format("%s,%s,%d,%s,%s", name, nric, age, maritalStatus.toString(), password);
	}
	
	@Override
	public String toString() {
		return "User [name=" + name + ", nric=" + nric + ", age=" + age + ", maritalStatus=" + maritalStatus
				+ ", password=" + password + "]";
	}
	
	@Override
	public boolean equals(Object object) {
		if (object == this) {
			return true;
		}
		
		if (!(object instanceof User)) {
			return false;
		}
		
		return ((User) object).getNric().equals(this.getNric());
	}
	
	public boolean nameEquals(User user) {
		return user.getName().equals(this.getName());
	}
	
	public EnquiriesSystem getEnquiriesSystem() {
		return enquiriesSystem;
	}

	public void setEnquiriesSystem(EnquiriesSystem enquiriesSystem) {
		if (enquiriesSystem == null) {
			this.enquiriesSystem.setDelegate(null);
		}
		this.enquiriesSystem = enquiriesSystem;
	}
}

