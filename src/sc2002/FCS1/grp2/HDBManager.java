package sc2002.FCS1.grp2;

import java.util.ArrayList;
import java.util.Scanner;

public class HDBManager extends User {

	Scanner scanner = new Scanner(System.in);
	public HDBManager(ArrayList<CSVCell> cells) {
		super(cells);
	}

	
//	public void createHDBProject() {
//		
//
//
//	}
	
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
	
	public CSVFileTypes sourceFileType() {
		return CSVFileTypes.MANAGER_LIST;
	}
	
	ArrayList<String> getMenu() {
		ArrayList<String> list = super.getMenu();
		
		for (UserMenu menu : Menu.values()) {
			list.add(menu.getMenuName());
		}
		
		return list;
	}
	
	enum Menu implements UserMenu {
		CREATE_PROJECT;
		
		public String getMenuName() {
			switch (this) {
			case CREATE_PROJECT: 
				return "Create Project";
			default:
				return null;
			}
		}
		
		public static Menu fromOrdinal(int o) {
			return Menu.values()[o];
		}
	}

	@Override
	String getReadableTypeName() {
		// TODO Auto-generated method stub
		return "HDB Manager";
	}
}

interface UserMenu {
	public String getMenuName();
}
