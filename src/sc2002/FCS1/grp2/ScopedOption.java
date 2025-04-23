package sc2002.FCS1.grp2;

/**
 * Enums that are intended to be used to identify an access control scope menu option should implement this interface.
 */
interface ScopedOption {
	/**
	 * Use this method to get the option name to be printed in the menu.
	 * 
	 * @return option name
	 */
	public String getOptionName();
}
