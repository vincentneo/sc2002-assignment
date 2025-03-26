package sc2002.FCS1.grp2;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Applicant extends User {

	public Applicant(String line) {
		super(line);
		// TODO Auto-generated constructor stub
	}

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
	boolean canApplyProject() {
		return true;
	}
	
	@Override
	ArrayList<String> getMenu() {
		ArrayList<String> list = super.getMenu();
		list.add("View Projects");
		return list;
	}
	
	@Override
	String getReadableTypeName() {
		return "Applicant";
	}
}
