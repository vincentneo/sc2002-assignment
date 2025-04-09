package sc2002.FCS1.grp2;

/**
 * A class taht represents a flat that has a flat type and can be assigned to an applicant if booked.
 */
public class Flat {
    private FlatType type;
    private Applicant bookedApplicant;

    public Flat(String value) {
        type = FlatType.fromString(value);
        bookedApplicant = null;
    }

    public Flat(String value, Applicant bookeApplicant) {
        type = FlatType.fromString(value);
        this.bookedApplicant = bookeApplicant;
    }
    
    public FlatType getFlatType() {
        return type;
    }

    public void setBookedApplicant(Applicant bookedApplicant) {
        this.bookedApplicant = bookedApplicant;
    }

    public Applicant getBookedApplicant() {
        return bookedApplicant;
    }
}
