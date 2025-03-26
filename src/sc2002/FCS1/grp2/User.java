package sc2002.FCS1.grp2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class User {
	private String name;
	private String nric;
	private int age;
	private MaritalStatus maritalStatus;
	private String password;
	
	public User(String line) {
		List<String> splitted = Arrays.asList(line.split(","));
		this.name = splitted.get(0);
		this.nric = splitted.get(1);
		this.age = Integer.parseInt(splitted.get(2));
		this.maritalStatus = MaritalStatus.fromString(splitted.get(3));
		this.password = splitted.get(4);
	}
	
	/**
	 * This getter returns the name of the user.
	 * @return name The name of the user.
	 */
	public String getName() {
		return name;
	}

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
	
	/**
	 * This method should prepare the list of tasks of which a user of such role can perform.
	 * It can be expected that the contents of the list will be presented to the user to let them know what they can do.
	 * Subclasses must add on to the list by the superclass, rather than creating a new list.
	 * @return ArrayList<String> List of tasks that user can do using our BTO system.
	 */
	ArrayList<String> getMenu() {
		ArrayList<String> list = new ArrayList<>();
		list.add("Change Password");
		return list;
	}
	
	void print() {
		System.out.printf("%s %s %d %s %s \n", this.name, this.nric, this.age, this.maritalStatus.toString(), this.password);
	}
	
	String encode() {
		return String.format("%s,%s,%d,%s,%s", name, nric, age, maritalStatus.toString(), password);
	}
}

