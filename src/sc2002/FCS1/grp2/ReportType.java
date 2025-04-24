package sc2002.FCS1.grp2;

/**
 * Type of report.
 */
enum ReportType {
    /**
     * Report that shows differences by flat type (e.g 2/3 room)
     */
    FLAT_TYPE("Flat Type"),
    /**
     * Report that shows how many applicants per project
     */
    PROJECT("BTO Project"),
    /**
     * Report that shows how many applicants by age 
     */
    AGE("Age"),
    /**
     * Report that shows how many applicants are married vs single
     */
    MARITAL_STATUS("Marital Status");

    /**
     * Readable name of this type.
     */
    private String name;

    /** Internal constructor for enum value */
    private ReportType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    /** get all possible types */
    public static ReportType[] all = ReportType.values();
   
    /**
     * construct from the index of enum 
     */
    public static ReportType fromOrdinal(int o) {
        if(o >= all.length) return null;
        return all[o];
    }
}