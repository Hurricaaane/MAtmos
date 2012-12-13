package eu.ha3.matmos.engine;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.stream.StreamResult;

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

public class Data
{
	public Map<String, ArrayList<Integer>> sheets;
	public int updateVersion;
	
	public Data()
	{
		this.sheets = new LinkedHashMap<String, ArrayList<Integer>>();
		this.updateVersion = 0;
		
	}
	
	public void flagUpdate()
	{
		this.updateVersion = this.updateVersion + 1;
		
	}
	
	public String createXML() throws XMLStreamException
	{
		StreamResult serialized = new StreamResult(new StringWriter());
		
		XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
		XMLEventFactory eventFactory = XMLEventFactory.newInstance();
		
		XMLEventWriter eventWriter = outputFactory.createXMLEventWriter(serialized);
		
		XMLEvent ret = eventFactory.createDTD("\n");
		XMLEvent tab = eventFactory.createDTD("\t");
		XMLEvent end = eventFactory.createDTD("\n");
		
		eventWriter.add(eventFactory.createStartDocument());
		eventWriter.add(ret);
		eventWriter.add(eventFactory.createStartElement("", "", "contents"));
		for (Iterator<Entry<String, ArrayList<Integer>>> iter = this.sheets.entrySet().iterator(); iter.hasNext();)
		{
			Entry<String, ArrayList<Integer>> entry = iter.next();
			
			eventWriter.add(ret);
			eventWriter.add(eventFactory.createStartElement("", "", "sheet"));
			eventWriter.add(eventFactory.createAttribute("name", entry.getKey()));
			eventWriter.add(eventFactory.createAttribute("size", entry.getValue().size() + ""));
			eventWriter.add(ret);
			
			int i = 0;
			for (Iterator<Integer> idter = entry.getValue().iterator(); idter.hasNext();)
			{
				eventWriter.add(tab);
				eventWriter.add(eventFactory.createStartElement("", "", "key"));
				eventWriter.add(eventFactory.createAttribute("id", i + ""));
				eventWriter.add(eventFactory.createCharacters(idter.next().toString()));
				eventWriter.add(eventFactory.createEndElement("", "", "key"));
				eventWriter.add(ret);
				
				i++;
				
			}
			
			eventWriter.add(eventFactory.createEndElement("", "", "sheet"));
		}
		
		eventWriter.add(ret);
		eventWriter.add(eventFactory.createEndElement("", "", "contents"));
		
		eventWriter.add(end);
		eventWriter.add(eventFactory.createEndDocument());
		eventWriter.close();
		
		return serialized.getWriter().toString();
	}
	
}
