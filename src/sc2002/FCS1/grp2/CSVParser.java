package sc2002.FCS1.grp2;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class CSVParser {
	private String path;
	
	CSVParser(String path) {
		this.path = path;
	}
	
	CSVParser() {
		String classpath = System.getProperty("java.class.path");
		String filesPath = classpath + "/sc2002/FCS1/grp2/files/";
		this.path = filesPath;
	}
	
	private <U extends User> ArrayList<U> parse(String path, Class<U> type) {
		ArrayList<U> users = new ArrayList<>();
		try {
			
			File file = new File(path);
			Scanner scanner = new Scanner(file);
			
			// skip first line
			scanner.nextLine();
			
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				users.add(type.getDeclaredConstructor(String.class).newInstance(line));
			}
			
			scanner.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return users;
	}
	
	ArrayList<Applicant> parseApplicants() {
		String filePath = path + "ApplicantList.csv";
		return parse(filePath, Applicant.class);
	}
	
	ArrayList<HDBManager> parseManagers() {
		String filePath = path + "ManagerList.csv";
		return parse(filePath, HDBManager.class);
	}
	
	ArrayList<HDBOfficer> parseOfficer() {
		String filePath = path + "OfficerList.csv";
		return parse(filePath, HDBOfficer.class);
	}
	
	ArrayList<User> retrieveAllUsers() {
		ArrayList<User> users = new ArrayList<>();
		users.addAll(parseApplicants());
		users.addAll(parseManagers());
		users.addAll(parseOfficer());
		return users;
	}
}
