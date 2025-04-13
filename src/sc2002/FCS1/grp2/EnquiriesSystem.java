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
	
}
