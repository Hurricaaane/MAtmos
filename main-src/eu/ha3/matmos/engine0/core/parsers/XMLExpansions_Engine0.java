package eu.ha3.matmos.engine0.core.parsers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.xpath.XPathExpressionException;

import net.sf.practicalxml.DomUtil;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import eu.ha3.matmos.engine0.core.implem.Condition;
import eu.ha3.matmos.engine0.core.implem.Event;
import eu.ha3.matmos.engine0.core.implem.Junction;
import eu.ha3.matmos.engine0.core.implem.Knowledge;
import eu.ha3.matmos.engine0.core.implem.Machine;
import eu.ha3.matmos.engine0.core.implem.Operator;
import eu.ha3.matmos.engine0.core.implem.SheetEntry;
import eu.ha3.matmos.engine0.core.implem.StreamInformation;
import eu.ha3.matmos.engine0.core.implem.TimedEvent;
import eu.ha3.matmos.engine0.core.implem.TimedEventInformation;
import eu.ha3.matmos.engine0.core.implem.abstractions.ProviderCollection;
import eu.ha3.matmos.engine0.core.interfaces.Named;

/* x-placeholder */

public class XMLExpansions_Engine0
{
	static final String NAME = "name";
	
	static final String DESCRIPTIBLE = "descriptible";
	
	static final String NICKNAME = "nickname";
	static final String DESCRIPTION = "description";
	static final String ICON = "icon";
	static final String META = "meta";
	
	static final String LIST = "list";
	
	static final String CONDITION = "condition";
	static final String SHEET = "sheet";
	static final String KEY = "key";
	static final String DYNAMICKEY = "dynamickey";
	static final String SYMBOL = "symbol";
	static final String CONSTANT = "constant";
	
	static final String SET = "set";
	static final String TRUEPART = "truepart";
	static final String FALSEPART = "falsepart";
	
	static final String EVENT = "event";
	static final String VOLMIN = "volmin";
	static final String VOLMAX = "volmax";
	static final String PITCHMIN = "pitchmin";
	static final String PITCHMAX = "pitchmax";
	static final String METASOUND = "metasound";
	static final String PATH = "path";
	
	static final String MACHINE = "machine";
	static final String ALLOW = "allow";
	static final String RESTRICT = "restrict";
	
	static final String DYNAMIC = "dynamic";
	static final String ENTRY = "entry";
	
	static final String EVENTTIMED = "eventtimed";
	static final String EVENTNAME = "eventname";
	static final String VOLMOD = "volmod";
	static final String PITCHMOD = "pitchmod";
	static final String DELAYSTART = "delaystart";
	static final String DELAYMIN = "delaymin";
	static final String DELAYMAX = "delaymax";
	
	static final String STREAM = "stream";
	//PATH already covered
	static final String VOLUME = "volume";
	static final String PITCH = "pitch";
	static final String FADEINTIME = "fadeintime";
	static final String FADEOUTTIME = "fadeouttime";
	static final String DELAYBEFOREFADEIN = "delaybeforefadein";
	static final String DELAYBEFOREFADEOUT = "delaybeforefadeout";
	static final String ISLOOPING = "islooping";
	static final String ISUSINGPAUSE = "isusingpause";
	
	private Map<String, Operator> inverseSymbols;
	
	private Knowledge knowledgeWorkstation;
	private LinkedHashSet<Named> elements;
	private ProviderCollection providers;
	
	public XMLExpansions_Engine0()
	{
		this.inverseSymbols = new HashMap<String, Operator>();
		
		Map<Operator, String> symbols = new HashMap<Operator, String>();
		symbols.put(Operator.NOT_EQUAL, "!=");
		symbols.put(Operator.EQUAL, "==");
		symbols.put(Operator.GREATER, ">");
		symbols.put(Operator.GREATER_OR_EQUAL, ">=");
		symbols.put(Operator.LESSER_, "<");
		symbols.put(Operator.LESSER_OR_EQUAL, "<=");
		symbols.put(Operator.IN_LIST, "in");
		symbols.put(Operator.NOT_IN_LIST, "!in");
		symbols.put(Operator.ALWAYS_FALSE, "><");
		
		for (Entry<Operator, String> is : symbols.entrySet())
		{
			this.inverseSymbols.put(is.getValue(), is.getKey());
		}
	}
	
	///
	
	public boolean loadKnowledge(Knowledge original, Document doc)
	{
		try
		{
			parseXML(original, doc);
			return true;
		}
		catch (XPathExpressionException e)
		{
			e.printStackTrace();
			return false;
			
		}
		catch (NumberFormatException e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
	private int toInt(String source)
	{
		try
		{
			return Integer.parseInt(source);
		}
		catch (NumberFormatException e)
		{
			// FIXME: Is cast from float to int safe for about everything?
			return (int) Float.parseFloat(source);
			
		}
		
	}
	
	/*private void parseXMLdynamic(Knowledge original, Element capsule, String name) throws XPathExpressionException
	{
		boolean exists = original.getDynamic(name) != null;
		if (exists && !allowOverrides)
			return;
		
		if (!exists)
		{
			original.addDynamic(name);
		}
		
		Dynamic descriptible = original.getDynamic(name);
		
		for (Element eelt : DomUtil.getChildren(capsule, ENTRY))
		{
			Node nameNode = eelt.getAttributes().getNamedItem(SHEET);
			
			if (nameNode != null)
			{
				// 1.7 DERAIL
				descriptible.addCouple(
					nameNode.getNodeValue(), Integer.toString(Integer.parseInt(eelt.getTextContent())));
				
			}
			
		}
		
	}*/
	
	/*private void parseXMLlist(Knowledge original, Element capsule, String name, boolean allowOverrides)
		throws XPathExpressionException
	{
		boolean exists = original.getList(name) != null;
		if (exists && !allowOverrides)
			return;
		
		if (!exists)
		{
			original.addList(name);
		}
		
		StringListContainer descriptible = original.getList(name);
		
		for (Element eelt : DomUtil.getChildren(capsule, CONSTANT))
		{
			String constant = textOf(eelt);
			
			descriptible.add(constant);
		}
	}*/
	
	private void parseXMLcondition(Element capsule, String name) throws XPathExpressionException
	{
		String sheet = eltString(SHEET, capsule);
		String index = eltString(KEY, capsule);
		String dynamickey = eltString(DYNAMICKEY, capsule);
		String symbol = eltString(SYMBOL, capsule);
		String constant = eltString(CONSTANT, capsule);
		String list = eltString(LIST, capsule);
		
		if (dynamickey != null)
		{
			sheet = "_DYNAMIC";
			index = dynamickey;
		}
		if (list != null)
		{
			constant = list;
		}
		
		Named element =
			new Condition(
				name, this.providers.getSheetCommander(), new SheetEntry(sheet, index),
				this.inverseSymbols.get(symbol), constant);
		this.elements.add(element);
	}
	
	private void parseXMLset(Element capsule, String name)
	{
		List<String> yes = new ArrayList<String>();
		List<String> no = new ArrayList<String>();
		
		for (Element eelt : DomUtil.getChildren(capsule, TRUEPART))
		{
			String truepart = textOf(eelt);
			yes.add(truepart);
		}
		
		for (Element eelt : DomUtil.getChildren(capsule, FALSEPART))
		{
			String falsepart = textOf(eelt);
			no.add(falsepart);
		}
		
		Named element = new Junction(name, this.providers.getCondition(), yes, no);
		this.elements.add(element);
	}
	
	private void parseXMLevent(Element capsule, String name)
	{
		List<String> paths = new ArrayList<String>();
		
		String volmin = eltString(VOLMIN, capsule);
		String volmax = eltString(VOLMAX, capsule);
		String pitchmin = eltString(PITCHMIN, capsule);
		String pitchmax = eltString(PITCHMAX, capsule);
		String metasound = eltString(METASOUND, capsule);
		
		float vol_min = volmin != null ? Float.parseFloat(volmin) : 1f;
		float vol_max = volmax != null ? Float.parseFloat(volmax) : 1f;
		float pitch_min = pitchmin != null ? Float.parseFloat(pitchmin) : 1f;
		float pitch_max = pitchmax != null ? Float.parseFloat(pitchmax) : 1f;
		int distance = metasound != null ? toInt(metasound) : 0;
		
		for (Element eelt : DomUtil.getChildren(capsule, PATH))
		{
			String path = textOf(eelt);
			paths.add(path);
		}
		
		Named element =
			new Event(name, this.providers.getSoundRelay(), paths, vol_min, vol_max, pitch_min, pitch_max, distance);
		this.elements.add(element);
	}
	
	private void parseXMLmachine(Element capsule, String name)
	{
		List<TimedEvent> events = new ArrayList<TimedEvent>();
		for (Element eelt : DomUtil.getChildren(capsule, EVENTTIMED))
		{
			events.add(inscriptXMLeventTimed(eelt));
		}
		
		StreamInformation stream = null;
		for (Element eelt : DomUtil.getChildren(capsule, STREAM))
		{
			stream = inscriptXMLstream(eelt, name);
		}
		
		List<String> allow = new ArrayList<String>();
		for (Element eelt : DomUtil.getChildren(capsule, ALLOW))
		{
			allow.add(textOf(eelt));
		}
		
		List<String> restrict = new ArrayList<String>();
		for (Element eelt : DomUtil.getChildren(capsule, RESTRICT))
		{
			restrict.add(textOf(eelt));
		}
		
		TimedEventInformation tie = null;
		if (events.size() > 0)
		{
			tie =
				new TimedEventInformation(
					name, this.providers.getMachine(), this.providers.getEvent(), this.providers.getReferenceTime(),
					events);
		}
		
		Named element = new Machine(name, this.providers.getJunction(), allow, restrict, tie, stream);
		this.elements.add(element);
	}
	
	private TimedEvent inscriptXMLeventTimed(Element specs)
	{
		String eventname = eltString(EVENTNAME, specs);
		String volmod = eltString(VOLMOD, specs);
		String pitchmod = eltString(PITCHMOD, specs);
		String delaymin = eltString(DELAYMIN, specs);
		String delaymax = eltString(DELAYMAX, specs);
		String delaystart = eltString(DELAYSTART, specs);
		
		float vol_mod = volmod != null ? Float.parseFloat(volmod) : 1f;
		float pitch_mod = pitchmod != null ? Float.parseFloat(pitchmod) : 1f;
		float delay_min = delaymin != null ? Float.parseFloat(delaymin) : 1f; // 1f is dummy
		float delay_max = delaymax != null ? Float.parseFloat(delaymax) : 1f; // 1f is dummy
		float delay_start = delaystart != null ? Float.parseFloat(delaystart) : 1f; // 1f is dummy
		
		return new TimedEvent(eventname, vol_mod, pitch_mod, delay_min, delay_max, delay_start);
	}
	
	private StreamInformation inscriptXMLstream(Element specs, String machineName)
	{
		String _PATH = eltString(PATH, specs);
		String _VOLUME = eltString(VOLUME, specs);
		String _PITCH = eltString(PITCH, specs);
		String _FADEINTIME = eltString(FADEINTIME, specs);
		String _FADEOUTTIME = eltString(FADEOUTTIME, specs);
		String _DELAYBEFOREFADEIN = eltString(DELAYBEFOREFADEIN, specs);
		String _DELAYBEFOREFADEOUT = eltString(DELAYBEFOREFADEOUT, specs);
		String _ISLOOPING = eltString(ISLOOPING, specs);
		String _ISUSINGPAUSE = eltString(ISUSINGPAUSE, specs);
		
		float vol = _VOLUME != null ? Float.parseFloat(_VOLUME) : 1f;
		float pitch = _PITCH != null ? Float.parseFloat(_PITCH) : 1f;
		float fadein = _FADEINTIME != null ? Float.parseFloat(_FADEINTIME) : 1f; // 1f is dummy
		float fadeout = _FADEOUTTIME != null ? Float.parseFloat(_FADEOUTTIME) : 1f; // 1f is dummy
		float delaybeforefadein = _DELAYBEFOREFADEIN != null ? Float.parseFloat(_DELAYBEFOREFADEIN) : 1f; // 1f is dummy
		float delaybeforefadeout = _DELAYBEFOREFADEOUT != null ? Float.parseFloat(_DELAYBEFOREFADEOUT) : 1f; // 1f is dummy
		boolean looping = toInt(_ISLOOPING) == 1;
		boolean pause = toInt(_ISUSINGPAUSE) == 1;
		
		return new StreamInformation(
			machineName, this.providers.getMachine(), this.providers.getReferenceTime(),
			this.providers.getSoundRelay(), _PATH, vol, pitch, delaybeforefadein, delaybeforefadeout, fadein, fadeout,
			looping, pause);
	}
	
	private void parseXML(Knowledge original, Document doc) throws XPathExpressionException, DOMException
	{
		Element elt = doc.getDocumentElement();
		DomUtil.removeEmptyTextRecursive(elt);
		
		this.knowledgeWorkstation = original;
		this.elements = new LinkedHashSet<Named>();
		this.providers = this.knowledgeWorkstation.obtainProviders();
		
		/*{
			NodeList cat = elt.getElementsByTagName(DYNAMIC);
			for (int i = 0; i < cat.getLength(); i++)
			{
				Element capsule = (Element) cat.item(i);
				Node nameNode = capsule.getAttributes().getNamedItem(NAME);
				
				if (nameNode != null)
				{
					parseXMLdynamic(original, capsule, nameNode.getNodeValue());
				}
			}
		}*/
		
		/*{
			NodeList cat = elt.getElementsByTagName(LIST);
			for (int i = 0; i < cat.getLength(); i++)
			{
				Element capsule = (Element) cat.item(i);
				Node nameNode = capsule.getAttributes().getNamedItem(NAME);
				
				if (nameNode != null)
				{
					parseXMLlist(original, capsule, nameNode.getNodeValue());
				}
			}
		}*/
		
		{
			for (Element capsule : DomUtil.getChildren(elt, CONDITION))
			{
				Node nameNode = capsule.getAttributes().getNamedItem(NAME);
				
				if (nameNode != null)
				{
					parseXMLcondition(capsule, nameNode.getNodeValue());
					
				}
			}
		}
		
		{
			NodeList cat = elt.getElementsByTagName(SET);
			for (int i = 0; i < cat.getLength(); i++)
			{
				Element capsule = (Element) cat.item(i);
				Node nameNode = capsule.getAttributes().getNamedItem(NAME);
				
				if (nameNode != null)
				{
					parseXMLset(capsule, nameNode.getNodeValue());
					
				}
			}
		}
		
		{
			NodeList cat = elt.getElementsByTagName(EVENT);
			for (int i = 0; i < cat.getLength(); i++)
			{
				Element capsule = (Element) cat.item(i);
				Node nameNode = capsule.getAttributes().getNamedItem(NAME);
				
				if (nameNode != null)
				{
					parseXMLevent(capsule, nameNode.getNodeValue());
				}
			}
		}
		
		{
			NodeList cat = elt.getElementsByTagName(MACHINE);
			for (int i = 0; i < cat.getLength(); i++)
			{
				Element capsule = (Element) cat.item(i);
				Node nameNode = capsule.getAttributes().getNamedItem(NAME);
				
				if (nameNode != null)
				{
					parseXMLmachine(capsule, nameNode.getNodeValue());
				}
			}
			
		}
		
		/*try
		{
			System.out.println(original.createXML());
		}
		catch (XMLStreamException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
	}
	
	private String eltString(String tagName, Element ele)
	{
		return textOf(DomUtil.getChild(ele, tagName));
	}
	
	private String textOf(Element ele)
	{
		if (ele == null || ele.getFirstChild() == null)
			return null;
		
		return ele.getFirstChild().getNodeValue();
	}
	
}
