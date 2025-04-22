package sc2002.FCS1.grp2;

import java.time.LocalDateTime;
import java.util.List;

import sc2002.FCS1.grp2.helpers.Utilities;

/**
 * This class represents a message of an enquiry, by a user of our application, typically an applicant.
 * 
 * It contains essential metadata such as the submitter's identity, date and time, as well as the message's content.
 * 
 * @author Vincent Neo
 */
public class Message extends CSVDecodable implements CSVEncodable {
	private User user;
	private String content;
	private LocalDateTime timestamp;
	
	private String nric;
	
	Message(List<CSVCell> cells) {
		this.nric = cells.get(0).getValue();
		this.content = cells.get(1).getValue();
		this.timestamp = cells.get(2).getDateTimeValue();
	}
	
	Message(User user, String content) {
		this.user = user;
		this.content = content;
		this.timestamp = LocalDateTime.now();
	}
	
	public User getUser() {
		return user;
	}

	public String getContent() {
		return content;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void updateContent(String newContent) {
		this.content = newContent;
		this.timestamp = LocalDateTime.now();
	}
	
	public void linkUser(List<User> users) {
		if (nric == null) return;
		this.user = users.stream()
				.filter(u -> u.getNric().equals(nric))
				.findFirst()
				.orElse(null);
//		System.out.println("x");
	}

	@Override
	public String encode() {
		String formattedTime = Utilities.getInstance().formatDateTime(timestamp);

		// double quotes (") may intefere with decoding, so swapping all to single quotes.
		String encodableContent = content.replaceAll("\"", "'");
		return String.format("%s,\"%s\",%s", user.getNric(), encodableContent, formattedTime);
	}

	@Override
	public CSVFileTypes sourceFileType() {
		// TODO Auto-generated method stub
		return CSVFileTypes.ENQUIRIES_LIST;
	}
}
