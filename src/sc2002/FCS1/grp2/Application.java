package sc2002.FCS1.grp2;

public class Application {
    private BTOProject project;
    private FlatType flatType;
    private ApplicationStatus status;
    private Applicant applicant;

    public Application(BTOProject project, FlatType flatType, ApplicationStatus status, Applicant applicant) {
        this.project = project;
        this.flatType = flatType;
        this.status = status;
        this.applicant = applicant;
    }
    
    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public FlatType getFlatType () {
        return flatType;
    }
    
    public Applicant getApplicant() {
        return applicant;
    }

    @Override
    public String toString() {
        return applicant.getName() + " " + applicant.getNric() + " " + applicant.getAge() + " " + applicant.getMaritalStatus() + " applied " + project.getProjectName() + " " + flatType + " - " + status;
    }
}
