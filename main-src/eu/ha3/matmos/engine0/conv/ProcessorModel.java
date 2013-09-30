package eu.ha3.matmos.engine0.conv;

import java.util.HashSet;
import java.util.Set;

import eu.ha3.matmos.engine0.core.implem.StringData;
import eu.ha3.matmos.engine0.core.interfaces.Sheet;

/*
            DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE 
                    Version 2, December 2004 

 Copyright (C) 2004 Sam Hocevar <sam@hocevar.net> 

 Everyone is permitted to copy and distribute verbatim or modified 
 copies of this license document, and changing it is allowed as long 
 as the name is changed. 

            DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE 
   TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION 

  0. You just DO WHAT THE FUCK YOU WANT TO. 
*/

public abstract class ProcessorModel implements Processor
{
	private StringData data;
	
	private String normalName;
	private String deltaName;
	
	private Sheet<String> normalSheet;
	private Sheet<String> deltaSheet;
	
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
	
	@Deprecated
	public void setValue(int index, int newValue)
	{
		setValue(index, Integer.toString(newValue));
	}
	
	public void setValue(String index, int newValue)
	{
		setValue(index, Integer.toString(newValue));
	}
	
	@Deprecated
	public void setValue(int index, String newValue)
	{
		// 1.7 DERAIL
		setValue(Integer.toString(index), newValue);
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
