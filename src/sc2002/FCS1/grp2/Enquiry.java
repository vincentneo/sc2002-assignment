package sc2002.FCS1.grp2;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class Enquiry extends CSVDecodable implements CSVEncodable {
	private UUID id;
	private Message question;
	private Message response = null;
	
	private String projectName;
	private BTOProject project;
	
	public Enquiry(List<CSVCell> cells) {
		this.id = cells.get(0).getUUIDValue();
		if (!cells.get(1).isBlank()) {
			this.question = new Message(cells.subList(1, 4));
		}
		
		if (!cells.get(4).isBlank()) {
			this.response = new Message(cells.subList(4, 7));
		}
		
		this.projectName = cells.get(7).getValue();
	}
	
	public Enquiry(Message question, BTOProject project) {
		this.id = UUID.randomUUID();
		this.question = question;
		this.project = project;
	}
	
	public UUID getId() {
		return id;
	}

	/** 
	 * Only call if projectName is provided by CSV parsing and not null.
	 */
	public void linkProject(ArrayList<BTOProject> projects) {
		BTOProject project = projects
				.stream()
				.filter(p -> p.getProjectName().equals(projectName))
				.findFirst()
				.orElse(null);
		
		this.project = project;
		this.projectName = null;
	}
	
	public void linkUsers(List<User> users) {
		if (question != null) {
			question.linkUser(users);
		}
		
		if (response != null) {
			response.linkUser(users);
		}
	}
	
	public boolean isUserInvolved(User user) {
		return (question != null && question.getUser().equals(user)) || (response != null && response.getUser().equals(user));
	}

	public Message getResponse() {
		return response;
	}

	public void setResponse(Message response) {
		this.response = response;
	}

	public Message getQuestion() {
		return question;
	}
	
	public boolean hasResponded() {
		return response != null;
	}

	@Override
	public String encode() {
		String questionEncoded = question == null ? ",," : question.encode();
		String responseEncoded = response == null ? ",," : response.encode();
		String projectName = project == null ? "" : project.getProjectName();
		return String.format("%s,%s,%s,%s", id.toString(), questionEncoded, responseEncoded, projectName);
	}

	@Override
	public CSVFileTypes sourceFileType() {
		return CSVFileTypes.ENQUIRIES_LIST;
	}
	
}
