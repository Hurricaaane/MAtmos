package eu.ha3.matmos.engine0.conv;

import eu.ha3.matmos.engine0.core.implem.DeltaSheet;
import eu.ha3.matmos.engine0.core.interfaces.Data;
import eu.ha3.matmos.engine0.core.interfaces.Sheet;
import eu.ha3.matmos.engine0.core.interfaces.VirtualSheet;

/* x-placeholder */

public abstract class ProcessorModel implements Processor
{
	private Sheet sheet;
	
	public ProcessorModel(Data data, String normalName, String deltaName)
	{
		if (deltaName.equals(null))
		{
			this.sheet = data.getSheet(normalName);
		}
		else
		{
			this.sheet = new DeltaSheet(data, normalName, deltaName);
		}
	}
	
	protected abstract void doProcess();
	
	@Override
	public void process()
	{
		doProcess();
		
		if (this.sheet instanceof VirtualSheet)
		{
			((VirtualSheet) this.sheet).apply();
		}
	}
	
	@Deprecated
	public void setValueLegacyIntIndexes(int index, long newValue)
	{
		setValue(Integer.toString(index), Long.toString(newValue));
	}
	
	@Deprecated
	public void setValueLegacyIntIndexes(int index, String newValue)
	{
		setValue(Integer.toString(index), newValue);
	}
	
	public void setValue(String index, long newValue)
	{
		setValue(index, Long.toString(newValue));
	}
	
	public void setValue(String index, String value)
	{
		this.sheet.set(index, value);
	}
}
