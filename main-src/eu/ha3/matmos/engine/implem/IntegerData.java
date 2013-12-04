package eu.ha3.matmos.engine.implem;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import eu.ha3.matmos.engine.interfaces.Data;
import eu.ha3.matmos.engine.interfaces.Sheet;
import eu.ha3.matmos.requirem.Requestable;
import eu.ha3.matmos.requirem.Requirements;

/* x-placeholder */

public class IntegerData implements Data, Requestable
{
	private Map<String, Sheet<Integer>> sheets;
	private int updateVersion;
	private Requirements requirements;
	
	public IntegerData(Requirements requirements)
	{
		this.sheets = new LinkedHashMap<String, Sheet<Integer>>();
		this.updateVersion = 0;
		this.requirements = requirements;
	}
	
	@Override
	public void flagUpdate()
	{
		this.updateVersion = this.updateVersion + 1;
	}
	
	@Override
	public int getVersion()
	{
		return this.updateVersion;
	}
	
	@Override
	public Sheet<Integer> getSheet(String name)
	{
		return this.sheets.get(name);
	}
	
	@Override
	public void setSheet(String name, Sheet<Integer> sheet)
	{
		this.sheets.put(name, sheet);
	}
	
	/*@Override
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
		for (Iterator<Entry<String, Sheet<Integer>>> iter = this.sheets.entrySet().iterator(); iter.hasNext();)
		{
			Entry<String, Sheet<Integer>> entry = iter.next();
			
			int size = entry.getValue().getSize();
			
			eventWriter.add(ret);
			eventWriter.add(eventFactory.createStartElement("", "", "sheet"));
			eventWriter.add(eventFactory.createAttribute("name", entry.getKey()));
			eventWriter.add(eventFactory.createAttribute("size", size + ""));
			eventWriter.add(ret);
			
			for (int i = 0; i < size; i++)
			{
				eventWriter.add(tab);
				eventWriter.add(eventFactory.createStartElement("", "", "key"));
				eventWriter.add(eventFactory.createAttribute("id", i + ""));
				eventWriter.add(eventFactory.createCharacters(entry.getValue().get(i).toString()));
				eventWriter.add(eventFactory.createEndElement("", "", "key"));
				eventWriter.add(ret);
				
			}
			
			eventWriter.add(eventFactory.createEndElement("", "", "sheet"));
		}
		
		eventWriter.add(ret);
		eventWriter.add(eventFactory.createEndElement("", "", "contents"));
		
		eventWriter.add(end);
		eventWriter.add(eventFactory.createEndDocument());
		eventWriter.close();
		
		return serialized.getWriter().toString();
	}*/
	
	@Override
	public Requirements getRequirements()
	{
		return this.requirements;
	}
	
	@Override
	public Set<String> getSheetNames()
	{
		return this.sheets.keySet();
	}
	
}
