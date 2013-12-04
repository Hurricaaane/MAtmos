package eu.ha3.matmos.requirem;

import java.util.Set;

/* x-placeholder */

public interface Requirements
{
	/**
	 * Returns a set of which sheets exist.
	 * 
	 * @param sheet
	 * @return
	 */
	public abstract Set<String> getRegisteredSheets();
	
	/**
	 * Returns a set of what is required in a sheet. If the sheet does not
	 * require anything, returns an empty set.
	 * 
	 * @param sheet
	 * @return
	 */
	public abstract Set<Integer> getRequirementsFor(String sheet);
	
	/**
	 * Tells if a sheet is required at all.
	 * 
	 * @param sheet
	 * @return
	 */
	public abstract boolean isRequired(String sheet);
	
}