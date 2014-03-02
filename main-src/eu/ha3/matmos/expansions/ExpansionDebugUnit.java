package eu.ha3.matmos.expansions;

import java.io.File;

import eu.ha3.matmos.engine.core.implem.Knowledge;

/*
--filenotes-placeholder
*/

public abstract class ExpansionDebugUnit
{
	private final File file;
	
	public ExpansionDebugUnit(File file)
	{
		this.file = file;
	}
	
	public File getExpansionFile()
	{
		return this.file;
	}
	
	public abstract Knowledge obtainKnowledge();
}
