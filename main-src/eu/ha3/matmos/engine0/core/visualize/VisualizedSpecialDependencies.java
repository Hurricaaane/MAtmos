package eu.ha3.matmos.engine0.core.visualize;

import java.util.Collection;

import eu.ha3.matmos.engine0.core.interfaces.Dependable;

/*
--filenotes-placeholder
*/

public interface VisualizedSpecialDependencies extends Visualized, Dependable
{
	public Collection<String> getSpecialDependencies(String type);
}
