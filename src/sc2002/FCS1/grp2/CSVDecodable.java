package sc2002.FCS1.grp2;

import java.util.List;

/*
 * The purpose of this abstract class is to provide a common constructor for the CSVParser class to construct objects.
 * 
 * This allows for a consistent CSV file decoding experience, of which all decodable files should subscribe to.
 */
public abstract class CSVDecodable {
	/*
	 * The CSVDecodable constructor should not be called directly. 
	 * Please override this constructor in the child classes.
	 * 
	 * @param cells Represents a row of values, in the CSV file. Should only be provided by the CSVParser class.
	 */
	CSVDecodable(List<CSVCell> cells) {}
	
	/** 
	 * Default constructor.
	 */
	CSVDecodable() {}
}
