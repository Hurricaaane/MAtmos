package eu.ha3.matmos.engine.core.implem;

import eu.ha3.matmos.engine.core.implem.abstractions.Component;
import eu.ha3.matmos.engine.core.interfaces.PossibilityList;

import java.util.List;

/*
--filenotes-placeholder
*/

public class Possibilities extends Component implements PossibilityList
{
	private final List<String> list;
	
	public Possibilities(String name, List<String> list)
	{
		super(name);
		
		this.list = list;
	}
	
	@Override
	public boolean listHas(String element)
	{
		return this.list.contains(element);
	}
}
