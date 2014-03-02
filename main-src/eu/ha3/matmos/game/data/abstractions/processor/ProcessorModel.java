package eu.ha3.matmos.game.data.abstractions.processor;

import eu.ha3.matmos.engine.core.implem.DeltaSheet;
import eu.ha3.matmos.engine.core.interfaces.Data;
import eu.ha3.matmos.engine.core.interfaces.Sheet;
import eu.ha3.matmos.engine.core.interfaces.VirtualSheet;
import eu.ha3.matmos.game.data.abstractions.Processor;

/* x-placeholder */

/**
 * A processor that contains a sheet, or a virtual delta sheet if the deltaName
 * is not null. Calling doProcess() will set the values of the sheet, and
 * automatically apply them at the end of the call if the sheet provided by the
 * Data is a virtual sheet, or if the sheet is a delta sheet.
 * 
 * @author Hurry
 */
public abstract class ProcessorModel implements Processor
{
	protected Sheet sheet;
	
	private int interval;
	private int callsRemaining;
	
	public ProcessorModel(Data data, String normalName, String deltaName)
	{
		if (deltaName == null)
		{
			this.sheet = data.getSheet(normalName);
		}
		else
		{
			this.sheet = new DeltaSheet(data, normalName, deltaName);
		}
		
		this.interval = 1;
		this.callsRemaining = 0;
	}
	
	protected abstract void doProcess();
	
	@Override
	public void process()
	{
		if (this.callsRemaining <= 0)
		{
			doProcess();
			
			if (this.sheet instanceof VirtualSheet)
			{
				((VirtualSheet) this.sheet).apply();
			}
			
			if (this.interval != 0)
			{
				this.callsRemaining = this.interval;
			}
		}
		else
		{
			this.callsRemaining = this.callsRemaining - 1;
		}
	}
	
	/**
	 * Sets the number of calls where nothing happens before the process is
	 * executed again. Defaults to 0 (call every time).
	 * 
	 * @param value
	 */
	public void setInterval(int value)
	{
		if (value < 0)
		{
			value = 0;
		}
		
		this.interval = value;
		this.callsRemaining = 0;
	}
	
	@Deprecated
	public void conversionComplete(int index, long newValue)
	{
		setValue(Integer.toString(index), Long.toString(newValue));
	}
	
	@Deprecated
	public void conversionComplete(int index, String newValue)
	{
		setValue(Integer.toString(index), newValue);
	}
	
	public void setValueLegacyIntIndexes(int index, long newValue)
	{
		setValue(Integer.toString(index), Long.toString(newValue));
	}
	
	public void setValueLegacyIntIndexes(int index, String newValue)
	{
		setValue(Integer.toString(index), newValue);
	}
	
	// -- -- -- -- //
	
	public void setValue(String index, boolean newValue)
	{
		setValue(index, newValue ? "1" : "0");
	}
	
	public void setValue(String index, long newValue)
	{
		setValue(index, Long.toString(newValue));
	}
	
	public void setValue(String index, String value)
	{
		if (value != null)
		{
			this.sheet.set(index, value);
		}
		else
		{
			this.sheet.set(index, "NULL");
		}
	}
}
