package sc2002.FCS1.grp2;

/**
 * Type of report.
 */
enum ReportType {
    FLAT_TYPE("Flat Type"),
    PROJECT("BTO Project"),
    AGE("Age"),
    MARITAL_STATUS("Marital Status");

    private String name;

    private ReportType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public static ReportType[] all = ReportType.values();
   
    public static ReportType fromOrdinal(int o) {
        if(o >= all.length) return null;
        return all[o];
    }
}