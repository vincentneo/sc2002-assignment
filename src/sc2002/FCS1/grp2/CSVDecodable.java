package sc2002.FCS1.grp2;

import java.util.ArrayList;

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
	 * @param cells Represents a row of values, in the CSV file. Should only be provided by the CSVParser class.
	 */
	CSVDecodable(ArrayList<CSVCell> cells) {}
	
	CSVDecodable() {}
}
