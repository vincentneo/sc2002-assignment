package sc2002.FCS1.grp2;

import java.util.ArrayList;
import java.util.List;

import sc2002.FCS1.grp2.builders.DisplayMenu;
import sc2002.FCS1.grp2.builders.Style;

/**
 * Handles all enquiry related processes for a user.
 * 
 * It must be constructed only on login, and destroyed on logout.
 */
public class EnquiriesSystem {
	private EnquiriesDelegate delegate;
	private List<Enquiry> ownEnquiries;
	private List<Enquiry> respondableEnquiries;
	
	EnquiriesSystem(EnquiriesDelegate delegate) {
		this.delegate = delegate;
		
		this.ownEnquiries = delegate.getOwnEnquiries();
		this.respondableEnquiries = delegate.getEnquiries();
	}

	public void setDelegate(EnquiriesDelegate delegate) {
		this.delegate = delegate;
	}
	
	public void addEnquiry(Enquiry enquiry) throws Exception {
		this.delegate.addEnquiry(enquiry);
		this.ownEnquiries.add(enquiry);
	}
	
	public List<Enquiry> getEnquiries() {
		var enquiries = new ArrayList<Enquiry>();
		enquiries.addAll(ownEnquiries);
		enquiries.addAll(respondableEnquiries);
		return enquiries;
	}

	public void removeEnquiry(Enquiry enquiry) throws Exception {
		delegate.removeEnquiry(enquiry);
		updateState();
	}

	public void updateEnquiry(Enquiry enquiry, String updatedQuestion) throws Exception {
		if (enquiry.hasResponded()) throw new IllegalStateException("You cannot change your message after an officer or manager has responded to you.");

		enquiry.getQuestion().updateContent(updatedQuestion);
		delegate.updateEnquiry();
	}

	/**
	 * Always call this after a list mutative action.
	 */
	private void updateState() {
		this.ownEnquiries = delegate.getOwnEnquiries();
		this.respondableEnquiries = delegate.getEnquiries();
	}

	public boolean isEmpty() {
		return ownEnquiries.isEmpty();
	}
	
	public int size() {
		return ownEnquiries.size();
	}
	
	private String buildMenuItem(int index, Enquiry enquiry) {
		Message question = enquiry.getQuestion();
		if (question == null) return null;
		
		String resValue = enquiry.hasResponded() ? "Responded" : "Not Responded";
		int colourCode = enquiry.hasResponded() ? 46 : 178;
		String responseState = new Style.Builder()
				.text(resValue)
				.add256Colour(colourCode, false)
				.toString();
		
		String projectName = enquiry.getProject().getProjectName();
		String title = String.format("(%s): %s", projectName, question.getContent());
		
		return String.format("%3d. %-50s %s", index, title, responseState);
	}
	
	public void displayEnquiriesMenu() {		
		int index = 1;
		
		boolean ownEmpty = ownEnquiries.isEmpty();
		boolean respondableEmpty = respondableEnquiries.isEmpty();
		boolean allEmpty = ownEmpty && respondableEmpty;
				
		var builder = new DisplayMenu.Builder();
		
		if (!ownEmpty) {
			List<String> myEnquiries = new ArrayList<>();
			
			myEnquiries.add(new Style.Builder().text("My Enquiries").bold().italic().toString());
			for (Enquiry enquiry : ownEnquiries) {
				myEnquiries.add(buildMenuItem(index, enquiry));
				
				index++;
			}
			
			builder.addContents(myEnquiries);
		}
		
		if (!respondableEmpty) {
			List<String> applicantEnquiries = new ArrayList<>();
			
			applicantEnquiries.add(new Style.Builder().text("Enquiries from Applicants").bold().italic().toString());
			for (Enquiry enquiry : respondableEnquiries) {
				applicantEnquiries.add(buildMenuItem(index, enquiry));
				
				index++;
			}
			
			builder.addContents(applicantEnquiries);
		}
		
		if (allEmpty) {
			builder.addContent(String.format("%s%s%s", " ".repeat(10), "No enquiries were found.", " ".repeat(10)));
		}
		
		builder
			.setTitle("Enquiries")
			.build()
			.display();
	}
}
