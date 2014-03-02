package eu.ha3.matmos.engine.core.implem.abstractions;

import eu.ha3.matmos.engine.core.interfaces.Dependable;

/*
--filenotes-placeholder
*/

public abstract class DependableComponent extends MultistateComponent implements Dependable
{
	public DependableComponent(String name)
	{
		super(name);
	}
}
