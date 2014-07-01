package eu.ha3.matmos.game.data.abstractions.module;

import java.util.Map;

/*
--filenotes-placeholder
*/

public interface EntryBasedModule extends Module
{
	public Map<String, EI> getModuleEntries();
}
