package eu.ha3.matmos.engine;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;

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

public class ConditionSet extends Switchable
{
	private Map<String, Boolean> conditions;
	private boolean isTrueEvaluated;
	
	public ConditionSet(Knowledge knowledgeIn)
	{
		super(knowledgeIn);
		this.isTrueEvaluated = false;
		
		this.conditions = new LinkedHashMap<String, Boolean>();
		
		//setSet(args);
		
	}
	
	@Override
	protected boolean testIfValid()
	{
		if (this.conditions.size() == 0)
			return false;
		
		Iterator<String> iterConditions = this.conditions.keySet().iterator();
		while (iterConditions.hasNext())
		{
			String condition = iterConditions.next();
			
			if (!this.knowledge.conditions.containsKey(condition))
				return false;
			
		}
		
		return true;
		
	}
	
	public void replaceConditionName(String name, String newName)
	{
		flagNeedsTesting();
		
		if (this.conditions.containsKey(name))
		{
			this.conditions.put(newName, this.conditions.get(name));
			this.conditions.remove(name);
			
		}
		
	}
	
	/**
	 * Sets the set.
	 */
	public void setSet(Object... args) throws IllegalArgumentException
	{
		flagNeedsTesting();
		
		if (args.length % 2 == 0)
		{
			this.conditions.clear();
			for (int i = 0; i < args.length / 2; i++)
			{
				this.conditions.put((String) args[i], (Boolean) args[i + 1]);
				
			}
			
		}
		else
		{
			this.conditions.clear();
			throw new IllegalArgumentException();
			
		}
		
	}
	
	public void addCondition(String name, boolean truth) throws IllegalArgumentException
	{
		flagNeedsTesting();
		
		this.conditions.put(name, truth);
		
	}
	
	public void removeCondition(String name)
	{
		flagNeedsTesting();
		
		this.conditions.remove(name);
		
	}
	
	public Map<String, Boolean> getSet()
	{
		return this.conditions;
		
	}
	
	public boolean evaluate()
	{
		if (!isValid())
			return false;
		
		boolean pre = this.isTrueEvaluated;
		this.isTrueEvaluated = testIfTrue();
		
		if (pre != this.isTrueEvaluated)
		{
			//MAtmosEngine.logger; //TODO Logger
			MAtmosLogger.LOGGER.finer(new StringBuilder("S:")
				.append(this.nickname).append(this.isTrueEvaluated ? " now On." : " now Off.").toString());
			
		}
		
		return this.isTrueEvaluated;
		
	}
	
	@Override
	public boolean isActive()
	{
		return isTrue();
		
	}
	
	public boolean isTrue()
	{
		return this.isTrueEvaluated;
		
	}
	
	public boolean testIfTrue()
	{
		if (!isValid())
			return false;
		
		boolean isTrue = true;
		
		Iterator<Entry<String, Boolean>> iterConditions = this.conditions.entrySet().iterator();
		while (isTrue && iterConditions.hasNext())
		{
			Entry<String, Boolean> condition = iterConditions.next();
			
			if (condition.getValue() != this.knowledge.conditions.get(condition.getKey()).isTrue())
			{
				isTrue = false;
				
			}
			
		}
		return isTrue;
		
	}
	
	@Override
	public String serialize(XMLEventWriter eventWriter) throws XMLStreamException
	{
		buildDescriptibleSerialized(eventWriter);
		
		for (Iterator<Entry<String, Boolean>> iter = this.conditions.entrySet().iterator(); iter.hasNext();)
		{
			Entry<String, Boolean> struct = iter.next();
			
			if (struct.getValue() == true)
			{
				createNode(eventWriter, "truepart", struct.getKey());
			}
			else
			{
				createNode(eventWriter, "falsepart", struct.getKey());
			}
			
		}
		
		return "";
	}
	
}
