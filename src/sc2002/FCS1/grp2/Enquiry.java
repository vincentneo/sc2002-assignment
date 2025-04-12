package sc2002.FCS1.grp2;

import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Collectors;

public class Enquiry extends CSVDecodable {
	private UUID id;
	private String title;
	private ArrayList<Message> messages;
	
	private String projectName;
	private BTOProject project;
	
	public Enquiry(ArrayList<CSVCell> cells) {
		this.id = cells.get(0).getUUIDValue();
		this.title = cells.get(1).getValue();
		this.projectName = cells.get(2).getValue();
	}
	
	public Enquiry(String title, ArrayList<Message> messages, BTOProject project) {
		this.id = UUID.randomUUID();
		this.title = title;
		this.messages = messages;
		this.project = project;
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
	
	public void linkMessages(ArrayList<Message> messagePool) {
		ArrayList<Message> messages = messagePool.stream()
				.filter(m -> m.getEnquiryId() == id)
				.collect(Collectors.toCollection(ArrayList::new));
		
		this.messages = messages;
	}
	
}
