package eu.ha3.matmos.engine0.core.implem;

import java.util.List;

import eu.ha3.matmos.engine0.core.implem.abstractions.Component;
import eu.ha3.matmos.engine0.core.interfaces.Evaluated;
import eu.ha3.matmos.engine0.core.interfaces.InformationContainer;
import eu.ha3.matmos.engine0.core.interfaces.SheetCommander;
import eu.ha3.matmos.engine0.core.interfaces.SheetIndex;

/* x-placeholder */

public class Dynamic extends Component implements Evaluated, InformationContainer<Long>
{
	private long value;
	
	private final List<SheetIndex> indexes;
	private final SheetCommander sheetCommander;
	
	public Dynamic(String name, SheetCommander sheetCommander, List<SheetIndex> indexes)
	{
		super(name);
		this.sheetCommander = sheetCommander;
		
		this.indexes = indexes;
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
}
