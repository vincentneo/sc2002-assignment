package sc2002.FCS1.grp2;

import java.util.Arrays;
import java.util.List;

public abstract class User {
	protected String name;
	protected String nric;
	protected int age;
	protected MaritalStatus maritalStatus;
	protected String password;
	
	public User(String line) {
		List<String> splitted = Arrays.asList(line.split(","));
		this.name = splitted.get(0);
		this.nric = splitted.get(1);
		this.age = Integer.parseInt(splitted.get(2));
		this.maritalStatus = MaritalStatus.fromString(splitted.get(3));
		this.password = splitted.get(4);
	}
	
	void print() {
		System.out.printf("%s %s %d %s %s \n", this.name, this.nric, this.age, this.maritalStatus.toString(), this.password);
	}
	
	String encode() {
		return String.format("%s,%s,%d,%s,%s", name, nric, age, maritalStatus.toString(), password);
	}
}

