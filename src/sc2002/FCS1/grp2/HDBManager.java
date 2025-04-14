package sc2002.FCS1.grp2;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class HDBManager extends User {

	Scanner scanner = new Scanner(System.in);
	public HDBManager(List<CSVCell> cells) {
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
		CREATE_PROJECT, EDIT_PROJECT, DELETE_PROJECT,
		VIEW_ALL_ENQUIRIES;
		
		public String getOptionName() {
			switch (this) {
			case CREATE_PROJECT: 
				return "Create Project";
			case EDIT_PROJECT:
				return "Edit Project";
			case DELETE_PROJECT:
				return "Delete Project";
			case VIEW_ALL_ENQUIRIES:
				return "View All Enquiries";
			default:
				return null;
			}
		}
		
		public static Menu[] allMenuOptions = Menu.values();
		
		public static Menu fromOrdinal(int o) {
			if (o >= allMenuOptions.length) return null;
			return allMenuOptions[o];
		}
	}

	@Override
	String getReadableTypeName() {
		// TODO Auto-generated method stub
		return "HDB Manager";
	}
}
