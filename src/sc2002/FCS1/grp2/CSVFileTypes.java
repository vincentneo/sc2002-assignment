package sc2002.FCS1.grp2;

public enum CSVFileTypes {
	APPLICANT_LIST("ApplicantList.csv"),
	MANAGER_LIST("ManagerList.csv"),
	OFFICER_LIST("OfficerList.csv"),
	PROJECT_LIST("ProjectList.csv");
	
	private String fileName;
	
	private CSVFileTypes(String fileName) {
		this.fileName = fileName;
	}

	public String getFileName() {
		return fileName;
	}
	
}
