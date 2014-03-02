package eu.ha3.matmos.engine.core.implem;

import eu.ha3.matmos.engine.core.interfaces.Data;
import eu.ha3.matmos.engine.core.interfaces.VirtualSheet;

/*
--filenotes-placeholder
*/

public class DeltaSheet extends GenericSheet implements VirtualSheet
{
	private final Data data;
	private final String actualSheet;
	private final String deltaSheet;
	
	public DeltaSheet(Data data, String actualSheet, String deltaSheet)
	{
		this.data = data;
		this.actualSheet = actualSheet;
		this.deltaSheet = deltaSheet;
	}
	
	@Override
	public void apply()
	{
		for (String key : this.values.keySet())
		{
			String newValue = this.values.get(key);
			Long newLong = LongFloatSimplificator.longOf(newValue);
			
			if (newLong != null)
			{
				String previousValue =
					this.data.getSheet(this.actualSheet).exists(key)
						? this.data.getSheet(this.actualSheet).get(key) : "0";
				
				Long previousLong = LongFloatSimplificator.longOf(previousValue);
				
				// Set it here, we needed to retreive previous value first
				this.data.getSheet(this.actualSheet).set(key, newValue);
				if (previousLong != null)
				{
					this.data.getSheet(this.deltaSheet).set(key, Long.toString(newLong - previousLong));
				}
				else
				{
					this.data.getSheet(this.deltaSheet).set(key, "NO_DELTA");
				}
			}
			else
			{
				this.data.getSheet(this.actualSheet).set(key, newValue);
				this.data.getSheet(this.deltaSheet).set(key, "NO_DELTA");
			}
		}
		
		clear();
	}
	
}
