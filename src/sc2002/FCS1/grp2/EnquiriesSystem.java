package sc2002.FCS1.grp2;

import java.util.ArrayList;
import java.util.List;

public class EnquiriesSystem {
	private EnquiriesDelegate delegate;
	private List<Enquiry> enquiries;
	
	EnquiriesSystem(EnquiriesDelegate delegate, User user) {
		this.delegate = delegate;
		this.enquiries = delegate.getApplicableEnquiries(user);
	}

	public void setDelegate(EnquiriesDelegate delegate) {
		this.delegate = delegate;
	}
	
	public void addEnquiry(Enquiry enquiry) throws Exception {
		this.delegate.addEnquiry(enquiry);
		this.enquiries.add(enquiry);
	}
	
	public List<Enquiry> getEnquiries() {
		return enquiries;
	}
	
	public void displayEnquiriesMenu() {
		List<String> contents = new ArrayList<>();
		
		int index = 1;
		for (Enquiry enquiry : enquiries) {
			Message question = enquiry.getQuestion();
			if (question == null) continue;
			
			String responseState = enquiry.hasResponded() ? "Responded" : "Not Responded";
			
			contents.add(String.format("%3d. %-50s %30s", index, question.getContent(), responseState));
			
			index++;
		}
		new DisplayMenu.Builder()
				.addContents(contents)
				.setTitle("Enquiries")
				.build()
				.display();
	}
}
