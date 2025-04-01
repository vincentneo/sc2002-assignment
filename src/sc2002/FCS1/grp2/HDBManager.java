package sc2002.FCS1.grp2;

import java.util.ArrayList;
import java.util.Scanner;

public class HDBManager extends User {

	Scanner scanner = new Scanner(System.in);
	public HDBManager(ArrayList<CSVCell> cells) {
		super(cells);
	}

	
	public void createHDBProject() {
		
		System.out.println("Enter Project Name: ");
		String ProjectName = scanner.toString();
		System.out.println("Enter Neighbourhood: ");
		String Neighborhood = scanner.toString();
		System.out.println("Enter Maximum two room units: ");
		int maxTwoRoomUnits = scanner.nextInt();
		System.out.println("Enter Maximum three room units: ");
		int maxThreeRoomUnits = scanner.nextInt();
		System.out.println("Enter Opening Date: ");
		String openingDate = scanner.toString();
		System.out.println("Enter Closing Date: ");
		String closingDate = scanner.toString();
		System.out.println("Enter number of officer slots: ");
		int officerSlots = scanner.nextInt();
		ArrayList<HDBOfficer> officers; //;
		// to do officers
		
		
		BTOProject project = new BTOProject(ProjectName, Neighborhood, maxTwoRoomUnits, maxThreeRoomUnits, openingDate, closingDate,  this, officerSlots, officers);
		

	}
	
	public void editHDBProject(String ProjectName) {
		
			
			
		}
		
		
	
	public void deleteHDBProject(String ProjectName) {
		
	}
	
	public void editVisibility() {
		
	}
	
	//to do view projects created by others
	
	boolean canApplyProject() {
		return false;
	}
}
