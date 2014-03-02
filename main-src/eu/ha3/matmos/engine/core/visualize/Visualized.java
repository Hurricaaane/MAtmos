package eu.ha3.matmos.engine.core.visualize;

import eu.ha3.matmos.engine.core.interfaces.Named;
import eu.ha3.matmos.engine.core.interfaces.Stated;

/*
--filenotes-placeholder
*/

public interface Visualized extends Named, Stated
{
	/**
	 * Represents the feed of this visualizable. It's a chain of characters that
	 * says what is happening.
	 * 
	 * @return
	 */
	public String getFeed();
}
