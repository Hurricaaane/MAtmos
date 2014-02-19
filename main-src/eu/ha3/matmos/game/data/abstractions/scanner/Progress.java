package eu.ha3.matmos.game.data.abstractions.scanner;

/*
--filenotes-placeholder
*/

public interface Progress
{
	/**
	 * Returns the current progress.
	 * 
	 * @return
	 */
	public int getProgress_Current();
	
	/**
	 * Returns the total value to "complete". Never set the total to zero, to
	 * prevent division by zero errors.
	 * 
	 * @return
	 */
	public int getProgress_Total();
}
