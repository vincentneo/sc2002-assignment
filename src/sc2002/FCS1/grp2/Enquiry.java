package sc2002.FCS1.grp2;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Each enquiry acts as a question and answer session, between applicants and officers or managers of a project.
 * 
 * @author Vincent Neo
 */
public class Enquiry extends CSVDecodable implements CSVEncodable {
	/**
	 * A unique identifier to identify each enquiry.
	 */
	private UUID id;
	
	/**
	 * A question intended for an authorised HDB manager or officer to respond to.
	 */
	private Message question;
	
	/**
	 * A response from an authorised HDB manager or officer to answer the question.
	 * 
	 * It is expected for this to be {@code null} if unanswered.
	 */
	private Message response = null;
	
	/** 
	 * Placeholder object as intermediary for CSV parsing
	 * 
	 * This is because the CSV cells only represents projects as their names.
	 * We need to further process to link actual {@code BTOProject} object to this class.
	 * Set to null when not in use.
	 */
	private String projectName;
	
	/**
	 * The BTO Project which the enquiry is intended to be about.
	 * 
	 * Each enquiry question is supposed to be related to a BTO project. 
	 * This enables the enquiry to be handled by a suitable personnel, such as the officer in charge of the project.
	 */
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
	
	/**
	 * Construct a new enquiry from an {@code Applicant} role.
	 * 
	 * @param question The question for an officer or manager to reply to.
	 * @param project The related project, for context of the question.
	 */
	public Enquiry(Message question, BTOProject project) {
		this.id = UUID.randomUUID();
		this.question = question;
		this.project = project;
	}
	
	/** 
	 * Only call if projectName is provided by CSV parsing and not null.
	 * @param projects All projects of system, used for matching and linking.
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
	
	/**
	 * Link connected users to this object.
	 * @param users All users supported in the system. This method will pick whichever user necessary.
	 */
	public void linkUsers(List<User> users) {
		if (question != null) {
			question.linkUser(users);
		}
		
		if (response != null) {
			response.linkUser(users);
		}
	}
	
	/**
	 * Checks if the provided user is involved in this enquiry, as an enquirer.
	 * @param user The user to be checked.
	 * @return true, if user provided is found to be involved in this enquiry.
	 */
	public boolean isUserInvolved(User user) {
		return (question != null && question.getUser() == user);// || (response != null && response.getUser().equals(user));
	}
	
	/** 
	 * Retrieve the unique identifier of this object.
	 * @return a UUID that uniquely represents this object.
	 */
	public UUID getId() {
		return id;
	}

	/**
	 * Retrieve the response object provided by an authorised officer or manager, if any.
	 * @return A message representing a response from an authorised personnel.
	 */
	public Message getResponse() {
		return response;
	}

	/** 
	 * Use this to provide a response to the enquiry question.
	 * @param response The response to be provided.
	 */
	public void setResponse(Message response) {
		this.response = response;
	}
	
	/**
	 * Retrieve the project associated to this enquiry.
	 * @return project information.
	 */
	public BTOProject getProject() {
		return project;
	}

	/**
	 * Retrieve the question message that has been added in by the applicant that has submitted this enquiry.
	 * @return the question message.
	 */
	public Message getQuestion() {
		return question;
	}
	
	/**
	 * Check if this enquiry has been responded by an authorised personnel or not.
	 * @return false, if no responses.
	 */
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
