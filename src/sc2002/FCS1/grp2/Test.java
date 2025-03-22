package sc2002.FCS1.grp2;

public class Test extends User {

	public Test(String line) {
		super(line);
		// TODO Auto-generated constructor stub
	}
	
	public void print() {
		System.out.printf("%s %s %d %s %s \n", this.name, this.nric, this.age, this.maritalStatus.toString(), this.password);
	}

}
