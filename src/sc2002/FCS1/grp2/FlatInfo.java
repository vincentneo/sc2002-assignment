package sc2002.FCS1.grp2;

/**
 * Class representing information about a flat in the HDB project.
 * This class implements the CSVEncodable interface to allow encoding of its data into a CSV format.
 * The class contains information about the flat type, the number of remaining units, and the price of the flat.
 */
public class FlatInfo implements CSVEncodable {
	/**
	 * Enum representing the type of flat.
	 */
	private FlatType type;
	/**
	 * The number of remaining units of the flat type.
	 */
	private int remainingUnits;
	/**
	 * The price of the flat type.
	 */
	private int price;
	
	/**
	 * Constructor to create a FlatInfo object with specified type, remaining units, and price.
	 * 
	 * @param type the type of flat
	 * @param remainingUnits the number of remaining units of the flat type
	 * @param price the price of the flat type
	 */
	FlatInfo(FlatType type, int remainingUnits, int price) {
		this.type = type;
		this.remainingUnits = remainingUnits;
		this.price = price;
	}
	
	/**
	 * Returns the type of flat.
	 * @return the type of flat
	 */
	public FlatType getType() {
		return type;
	}
	
	/**
	 * Returns the number of remaining units of the flat type.
	 * @return the number of remaining units
	 */
	public int getRemainingUnits() {
		return remainingUnits;
	}
	
	/**
	 * Returns the price of the flat type.
	 * @return the price of the flat type
	 */
	public int getPrice() {
		return price;
	}
	
	/**
	 * Sets the type of flat.
	 * @param type the new type of flat
	 */
	public void setType(FlatType type) {
		this.type = type;
	}

	/**
	 * Sets the number of remaining units of the flat type.
	 * @param remainingUnits the new number of remaining units
	 */
	public void setRemainingUnits(int remainingUnits) {
		this.remainingUnits = remainingUnits;
	}

	/**
	 * Sets the price of the flat type.
	 * @param price the new price of the flat type
	 */
	public void setPrice(int price) {
		this.price = price;
	}

	/**
	 * Encodes the FlatInfo object into a CSV format string.
	 * The format is: type,remainingUnits,price
	 * 
	 * @return the encoded CSV string representation of the FlatInfo object
	 */
	@Override
	public String encode() {
		return String.format("%s,%d,%d", type.toString(), remainingUnits, price);
	}
	
	/**
	 * Returns the type of the source file for this object.
	 * In this case, it returns null as FlatInfo does not have a specific source file type.
	 * 
	 * @return null
	 */
	@Override
	public CSVFileTypes sourceFileType() {
		return null;
	}
}
