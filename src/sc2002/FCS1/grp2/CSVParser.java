package sc2002.FCS1.grp2;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class CSVParser {
	String path;
	
	CSVParser(String path) {
		this.path = path;
	}
	
	CSVParser() {
		String classpath = System.getProperty("java.class.path");
		String filesPath = classpath + "/sc2002/FCS1/grp2/files/";
		this.path = filesPath;
	}
	
	ArrayList<User> parseApplicants() {
		ArrayList<User> applicants = new ArrayList<>();
		try {
			
			File file = new File(path + "ApplicantList.csv");
			Scanner scanner = new Scanner(file);
			
			// skip first line
			scanner.nextLine();
			
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				applicants.add(new Test(line));
			}
			
			scanner.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return applicants;
	}
}
