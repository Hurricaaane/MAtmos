package eu.ha3.matmos.conv;

import java.util.HashSet;
import java.util.Set;

import eu.ha3.matmos.engine.implem.IntegerData;
import eu.ha3.matmos.engine.interfaces.Sheet;

/* x-placeholder-wtfplv2 */

public abstract class ProcessorModel implements Processor
{
	private IntegerData data;
	
	private String normalName;
	private String deltaName;
	
	private Sheet<Integer> normalSheet;
	private Sheet<Integer> deltaSheet;
	
	private boolean normalRequired = false;
	private boolean deltaRequired = false;
	
	private HashSet<Integer> requirementsSet;
	
	public ProcessorModel(IntegerData dataIn, String normalNameIn, String deltaNameIn)
	{
		this.data = dataIn;
		this.normalName = normalNameIn;
		this.deltaName = deltaNameIn;
		
		this.requirementsSet = new HashSet<Integer>();
	}
	
	public IntegerData data()
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
	
	public void setValue(int index, int newValue)
	{
		int previousValue = this.normalSheet.get(index);
		this.normalSheet.set(index, newValue);
		
		if (this.deltaName != null)
		{
			this.deltaSheet.set(index, newValue - previousValue);
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
	
	public Set<Integer> getRequired()
	{
		if (this.deltaName == null)
			return this.data.getRequirements().getRequirementsFor(this.normalName);
		
		this.requirementsSet.clear();
		
		this.requirementsSet.addAll(this.data.getRequirements().getRequirementsFor(this.normalName));
		this.requirementsSet.addAll(this.data.getRequirements().getRequirementsFor(this.deltaName));
		
		return this.requirementsSet;
	}
	
}
