package eu.ha3.matmos.engine0.core.implem;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

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

public class Dynamic extends Switchable
{
	private List<Entry<String, String>> entries;
	
	private int value;
	
	public Dynamic(Knowledge knowledgeIn)
	{
		super(knowledgeIn);
		
		this.entries = new ArrayList<Entry<String, String>>();
		
		this.value = 0;
		
	}
	
	public void addCouple(String sheet, String key)
	{
		this.entries.add(new AbstractMap.SimpleEntry<String, String>(sheet, key));
		flagNeedsTesting();
		
	}
	
	public void removeCouple(int id)
	{
		this.entries.remove(id);
		flagNeedsTesting();
		
	}
	
	public void set(int id, String sheet, String key)
	{
		this.entries.set(id, new AbstractMap.SimpleEntry<String, String>(sheet, key));
		flagNeedsTesting();
		
	}
	
	public List<Entry<String, String>> getEntries()
	{
		return this.entries;
	}
	
	public Entry<String, String> getEntry(int id)
	{
		return this.entries.get(id);
		
	}
	
	@Override
	public boolean isActive()
	{
		return false;
	}
	
	public void evaluate()
	{
		this.value = 0;
		
		if (!isValid())
			return;
		
		for (Entry<String, String> entry : this.entries)
		{
			String in = this.knowledge.getData().getSheet(entry.getKey()).get(entry.getValue());
			int integerForm = 0;
			
			try
			{
				integerForm = Integer.parseInt(in);
			}
			catch (Exception e)
			{
			}
			
			this.value = this.value + integerForm;
		}
		
	}
	
	@Override
	protected boolean testIfValid()
	{
		for (Entry<String, String> entry : this.entries)
		{
			if (this.knowledge.getData().getSheet(entry.getKey()) == null
				|| !this.knowledge.getData().getSheet(entry.getKey()).containsKey(entry.getValue()))
				return false;
		}
		
		return true;
	}
	
	public int getValue()
	{
		return this.value;
	}
	
	@Override
	public String serialize(XMLEventWriter eventWriter) throws XMLStreamException
	{
		buildDescriptibleSerialized(eventWriter);
		
		XMLEventFactory eventFactory = XMLEventFactory.newInstance();
		
		XMLEvent tab = eventFactory.createDTD("\t");
		XMLEvent ret = eventFactory.createDTD("\n");
		
		for (int i = 0; i < this.entries.size(); i++)
		{
			eventWriter.add(tab);
			eventWriter.add(eventFactory.createStartElement("", "", "entry"));
			eventWriter.add(eventFactory.createAttribute("sheet", this.entries.get(i).getKey()));
			eventWriter.add(eventFactory.createCharacters(this.entries.get(i).getValue() + ""));
			eventWriter.add(eventFactory.createEndElement("", "", "entry"));
			eventWriter.add(ret);
			
		}
		
		return null;
	}
	
}
