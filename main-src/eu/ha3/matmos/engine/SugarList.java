package eu.ha3.matmos.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

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

public class SugarList extends Descriptible
{
	private List<Integer> list;
	
	SugarList()
	{
		this.list = new ArrayList<Integer>();
		
	}
	
	public List<Integer> getList()
	{
		return this.list;
		
	}
	
	public boolean contains(int in)
	{
		return this.list.contains(in);
		
	}
	
	public void add(int in)
	{
		if (this.list.contains(in))
			return;
		
		this.list.add(in);
		Collections.sort(this.list);
	}
	
	public void remove(int in)
	{
		this.list.remove(in);
	}
	
	public void clear()
	{
		this.list.clear();
	}
	
	@Override
	public String serialize(XMLEventWriter eventWriter) throws XMLStreamException
	{
		buildDescriptibleSerialized(eventWriter);
		
		for (Iterator<Integer> iter = this.list.iterator(); iter.hasNext();)
		{
			createNode(eventWriter, "constant", iter.next().toString());
			
		}
		
		return null;
		
	}
	
}
