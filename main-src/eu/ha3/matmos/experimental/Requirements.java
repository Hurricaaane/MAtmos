package eu.ha3.matmos.experimental;

import java.util.Set;

/*
            DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE 
                    Version 2, December 2004 

 Copyright (C) 2004 Sam Hocevar <sam@hocevar.net> 

 Everyone is permitted to copy and distribute verbatim or modified 
 copies of this license document, and changing it is allowed as long 
 as the name is changed. 

            DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE 
   TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION 

  0. You just DO WHAT THE FUCK YOU WANT TO. 
*/

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