package sc2002.FCS1.grp2;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Utilities {
	
	private static Utilities instance = new Utilities();
	private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d'/'M'/'uu");
	
	private Utilities() {}

	public static Utilities getInstance() {
		return instance;
	}
	
	public LocalDate parseDate(String dateString) {
		return LocalDate.parse(dateString, formatter);
	}
	
	public String formatDate(LocalDate date) {
		return date.format(formatter);
	}
}
