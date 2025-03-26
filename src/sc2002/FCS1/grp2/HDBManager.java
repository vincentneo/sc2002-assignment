package sc2002.FCS1.grp2;

public class HDBManager extends User {

	public HDBManager(String line) {
		super(line);
		// TODO Auto-generated constructor stub
	}

	@Override
	boolean canApplyProject() {
		return false;
	}
	
	@Override
	String getReadableTypeName() {
		return "HDB Manager";
	}

}
