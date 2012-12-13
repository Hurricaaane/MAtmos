package eu.ha3.matmos.engine;

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

public abstract class Descriptible
{
	public String nickname = "";
	public String description = "";
	public String icon = "";
	public String meta = "";
	
	public abstract String serialize(XMLEventWriter eventWriter) throws XMLStreamException;
	
	@Override
	public String toString()
	{
		return "[(" + this.getClass().toString() + ") " + this.nickname + "]";
		
	}
	
	protected void buildDescriptibleSerialized(XMLEventWriter eventWriter) throws XMLStreamException
	{
		XMLEventFactory eventFactory = XMLEventFactory.newInstance();
		XMLEvent ret = eventFactory.createDTD("\n");
		XMLEvent tab = eventFactory.createDTD("\t");
		
		eventWriter.add(tab);
		eventWriter.add(eventFactory.createStartElement("", "", "descriptible"));
		eventWriter.add(ret);
		createNode(eventWriter, "nickname", this.nickname, 2);
		createNode(eventWriter, "description", this.description, 2);
		createNode(eventWriter, "icon", this.icon, 2);
		createNode(eventWriter, "meta", this.meta, 2);
		eventWriter.add(tab);
		eventWriter.add(eventFactory.createEndElement("", "", "descriptible"));
		eventWriter.add(ret);
		
	}
	
	protected void createNode(XMLEventWriter eventWriter, String name, String value, int tabCount)
		throws XMLStreamException
	{
		XMLEventFactory eventFactory = XMLEventFactory.newInstance();
		XMLEvent tab = eventFactory.createDTD("\t");
		XMLEvent end = eventFactory.createDTD("\n");
		
		for (int i = 0; i < tabCount; i++)
		{
			eventWriter.add(tab);
		}
		
		eventWriter.add(eventFactory.createStartElement("", "", name));
		eventWriter.add(eventFactory.createCharacters(value));
		eventWriter.add(eventFactory.createEndElement("", "", name));
		eventWriter.add(end);
		
	}
	
	protected void createNode(XMLEventWriter eventWriter, String name, String value) throws XMLStreamException
	{
		createNode(eventWriter, name, value, 1);
		
	}
	
}
