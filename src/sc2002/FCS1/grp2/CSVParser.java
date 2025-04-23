package sc2002.FCS1.grp2;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * This class parses CSV files.
 * 
 */
public class CSVParser {
	private String path;
	
	/**
	 * Use this constructor if you would like to parse CSV files in a non-default directory.
	 * 
	 * @param path Direct path to directory that stores the CSV files relevant to this BTO application.
	 */
	CSVParser(String path) {
		this.path = path;
	}
	
	/**
	 * Use this constructor to parse CSVs as expected in default CSV file directory.
	 * 
	 * Before compiling the project, place the CSV files in the sc2002.FCS1.grp2.files folder.
	 * After compilation, these files will be in the bin directory, files folder, along with the built class files.
	 * 
	 * This constructor will automatically parse files based on this directory.
	 */
	CSVParser() {
		String classpath = System.getProperty("java.class.path");
		String filesPath = classpath + "/sc2002/FCS1/grp2/files/";
		this.path = filesPath;
	}
	
	private<Decodable extends CSVDecodable> ArrayList<Decodable> parse(String path, Class<Decodable> type) {
		ArrayList<Decodable> decodables = new ArrayList<>();
		try {
			
			File file = new File(path);
			Scanner scanner = new Scanner(file);
			
			// skip first line
			scanner.nextLine();
			
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				
				ArrayList<CSVCell> cells = parseLine(line);
				Decodable object = type.getDeclaredConstructor(List.class).newInstance(cells);
				decodables.add(object);
			}
			
			scanner.close();
		}
		catch (FileNotFoundException e) { // while file has not been created yet (i.e. have not prev used enquiries)
			return decodables;
		}
		catch (NoSuchElementException e) { // when file exists but is absolutely empty
			return decodables;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return decodables;
	}
	
	/**
	 * Parse each line, with special handling for CSV cells with list values.
	 * @param line A CSV line.
	 * @return a list of cells, of that line.
	 */
	private ArrayList<CSVCell> parseLine(String line) {
		ArrayList<CSVCell> cells = new ArrayList<>();
		
		String current = "";
		boolean inString = false;
		
		for (char c : line.toCharArray()) {
			if (c == '"') {
				inString = !inString;
			}
			else if (c == ',') {
				if (inString) {
					current += c;
					continue;
				}
				cells.add(new CSVCell(current));
				current = "";
			}
			else {
				current += c;
			}
			
		}
		
		cells.add(new CSVCell(current));
		
		return cells;
	}
	
	ArrayList<Applicant> parseApplicants() {
		String filePath = path + CSVFileTypes.APPLICANT_LIST.getFileName();
		return parse(filePath, Applicant.class);
	}
	
	ArrayList<HDBManager> parseManagers() {
		String filePath = path + CSVFileTypes.MANAGER_LIST.getFileName();
		return parse(filePath, HDBManager.class);
	}
	
	ArrayList<HDBOfficer> parseOfficer() {
		String filePath = path + CSVFileTypes.OFFICER_LIST.getFileName();
		return parse(filePath, HDBOfficer.class);
	}
	
	ArrayList<BTOProject> parseProjects() {
		String filePath = path + CSVFileTypes.PROJECT_LIST.getFileName();
		return parse(filePath, BTOProject.class);
	}
	
	ArrayList<Enquiry> parseEnquiries() {
		String filePath = path + CSVFileTypes.ENQUIRIES_LIST.getFileName();
		return parse(filePath, Enquiry.class);
	}
	
	ArrayList<Application> parseApplications() {
		String filePath = path + CSVFileTypes.APPLICATIONS_LIST.getFileName();
		return parse(filePath, Application.class);
	}
	
	/**
	 * Parse all users, regardless of user type.
	 * @return all user objects.
	 */
	ArrayList<User> retrieveAllUsers() {
		ArrayList<User> users = new ArrayList<>();
		users.addAll(parseApplicants());
		users.addAll(parseManagers());
		users.addAll(parseOfficer());
		return users;
	}
}
