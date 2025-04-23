package sc2002.FCS1.grp2;

import java.time.LocalDateTime;
import java.util.List;

import sc2002.FCS1.grp2.builders.DisplayMenu;
import sc2002.FCS1.grp2.builders.Style;
import sc2002.FCS1.grp2.helpers.Utilities;

/**
 * This class represents a BTO application.
 */
public class Application extends CSVDecodable implements CSVEncodable {
	/**
	 * The project which this application is applied to.
	 */
    private BTOProject project;

	/**
	 * The flat type that is applied.
	 */
    private FlatType flatType;

	/** 
	 * The status of this application.
	 */
    private ApplicationStatus status;

	/**
	 * Whether if this application shall be invalidated due to withdrawal.
	 */
    private WithdrawalStatus withdrawalStatus;

	/** The applicant */
    private Applicant applicant;

	/** The last updated date and time */
	private LocalDateTime lastUpdated;
    
	//region temporary values

	/** Temporary value for CSV parse and link */
    private String projectName = null;
	/** Temporary value for CSV parse and link */
    private String applicantNRIC = null;

	//endregion

	/**
	 * Construct a BTO application.
	 * @param project The project which this application is applying to.
	 * @param flatType The flat type/size applied for.
	 * @param applicant The applicant
	 */
    public Application(BTOProject project, FlatType flatType, Applicant applicant) {
        this.project = project;
        this.flatType = flatType;
        this.status = ApplicationStatus.PENDING;
        this.withdrawalStatus = WithdrawalStatus.NOT;
        this.applicant = applicant;
		this.lastUpdated = LocalDateTime.now();
    }
    
	/**
	 * For {@code CSVParser} to parse a CSV row representing this class.
	 * @param cells cells of a CSV row, of application type.
	 */
    public Application(List<CSVCell> cells) {
    	super(cells);
    	this.projectName = cells.get(0).getValue();
    	this.flatType = FlatType.fromString(cells.get(1).getValue());
    	this.status = ApplicationStatus.fromString(cells.get(2).getValue());
    	this.applicantNRIC = cells.get(3).getValue();

    	if (cells.size() > 4) {
    		this.withdrawalStatus = WithdrawalStatus.fromString(cells.get(4).getValue());
    	}
    	else {
    		this.withdrawalStatus = WithdrawalStatus.NOT;
    	}

		if (cells.size() > 5) {
			this.lastUpdated = cells.get(5).getDateTimeValue();
		}
		else {
			this.lastUpdated = LocalDateTime.now();
		}
    }
    
	/**
	 * Call this to link relevant applicant objects to this object, post CSV parse.
	 * @param applicants Pool of all applicants
	 */
    void linkApplicant(List<Applicant> applicants) { //throws Exception {
    	// if (applicantNRIC == null) throw new IllegalArgumentException();
    	
    	Applicant applicant = applicants.stream()
    			.filter(ap -> ap.getNric().equalsIgnoreCase(applicantNRIC))
    			.findFirst()
    			.orElseThrow();
    	
    	this.applicant = applicant;
    	this.applicantNRIC = null;
    }
    
	/**
	 * Call this to link relevant project to this object, post CSV parse.
	 * @param projects Pool of all projects
	 */
    void linkProject(List<BTOProject> projects) {// throws Exception {
    	// if (projectName == null) throw new IllegalArgumentException();
    	
    	BTOProject project = projects.stream()
    			.filter(p -> p.getProjectName().equals(projectName))
    			.findFirst()
    			.orElseThrow();
    	
    	this.project = project;
    	this.projectName = null;
    }

	/**
	 * Withdraw this application.
	 * Should only be called by applicant.
	 * @throws Exception access control.
	 */
	public void withdraw() throws Exception {

		var system = BTOManagementSystem.common();
		var user = system.getActiveUserForPermittedTask(Applicant.class);

		if (!this.applicant.getNric().equals(user.getNric())) throw new IllegalStateException("You are attempting to mutate someone else's application. This is not allowed.");
		this.withdrawalStatus = WithdrawalStatus.WITHDRAWN;
	}
    
	/**
	 * Update status and update last updated time.
	 * @param status application status
	 */
    public void setStatus(ApplicationStatus status) {
        this.status = status;
		this.lastUpdated = LocalDateTime.now();
    }

	/**
	 * Get the application's status.
	 * @return the status.
	 */
    public ApplicationStatus getStatus() {
        return status;
    }

	/**
	 * Get the withdrawal status.
	 * @return the status.
	 */
    public WithdrawalStatus getWithdrawalStatus() {
		return withdrawalStatus;
	}

	/**
	 * Set the withdrawal status.
	 * @param withdrawalStatus the new status.
	 */
	public void setWithdrawalStatus(WithdrawalStatus withdrawalStatus) {
		this.withdrawalStatus = withdrawalStatus;
		this.lastUpdated = LocalDateTime.now();
	}

	/**
	 * Get flat type
	 * @return the flat type
	 */
	public FlatType getFlatType() {
        return flatType;
    }
    
	/**
	 * Get the applicant of this application
	 * @return the applicant
	 */
    public Applicant getApplicant() {
        return applicant;
    }
    
	/**
	 * The project that's being applied for.
	 * @return the project.
	 */
	public BTOProject getProject() {
		return project;
	}

	/**
	 * The last update date and time.
	 * @return date and time object.
	 */
	public LocalDateTime getLastUpdatedDate() {
		return lastUpdated;
	}

	/**
	 * The receipt for a successfully booked application.
	 * @return The receipt.
	 * @throws Exception Non-booked applications does not have receipts.
	 */
	public String getReceipt() throws Exception {
		if (this.status != ApplicationStatus.BOOKED) throw new IllegalArgumentException("No receipt can be generated as application has not been booked.");

		int size = 60;

		var date = Utilities.getInstance().formatDateFromDateTime(lastUpdated);
		var time = Utilities.getInstance().formatTimeFromDateTime(lastUpdated);

		var projectName = this.project.getProjectName();
		var neighborhood = this.project.getNeighborhood();

		var flatType = this.flatType;
		var price = this.project.getFlatForType(flatType).getPrice();

		var total = String.format("%-20s%s", " ", new Style.Builder().text("TOTAL (INCL. TAX): $" + price).bold().toString());

		var purchaserInfo = String.format("Purchased by: %s (%s)", applicant.getName(), applicant.getNric());

		String receipt = new DisplayMenu.Builder()
		.addCenteredContent("Housing & Development Board", size)
		.addCenteredContent("480 Lorong 6 Toa Payoh, Singapore 310480", size)
		.addCenteredContent("Phone: 6490 1111", size)
		.addContent(String.format("%-40s%20s", date, time))
		.addDivider()
		.addContent(String.format("%-40s%20s", "BTO Flat of Project " + projectName, "$" + price))
		.addContent(String.format(" -  %-50s", "at " + neighborhood + " neighbourhood"))
		.addContent(String.format(" -  %-50s", flatType))
		.addContent(" ")
		.addContent(" ")
		.addContent(" ")
		.addDivider()
		.addContent(total)
		.addContent(" ")
		.addCenteredContent(purchaserInfo, size)
		.build()
		.asString();
		return receipt;
	}
	
    @Override
    public String toString() {
        return applicant.getName() + " " + applicant.getNric() + " " + applicant.getAge() + " " + applicant.getMaritalStatus() + " applied " + project.getProjectName() + " " + flatType + " - " + status;
    }

	@Override
	public String encode() {
		String lastUpdatedFormatted = Utilities.getInstance().formatDateTime(lastUpdated);
		return String.format("%s,%s,%s,%s,%s,%s", project.getProjectName(), flatType.toString(), status.toString(), applicant.getNric(), withdrawalStatus.toString(), lastUpdatedFormatted);
	}

	@Override
	public CSVFileTypes sourceFileType() {
		return CSVFileTypes.APPLICATIONS_LIST;
	}
}
