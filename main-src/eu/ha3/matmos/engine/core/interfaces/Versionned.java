package eu.ha3.matmos.engine.core.interfaces;

/*
--filenotes-placeholder
*/

public interface Versionned
{
	/**
	 * Gets the version of this. Starts at -1.
	 * 
	 * @return
	 */
	public int version();
	
	/**
	 * Increments the version.
	 */
	public void incrementVersion();
	
	/**
	 * Registers a version listener that is called after incrementation.
	 * 
	 * @param listener
	 */
	public void registerVersionListener(VersionListener listener);
}
