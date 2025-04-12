package sc2002.FCS1.grp2;

/*
 * The purpose of this interface is to provide a set of common functionality for the {@code CSVEncoder} class to encode objects for generating a CSV file, that is complaint for future parsing.
 * 
 * This allows for a consistent CSV file encoding experience, of which all encodable files should subscribe to.
 */
public interface CSVEncodable {
	/**
	 * Override this to provide a line/row of CSV content.
	 * 
	 * This will be called one by one in the {@code CSVEncoder}, to be written in file.
	 * @return A CSV row, in text string form.
	 */
	String encode();
	
	/**
	 * Override this to provide which file type this class should be represented as.
	 * 
	 * This ensures that the right data is written in the right file, depending on the class type.
	 * @return a CSVFileType, such as {@code CSVFileType.MANAGER_LIST}.
	 */
	CSVFileTypes sourceFileType();
}
