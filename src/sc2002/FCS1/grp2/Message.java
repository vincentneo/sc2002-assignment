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
	/**
	 * User involved in this message.
	 */
	private User user;

	/**
	 * Contents of this message.
	 */
	private String content;

	/**
	 * The time of which this message was constructed or last updated.
	 */
	private LocalDateTime timestamp;
	
	/**
	 * Temporary value for CSV parsing and further linking.
	 */
	private String nric;
	
	/**
	 * Constructor for {@code CSVParser}
	 * @param cells CSV cells.
	 */
	Message(List<CSVCell> cells) {
		this.nric = cells.get(0).getValue();
		this.content = cells.get(1).getValue();
		this.timestamp = cells.get(2).getDateTimeValue();
	}
	
	/**
	 * Construct a message for a user.
	 * @param user User that wrote this message.
	 * @param content Content of this message.
	 */
	Message(User user, String content) {
		this.user = user;
		this.content = content;
		this.timestamp = LocalDateTime.now();
	}
	
	/**
	 * Get the user.
	 * @return the user.
	 */
	public User getUser() {
		return user;
	}

	/**
	 * Get the content.
	 * @return the message contents.
	 */
	public String getContent() {
		return content;
	}

	/**
	 * Get the timestamp whereby message was submitted.
	 * @return the timestamp.
	 */
	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	/**
	 * Call this to update/edit the message.
	 * @param newContent the updated message.
	 */
	public void updateContent(String newContent) {
		this.content = newContent;
		this.timestamp = LocalDateTime.now();
	}
	
	/**
	 * Use this to link {@code User} objects to this Message.
	 * @param users All users.
	 */
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
		return CSVFileTypes.ENQUIRIES_LIST;
	}
}
