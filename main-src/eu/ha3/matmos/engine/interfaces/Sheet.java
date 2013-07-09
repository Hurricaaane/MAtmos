package eu.ha3.matmos.engine.interfaces;

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

public interface Sheet<T>
{
	/**
	 * Get the value of a certain position. This should return a default value
	 * if the position does not exist.
	 * 
	 * @param pos
	 * @return
	 */
	public T get(int pos);
	
	/**
	 * Sets the value of a certain position. This should return a default value
	 * if the position does not exist.
	 * 
	 * @param pos
	 * @return
	 */
	public void set(int pos, T value);
	
	/**
	 * Gets the maximum possible position + 1 of this sheet.
	 * 
	 * @param pos
	 * @return
	 */
	public int getSize();
	
	/**
	 * Returns a number that changes every time this value is changed to a
	 * different value (as opposed to set).<br>
	 * Non-initialized positions start at 0.
	 * 
	 * @param pos
	 * @return
	 */
	public int getVersionOf(int pos);
}