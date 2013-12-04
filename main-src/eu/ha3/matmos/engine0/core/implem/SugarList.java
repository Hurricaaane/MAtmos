package eu.ha3.matmos.engine0.core.implem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;

/* x-placeholder */

public class SugarList extends Descriptible
{
	private List<String> list;
	
	public SugarList()
	{
		this.list = new ArrayList<String>();
	}
	
	public List<String> getList()
	{
		return this.list;
		
	}
	
	public boolean contains(String in)
	{
		return this.list.contains(in);
		
	}
	
	public void add(String in)
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
		
		for (Iterator<String> iter = this.list.iterator(); iter.hasNext();)
		{
			createNode(eventWriter, "constant", iter.next());
			
		}
		
		return null;
		
	}
	
}
