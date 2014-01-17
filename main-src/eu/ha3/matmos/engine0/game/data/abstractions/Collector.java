package eu.ha3.matmos.engine0.game.data.abstractions;

import java.util.Set;

/*
--filenotes-placeholder
*/

public interface Collector
{
	public void addModuleStack(String name, Set<String> requiredModules);
	
	public void removeModuleStack(String name);
	
	/**
	 * Tells if this collector requires a certain module.
	 * 
	 * @param moduleName
	 * @return
	 */
	public boolean requires(String moduleName);
}
