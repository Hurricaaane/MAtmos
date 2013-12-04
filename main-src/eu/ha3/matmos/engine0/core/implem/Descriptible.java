package eu.ha3.matmos.engine0.core.implem;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

/* x-placeholder */

public abstract class Descriptible
{
	public String name = "";
	public String description = "";
	public String icon = "";
	public String meta = "";
	
	@Override
	public String toString()
	{
		return "[(" + this.getClass().toString() + ") " + this.name + "]";
	}
	
	@Deprecated
	public abstract String serialize(XMLEventWriter eventWriter) throws XMLStreamException;
	
	@Deprecated
	protected void buildDescriptibleSerialized(XMLEventWriter eventWriter) throws XMLStreamException
	{
		XMLEventFactory eventFactory = XMLEventFactory.newInstance();
		XMLEvent ret = eventFactory.createDTD("\n");
		XMLEvent tab = eventFactory.createDTD("\t");
		
		eventWriter.add(tab);
		eventWriter.add(eventFactory.createStartElement("", "", "descriptible"));
		eventWriter.add(ret);
		createNode(eventWriter, "nickname", this.name, 2);
		createNode(eventWriter, "description", this.description, 2);
		createNode(eventWriter, "icon", this.icon, 2);
		createNode(eventWriter, "meta", this.meta, 2);
		eventWriter.add(tab);
		eventWriter.add(eventFactory.createEndElement("", "", "descriptible"));
		eventWriter.add(ret);
		
	}
	
	@Deprecated
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
