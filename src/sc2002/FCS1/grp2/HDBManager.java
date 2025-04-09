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
	
	public CSVFileTypes sourceFileType() {
		return CSVFileTypes.MANAGER_LIST;
	}
	
	/**
	 * Prepares a list of menu options that includes options specific to managers only.
	 * 
	 *  @return list of menu options, including both common and access control scoped options.
	 */
	@Override
	ArrayList<String> getMenu() {
		return super.getMenuWithScopedOptions(Menu.allMenuOptions);
	}
	
	/**
	 * Possible menu options for a HDB Manager role
	 * 
	 * Each option listed here are options that only a HDB Manager can interact with. 
	 */
	enum Menu implements ScopedOption {
		CREATE_PROJECT;
		
		public String getOptionName() {
			switch (this) {
			case CREATE_PROJECT: 
				return "Create Project";
			default:
				return null;
			}
		}
		
		public static Menu[] allMenuOptions = Menu.values();
		
		public static Menu fromOrdinal(int o) {
			return allMenuOptions[o];
		}
	}

	@Override
	String getReadableTypeName() {
		// TODO Auto-generated method stub
		return "HDB Manager";
	}
}
