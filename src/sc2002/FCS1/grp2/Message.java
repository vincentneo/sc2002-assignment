package sc2002.FCS1.grp2;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
	
	public void linkUser(List<User> users) {
		if (nric == null) return;
		user = users.stream().filter(u -> u.getNric().equals(nric)).findFirst().orElse(null);
	}

	@Override
	public String encode() {
		String formattedTime = Utilities.getInstance().formatDateTime(timestamp);
		return String.format("%s,%s,%s", user.getNric(), content, formattedTime);
	}

	@Override
	public CSVFileTypes sourceFileType() {
		// TODO Auto-generated method stub
		return CSVFileTypes.ENQUIRIES_LIST;
	}
}
