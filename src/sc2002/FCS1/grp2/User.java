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
public abstract class User extends CSVDecodable {
	private String name;
	private String nric;
	private int age;
	private MaritalStatus maritalStatus;
	private String password;
	
	public User(ArrayList<CSVCell> cells) {
		super(cells);
		this.name = cells.get(0).getValue();
		this.nric = cells.get(1).getValue();
		this.age = cells.get(2).getIntValue();
		this.maritalStatus = MaritalStatus.fromString(cells.get(3).getValue());
		this.password = cells.get(4).getValue();
	}
	
	/**
	 * This getter returns the name of the user.
	 * @return name The name of the user.
	 */
	public String getName() {
		return name;
	}

	/**
	 * This setter sets the name of the user.
	 * @param name The name of the user.
	 */
	void setName(String name) {
		this.name = name;
	}

	String getNric() {
		return nric;
	}

	void setNric(String nric) {
		this.nric = nric;
	}

	int getAge() {
		return age;
	}

	void setAge(int age) {
		this.age = age;
	}

	MaritalStatus getMaritalStatus() {
		return maritalStatus;
	}

	void setMaritalStatus(MaritalStatus maritalStatus) {
		this.maritalStatus = maritalStatus;
	}
	
	boolean checkPassword(String password) {
		return this.password.equals(password);
	}

	void setPassword(String password) {
		this.password = password;
	}
	
	abstract boolean canApplyProject();
	
	abstract String getReadableTypeName();
	
	/**
	 * This method should prepare the list of tasks of which a user of such role can perform.
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
	
	//
	void print() {
		System.out.printf("%s %s %d %s %s \n", this.name, this.nric, this.age, this.maritalStatus.toString(), this.password);
	}
	
	String encode() {
		return String.format("%s,%s,%d,%s,%s", name, nric, age, maritalStatus.toString(), password);
	}
	
	@Override
	public String toString() {
		return "User [name=" + name + ", nric=" + nric + ", age=" + age + ", maritalStatus=" + maritalStatus
				+ ", password=" + password + "]";
	}
}

