package sc2002.FCS1.grp2;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Use this class to write CSV files for each supported types (such as the users and projects).
 * 
 * Encodable classes must subscribe to {@code CSVEncodable} interface, in order to use for this class.
 */
public class CSVEncoder {
	
	/**
	 * The path for which the CSV file should be written at.
	 */
	private String path;
	
	/**
	 * Represents the header row.
	 */
	private String headerLine;
	
	/**
	 * Each row of data that should be written in the CSV file.
	 */
	private ArrayList<CSVEncodable> encodables;
	
	/**
	 * Construct using this constructor if CSV file is expected at the default location. (bin/sc2002/FCS1/grp2/files)
	 * @param fileName The name of the file.
	 * @param encodables Data that represents each row of the CSV file to be written.
	 */
	CSVEncoder(String fileName, String headerLine, ArrayList<CSVEncodable> encodables) {
		String classpath = System.getProperty("java.class.path");
		String filesPath = classpath + "/sc2002/FCS1/grp2/files/";
		this.path = filesPath + fileName;
		this.headerLine = headerLine;
		this.encodables = encodables;
	}
	
	/**
	 * Call this method to write the CSV file with the respective data at path.
	 * @throws java.io.IOException if path provided could not be used to write the file, for whatever reason.
	 */
	public void encode() throws IOException {
		// TODO: support case whereby callable without retrieval of header row.
		//File file = new File(path);
		//Scanner reader = new Scanner(file);
		
		// get the header row from the current file.
		//String headerLine = reader.nextLine();
		//reader.close();
		
		FileWriter writer = new FileWriter(path);
		BufferedWriter bufferedWriter = new BufferedWriter(writer);
		
		// write the header row.
		bufferedWriter.write(headerLine);
		bufferedWriter.newLine();
		
		for (CSVEncodable encodable : encodables) {
			bufferedWriter.write(encodable.encode());
			bufferedWriter.newLine();
		}
		
		bufferedWriter.close();
	}
	
	/**
	 * Encode Booleans 
	 * 
	 * In caps, as it seems Excel does it this way.
	 * @param bool The boolean
	 * @return encoded boolean ready for a CSV cell.
	 */
	public static String encodeBoolean(boolean bool) {
		if (bool) return "TRUE";
		return "FALSE";
	}
	
	/**
	 * Encode a list of strings to be represented within a CSV cell.
	 * @param strings list of strings.
	 * @return a string, accurately formatted for future decodes.
	 */
	public static String encodeListOfStrings(List<String> strings) {
		return String.format("\"%s\"", String.join(",", strings));
	}
}
