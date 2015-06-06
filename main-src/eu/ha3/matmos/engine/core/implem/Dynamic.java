package eu.ha3.matmos.engine.core.implem;

import eu.ha3.matmos.engine.core.implem.abstractions.Component;
import eu.ha3.matmos.engine.core.interfaces.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/* x-placeholder */

public class Dynamic extends Component implements Evaluated, InformationContainer<Long>, Dependable
{
	public static final String DEDICATED_SHEET = "_DYNAMIC";
	
	private long value;
	
	private final List<SheetIndex> indexes;
	
	private final SheetCommander sheetCommander;
	
	private Collection<String> dependencies;
	
	public Dynamic(String name, SheetCommander sheetCommander, List<SheetIndex> indexes)
	{
		super(name);
		this.sheetCommander = sheetCommander;
		
		this.indexes = indexes;
		
		this.dependencies = new HashSet<String>();
		for (SheetIndex index : indexes)
		{
			this.dependencies.add(index.getSheet());
		}
	}
	
	@Override
	public void evaluate()
	{
		long previous = this.value;
		
		this.value = 0;
		
		for (SheetIndex sheetIndex : this.indexes)
		{
			if (this.sheetCommander.exists(sheetIndex))
			{
				Long value = LongFloatSimplificator.longOf(this.sheetCommander.get(sheetIndex));
				if (value != null)
				{
					this.value = this.value + value;
				}
			}
		}
		
		if (previous != this.value)
		{
			incrementVersion();
		}
	}
	
	@Override
	public Long getInformation()
	{
		return this.value;
	}
	
	@Override
	public Collection<String> getDependencies()
	{
		return this.dependencies;
	}
}
