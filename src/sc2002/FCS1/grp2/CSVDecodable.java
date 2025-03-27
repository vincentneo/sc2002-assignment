package sc2002.FCS1.grp2;

/*
 * The purpose of this abstract class is very simple.
 * 
 * It is to provide a common constructor for the CSVParser class to construct objects.
 */
public abstract class CSVDecodable {
	/*
	 * The CSVDecodable constructor should not be called directly. 
	 * Please override this constructor in the child classes.
	 * 
	 * @param line A row of values, separated by commas. Should only be provided by the CSVParser class.
	 */
	CSVDecodable(String line) {}
	
	CSVDecodable() {}
}
