package sc2002.FCS1.grp2;

public enum CSVFileTypes {
	APPLICANT_LIST("ApplicantList.csv"),
	MANAGER_LIST("ManagerList.csv"),
	OFFICER_LIST("OfficerList.csv"),
	PROJECT_LIST("ProjectList.csv"),
	ENQUIRIES_LIST("EnquiriesList.csv");
	
	private String fileName;
	
	private CSVFileTypes(String fileName) {
		this.fileName = fileName;
	}

	public String getFileName() {
		return fileName;
	}
	
	public String getHeader() {
		switch (this) {
		case APPLICANT_LIST, MANAGER_LIST, OFFICER_LIST:
			return "Name,NRIC,Age,Marital Status,Password";
		case PROJECT_LIST:
			return "Project Name,Neighborhood,Type 1,Number of units for Type 1,Selling price for Type 1,Type 2,Number of units for Type 2,Selling price for Type 2,Application opening date,Application closing date,Manager,Officer Slot,Officer,Public Visibility";
		case ENQUIRIES_LIST:
			return "ID,Applicant NRIC,Query,Query Submission Time,Responder NRIC,Response,Response Submission Time,Project Name";
		default:
			return null;
		}
	}
	
}
