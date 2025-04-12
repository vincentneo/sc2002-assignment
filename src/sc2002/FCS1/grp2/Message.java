package sc2002.FCS1.grp2;

import java.time.LocalDateTime;
import java.util.UUID;

public class Message {
	private User user;
	private String query;
	private LocalDateTime timestamp;
	private UUID enquiryId;
	
	Message(User user, String query) {
		this.user = user;
		this.query = query;
		this.timestamp = LocalDateTime.now();
	}
	
	public User getUser() {
		return user;
	}

	public String getQuery() {
		return query;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public UUID getEnquiryId() {
		return enquiryId;
	}
}
