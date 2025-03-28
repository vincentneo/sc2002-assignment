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
	 * Constructor that is intended for use by (@code CSVParser} class only.
	 * @param cells Represents a row of a CSV spreadsheet.
	 */
	public User(ArrayList<CSVCell> cells) {
		super(cells);
		this.name = cells.get(0).getValue();
		this.nric = cells.get(1).getValue();
		this.age = cells.get(2).getIntValue();
		this.maritalStatus = MaritalStatus.fromString(cells.get(3).getValue());
		this.password = cells.get(4).getValue();
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
	ArrayList<String> getMenu() {
		ArrayList<String> list = new ArrayList<>();
		list.add("Change Password");
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
}

