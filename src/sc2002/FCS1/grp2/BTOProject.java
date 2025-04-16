package sc2002.FCS1.grp2;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.EnumMap;
import java.util.Set;

//TODO: Error handling

/**
 * A class representing BTO Project containing information for the project and has functions to modify the project information and handle users' applications to the project.
 * 
 *  @author Ryu Hyungjoon
 */
public class BTOProject extends CSVDecodable implements CSVEncodable {
    private String projectName;
    private String neighborhood;

    //TODO: Store which floors are assigned to certain users
//    private int maxTwoRoomUnits;
//    private int maxThreeRoomUnits;
//
//    private EnumMap<FlatType, ArrayList<Flat>> remainingRooms;
//    private EnumMap<FlatType, ArrayList<Flat>> bookedRooms;
//
//    private int twoRoomPrice;
//    private int threeRoomPrice;
    
    private EnumMap<FlatType,FlatInfo> flats = new EnumMap<>(FlatType.class);

    private ArrayList<Application> applications;

    private LocalDate openingDate;
    private LocalDate closingDate;

    private String managerName;
    private HDBManager managerInCharge;
    private int totalOfficerSlots;
    
    private List<String> officerNames;
    private List<HDBOfficer> officers;

    private boolean visibility;

    static private int MAX_OFFICER_NUM = 10;

    //region Consturctors
    //Construct with a string parsed from a csv file.
    public BTOProject (List<CSVCell> cells) throws Exception {
    	super(cells);
//        List<String> splitted = Arrays.asList(line.split(","));
		projectName = cells.get(0).getValue();
		neighborhood = cells.get(1).getValue();

//		  maxTwoRoomUnits = cells.get(3).getIntValue();
//        maxThreeRoomUnits = cells.get(6).getIntValue();
//        // TODO: Retrieve booked rooms
//
//        twoRoomPrice = cells.get(4).getIntValue();
//        threeRoomPrice = cells.get(7).getIntValue();
		
		FlatType type1 = FlatType.fromString(cells.get(2).getValue());
		int units1 = cells.get(3).getIntValue();
		int price1 = cells.get(4).getIntValue();
		FlatInfo info1 = new FlatInfo(type1, units1, price1);
		
		FlatType type2 = FlatType.fromString(cells.get(5).getValue());
		int units2 = cells.get(6).getIntValue();
		int price2 = cells.get(7).getIntValue();
		FlatInfo info2 = new FlatInfo(type2, units2, price2);
		
		flats.put(type1, info1);
		flats.put(type2, info2);

		openingDate = cells.get(8).getDateValue();//the parse method throws a DateTimeParseExecption if the string is in wrong format.
        //For throwing exception
		closingDate = cells.get(9).getDateValue();
        
        //TODO: Get objects for manager and officers
        
        managerName = cells.get(10).getValue();
        totalOfficerSlots = cells.get(11).getIntValue();
        
        String[] officerCell = cells.get(12).getValues();
        if (officerCell != null) {
        	officerNames = Arrays.asList(officerCell);
        }
        else {
        	officerNames = new ArrayList<>();
        }
        
        if (cells.size() > 13) {
        	try {
        		boolean visibility = cells.get(13).getBoolValue();
        		this.visibility = visibility;
        	}
        	catch (Exception e) {
        		this.visibility = true;
        	}
        }
        //officers = new ArrayList<String>(splitted.subList(12, splitted.size()));

        if (projectName == null || projectName.isEmpty()) {
            throw new IllegalArgumentException("Project name cannot be empty.");
        }
        else if (neighborhood == null || neighborhood.isEmpty()) {
            throw new IllegalArgumentException("Neighborhood cannot be empty.");
        }
//        else if (maxTwoRoomUnits < 0 || maxThreeRoomUnits < 0) {
//            throw new IllegalArgumentException("Number of units cannot be negative.");
//        }
//        else if (maxTwoRoomUnits < 0 || maxThreeRoomUnits < 0) {
//            throw new IllegalArgumentException("Number of price cannot be negative.");
//        }
        else if (openingDate.isAfter(closingDate)) { // the parse method throws a DateTimeParseExecption if the string is in wrong format.
            throw new IllegalArgumentException("Opening date cannot be after closing date.");
        }
        else if (totalOfficerSlots < 0) {
            throw new IllegalArgumentException("Number of price cannot be negative.");
        }
    }

    //Construct with values
    public BTOProject (String projectName, String neighborhood, int maxTwoRoomUnits, int maxThreeRoomUnits, int twoRoomPrice, int threeRoomPrice, LocalDate openingDate, LocalDate closingDate, HDBManager managerInCharge, int totalOfficerSlots, ArrayList<HDBOfficer> officers)
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
        else if (openingDate.isAfter(closingDate)) { // the parse method throws a DateTimeParseExecption if the string is in wrong format.
            throw new IllegalArgumentException("Opening date cannot be after closing date.");
        }
        else if (totalOfficerSlots < 0) {
            throw new IllegalArgumentException("Number of price cannot be negative.");
        }

        this.projectName = projectName;
        this.neighborhood = neighborhood;
//        this.maxTwoRoomUnits = maxTwoRoomUnits;
//        this.maxThreeRoomUnits = maxThreeRoomUnits;
//        this.twoRoomPrice = twoRoomPrice;
//        this.threeRoomPrice = threeRoomPrice;
        // TODO: Retrieve booked rooms
        
        this.openingDate = openingDate;
        this.closingDate = closingDate;


        //TODO: Get objects for managers and officers
        this.managerInCharge = managerInCharge;
        this.totalOfficerSlots = totalOfficerSlots;
        this.officers = officers;
        
        this.visibility = true;
        //this.officers = officers != null ? officers : new ArrayList<String>();
    }
    //endregion
    
    
    public void retrieveConnectedUsers(ArrayList<HDBOfficer> officers, ArrayList<HDBManager> managers) {
    	this.officers = officers
				    		.stream()
				    		.filter(o -> officerNames.contains(o.getName()))
				    		.collect(Collectors.toList());
    	this.officerNames = null;
    	this.managerInCharge = managers
    			.stream()
    			.filter(m -> m.getName().equals(managerName))
    			.findFirst()
    			.orElse(null);
    	this.managerName = null;
    }

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
    
    public List<FlatInfo> getApplicableFlats(Set<FlatType> applicableTypes) {
    	ArrayList<FlatInfo> applicableFlats = new ArrayList<>();
    	for (FlatType type : applicableTypes) {
    		applicableFlats.add(flats.get(type));
    	}
    	
    	return applicableFlats;
    }
    
    public FlatInfo getFlatForType(FlatType type) {
    	return flats.get(type);
    }
    
    public void setTwoRoomUnits(int twoRoomUnits) throws IllegalArgumentException{
    	if(twoRoomUnits < 0) {
    		throw new IllegalArgumentException("Number of 2 room units cannot be negative");
    	}
    	this.flats.get(FlatType.TWO_ROOM).setRemainingUnits(twoRoomUnits);
    }
    
    public void setThreeRoomUnits(int threeRoomUnits) throws IllegalArgumentException{
    	if(threeRoomUnits < 0) {
    		throw new IllegalArgumentException("Number of 3 room units cannot be negative");
    	}
    	this.flats.get(FlatType.THREE_ROOM).setRemainingUnits(threeRoomUnits);
    }
    
    public int getTwoRoomUnits() {
    	return this.flats.get(FlatType.TWO_ROOM).getRemainingUnits();
    }
    
    public int getThreeRoomUnits() {
    	return this.flats.get(FlatType.THREE_ROOM).getRemainingUnits();
    }

    public void setTwoRoomPrice(int twoRoomPrice) throws IllegalArgumentException {
        if (twoRoomPrice < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        this.flats.get(FlatType.TWO_ROOM).setPrice(twoRoomPrice);
    }

    public void setThreeRoomPrice(int threeRoomPrice) throws IllegalArgumentException {
        if (threeRoomPrice < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        this.flats.get(FlatType.THREE_ROOM).setPrice(threeRoomPrice);
    }

    public int getTwoRoomPrice() {
        return this.flats.get(FlatType.TWO_ROOM).getPrice();
    }

    public int getThreeRoomPrice() {
        return this.flats.get(FlatType.THREE_ROOM).getPrice();
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

    public void setTotalOfficerSlots(int slots) throws IllegalArgumentException{
        if (slots > MAX_OFFICER_NUM) {
            throw new IllegalArgumentException("Invalid Input! The number exceeds maximum officer slots (" + MAX_OFFICER_NUM + ").");
        }
        else if (slots > officers.size()) {
            throw new IllegalArgumentException("Invalid Input! The number exceeds current number of assigned HBD officers ()" + officers.size() + ").");
        }
        else if (slots < 0) {
            throw new IllegalArgumentException("Invalid Input! The number cannot be below zero.");
        }

        totalOfficerSlots = slots;
    }

    public List<HDBOfficer> getOfficers() {
        return officers;
    }

    public void addOfficer(HDBOfficer officer) throws IllegalArgumentException {
        if (officers.size() >= totalOfficerSlots) {
            throw new IllegalArgumentException("Invalid Input! The project is full. Cannot register more officers.");
        }
        else if (officers.contains(officer)) {
            throw new IllegalArgumentException("Invalid Input! The officer is already registered.");
        }
        else if (officer == null) {
            throw new IllegalArgumentException("Invalid Input! The officer cannot be null.");
        }

        officers.add(officer);
    }

    public void removeOfficer(HDBOfficer officer) throws IllegalArgumentException {
        if(officers.contains(officer)) {
            officers.remove(officer);
        }
        else if (officers.isEmpty()) {
            throw new IllegalArgumentException("Invalid Input! The project does not have any HBD officers.");
        }
        else {
            throw new IllegalArgumentException("Invalid Input! The project does not have HBD officer " + officer + ".");
        }
    }
    //endregion

    
    // TODO: Think about next steps for how to handle application (probs move to *Action class)
//    //region Applications and allocation of rooms
//    public void submitApplication(Applicant applicant, FlatType flatType) throws IllegalArgumentException {
//        ArrayList<Flat> remaining = remainingRooms.get(flatType);
//
//        if (remaining.isEmpty()) {
//            throw new IllegalArgumentException("Invalid application! There are no remaing " + flatType + " in " + projectName + ".");
//        }
//        
//        applications.add(new Application(this, flatType, ApplicationStatus.PENDING, applicant));
//    }
//
//    // The user inputs index to identify which application to approve/reject/book
//    public void approveApplication(int index) throws IllegalArgumentException {
//        if(applications.isEmpty()) {
//            throw new IllegalArgumentException("There is no applications for the project " + projectName + ".");
//        }
//        else if (index - 1 > applications.size() || index - 1 < 0) {
//            throw new IllegalArgumentException("Invalid input! The index out of bound!");
//        }
//        
//        Application appli = applications.get(index - 1);
//        ApplicationStatus status = appli.getStatus();
//        if (status != ApplicationStatus.PENDING) {
//            throw new IllegalArgumentException("Invalid input! The Application has been already processed!");
//        }
//        else if (remainingRooms.get(appli.getFlatType()).isEmpty()) {
//            appli.setStatus(ApplicationStatus.UNSUCCESSFUL);
//            throw new IllegalArgumentException("The selected room type has been already fully booked!");
//        }
//
//        appli.setStatus(ApplicationStatus.SUCCESSFUL);
//        System.out.println("Approval successful.");
//    }
//
//    public void rejectApplication(int index) throws IllegalArgumentException {
//        if(applications.isEmpty()) {
//            throw new IllegalArgumentException("There is no applications for project " + projectName + ".");
//        }
//        else if (index - 1 > applications.size() || index - 1 < 0) {
//            throw new IllegalArgumentException("Invalid input! Index out of bound!");
//        }
//        
//        ApplicationStatus status = applications.get(index - 1).getStatus();
//        if (status != ApplicationStatus.PENDING) {
//            throw new IllegalArgumentException("Invalid input! The Application has been already processed!");
//        }
//
//        applications.get(index - 1).setStatus(ApplicationStatus.UNSUCCESSFUL);
//        System.out.println("Rejection successful.");
//    }
//
//    public void bookApplication(int index) throws IllegalArgumentException {
//        if(applications.isEmpty()) {
//            throw new IllegalArgumentException("There is no applications for project " + projectName + ".");
//        }
//        else if (index - 1 > applications.size() || index - 1 < 0) {
//            throw new IllegalArgumentException("Invalid input! Index out of bound!");
//        }
//        
//        Application appli = applications.get(index - 1);
//
//        ApplicationStatus status = appli.getStatus();
//        ArrayList<Flat> remaining = remainingRooms.get(appli.getFlatType());
//
//        if (status != ApplicationStatus.SUCCESSFUL) {
//            throw new IllegalArgumentException("Invalid input! The application is not successful!");
//        }
//        else if (remaining.isEmpty()) {
//            appli.setStatus(ApplicationStatus.UNSUCCESSFUL);
//            throw new IllegalArgumentException("The selected room type has been already fully booked!");
//        }
//
//        appli.setStatus(ApplicationStatus.BOOKED);
//        
//        Flat bookedFlat = remaining.removeLast();
//        bookedFlat.setBookedApplicant(appli.getApplicant());
//        bookedRooms.get(appli.getFlatType()).add(bookedFlat);
//        System.out.println("Booking successful.");
//    }
    
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
    
    public boolean isEligible(Set<FlatType> flatTypes) {
    	for (FlatType type : flatTypes) {
    		if (flats.get(type).getRemainingUnits() > 0) return true;
    	}
    	
    	return false;
    }
    
    private List<String> getHDBOfficersNames() {
    	return officers
    			.stream()
    			.map(o -> o.getName())
    			.collect(Collectors.toList());
    }

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
                "Two Room Units=" + flats.get(FlatType.TWO_ROOM).getRemainingUnits() + ", " +
                "Two Room Price=" + flats.get(FlatType.TWO_ROOM).getPrice() + ", " +
                "Three Room Units=" + flats.get(FlatType.THREE_ROOM).getRemainingUnits() + ", " +
                "Three Room Price=" + flats.get(FlatType.THREE_ROOM).getPrice() + ", " +
                "Application Opening Date=" + openingDate + ", " +
                "Application Closing Date=" + closingDate + ", " +
                "HBD Manager In Charge=" + managerInCharge + ", " + 
                "Total Officer Slots=" + totalOfficerSlots + ", " +
//                "Number of HBD Officers Assigned" + officers.size() + ", " +
                "List of HBD Officers" + String.join(", ", officerNames) + ", " +
                "Visibility=" + visibility + ".";
    }

	@Override
	public String encode() {
		// TODO: Flat Type should be stored not hardcoded.
		FlatType roomOneType = FlatType.TWO_ROOM;
		FlatType roomTwoType = FlatType.THREE_ROOM;
		
		FlatInfo roomOne = flats.get(roomOneType);
		FlatInfo roomTwo = flats.get(roomTwoType);
		
		String formattedOpeningDate = Utilities.getInstance().formatDate(openingDate);
		String formattedClosingDate = Utilities.getInstance().formatDate(closingDate);
		
		String managerName = "";
		
		if (managerInCharge != null) {
			managerName = managerInCharge.getName();
		}
		
		String officerNames = CSVEncoder.encodeListOfStrings(getHDBOfficersNames());
		
		return String.format("%s,%s,%s,%d,%d,%s,%d,%d,%s,%s,%s,%d,%s,%s",
				projectName,
				neighborhood,
				roomOneType.toString(),
				roomOne.getRemainingUnits(),
				roomOne.getPrice(),
				roomTwoType.toString(),
				roomTwo.getRemainingUnits(),
				roomTwo.getPrice(),
				formattedOpeningDate,
				formattedClosingDate,
				managerName,
				totalOfficerSlots,
				officerNames,
				CSVEncoder.encodeBoolean(visibility)
				);
	}

	@Override
	public CSVFileTypes sourceFileType() {
		// TODO Auto-generated method stub
		return CSVFileTypes.PROJECT_LIST;
	}
	
	/**
	 * Use this to draw a table of projects.
	 * 
	 * Work In Progress. Expected to be more customisable, depending on the needs.
	 * 
	 * @param projects List of projects to be displayed.
	 * @param displayIndex If true, there will be an additional column on the far left denoting list position index of row.
	 */
	static void display(List<BTOProject> projects, boolean displayIndex) {
		
		List<String> values = new ArrayList<>();
		
		int index = 0;
		
		for (BTOProject project : projects) {
			HDBManager manager = project.getManagerInCharge();
			String managerName = manager == null ? " " : manager.getName();
			
			String formattedOpenDate = Utilities.getInstance().formatUserReadableDate(project.openingDate);
			String formattedCloseDate = Utilities.getInstance().formatUserReadableDate(project.closingDate);
			
			if (displayIndex) {
				values.add(String.format("%3d. │ %-25s │ %-25s │ %-12s │ %-15s │ %-15s", index + 1, project.getProjectName(), project.getNeighborhood(), managerName, formattedOpenDate, formattedCloseDate));
			}
			else {
				values.add(String.format("%-25s │ %-25s │ %-12s │ %-15s │ %-15s", project.getProjectName(), project.getNeighborhood(), managerName, formattedOpenDate, formattedCloseDate));
			}
			
			index++;
		}
		String header;
		
		if (displayIndex) {
			header = String.format("%3s    %-25s │ %-25s │ %-12s │ %-15s │ %-15s", "No.", "Name", "Neighborhood", "Manager", "Opening Date", "Closing Date");
		}
		else {
			header = String.format("%-25s │ %-25s │ %-12s │ %-15s │ %-15s", "Name", "Neighborhood", "Manager", "Opening Date", "Closing Date");
		}
		
		new DisplayMenu.Builder()
			.addContent(header)
			.addContents(values)
			.build()
			.display();
	}
}