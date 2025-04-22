package sc2002.FCS1.grp2;

import java.time.LocalDateTime;
import java.util.List;

import sc2002.FCS1.grp2.helpers.Utilities;

public class Application extends CSVDecodable implements CSVEncodable {
    private BTOProject project;
    private FlatType flatType;
    private ApplicationStatus status;
    private WithdrawalStatus withdrawalStatus;
    private Applicant applicant;
	private LocalDateTime lastUpdated;
    
    private String projectName = null;
    private String applicantNRIC = null;

    public Application(BTOProject project, FlatType flatType, Applicant applicant) {
        this.project = project;
        this.flatType = flatType;
        this.status = ApplicationStatus.PENDING;
        this.withdrawalStatus = WithdrawalStatus.NOT;
        this.applicant = applicant;
		this.lastUpdated = LocalDateTime.now();
    }
    
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
    
    void linkApplicant(List<Applicant> applicants) { //throws Exception {
    	// if (applicantNRIC == null) throw new IllegalArgumentException();
    	
    	Applicant applicant = applicants.stream()
    			.filter(ap -> ap.getNric().equalsIgnoreCase(applicantNRIC))
    			.findFirst()
    			.orElseThrow();
    	
    	this.applicant = applicant;
    	this.applicantNRIC = null;
    }
    
    void linkProject(List<BTOProject> projects) {// throws Exception {
    	// if (projectName == null) throw new IllegalArgumentException();
    	
    	BTOProject project = projects.stream()
    			.filter(p -> p.getProjectName().equals(projectName))
    			.findFirst()
    			.orElseThrow();
    	
    	this.project = project;
    	this.projectName = null;
    }
    
    public void setStatus(ApplicationStatus status) {
        this.status = status;
		this.lastUpdated = LocalDateTime.now();
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public WithdrawalStatus getWithdrawalStatus() {
		return withdrawalStatus;
	}

	public void setWithdrawalStatus(WithdrawalStatus withdrawalStatus) {
		this.withdrawalStatus = withdrawalStatus;
		this.lastUpdated = LocalDateTime.now();
	}

	public FlatType getFlatType() {
        return flatType;
    }
    
    public Applicant getApplicant() {
        return applicant;
    }
    
	public BTOProject getProject() {
		return project;
	}

	public LocalDateTime getLastUpdatedDate() {
		return lastUpdated;
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
