package sc2002.FCS1.grp2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.EnumMap;

//TODO: Error handling
public class BTOProject extends CSVDecodable {
    private String projectName;
    private String neighborhood;

    //TODO: Create a seperate class for room units & Store which floors are assigned to certain users
    private int maxTwoRoomUnits;
    private int maxThreeRoomUnits;
    //private int remainingTwoRoomUnits;
    //private int remainingThreeRoomUnits;

    private EnumMap<FlatType, ArrayList<Flat>> remainingRooms;
    private EnumMap<FlatType, ArrayList<Flat>> bookedRooms;

    //TODO: Selling price

    private ArrayList<Application> applications;

    private String openingDate;
    private String closingDate;

    private HDBManager managerInCharge;
    private int totalOfficerSlots;
    
    private List<String> officerNames;
    private List<HDBOfficer> officers;

    private boolean visibility;

    static private int MAX_OFFICER_NUM = 10;

    //region Consturctors
    //Construct with a string parsed from a csv file.
    public BTOProject (ArrayList<CSVCell> cells) {
    	super(cells);
//        List<String> splitted = Arrays.asList(line.split(","));
		projectName = cells.get(0).getValue();
		neighborhood = cells.get(1).getValue();

		maxTwoRoomUnits = cells.get(3).getIntValue();
        // TODO: Retrieve booked rooms
        maxThreeRoomUnits = cells.get(6).getIntValue();
        // Retrieve booked rooms

		openingDate = cells.get(8).getValue();
		closingDate = cells.get(9).getValue();
        
        //TODO: Get objects for manager and officers
        
        //managerInCharge = splitted.get(10);
        totalOfficerSlots = cells.get(11).getIntValue();
        officerNames = Arrays.asList(cells.get(12).getValues());
        //officers = new ArrayList<String>(splitted.subList(12, splitted.size()));
    }
    
    public void retrieveConnectedUsers(ArrayList<HDBOfficer> officers) {
    	this.officers = officers
				    		.stream()
				    		.filter(o -> officerNames.contains(o.getName()))
				    		.collect(Collectors.toList());
    }

    //Construct with values
    public BTOProject (String projectName, String neighborhood, int maxTwoRoomUnits, int maxThreeRoomUnits, String openingDate, String closingDate, String managerInCharge, int officerSlots, ArrayList<String> officers) {
        this.projectName = projectName;
        this.neighborhood = neighborhood;
        this.maxTwoRoomUnits = maxTwoRoomUnits;
        this.maxThreeRoomUnits = maxThreeRoomUnits;
        // TODO: Retrieve booked rooms

        
        // TODO: Check whether the date range is valid
        this.openingDate = openingDate;
        this.closingDate = closingDate;

        //TODO: Get objects for managers and officers
        //this.managerInCharge = managerInCharge;
        this.totalOfficerSlots = officerSlots;
        //this.officers = officers != null ? officers : new ArrayList<String>();
    }
    //endregion

    //region Project Name
    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String name) {
        projectName = name;
    }
    //endregion

    //region Neighborhood
    public String getNeighborhood() {
        return neighborhood;
    }

    public void setNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
    }
    //endregion 

    //region Room Units
    //TODO: Get and set for room units
    //endregion

    //region Dates

    /**
     * Converts date formatted in string to an unique number
     * calculating the total number of days since 1900-01-01.
    */
    public static long stringDateToNum(String date)
    {
        String[] parts = date.split("-");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid date format. Use yyyy-MM-dd.");
        }

        int year = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        int day = Integer.parseInt(parts[2]);

        long totalDays = 0;

        // Calculate days from 1900 to input year
        for (int y = 1900; y < year; y++) {
            if (isLeapYear(y)) {
                totalDays += 366;
            } else {
                totalDays += 365;
            }
        }

        // Calculate days from start of year to input month
        int[] monthDays = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        if (isLeapYear(year)) {
            monthDays[1] = 29; // February has 29 days in a leap year
        }
        for (int m = 1; m < month; m++) {
            totalDays += monthDays[m - 1];
        }

        // Add days of the month
        totalDays += day - 1; // Subtract 1 because we're counting from 0

        return totalDays;
    }

    private static boolean isLeapYear(int year) {
        return year % 4 == 0 && (year % 100 != 0 || year % 400 == 0);
    }

    public long getNumOpeningDate() {
        return stringDateToNum(openingDate);
    }

    public long getNumClosingDate() {
        return stringDateToNum(closingDate);
    }

    public String getStringOpeningDate() {
        return openingDate;
    }

    public String getStringEndDate() {
    
        return closingDate;
    }

    //TODO: Check whether the date range is valid.
    public void setOpeningDate(String date) {
        openingDate = date;
    }

    public void setClosingDate(String date) {
        closingDate = date;
    }

    public boolean isDateWithin(String date) {
        return isDateWithin(stringDateToNum(date));
    }

    public boolean isDateWithin(long date) {
        return date >= getNumOpeningDate() && date <= getNumClosingDate();
    }
    //endregion

    //region Visibility 
    public boolean getVisibility() {
        return visibility;
    }

    public void toggleVisibility() {
        visibility = !visibility;
    }

    public void setVisibility(boolean visibility) {
        this.visibility = visibility;
    }
    //endregion

    //region Manager In Charge
    public HDBManager getManagerInCharge() {
        return managerInCharge;
    }
    //endregion

    //region Officers
    public int getTotalOfficerSlots() {
        return totalOfficerSlots;
    }

    public void setTotalOfficerSlots(int slots) {
        if (slots > MAX_OFFICER_NUM) {
            System.out.println("Invalid Input! The number exceeds maximum officer slots (" + MAX_OFFICER_NUM + ").");
            return;
        }
        else if (slots > officers.size()) {
            System.out.println("Invalid Input! The number exceeds current number of assigned HBD officers ()" + officers.size() + ").");
            return;
        }
        else if (slots < 0) {
            System.out.println("Invalid Input! The number cannot be below zero.");
            return;
        }

        totalOfficerSlots = slots;
    }

    public List<HDBOfficer> getOfficers() {
        return officers;
    }

    public void addOfficer(HDBOfficer officer) {
        if (officers.size() >= totalOfficerSlots) {
            System.out.println("Invalid Input! The project is full. Cannot register more officers.");
            return;
        }

        officers.add(officer);
    }

    public void removeOfficer(HDBOfficer officer) {
        if(officers.contains(officer)) {
            officers.remove(officer);
        }
        else if (officers.isEmpty()) {
           System.out.println("Invalid Input! The project does not have any HBD officers.");
        }
        else {
            System.out.println("Invalid Input! The project does not have HBD officer " + officer + ".");
        }
    }
    //endregion

    //region Applications and allocation of rooms
    public boolean submitApplication(Applicant applicant, FlatType flatType) {
        ArrayList<Flat> remaining = remainingRooms.get(flatType);

        if (remaining.isEmpty()) {
            System.out.println("Invalid application! There are no remaing " + flatType + " in " + projectName + ".");
            return false;
        }
        
        applications.add(new Application(this, flatType, ApplicationStatus.PENDING, applicant));
        return true;
    }

    // The user inputs index to identify which application to approve/reject/book
    public boolean approveApplication(int index) {
        if(applications.isEmpty()) {
            System.out.println("There is no applications for project " + projectName + ".");
            return false;
        }
        else if (index - 1 > applications.size() || index - 1 < 0) {
            System.out.println("Invalid input!");
            return false;
        }
        
        Application appli = applications.get(index - 1);
        ApplicationStatus status = appli.getStatus();
        if (status != ApplicationStatus.PENDING) {
            System.out.println("Invalid input! The Application has been already processed!");
            return false;
        }
        else if (remainingRooms.get(appli.getFlatType()).isEmpty()) {
            System.out.println("The selected room type has been already fully booked!");
            appli.setStatus(ApplicationStatus.UNSUCCESSFUL);
            return false;
        }

        appli.setStatus(ApplicationStatus.SUCCESSFUL);
        return true;
    }

    public boolean rejectApplication(int index) {
        if(applications.isEmpty()) {
            System.out.println("There is no applications for project " + projectName + ".");
            return false;
        }
        else if (index - 1 > applications.size() || index - 1 < 0) {
            System.out.println("Invalid input! Index out of bound!");
            return false;
        }
        
        ApplicationStatus status = applications.get(index - 1).getStatus();
        if (status != ApplicationStatus.PENDING) {
            System.out.println("Invalid input! The Application has been already processed!");
            return false;
        }

        applications.get(index - 1).setStatus(ApplicationStatus.UNSUCCESSFUL);
        return true;
    }

    public boolean bookApplication(int index) {
        if(applications.isEmpty()) {
            System.out.println("There is no applications for project " + projectName + ".");
            return false;
        }
        else if (index - 1 > applications.size() || index - 1 < 0) {
            System.out.println("Invalid input! Index out of bound!");
            return false;
        }
        
        Application appli = applications.get(index - 1);

        ApplicationStatus status = appli.getStatus();
        ArrayList<Flat> remaining = remainingRooms.get(appli.getFlatType());

        if (status != ApplicationStatus.SUCCESSFUL) {
            System.out.println("Invalid input! The application is not successful!");
            return false;
        }
        else if (remaining.isEmpty()) {
            System.out.println("The selected room type has been already fully booked!");
            appli.setStatus(ApplicationStatus.UNSUCCESSFUL);
            return false;
        }

        appli.setStatus(ApplicationStatus.BOOKED);
        
        Flat bookedFlat = remaining.removeLast();
        bookedFlat.setBookedApplicant(appli.getApplicant());
        bookedRooms.get(appli.getFlatType()).add(bookedFlat);
        return true;
    }
    
    public void printApplications() {
        if (applications.isEmpty()) {
            System.out.println("There is no applications for project " + projectName + ".");
            return;
        }

        for(int i = 0; i < applications.size(); i++) {
            System.out.println((i + 1) + ": " + applications.get(i));
        }
    }
    //endregion

    @Override
	public String toString() {
        ArrayList<String> officerNames = new ArrayList<String>();
        for(int i = 0; i < officers.size(); i++) {
            officerNames.add(officers.get(i).getName());
        }
        return "BTO Project: " +
                "Project Name=" + projectName + ", " +
                "Neighborhood=" + neighborhood + ", " +
                "Two Room Units=" + maxTwoRoomUnits + ", " +
                "Thre Room Units=" + maxThreeRoomUnits + ", " +
                "Application Opening Date=" + openingDate + ", " +
                "Application Closing Date=" + closingDate + ", " +
                "HBD Manager In Charge=" + managerInCharge + ", " + 
                "Total Officer Slots=" + totalOfficerSlots + ", " +
                "Number of HBD Officers Assigned" + officers.size() + ", " +
                "List of HBD Officers" + String.join(", ", officerNames) + ", " +
                "Visibility=" + visibility + ".";
    }
}