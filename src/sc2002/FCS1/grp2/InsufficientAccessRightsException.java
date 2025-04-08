package sc2002.FCS1.grp2;

/**
 * This exception should be thrown when a restricted method is called while the logged in user is unauthorized to perform said action.
 * 
 * Certain roles such as HDB Manager, holds the right to perform actions such as creating of a new project. 
 * If an Applicant were to be able to call such a method, as they are not authorized, this exception should be thrown.
 * This exception is intended to highlight the severity of an access right violation, for detection of such issues when testing.
 */
@SuppressWarnings("serial")
class InsufficientAccessRightsException extends Exception {
	InsufficientAccessRightsException() {
		super("Logged in user is not permitted to perform the current action.");
	}
}
