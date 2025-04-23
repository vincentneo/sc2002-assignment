package sc2002.FCS1.grp2;

import java.util.List;

/**
 * For enquiries related functionality
 */
public interface EnquiriesDelegate {
	/**
	 * Add an enquiry to system.
	 * @param enquiry The enquiry
	 * @throws Exception access control.
	 */
	public void addEnquiry(Enquiry enquiry) throws Exception;
	/**
	 * Remove an enquiry in system.
	 * @param enquiry The enquiry to remove
	 * @throws Exception access control
	 */
	public void removeEnquiry(Enquiry enquiry) throws Exception;

	/**
	 * Update an enquiry of system.
	 * @param enquiry The enquiry
	 * @throws Exception access control
	 */
	public void updateEnquiry();

	/**
	 * Retrieve all enquiries of own.
	 * @return list of enquiries.
	 */
	public List<Enquiry> getOwnEnquiries();

	/**
	 * Retrieve enquiries that is respondable by an authorised personnel, subject to individual rights
	 * @return list of enquiries.
	 */
	public List<Enquiry> getEnquiries();
}
