package sc2002.FCS1.grp2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//TODO: Error handling
public class BTOProject {
    private String projectName;
    private String neighborhood;

    //TODO: Create a seperate class for room units & Store which floors are assigned to certain users
    private int maxTwoRoomUnits;   
    private int maxThreeRoomUnits; 
    private int remainingTwoRoomUnits;
    private int remainingThreeRoomUnits;

    //TODO: Selling price

    private String openingDate;
    private String closingDate;

    private String managerInCharge;
    private int totalOfficerSlots;
    private ArrayList<String> officers;

    private boolean visibility;

    static final private int MAX_OFFICER_NUM = 10;

    //region Consturctors
    //Construct with a string parsed from a csv file.
    public BTOProject (String line) {
        List<String> splitted = Arrays.asList(line.split(","));
		projectName = splitted.get(0);
		neighborhood = splitted.get(1);

		maxTwoRoomUnits = Integer.parseInt(splitted.get(3));
        remainingTwoRoomUnits = maxTwoRoomUnits;
        maxThreeRoomUnits = Integer.parseInt(splitted.get(6));
        remainingThreeRoomUnits = maxThreeRoomUnits;

		openingDate = splitted.get(8);
		closingDate = splitted.get(9);
        managerInCharge = splitted.get(10);
        totalOfficerSlots = Integer.parseInt(splitted.get(11));
        officers = new ArrayList<String>(splitted.subList(12, splitted.size()));
    }

    //Construct with values
    public BTOProject (String projectName, String neighborhood, int maxTwoRoomUnits, int maxThreeRoomUnits, String openingDate, String closingDate, String managerInCharge, int officerSlots, ArrayList<String> officers) {
        this.projectName = projectName;
        this.neighborhood = neighborhood;
        this.maxTwoRoomUnits = maxTwoRoomUnits;
        this.maxThreeRoomUnits = maxThreeRoomUnits;
        remainingTwoRoomUnits = maxTwoRoomUnits;
        remainingThreeRoomUnits = maxThreeRoomUnits;

        // TODO: Check whether the date range is valid
        this.openingDate = openingDate;
        this.closingDate = closingDate;

        this.managerInCharge = managerInCharge;
        this.totalOfficerSlots = officerSlots;
        this.officers = officers != null ? officers : new ArrayList<String>();
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
    //TODO: Assinging room units for applicants
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
    public String getManagerInCharge() {
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

    public ArrayList<String> getOfficers() {
        return officers;
    }

    public void addOfficer(String name) {
        if (officers.size() >= totalOfficerSlots) {
            System.out.println("Invalid Input! The project is full. Cannot register more officers.");
            return;
        }

        officers.add(name);
    }

    public void removeOfficer(String name) {
        if(officers.contains(name)) {
            officers.remove(name);
        }
        else if (officers.isEmpty()) {
           System.out.println("Invalid Input! The project does not have any HBD officers.");
        }
        else {
            System.out.println("Invalid Input! The project does not have HBD officer " + name + ".");
        }
    }
    //endregion

    @Override
	public String toString() {
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
                "List of HBD Officers" + String.join(", ", officers) + ", " +
                "Visibility=" + visibility + ".";
    }
}