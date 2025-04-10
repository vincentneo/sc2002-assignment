package sc2002.FCS1.grp2;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.EnumMap;

//TODO: Error handling

/**
 * A class representing BTO Project containing information for the project and has functions to modify the project information and handle users' applications to the project.
 * 
 *  @author Ryu Hyungjoon
 */
public class BTOProject extends CSVDecodable {
    private String projectName;
    private String neighborhood;

    //TODO: Store which floors are assigned to certain users
    private int maxTwoRoomUnits;
    private int maxThreeRoomUnits;

    private EnumMap<FlatType, ArrayList<Flat>> remainingRooms;
    private EnumMap<FlatType, ArrayList<Flat>> bookedRooms;

    private int twoRoomPrice;
    private int threeRoomPrice;

    private ArrayList<Application> applications;

    private LocalDate openingDate;
    private LocalDate closingDate;

    private HDBManager managerInCharge;
    private int totalOfficerSlots;
    
    private List<String> officerNames;
    private List<HDBOfficer> officers;

    private boolean visibility;

    static private int MAX_OFFICER_NUM = 10;

    //region Consturctors
    //Construct with a string parsed from a csv file.
    public BTOProject (ArrayList<CSVCell> cells) throws Exception {
    	super(cells);
//        List<String> splitted = Arrays.asList(line.split(","));
		projectName = cells.get(0).getValue();
		neighborhood = cells.get(1).getValue();

		maxTwoRoomUnits = cells.get(3).getIntValue();
        maxThreeRoomUnits = cells.get(6).getIntValue();
        // TODO: Retrieve booked rooms

        twoRoomPrice = cells.get(4).getIntValue();
        threeRoomPrice = cells.get(7).getIntValue();

		openingDate = LocalDate.parse(cells.get(8).getValue()); //the parse method throws a DateTimeParseExecption if the string is in wrong format.
        //For throwing exception
		closingDate = LocalDate.parse(cells.get(9).getValue());
        
        //TODO: Get objects for manager and officers
        
        //managerInCharge = splitted.get(10);
        totalOfficerSlots = cells.get(11).getIntValue();
        officerNames = Arrays.asList(cells.get(12).getValues());
        //officers = new ArrayList<String>(splitted.subList(12, splitted.size()));

        if (projectName == null || projectName.isEmpty()) {
            throw new IllegalArgumentException("Project name cannot be empty.");
        }
        else if (neighborhood == null || neighborhood.isEmpty()) {
            throw new IllegalArgumentException("Neighborhood cannot be empty.");
        }
        else if (maxTwoRoomUnits < 0 || maxThreeRoomUnits < 0) {
            throw new IllegalArgumentException("Number of units cannot be negative.");
        }
        else if (maxTwoRoomUnits < 0 || maxThreeRoomUnits < 0) {
            throw new IllegalArgumentException("Number of price cannot be negative.");
        }
        else if (openingDate.isAfter(closingDate)) { // the parse method throws a DateTimeParseExecption if the string is in wrong format.
            throw new IllegalArgumentException("Opening date cannot be after closing date.");
        }
        else if (totalOfficerSlots < 0) {
            throw new IllegalArgumentException("Number of price cannot be negative.");
        }
    }
    
    public void retrieveConnectedUsers(ArrayList<HDBOfficer> officers) {
    	this.officers = officers
				    		.stream()
				    		.filter(o -> officerNames.contains(o.getName()))
				    		.collect(Collectors.toList());
    }

    //Construct with values
    public BTOProject (String projectName, String neighborhood, int maxTwoRoomUnits, int maxThreeRoomUnits, int twoRoomPrice, int threeRoomPrice, String openingDate, String closingDate, String managerInCharge, int totalOfficerSlots, ArrayList<String> officers)
    throws Exception
    {
        if (projectName == null || projectName.isEmpty()) {
            throw new IllegalArgumentException("Project name cannot be empty.");
        }
        else if (neighborhood == null || neighborhood.isEmpty()) {
            throw new IllegalArgumentException("Neighborhood cannot be empty.");
        }
        else if (maxTwoRoomUnits < 0 || maxThreeRoomUnits < 0) {
            throw new IllegalArgumentException("Number of units cannot be negative.");
        }
        else if (maxTwoRoomUnits < 0 || maxThreeRoomUnits < 0) {
            throw new IllegalArgumentException("Number of price cannot be negative.");
        }
        else if (LocalDate.parse(openingDate).isAfter(LocalDate.parse(closingDate))) { // the parse method throws a DateTimeParseExecption if the string is in wrong format.
            throw new IllegalArgumentException("Opening date cannot be after closing date.");
        }
        else if (totalOfficerSlots < 0) {
            throw new IllegalArgumentException("Number of price cannot be negative.");
        }

        this.projectName = projectName;
        this.neighborhood = neighborhood;
        this.maxTwoRoomUnits = maxTwoRoomUnits;
        this.maxThreeRoomUnits = maxThreeRoomUnits;
        this.twoRoomPrice = twoRoomPrice;
        this.threeRoomPrice = threeRoomPrice;
        // TODO: Retrieve booked rooms
        
        this.openingDate = LocalDate.parse(openingDate);
        this.closingDate = LocalDate.parse(closingDate);


        //TODO: Get objects for managers and officers
        //this.managerInCharge = managerInCharge;
        this.totalOfficerSlots = totalOfficerSlots;
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

    public void setTwoRoomPrice(int twoRoomPrice) throws IllegalArgumentException {
        if (twoRoomPrice < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        this.twoRoomPrice = twoRoomPrice;
    }

    public void setThreeRoomPrice(int threeRoomPrice) throws IllegalArgumentException {
        if (threeRoomPrice < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        this.threeRoomPrice = threeRoomPrice;
    }

    public int getTwoRoomPrice() {
        return twoRoomPrice;
    }

    public int getThreeRoomPrice() {
        return threeRoomPrice;
    }

    //endregion

    //region Dates

    public LocalDate getOpeningDate() {
        return openingDate;
    }

    public LocalDate getClosingDate() {
        return closingDate;
    }

    public String getStringOpeningDate() {
        return openingDate.toString();
    }

    public String getStringEndDate() {
        return closingDate.toString();
    }

    public void setOpeningDate(String date) throws DateTimeException {
        setOpeningDate(LocalDate.parse(date));
    }

    public void setOpeningDate(LocalDate date) throws DateTimeException {
        if(date.compareTo(closingDate) > 0) {
            throw new DateTimeException("The new opening date is greater than the closing date!");
        }
        openingDate = date;
    }

    public void setClosingDate(String date) throws DateTimeException {
        setClosingDate(LocalDate.parse(date));
    }

    public void setClosingDate(LocalDate date) throws DateTimeException {
        if(date.compareTo(openingDate) < 0) {
            throw new DateTimeException("The new closing date is lesser than the opening date!");
        }
        closingDate = date;
    }

    public boolean isDateWithin(String date) {
        return isDateWithin(LocalDate.parse(date));
    }

    public boolean isDateWithin(LocalDate date) {
        return date.compareTo(closingDate) <= 0 && date.compareTo(openingDate) >= 0;
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
    public void submitApplication(Applicant applicant, FlatType flatType) throws IllegalArgumentException {
        ArrayList<Flat> remaining = remainingRooms.get(flatType);

        if (remaining.isEmpty()) {
            throw new IllegalArgumentException("Invalid application! There are no remaing " + flatType + " in " + projectName + ".");
        }
        
        applications.add(new Application(this, flatType, ApplicationStatus.PENDING, applicant));
    }

    // The user inputs index to identify which application to approve/reject/book
    public void approveApplication(int index) throws IllegalArgumentException {
        if(applications.isEmpty()) {
            throw new IllegalArgumentException("There is no applications for the project " + projectName + ".");
        }
        else if (index - 1 > applications.size() || index - 1 < 0) {
            throw new IllegalArgumentException("Invalid input! The index out of bound!");
        }
        
        Application appli = applications.get(index - 1);
        ApplicationStatus status = appli.getStatus();
        if (status != ApplicationStatus.PENDING) {
            throw new IllegalArgumentException("Invalid input! The Application has been already processed!");
        }
        else if (remainingRooms.get(appli.getFlatType()).isEmpty()) {
            appli.setStatus(ApplicationStatus.UNSUCCESSFUL);
            throw new IllegalArgumentException("The selected room type has been already fully booked!");
        }

        appli.setStatus(ApplicationStatus.SUCCESSFUL);
        System.out.println("Approval successful.");
    }

    public void rejectApplication(int index) throws IllegalArgumentException {
        if(applications.isEmpty()) {
            throw new IllegalArgumentException("There is no applications for project " + projectName + ".");
        }
        else if (index - 1 > applications.size() || index - 1 < 0) {
            throw new IllegalArgumentException("Invalid input! Index out of bound!");
        }
        
        ApplicationStatus status = applications.get(index - 1).getStatus();
        if (status != ApplicationStatus.PENDING) {
            throw new IllegalArgumentException("Invalid input! The Application has been already processed!");
        }

        applications.get(index - 1).setStatus(ApplicationStatus.UNSUCCESSFUL);
        System.out.println("Rejection successful.");
    }

    public void bookApplication(int index) throws IllegalArgumentException {
        if(applications.isEmpty()) {
            throw new IllegalArgumentException("There is no applications for project " + projectName + ".");
        }
        else if (index - 1 > applications.size() || index - 1 < 0) {
            throw new IllegalArgumentException("Invalid input! Index out of bound!");
        }
        
        Application appli = applications.get(index - 1);

        ApplicationStatus status = appli.getStatus();
        ArrayList<Flat> remaining = remainingRooms.get(appli.getFlatType());

        if (status != ApplicationStatus.SUCCESSFUL) {
            throw new IllegalArgumentException("Invalid input! The application is not successful!");
        }
        else if (remaining.isEmpty()) {
            appli.setStatus(ApplicationStatus.UNSUCCESSFUL);
            throw new IllegalArgumentException("The selected room type has been already fully booked!");
        }

        appli.setStatus(ApplicationStatus.BOOKED);
        
        Flat bookedFlat = remaining.removeLast();
        bookedFlat.setBookedApplicant(appli.getApplicant());
        bookedRooms.get(appli.getFlatType()).add(bookedFlat);
        System.out.println("Booking successful.");
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
        if (officers != null) {
	        for(int i = 0; i < officers.size(); i++) {
	            officerNames.add(officers.get(i).getName());
	        }
        }
        return "BTO Project: " +
                "Project Name=" + projectName + ", " +
                "Neighborhood=" + neighborhood + ", " +
                "Two Room Units=" + maxTwoRoomUnits + ", " +
                "Two Room Price=" + twoRoomPrice + ", " +
                "Three Room Units=" + maxThreeRoomUnits + ", " +
                "Three Room Units=" + threeRoomPrice + ", " +
                "Application Opening Date=" + openingDate + ", " +
                "Application Closing Date=" + closingDate + ", " +
                "HBD Manager In Charge=" + managerInCharge + ", " + 
                "Total Officer Slots=" + totalOfficerSlots + ", " +
//                "Number of HBD Officers Assigned" + officers.size() + ", " +
                "List of HBD Officers" + String.join(", ", officerNames) + ", " +
                "Visibility=" + visibility + ".";
    }
}