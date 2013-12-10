package eu.ha3.matmos.engine0.conv;

import java.util.HashSet;
import java.util.Set;

import eu.ha3.matmos.engine0.core.implem.StringData;
import eu.ha3.matmos.engine0.core.interfaces.Sheet;

/* x-placeholder */

public abstract class ProcessorModel implements Processor
{
	private StringData data;
	
	private String normalName;
	private String deltaName;
	
	private Sheet normalSheet;
	private Sheet deltaSheet;
	
	private boolean normalRequired = false;
	private boolean deltaRequired = false;
	
	private HashSet<String> requirementsSet;
	
	public ProcessorModel(StringData dataIn, String normalNameIn, String deltaNameIn)
	{
		this.data = dataIn;
		this.normalName = normalNameIn;
		this.deltaName = deltaNameIn;
		
		this.requirementsSet = new HashSet<String>();
	}
	
	public StringData data()
	{
		return this.data;
	}
	
	protected abstract void doProcess();
	
	@Override
	public void process()
	{
		this.normalRequired = this.data.getRequirements().isRequired(this.normalName);
		this.deltaRequired = this.deltaName != null && this.data.getRequirements().isRequired(this.deltaName);
		
		if (!isRequired())
			return;
		
		this.normalSheet = data().getSheet(this.normalName);
		if (this.deltaName != null)
		{
			this.deltaSheet = data().getSheet(this.deltaName);
		}
		
		doProcess();
	}
	
	public void setValueLegacyIntIndexes(int index, int newValue)
	{
		setValue(Integer.toString(index), Integer.toString(newValue));
	}
	
	public void setValueLegacyIntIndexes(int index, String newValue)
	{
		setValue(Integer.toString(index), newValue);
	}
	
	public void setValue(String index, int newValue)
	{
		setValue(index, Integer.toString(newValue));
	}
	
	public void setValue(String index, String newValue)
	{
		String previousValue = this.normalSheet.get(index);
		this.normalSheet.set(index, newValue);
		
		if (this.deltaName != null)
		{
			try
			{
				int previousValueIntegerForm = Integer.parseInt(previousValue);
				int newValueIntegerForm = Integer.parseInt(newValue);
				this.deltaSheet.set(index, Integer.toString(newValueIntegerForm - previousValueIntegerForm));
			}
			catch (Exception e)
			{
				this.deltaSheet.set(index, "");
			}
		}
	}
	
	/**
	 * Must be called after a certain point in process()
	 * 
	 * @return
	 */
	public boolean isRequired()
	{
		return this.normalRequired || this.deltaRequired;
	}
	
	public Set<String> getRequired()
	{
		if (this.deltaName == null)
			return this.data.getRequirements().getRequirementsFor(this.normalName);
		
		this.requirementsSet.clear();
		
		this.requirementsSet.addAll(this.data.getRequirements().getRequirementsFor(this.normalName));
		this.requirementsSet.addAll(this.data.getRequirements().getRequirementsFor(this.deltaName));
		
		return this.requirementsSet;
	}
	
}
