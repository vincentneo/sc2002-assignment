package sc2002.FCS1.grp2;

public class FlatInfo implements CSVEncodable {
	private FlatType type;
	private int remainingUnits;
	private int price;
	
	FlatInfo(FlatType type, int remainingUnits, int price) {
		this.type = type;
		this.remainingUnits = remainingUnits;
		this.price = price;
	}
	
	public FlatType getType() {
		return type;
	}
	
	public int getRemainingUnits() {
		return remainingUnits;
	}
	
	public int getPrice() {
		return price;
	}
	
	public void setType(FlatType type) {
		this.type = type;
	}

	public void setRemainingUnits(int remainingUnits) {
		this.remainingUnits = remainingUnits;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	@Override
	public String encode() {
		return String.format("%s,%d,%d", type.toString(), remainingUnits, price);
	}

	@Override
	public CSVFileTypes sourceFileType() {
		return null;
	}
}
