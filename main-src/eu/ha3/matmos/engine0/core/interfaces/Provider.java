package eu.ha3.matmos.engine0.core.interfaces;

/*
--filenotes-placeholder
*/

public interface Provider<T>
{
	/**
	 * Returns whether a named object exists.
	 * 
	 * @param name
	 * @return
	 */
	public boolean exists(String name);
	
	/**
	 * Returns a named object version from the provider, or -1 if it doesn't
	 * exist.
	 * 
	 * @param name
	 * @return
	 */
	public int version(String name);
	
	/**
	 * Returns an named object from the provider, or null if it doesn't exist.
	 * 
	 * @param name
	 * @return
	 */
	public T get(String name);
	
	/**
	 * Returns a commanding instance of the provider.
	 * 
	 * @param name
	 * @return
	 */
	public T instance();
}
