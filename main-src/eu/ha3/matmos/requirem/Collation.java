package eu.ha3.matmos.requirem;

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

public interface Collation extends Requirements // Extends Requirements is required
{
	/**
	 * Add a Requirements to this collation. This causes the collation to
	 * recompute.
	 * 
	 * @param owner
	 * @param req
	 */
	public void addRequirements(String owner, Requirements req);
	
	/**
	 * Removes a Requirements from this collation. This causes the collation to
	 * recompute, unless the name does not exist.
	 * 
	 * @param owner
	 * @param req
	 */
	public void removeRequirements(String owner);
}
