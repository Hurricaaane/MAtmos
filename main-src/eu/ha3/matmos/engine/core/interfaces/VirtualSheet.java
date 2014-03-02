package eu.ha3.matmos.engine.core.interfaces;

/*
--filenotes-placeholder
*/

public interface VirtualSheet extends Sheet
{
	/**
	 * Commits this virtual sheet.
	 */
	public void apply();
}
