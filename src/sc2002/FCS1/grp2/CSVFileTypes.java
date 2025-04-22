package sc2002.FCS1.grp2;

/**
 * Represents a CSV File Type that can be encodable.
 * @author Vincent Neo
 */
public enum CSVFileTypes {
	/**
	 * Represents a CSV file that contains a list of {@code Applicant} objects, when decoded.
	 */
	APPLICANT_LIST("ApplicantList.csv"),
	/**
	 * Represents a CSV file that contains a list of {@code HDBManager} objects, when decoded.
	 */
	MANAGER_LIST("ManagerList.csv"),
	/**
	 * Represents a CSV file that contains a list of {@code HDBOfficer} objects, when decoded.
	 */
	OFFICER_LIST("OfficerList.csv"),
	/**
	 * Represents a CSV file that contains a list of {@code BTOProject} objects, when decoded.
	 */
	PROJECT_LIST("ProjectList.csv"),
	/**
	 * Represents a CSV file that contains a list of {@code Enquiry} objects, when decoded.
	 */
	ENQUIRIES_LIST("EnquiriesList.csv"),
	/**
	 * Represents a CSV file that contains a list of {@code Application} objects, when decoded.
	 */
	APPLICATIONS_LIST("ApplicationsList.csv");
	
	/** 
	 * CSV filename of that this enum value represents.
	 */
	private String fileName;
	
	/**
	 * Enum constructor
	 * @param fileName File name used for constructing this value
	 */
	private CSVFileTypes(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * Get file name of this CSV file. (inclusive of file extension .csv)
	 * @return File name of respective CSV file.
	 */
	public String getFileName() {
		return fileName;
	}
	
	/**
	 * Provides the header of a CSV file
	 * 
	 * This ensures that the generated or updated CSV file retains its header,
	 * such that it is still readable in a spreadsheets application.
	 * @return A string that represents the first row (i.e. header) of the respective CSV file type.
	 */
	public String getHeader() {
		switch (this) {
		case APPLICANT_LIST, MANAGER_LIST, OFFICER_LIST:
			return "Name,NRIC,Age,Marital Status,Password";
		case PROJECT_LIST:
			return "Project Name,Neighborhood,Type 1,Number of units for Type 1,Selling price for Type 1,Type 2,Number of units for Type 2,Selling price for Type 2,Application opening date,Application closing date,Manager,Officer Slot,Officer,Public Visibility,Pending Officers";
		case ENQUIRIES_LIST:
			return "ID,Applicant NRIC,Query,Query Submission Time,Responder NRIC,Response,Response Submission Time,Project Name";
		case APPLICATIONS_LIST:
			return "Project Name,Flat Type,Status,Applicant NRIC,Is Withdrawn?";
		default:
			return null;
		}
	}
	
}
