package eu.ha3.matmos.engine0.core.interfaces;

import java.util.Set;

/* x-placeholder */

public interface Sheet<T>
{
	/**
	 * Get the value of a certain position. This should return a default value
	 * if the position does not exist.
	 * 
	 * @param key
	 * @return
	 */
	public T get(String key);
	
	/**
	 * Sets the value of a certain position. This should return a default value
	 * if the position does not exist.
	 * 
	 * @param key
	 * @return
	 */
	public void set(String key, T value);
	
	/**
	 * Gets the maximum possible position + 1 of this sheet.
	 * 
	 * @param pos
	 * @return
	 */
	//public int getSize();
	
	/**
	 * Returns a number that changes every time this value is changed to a
	 * different value (as opposed to set).<br>
	 * Non-initialized positions start at 0.
	 * 
	 * @param key
	 * @return
	 */
	public int getVersionOf(String key);
	
	/**
	 * Tells if this sheet contains a certain key.
	 * 
	 * @param key
	 * @return
	 */
	public boolean containsKey(String key);
	
	/**
	 * Returns the set of keys.
	 * 
	 * @return
	 */
	public Set<String> keySet();
}