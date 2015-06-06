package eu.ha3.matmos.engine.core.visualize;

import eu.ha3.matmos.engine.core.interfaces.Dependable;

import java.util.Collection;

/*
--filenotes-placeholder
*/

public interface VisualizedSpecialDependencies extends Visualized, Dependable
{
	public Collection<String> getSpecialDependencies(String type);
}
