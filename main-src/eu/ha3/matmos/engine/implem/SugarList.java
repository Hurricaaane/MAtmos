package eu.ha3.matmos.engine.implem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;

/* x-placeholder */

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
