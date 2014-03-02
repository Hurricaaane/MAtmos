package eu.ha3.matmos.engine.core.visualize;

import java.util.Collection;

import eu.ha3.matmos.engine.core.interfaces.Dependable;

/*
--filenotes-placeholder
*/

public interface VisualizedSpecialDependencies extends Visualized, Dependable
{
	public Collection<String> getSpecialDependencies(String type);
}
