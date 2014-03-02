package eu.ha3.matmos.engine.core.interfaces;

import java.util.Set;

/* x-placeholder */

public interface Sheet
{
	/**
	 * Get the value of a certain position. This should return a default value
	 * if the position does not exist.
	 * 
	 * @param key
	 * @return
	 */
	public String get(String key);
	
	/**
	 * Sets the value of a certain position.
	 * 
	 * @param key
	 * @param value
	 */
	public void set(String key, String value);
	
	/**
	 * Returns a number that increments every time this value is changed to a
	 * different value (as opposed to set).<br>
	 * Non-initialized positions start at -1.
	 * 
	 * @param key
	 * @return
	 */
	public int version(String key);
	
	/**
	 * Tells if this sheet contains a certain key.
	 * 
	 * @param key
	 * @return
	 */
	public boolean exists(String key);
	
	/**
	 * Returns the set of keys.
	 * 
	 * @return
	 */
	public Set<String> keySet();
	
	/**
	 * Empty this sheet. Versions are preserved if cleared this way.
	 */
	public void clear();
	
	/**
	 * Sets a default value to be given out if some module tries to get an
	 * uninitialized entry. This is useful in case some sheet want to consider
	 * uninitialized values to be false or zero.
	 * 
	 * @param def
	 */
	public void setDefaultValue(String def);
	
	/**
	 * Returns the default value given out if some module tries to get an
	 * uninitialized entry. This is useful in case some sheet want to consider
	 * uninitialized values to be false or zero.
	 * 
	 * @return
	 */
	public String getDefaultValue();
}