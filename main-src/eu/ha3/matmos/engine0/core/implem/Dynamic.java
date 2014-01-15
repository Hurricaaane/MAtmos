package eu.ha3.matmos.engine0.core.implem;

import java.util.List;

import eu.ha3.matmos.engine0.core.interfaces.Evaluated;
import eu.ha3.matmos.engine0.core.interfaces.InformationContainer;
import eu.ha3.matmos.engine0.core.interfaces.Provider;
import eu.ha3.matmos.engine0.core.interfaces.SheetCommander;

/* x-placeholder */

public class Dynamic extends Component implements Evaluated, InformationContainer<Long>
{
	private long value;
	
	private final List<SheetIndex> indexes;
	private final Provider<SheetCommander> x;
	
	public Dynamic(String name, Provider<SheetCommander> x, List<SheetIndex> indexes)
	{
		super(name);
		this.x = x;
		
		this.indexes = indexes;
	}
	
	@Override
	public void evaluate()
	{
		long previous = this.value;
		
		this.value = 0;
		
		for (SheetIndex sheetIndex : this.indexes)
		{
			if (this.x.instance().exists(sheetIndex))
			{
				Long value = LongFloatSimplificator.longOf(this.x.instance().get(sheetIndex));
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
