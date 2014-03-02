package eu.ha3.matmos.engine.core.interfaces;

import java.util.Set;

/* x-placeholder */

public interface Data
{
	public Sheet getSheet(String name);
	
	public Set<String> getSheetNames();
	
	/**
	 * Empties the data overall
	 */
	public void clear();
	
	/**
	 * Empties the individual sheets
	 */
	public void clearContents();
}