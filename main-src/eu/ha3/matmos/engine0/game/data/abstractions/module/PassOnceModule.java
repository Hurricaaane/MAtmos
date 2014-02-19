package eu.ha3.matmos.engine0.game.data.abstractions.module;

import java.util.Set;

/*
--filenotes-placeholder
*/

public interface PassOnceModule extends Module
{
	/**
	 * Returns a set of modules this pass-once module is capable to handle.
	 * 
	 * @return
	 */
	public Set<String> getSubModules();
}
