package sc2002.FCS1.grp2;

import java.util.ArrayList;

public class HDBManager extends User {

	public HDBManager(String line) {
		super(line);
		
	}

	
	
	
	public void createHDBProject() {
		
		System.out.println();
		
		BTOProject project = new BTOProject(projectName, neighborhood, maxTwoRoomUnits, maxThreeRoomUnits, openingDate, closingDate,  managerInCharge, officerSlots, officers);
		

	}
	
	public void  editHDBProject(String ProjectName) {
		
		
	}
	
	public void deleteHDBProject() {
		
	}
	
	public void editVisibility() {
		
	}
	
	//to do view projects created by others
	
	boolean canApplyProject() {
		return false;
	}
}
