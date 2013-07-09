package eu.ha3.matmos.engine.implem;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
	private List<String> sheets;
	private List<Integer> keys;
	
	public int value;
	
	public Dynamic(Knowledge knowledgeIn)
	{
		super(knowledgeIn);
		
		this.sheets = new ArrayList<String>();
		this.keys = new ArrayList<Integer>();
		
		this.value = 0;
		
	}
	
	public void addCouple(String sheet, int key)
	{
		this.sheets.add(sheet);
		this.keys.add(key);
		flagNeedsTesting();
		
	}
	
	public void removeCouple(int id)
	{
		this.sheets.remove(id);
		this.keys.remove(id);
		flagNeedsTesting();
		
	}
	
	public void setSheet(int id, String sheet)
	{
		this.sheets.set(id, sheet);
		flagNeedsTesting();
		
	}
	
	public void setKey(int id, int key)
	{
		this.keys.set(id, key);
		flagNeedsTesting();
		
	}
	
	public List<String> getSheets()
	{
		return this.sheets;
		
	}
	
	public List<Integer> getKeys()
	{
		return this.keys;
		
	}
	
	public String getSheet(int id)
	{
		return this.sheets.get(id);
		
	}
	
	public int getKey(int id)
	{
		return this.keys.get(id);
		
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
		
		Iterator<String> iterSheets = this.sheets.iterator();
		Iterator<Integer> iterKeys = this.keys.iterator();
		
		while (iterSheets.hasNext())
		{
			String sheet = iterSheets.next();
			Integer key = iterKeys.next();
			
			this.value = this.value + this.knowledge.getData().getSheet(sheet).get(key);
			
		}
		
	}
	
	@Override
	protected boolean testIfValid()
	{
		Iterator<String> iterSheets = this.sheets.iterator();
		Iterator<Integer> iterKeys = this.keys.iterator();
		
		while (iterSheets.hasNext())
		{
			String sheet = iterSheets.next();
			Integer key = iterKeys.next();
			
			if (this.knowledge.getData().getSheet(sheet) != null)
			{
				if (!(key >= 0 && key < this.knowledge.getData().getSheet(sheet).getSize()))
					return false;
				
			}
			else
				return false;
			
		}
		
		return true;
	}
	
	@Override
	public String serialize(XMLEventWriter eventWriter) throws XMLStreamException
	{
		buildDescriptibleSerialized(eventWriter);
		
		XMLEventFactory eventFactory = XMLEventFactory.newInstance();
		
		XMLEvent tab = eventFactory.createDTD("\t");
		XMLEvent ret = eventFactory.createDTD("\n");
		
		for (int i = 0; i < this.sheets.size(); i++)
		{
			eventWriter.add(tab);
			eventWriter.add(eventFactory.createStartElement("", "", "entry"));
			eventWriter.add(eventFactory.createAttribute("sheet", this.sheets.get(i)));
			eventWriter.add(eventFactory.createCharacters(this.keys.get(i) + ""));
			eventWriter.add(eventFactory.createEndElement("", "", "entry"));
			eventWriter.add(ret);
			
		}
		
		return null;
	}
	
}
