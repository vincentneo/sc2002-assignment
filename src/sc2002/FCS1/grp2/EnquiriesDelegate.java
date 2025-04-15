package sc2002.FCS1.grp2;

import java.util.List;

public interface EnquiriesDelegate {
	public void addEnquiry(Enquiry enquiry) throws Exception;
	public List<Enquiry> getApplicableEnquiries();
}
