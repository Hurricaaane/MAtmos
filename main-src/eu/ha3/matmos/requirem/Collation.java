package eu.ha3.matmos.requirem;

/* x-placeholder */

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
