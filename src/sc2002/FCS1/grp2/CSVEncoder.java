package sc2002.FCS1.grp2;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class CSVEncoder {
	private String path;
	private ArrayList<CSVEncodable> encodables;
	
	CSVEncoder(String fileName, ArrayList<CSVEncodable> encodables) {
		String classpath = System.getProperty("java.class.path");
		String filesPath = classpath + "/sc2002/FCS1/grp2/files/";
		this.path = filesPath + fileName;
		this.encodables = encodables;
	}
	
	public void encode() throws IOException {
		File file = new File(path);
		Scanner reader = new Scanner(file);
		
		// get the header row from the current file.
		String headerLine = reader.nextLine();
		reader.close();
		
		FileWriter writer = new FileWriter(path);
		BufferedWriter bufferedWriter = new BufferedWriter(writer);
		
		// write the header row as retrieved earlier.
		bufferedWriter.write(headerLine);
		bufferedWriter.newLine();
		
		for (CSVEncodable encodable : encodables) {
			bufferedWriter.write(encodable.encode());
			bufferedWriter.newLine();
		}
		
		bufferedWriter.close();
	}
}
