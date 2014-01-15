package eu.ha3.matmos.engine0.core.interfaces;

/*
--filenotes-placeholder
*/

public interface Overrided
{
	/**
	 * Bypass internal logic to force this on.
	 */
	public void overrideForceOn();
	
	/**
	 * Bypass internal logic to force this off.
	 */
	public void overrideForceOff();
	
	/**
	 * Stop bypassing internal logic.
	 */
	public void overrideFinish();
}
