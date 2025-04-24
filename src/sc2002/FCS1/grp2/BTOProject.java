package sc2002.FCS1.grp2;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import sc2002.FCS1.grp2.builders.DisplayMenu;
import sc2002.FCS1.grp2.helpers.Utilities;

import java.util.EnumMap;
import java.util.Set;


/**
 * A class representing BTO Project containing information for the project and has functions to modify the project information and handle users' applications to the project.
 * 
 *  @author Ryu Hyungjoon
 */
public class BTOProject extends CSVDecodable implements CSVEncodable {
    /**
     * name of project
     */
    private String projectName;
    /**
     * neighbourhood which project belong to
     */
    private String neighborhood;
    
    /** flat info, based on flat type */
    private EnumMap<FlatType,FlatInfo> flats = new EnumMap<>(FlatType.class);

    /** Opening date of project */
    private LocalDate openingDate;
    /** closing date of project */
    private LocalDate closingDate;

    /** temporary value for decoding and linking purposes. */
    private String managerName;
    /** the manager in charge of this project */
    private HDBManager managerInCharge;

    /** slots of project, for project. does not decrement */
    private int totalOfficerSlots;
    
    /** temporary value for decoding and linking purposes. */
    private List<String> officerNames;
    /** officers of this project */
    private List<HDBOfficer> officers;

    /** whether this is pubically visible */
    private boolean visibility;
    
     /** temporary value for decoding and linking purposes. */
    private List<String> pendingOfficerNames;
    /** Officers that are pending approval */
    private List<HDBOfficer> pendingOfficers;

    /** Number of officers at max */
    static private int MAX_OFFICER_NUM = 10;
    
	//region Consturctors
    //Construct with a string parsed from a csv file.
    public BTOProject (List<CSVCell> cells) throws Exception {
    	super(cells);
		projectName = cells.get(0).getValue();
		neighborhood = cells.get(1).getValue();
		
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

		openingDate = cells.get(8).getDateValue();
		closingDate = cells.get(9).getDateValue();
        
        managerName = cells.get(10).getValue();
        totalOfficerSlots = cells.get(11).getIntValue();
        
        String[] officerCell = cells.get(12).getValuesOrValue();
        officerNames = Arrays.asList(officerCell);
        
        int colSize = cells.size();
        
        if (colSize > 13) {
        	try {
        		boolean visibility = cells.get(13).getBoolValue();
        		this.visibility = visibility;
        	}
        	catch (Exception e) {
        		this.visibility = true;
        	}
        }
        
        if (colSize > 14) {
        	this.pendingOfficerNames = Arrays.asList(cells.get(14).getValuesOrValue());
        }
        else {
        	this.pendingOfficerNames = new ArrayList<>();
        }
        

        if (projectName == null || projectName.isEmpty()) {
            throw new IllegalArgumentException("Project name cannot be empty.");
        }
        else if (neighborhood == null || neighborhood.isEmpty()) {
            throw new IllegalArgumentException("Neighborhood cannot be empty.");
        }
        else if (openingDate.isAfter(closingDate)) { // the parse method throws a DateTimeParseExecption if the string is in wrong format.
            throw new IllegalArgumentException("Opening date cannot be after closing date.");
        }
        else if (totalOfficerSlots < 0) {
            throw new IllegalArgumentException("Number of price cannot be negative.");
        }
    }

    //Construct with values
    /**
     * Constructor for BTOProject class when manager creates a new BTOProject.
     * @param projectName Name of the project.
     * @param neighborhood Neighborhood of the project.
     * @param maxTwoRoomUnits Maximum number of two room units in the project.
     * @param maxThreeRoomUnits Maximum number of three room units in the project.
     * @param twoRoomPrice Price of two room units in the project.
     * @param threeRoomPrice Price of three room units in the project.
     * @param openingDate Opening date of the project.
     * @param closingDate Closing date of the project.
     * @param managerInCharge Manager in charge of the project.
     * @param totalOfficerSlots Total number of officer slots available for the project.
     * @throws Exception if any of the input values are invalid
     */
    public BTOProject (String projectName, String neighborhood, int maxTwoRoomUnits, int maxThreeRoomUnits, int twoRoomPrice, int threeRoomPrice, LocalDate openingDate, LocalDate closingDate, HDBManager managerInCharge, int totalOfficerSlots)
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
        
        FlatType type1 = FlatType.TWO_ROOM;
		FlatInfo info1 = new FlatInfo(type1, maxTwoRoomUnits, twoRoomPrice);
		
		FlatType type2 = FlatType.THREE_ROOM;
		FlatInfo info2 = new FlatInfo(type2, maxThreeRoomUnits, threeRoomPrice);
		
		flats.put(type1, info1);
		flats.put(type2, info2);


        this.openingDate = openingDate;
        this.closingDate = closingDate;

        this.managerInCharge = managerInCharge;
        this.totalOfficerSlots = totalOfficerSlots;
        this.officers = new ArrayList<>();
        this.officerNames = new ArrayList<>();
        
        this.visibility = true;
        
        this.pendingOfficers = new ArrayList<>();
        this.pendingOfficerNames = new ArrayList<>();
    }
    //endregion
    
    /**
     * Retrieve the connected officers and manager as an object for this project by comparing the names the project has.
     * @param officers List of officer objects
     * @param managers List of manager objects
     */
    public void retrieveConnectedUsers(ArrayList<HDBOfficer> officers, ArrayList<HDBManager> managers) {
    	
    	if (officerNames != null) {
	    	this.officers = officers
					    		.stream()
					    		.filter(o -> officerNames.contains(o.getName()))
					    		.collect(Collectors.toList());
	    	this.officerNames = null;
    	}
    	
    	if (pendingOfficerNames != null) {
	    	this.pendingOfficers = officers
		    		.stream()
		    		.filter(o -> pendingOfficerNames.contains(o.getName()))
		    		.collect(Collectors.toList());
	    	this.pendingOfficerNames = null;
    	}
    	
    	
    	this.managerInCharge = managers
    			.stream()
    			.filter(m -> m.getName().equals(managerName))
    			.findFirst()
    			.orElse(null);
    	this.managerName = null;
    }
    
    public boolean containsUnitsThatFitsPriceRange(int min, int max) {
    	for (FlatType type : flats.keySet()) {
    		FlatInfo flat = flats.get(type);
    		int price = flat.getPrice();
    		
    		if (price >= min && price <= max) return true;
    	}
    	return false;
    }

    //region Project Name
    /**
     * Get the project name of the BTO project.
     * @return the project name of the BTO project.
     */
    public String getProjectName() {
        return projectName;
    }

    /**
     * Set the project name of the BTO project.
     * @param name the new project name of the BTO project.
     */
    public void setProjectName(String name) {
        projectName = name;
    }
    //endregion

    //region Neighborhood
    /**
     * Get the neighborhood of the BTO project.
     * @return the neighborhood of the BTO project.
     */
    public String getNeighborhood() {
        return neighborhood;
    }

    /**
     * Set the neighborhood of the BTO project.
     * @param neighborhood the new neighborhood of the BTO project.
     */
    public void setNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
    }
    //endregion 
    

    //region Room Units
    //TODO: Get and set for room units
    
    /**
     * Get the applicable flat information for specific flat types.
     * @param applicableTypes Set of flat types to get applicable flats for.
     * @return  List of FlatInfo objects representing the applicable flats for the given flat types.
     */
    public List<FlatInfo> getApplicableFlats(Set<FlatType> applicableTypes) {
    	ArrayList<FlatInfo> applicableFlats = new ArrayList<>();
    	for (FlatType type : applicableTypes) {
    		applicableFlats.add(flats.get(type));
    	}
    	
    	return applicableFlats;
    }
    
    /**
     * Get the flat information for a specific flat type.
     * @param type The flat type to get information for.
     * @return The FlatInfo object representing the flat information for the given flat type.
     */
    public FlatInfo getFlatForType(FlatType type) {
    	return flats.get(type);
    }
    
    /**
     * Set the number of two room units available in the project.
     * @param twoRoomUnits Number of two room units to set.
     * @throws IllegalArgumentException if the number of two room units is negative.
     */
    public void setTwoRoomUnits(int twoRoomUnits) throws IllegalArgumentException{
    	if(twoRoomUnits < 0) {
    		throw new IllegalArgumentException("Number of 2 room units cannot be negative");
    	}
    	this.flats.get(FlatType.TWO_ROOM).setRemainingUnits(twoRoomUnits);
    }
    
    /**
     * Set the number of three room units available in the project.
     * @param threeRoomUnits Number of three room units to set.
     * @throws IllegalArgumentException if the number of three room units is negative.
     */
    public void setThreeRoomUnits(int threeRoomUnits) throws IllegalArgumentException{
    	if(threeRoomUnits < 0) {
    		throw new IllegalArgumentException("Number of 3 room units cannot be negative");
    	}
    	this.flats.get(FlatType.THREE_ROOM).setRemainingUnits(threeRoomUnits);
    }
    
    /**
     * Get the number of two room units available in the project.
     * @return Number of two room units available in the project.
     */
    public int getTwoRoomUnits() {
    	return this.flats.get(FlatType.TWO_ROOM).getRemainingUnits();
    }
    
    /**
     * Get the number of three room units available in the project.
     * @return Number of three room units available in the project.
     */
    public int getThreeRoomUnits() {
    	return this.flats.get(FlatType.THREE_ROOM).getRemainingUnits();
    }

    /**
     * Set the price of two room units in the project.
     * @param twoRoomPrice Price of two room units to set.
     * @throws IllegalArgumentException if the price is negative.
     */
    public void setTwoRoomPrice(int twoRoomPrice) throws IllegalArgumentException {
        if (twoRoomPrice < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        this.flats.get(FlatType.TWO_ROOM).setPrice(twoRoomPrice);
    }

    /**
     * Set the price of three room units in the project.
     * @param threeRoomPrice Price of three room units to set.
     * @throws IllegalArgumentException if the price is negative.
     */
    public void setThreeRoomPrice(int threeRoomPrice) throws IllegalArgumentException {
        if (threeRoomPrice < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        this.flats.get(FlatType.THREE_ROOM).setPrice(threeRoomPrice);
    }

    /**
     * Get the price of two room units in the project.
     * @return Price of two room units in the project.
     */
    public int getTwoRoomPrice() {
        return this.flats.get(FlatType.TWO_ROOM).getPrice();
    }

    /**
     * Get the price of three room units in the project.
     * @return Price of three room units in the project.
     */
    public int getThreeRoomPrice() {
        return this.flats.get(FlatType.THREE_ROOM).getPrice();
    }

    //endregion

    //region Dates

    /**
     * Get the opening date of the project.
     * @return The opening date of the project.
     */
    public LocalDate getOpeningDate() {
        return openingDate;
    }

    /**
     * Get the closing date of the project.
     * @return The closing date of the project.
     */
    public LocalDate getClosingDate() {
        return closingDate;
    }

    /**
     * Get the opening date as a string.
     * @return The opening date as a string.
     */
    public String getStringOpeningDate() {
        return openingDate.toString();
    }

    /**
     * Get the closing date as a string.
     * @return The closing date as a string.
     */
    public String getStringEndDate() {
        return closingDate.toString();
    }

    /**
     * Set the opening date of the project.
     * @param date The new opening date of the project.
     * @throws DateTimeException if the new opening date is after the closing date.
     */
    public void setOpeningDate(String date) throws DateTimeException {
        setOpeningDate(LocalDate.parse(date));
    }

    /**
     * Set the opening date of the project.
     * @param date The new opening date of the project.
     * @throws DateTimeException if the new opening date is after the closing date.
     */
    public void setOpeningDate(LocalDate date) throws DateTimeException {
        if(date.compareTo(closingDate) > 0) {
            throw new DateTimeException("The new opening date is greater than the closing date!");
        }
        openingDate = date;
    }

    /**
     * Set the closing date of the project.
     * @param date The new closing date of the project.
     * @throws DateTimeException if the new closing date is before the opening date.
     */
    public void setClosingDate(String date) throws DateTimeException {
        setClosingDate(LocalDate.parse(date));
    }

    /**
     * Set the closing date of the project.
     * @param date The new closing date of the project.
     * @throws DateTimeException if the new closing date is before the opening date.
     */
    public void setClosingDate(LocalDate date) throws DateTimeException {
        if(date.compareTo(openingDate) < 0) {
            throw new DateTimeException("The new closing date is lesser than the opening date!");
        }
        closingDate = date;
    }

    /**
     * Check if the given date is within the opening and closing dates of the project.
     * @param date The date to check.
     * @return true if the date is within the opening and closing dates, false otherwise.
     */
    public boolean isDateWithin(String date) {
        return isDateWithin(LocalDate.parse(date));
    }

    /**
     * Check if the given date is within the opening and closing dates of the project.
     * @param date The date to check.
     * @return true if the date is within the opening and closing dates, false otherwise.
     */
    public boolean isDateWithin(LocalDate date) {
        return date.compareTo(closingDate) <= 0 && date.compareTo(openingDate) >= 0;
    }
    //endregion

    //region Visibility 
    /**
     * Get the visibility of the project.
     * @return true if the project is visible, false otherwise.
     */
    public boolean getVisibility() {
        return visibility;
    }

    /**
     * Toggle the visibility of the project.
     */
    public void toggleVisibility() {
        visibility = !visibility;
    }

    /**
     * Set the visibility of the project.
     * @param visibility The new visibility of the project.
     */
    public void setVisibility(boolean visibility) {
        this.visibility = visibility;
    }
    //endregion

    //region Manager In Charge
    /**
     * Get the object of the manager in charge of the project.
     * @return The object of the manager in charge of the project.
     */
    public HDBManager getManagerInCharge() {
        return managerInCharge;
    }
    //endregion

    //region Officers
    /**
     * Get the total number of officer slots available for the project.
     * @return The total number of officer slots available for the project.
     */
    public int getTotalOfficerSlots() {
        return totalOfficerSlots;
    }

    /**
     * Set the total number of officer slots available for the project.
     * @param slots The new total number of officer slots available for the project.
     * @throws IllegalArgumentException if the number of slots is negative, is below the number of current officers, or exceeds the maximum number of officers.
     */
    public void setTotalOfficerSlots(int slots) throws IllegalArgumentException{
        if (slots > MAX_OFFICER_NUM) {
            throw new IllegalArgumentException("Invalid Input! The number exceeds maximum officer slots (" + MAX_OFFICER_NUM + ").");
        }
        else if (slots < officers.size()) {
            throw new IllegalArgumentException("Invalid Input! The number is below the current number of assigned HBD officers (" + officers.size() + ").");
        }
        else if (slots < 0) {
            throw new IllegalArgumentException("Invalid Input! The number cannot be below zero.");
        }

        totalOfficerSlots = slots;
    }

    /**
     * Get the list of officers assigned to the project.
     * @return The list of officers assigned to the project.
     */
    public List<HDBOfficer> getOfficers() {
        return officers;
    }

    /**
     * Add an officer to the project.
     * @param officer The officer to add.
     * @throws IllegalArgumentException if the officer is null, already registered, or if the project is full.
     */
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

    /**
     * Remove an officer from the project.
     * @param officer The officer to remove.
     * @throws IllegalArgumentException if the officer is not registered or if the project has no officers.
     */
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
    
    public void addCurrentUserToPendingList() throws Exception {
    	BTOManagementSystem system = BTOManagementSystem.common();
    	HDBOfficer officer = system.getActiveUserForPermittedTask(HDBOfficer.class);
    	pendingOfficers.add(officer);
    }
    
    public void removeOfficerFromPendingList(HDBOfficer officer) throws Exception {
    	BTOManagementSystem system = BTOManagementSystem.common();
		if (!system.isActiveUserPermitted(HDBManager.class)) throw new InsufficientAccessRightsException();
    	if (pendingOfficers.contains(officer)) pendingOfficers.remove(officer);
    }
    
    public List<HDBOfficer> getPendingOfficers() {
    	return pendingOfficers;
    }

    /**
     * Print the list of applications for the project.
     */
    // public void printApplications() {
    //     if (applications.isEmpty()) {
    //         System.out.println("There is no applications for project " + projectName + ".");
    //         return;
    //     }

    //     for(int i = 0; i < applications.size(); i++) {
    //         System.out.println((i + 1) + ": " + applications.get(i));
    //     }
    // }
    //endregion
    
    /**
     * Check if the given flat types are eligible for application.
     * @param flatTypes Set of flat types to check for eligibility.
     * @return true if the project has available units for the given flat types, false otherwise.
     */
    public boolean isEligible(Set<FlatType> flatTypes) {
    	for (FlatType type : flatTypes) {
    		if (flats.get(type).getRemainingUnits() > 0) return true;
    	}
    	
    	return false;
    }
    
    /**
     * Get the list of officer names assigned to the project.
     * @return List of officer names assigned to the project.
     */
    private List<String> getHDBOfficersNames() {
    	return officers
    			.stream()
    			.map(o -> o.getName())
    			.collect(Collectors.toList());
    }

    /**
     * Get the list of pending approval officer's names to the project.
     * @return List of pending approval officer's names assigned to the project.
     */
    private List<String> getPendingOfficersNames() {
    	return pendingOfficers
    			.stream()
    			.map(o -> o.getName())
    			.collect(Collectors.toList());
    }

    boolean bookFlat(FlatType type) throws Exception {
        if (!BTOManagementSystem.common().isActiveUserPermitted(HDBOfficer.class)) throw new InsufficientAccessRightsException();
        int currentUnits = flats.get(type).getRemainingUnits();

        if (currentUnits > 0) {
            flats.get(type).setRemainingUnits(currentUnits - 1);
            return true;
        }
        return false;
    }

    /**
     * Get the string representation of the BTO project.
     * @return The string representation of the BTO project.
     */
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

    /**
     * Encode the BTO project to a CSV string.
     * @return The CSV string representation of the BTO project.
     */
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
		
		return String.format("%s,%s,%s,%d,%d,%s,%d,%d,%s,%s,%s,%d,%s,%s,%s",
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
				CSVEncoder.encodeBoolean(visibility),
				CSVEncoder.encodeListOfStrings(getPendingOfficersNames())			
		);
	}

    /**
     * Get the type of the source file for the BTO project.
     * @return The type of the source file for the BTO project.
     */
	@Override
	public CSVFileTypes sourceFileType() {
		return CSVFileTypes.PROJECT_LIST;
	}
	
	/**
	 * Use this to draw a table of projects.
	 * 
	 * @param projects List of projects to be displayed.
	 * @param options Additional columns to display.
	 */
	static void display(List<BTOProject> projects, List<TableColumnOption> options) {
        if(projects == null || projects.isEmpty()) {
            System.out.println("No projects to display.");
            return;
        }
        
		List<String> values = new ArrayList<>();
		
		String headerFormat = "";
		String valueFormat = "";
		
		List<String> headerFormatList = new ArrayList<>();
		List<String> valueFormatList = new ArrayList<>();
		
		if (options.contains(TableColumnOption.INDEX_NUMBER)) {
			headerFormatList.add("%-" + TableColumnOption.INDEX_NUMBER.getHeaderSpacing() + "s");
			valueFormat += ("%-" + TableColumnOption.INDEX_NUMBER.getValueSpacing() + "s");
		}
		
		// project name
		headerFormatList.add("%-25s");
		valueFormatList.add("%-25s");
		
		// neighborhood
		headerFormatList.add("%-25s");
		valueFormatList.add("%-25s");
		
		for (TableColumnOption option : options) {
			if (option == TableColumnOption.INDEX_NUMBER) continue;
			headerFormatList.add("%-" + option.getHeaderSpacing() + "s");
			valueFormatList.add("%-" + option.getValueSpacing() + "s");
		}
		
		headerFormat = String.join(" │ ", headerFormatList);
		valueFormat += String.join(" │ ", valueFormatList);
		
		int index = 0;
		
		for (BTOProject project : projects) {
			List<String> row = new ArrayList<>();
			
			if (options.contains(TableColumnOption.INDEX_NUMBER)) {
				row.add("" + (index + 1) + ".");
			}
			
			row.add(project.getProjectName());
			row.add(project.getNeighborhood());
			
			for (TableColumnOption option : options) {
				switch (option) {
				case INDEX_NUMBER:
					continue;
				case MANAGER:
					row.add(project.getManagerInCharge().getName());
					break;
				case OFFICERS: {
					String joined = String.join(", ", project.officers.stream().map(o -> o.getName()).toList());
					row.add(joined);
					break;
				}
				case OFFICER_SLOTS:
					row.add("" + project.totalOfficerSlots);
					break;
				case OPENING_DATE:
					String formattedOpenDate = Utilities.getInstance().formatUserReadableDate(project.openingDate);
					row.add(formattedOpenDate);
					break;
				case CLOSING_DATE:
					String formattedCloseDate = Utilities.getInstance().formatUserReadableDate(project.closingDate);
					row.add(formattedCloseDate);
					break;
				case ROOM_ONE_PRICE:
					row.add("$" + project.getTwoRoomPrice());
					break;
				case ROOM_ONE_UNITS:
					row.add("" + project.getTwoRoomUnits());
					break;
				case ROOM_TWO_PRICE:
					row.add("$" + project.getThreeRoomPrice());
					break;
				case ROOM_TWO_UNITS:
					row.add("" + project.getThreeRoomUnits());
					break;
				case VISIBILITY:
					row.add(project.getVisibility() ? "Yes" : "No");
					break;
				case PENDING_OFFICERS: {
					row.add("" + project.getPendingOfficersNames().size());
				}
				default:
					break;
				}
			}
			
			index++;
			
			values.add(String.format(valueFormat, row.toArray()));
		}
		String header;

		List<String> headerRow = new ArrayList<>();
		
		if (options.contains(TableColumnOption.INDEX_NUMBER)) {
			headerRow.add(TableColumnOption.INDEX_NUMBER.getTitle());
		}
		
		headerRow.add("Name");
		headerRow.add("Neighborhood");
		
		for (TableColumnOption option : options) {
			if (option == TableColumnOption.INDEX_NUMBER) continue;
			headerRow.add(option.getTitle());
		}
		
		header = String.format(headerFormat, headerRow.toArray());
		new DisplayMenu.Builder()
			.addContent(header)
			.addContents(values)
			.build()
			.display();
	}
	
    /** Options to decide which columns to be displayed */
	enum TableColumnOption {
		INDEX_NUMBER(3,6, "No."),
		MANAGER(12, "Manager"),
		ROOM_ONE_UNITS(15, "2-Room Units"),
		ROOM_ONE_PRICE(15, "2-Room Price"),
		ROOM_TWO_UNITS(15, "3-Room Units"),
		ROOM_TWO_PRICE(15, "3-Room Price"),
		OPENING_DATE(15, "Opening Date"),
		CLOSING_DATE(15, "Closing Date"),
		OFFICER_SLOTS(16, "Officer Slots"),
		OFFICERS(30, "Officers"),
		VISIBILITY(10, "Visibility"),
		PENDING_OFFICERS(30, "Pending Officers");

		private int headerSpacing;
		private int valueSpacing;
		private String title;
		
		private TableColumnOption(int common, String title) {
			this.headerSpacing = common;
			this.valueSpacing = common;
			this.title = title;
		}
		
		private TableColumnOption(int header, int value, String title) {
			this.headerSpacing = header;
			this.valueSpacing = value;
			this.title = title;
		}
		
        public int getHeaderSpacing() {
			return headerSpacing;
		}

		public int getValueSpacing() {
			return valueSpacing;
		}
		
		public String getTitle() {
			return title;
		}
	}
}