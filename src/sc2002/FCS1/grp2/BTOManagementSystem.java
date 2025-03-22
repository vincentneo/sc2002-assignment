package sc2002.FCS1.grp2;

import java.util.ArrayList;

public class BTOManagementSystem {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		CSVParser parser = new CSVParser();
		
		ArrayList<User> applicants = parser.parseApplicants();
		
		for (User applicant : applicants) {
			applicant.print();
		}
	}

}
