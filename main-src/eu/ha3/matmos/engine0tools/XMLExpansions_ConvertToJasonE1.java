package eu.ha3.matmos.engine0tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.xml.xpath.XPathExpressionException;

import net.sf.practicalxml.DomUtil;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import eu.ha3.matmos.engine0.core.implem.Operator;
import eu.ha3.matmos.engine0.core.implem.StreamInformation;
import eu.ha3.matmos.engine0.core.parsers.UnexpectedDataException;

/*
--filenotes-placeholder
*/

public class XMLExpansions_ConvertToJasonE1
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
	
	@SuppressWarnings("rawtypes")
	private Map o;
	private Map<String, String> names;
	
	public XMLExpansions_ConvertToJasonE1()
	{
		this.inverseSymbols = new HashMap<String, Operator>();
		
		Map<Operator, String> symbols = new HashMap<Operator, String>();
		symbols.put(Operator.NOT_EQUAL, "!=");
		symbols.put(Operator.EQUAL, "==");
		symbols.put(Operator.GREATER, ">");
		symbols.put(Operator.GREATER_OR_EQUAL, ">=");
		symbols.put(Operator.LESSER, "<");
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
	
	public String convertToJason(Document doc) throws UnexpectedDataException
	{
		this.o = new TreeMap<String, Object>();
		this.names = new HashMap<String, String>();
		
		try
		{
			parseToObject(this.o, doc);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new UnexpectedDataException();
		}
		
		Gson gson = new GsonBuilder().create();
		String jason = gson.toJson(this.o);
		
		return jason;
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
	
	private void parseXMLdynamic(Map o, Element capsule, String name) throws XPathExpressionException
	{
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		
		for (Element eelt : DomUtil.getChildren(capsule, ENTRY))
		{
			Node nameNode = eelt.getAttributes().getNamedItem(SHEET);
			
			if (nameNode != null)
			{
				Map<String, String> m = new TreeMap<String, String>();
				m.put("sheet", nameNode.getNodeValue());
				m.put("index", eelt.getTextContent());
				list.add(m);
			}
		}
		
		o.put(name, list);
		
	}
	
	private void parseXMLlist(Map o, Element capsule, String name) throws XPathExpressionException
	{
		List<String> list = new ArrayList<String>();
		
		for (Element eelt : DomUtil.getChildren(capsule, CONSTANT))
		{
			String constant = textOf(eelt);
			list.add(constant);
		}
		Collections.sort(list);
		
		o.put(name, list);
	}
	
	private void parseXMLcondition(Map o, Element capsule, String name) throws XPathExpressionException
	{
		Map<String, Object> m = new LinkedHashMap<String, Object>();
		Map<String, String> a = new LinkedHashMap<String, String>();
		Map<String, String> b = new LinkedHashMap<String, String>();
		
		String sheet = eltString(SHEET, capsule);
		String key = eltString(KEY, capsule);
		String dynamickey = eltString(DYNAMICKEY, capsule);
		String symbol = eltString(SYMBOL, capsule);
		String constant = eltString(CONSTANT, capsule);
		String list = eltString(LIST, capsule);
		
		m.put("a", a);
		
		a.put("type", "sheet");
		if (sheet != null)
		{
			a.put("sheet", sheet);
		}
		
		if (key != null)
		{
			a.put("index", key);
		}
		
		if (dynamickey != null && !dynamickey.equals(""))
		{
			a.put("sheet", "_DYNAMIC");
			a.put("index", key);
		}
		
		if (symbol != null)
		{
			m.put("operator", symbol);
		}
		
		m.put("b", b);
		if (constant != null)
		{
			b.put("type", "constant");
			b.put("constant", constant);
		}
		
		if (list != null)
		{
			b.put("type", "list");
			b.put("list", list);
		}
		
		o.put(name, m);
	}
	
	private void parseXMLset(Map o, Element capsule, String name) throws XPathExpressionException
	{
		Map<String, Object> m = new TreeMap<String, Object>();
		List<String> t = new ArrayList<String>();
		List<String> f = new ArrayList<String>();
		
		m.put("truepart", t);
		for (Element eelt : DomUtil.getChildren(capsule, TRUEPART))
		{
			String truepart = textOf(eelt);
			t.add(truepart);
		}
		Collections.sort(t);
		
		m.put("falsepart", f);
		for (Element eelt : DomUtil.getChildren(capsule, FALSEPART))
		{
			String falsepart = textOf(eelt);
			f.add(falsepart);
		}
		Collections.sort(f);
		
		o.put(name, m);
	}
	
	private void parseXMLevent(Map o, Element capsule, String name) throws XPathExpressionException
	{
		Map<String, Object> m = new TreeMap<String, Object>();
		List<String> p = new ArrayList<String>();
		
		String volmin = eltString(VOLMIN, capsule);
		String volmax = eltString(VOLMAX, capsule);
		String pitchmin = eltString(PITCHMIN, capsule);
		String pitchmax = eltString(PITCHMAX, capsule);
		String metasound = eltString(METASOUND, capsule);
		
		if (volmin != null)
		{
			m.put("vol_min", Float.parseFloat(volmin));
		}
		
		if (volmax != null)
		{
			m.put("vol_max", Float.parseFloat(volmax));
		}
		
		if (pitchmin != null)
		{
			m.put("pitch_min", Float.parseFloat(pitchmin));
		}
		
		if (pitchmax != null)
		{
			m.put("pitch_max", Float.parseFloat(pitchmax));
		}
		
		if (metasound != null)
		{
			m.put("distance", toInt(metasound));
		}
		
		m.put("paths", p);
		for (Element eelt : DomUtil.getChildren(capsule, PATH))
		{
			String path = textOf(eelt);
			p.add(path);
		}
		Collections.sort(p);
		
		o.put(name, m);
	}
	
	private void parseXMLmachine(Map o, Element capsule, String name) throws XPathExpressionException
	{
		Map<String, Object> m = new TreeMap<String, Object>();
		Map<String, Object> timed = new TreeMap<String, Object>();
		List<String> allow = new ArrayList<String>();
		List<String> restrict = new ArrayList<String>();
		
		m.put("events", timed);
		for (Element eelt : DomUtil.getChildren(capsule, EVENTTIMED))
		{
			Map<String, Object> event = new TreeMap<String, Object>();
			inscriptXMLeventTimed(event, eelt);
		}
		
		for (Element eelt : DomUtil.getChildren(capsule, STREAM))
		{
			int size = descriptible.addStream();
			inscriptXMLstream(descriptible.getStream(size - 1), eelt);
		}
		
		m.put("allow", allow);
		for (Element eelt : DomUtil.getChildren(capsule, ALLOW))
		{
			allow.add(textOf(eelt));
		}
		Collections.sort(allow);
		
		m.put("restrict", restrict);
		for (Element eelt : DomUtil.getChildren(capsule, RESTRICT))
		{
			restrict.add(textOf(eelt));
		}
		Collections.sort(restrict);
		
		o.put(name, m);
	}
	
	private void inscriptXMLeventTimed(Map<String, Object> m, Element specs) throws XPathExpressionException
	{
		String eventname = eltString(EVENTNAME, specs);
		String volmod = eltString(VOLMOD, specs);
		String pitchmod = eltString(PITCHMOD, specs);
		String delaystart = eltString(DELAYSTART, specs);
		String delaymin = eltString(DELAYMIN, specs);
		String delaymax = eltString(DELAYMAX, specs);
		
		if (eventname != null)
		{
			m.put("event", eventname);
		}
		
		if (volmod != null)
		{
			m.put("vol_mod", Float.parseFloat(volmod));
		}
		
		if (pitchmod != null)
		{
			m.put("pitch_mod", Float.parseFloat(pitchmod));
		}
		
		if (delaystart != null)
		{
			m.put("delay_start", Float.parseFloat(delaystart));
		}
		
		if (delaymin != null)
		{
			m.put("delay_min", Float.parseFloat(delaymin));
		}
		
		if (delaymax != null)
		{
			m.put("delay_max", Float.parseFloat(delaymax));
		}
	}
	
	private void inscriptXMLstream(StreamInformation inscriptible, Element specs) throws XPathExpressionException
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
		
		if (_PATH != null)
		{
			inscriptible.path = _PATH;
		}
		
		if (_VOLUME != null)
		{
			inscriptible.volume = Float.parseFloat(_VOLUME);
		}
		
		if (_PITCH != null)
		{
			inscriptible.pitch = Float.parseFloat(_PITCH);
		}
		
		if (_FADEINTIME != null)
		{
			inscriptible.fadeInTime = Float.parseFloat(_FADEINTIME);
		}
		
		if (_FADEOUTTIME != null)
		{
			inscriptible.fadeOutTime = Float.parseFloat(_FADEOUTTIME);
		}
		
		if (_DELAYBEFOREFADEIN != null)
		{
			inscriptible.delayBeforeFadeIn = Float.parseFloat(_DELAYBEFOREFADEIN);
		}
		
		if (_DELAYBEFOREFADEOUT != null)
		{
			inscriptible.delayBeforeFadeOut = Float.parseFloat(_DELAYBEFOREFADEOUT);
		}
		
		if (_ISLOOPING != null)
		{
			inscriptible.isLooping = toInt(_ISLOOPING) == 1;
		}
		
		if (_ISUSINGPAUSE != null)
		{
			inscriptible.usesPause = toInt(_ISUSINGPAUSE) == 1;
		}
		
	}
	
	private void parseToObject(Map<String, Object> o, Document doc) throws XPathExpressionException, DOMException
	{
		Element elt = doc.getDocumentElement();
		DomUtil.removeEmptyTextRecursive(elt);
		
		o.put("dynamics", new TreeMap<String, Object>());
		o.put("lists", new TreeMap<String, Object>());
		o.put("conditions", new TreeMap<String, Object>());
		o.put("sets", new TreeMap<String, Object>());
		o.put("events", new TreeMap<String, Object>());
		o.put("machines", new TreeMap<String, Object>());
		
		{
			Map w = (Map) o.get("dynamics");
			
			NodeList cat = elt.getElementsByTagName(DYNAMIC);
			for (int i = 0; i < cat.getLength(); i++)
			{
				Element capsule = (Element) cat.item(i);
				Node nameNode = capsule.getAttributes().getNamedItem(NAME);
				
				if (nameNode != null)
				{
					String name = generateName(name);
					parseXMLdynamic(w, capsule, nameNode.getNodeValue());
				}
			}
		}
		
		{
			Map w = (Map) o.get("lists");
			
			NodeList cat = elt.getElementsByTagName(LIST);
			for (int i = 0; i < cat.getLength(); i++)
			{
				Element capsule = (Element) cat.item(i);
				Node nameNode = capsule.getAttributes().getNamedItem(NAME);
				
				if (nameNode != null)
				{
					parseXMLlist(w, capsule, nameNode.getNodeValue());
					
				}
				
			}
			
		}
		
		{
			Map w = (Map) o.get("conditions");
			
			for (Element capsule : DomUtil.getChildren(elt, CONDITION))
			{
				Node nameNode = capsule.getAttributes().getNamedItem(NAME);
				
				if (nameNode != null)
				{
					parseXMLcondition(w, capsule, nameNode.getNodeValue());
					
				}
				
			}
			
		}
		
		{
			Map w = (Map) o.get("sets");
			
			NodeList cat = elt.getElementsByTagName(SET);
			for (int i = 0; i < cat.getLength(); i++)
			{
				Element capsule = (Element) cat.item(i);
				Node nameNode = capsule.getAttributes().getNamedItem(NAME);
				
				if (nameNode != null)
				{
					parseXMLset(w, capsule, nameNode.getNodeValue());
					
				}
				
			}
			
		}
		
		{
			Map w = (Map) o.get("events");
			
			NodeList cat = elt.getElementsByTagName(EVENT);
			for (int i = 0; i < cat.getLength(); i++)
			{
				Element capsule = (Element) cat.item(i);
				Node nameNode = capsule.getAttributes().getNamedItem(NAME);
				
				if (nameNode != null)
				{
					parseXMLevent(w, capsule, nameNode.getNodeValue());
				}
			}
		}
		
		{
			Map w = (Map) o.get("machines");
			
			NodeList cat = elt.getElementsByTagName(MACHINE);
			for (int i = 0; i < cat.getLength(); i++)
			{
				Element capsule = (Element) cat.item(i);
				Node nameNode = capsule.getAttributes().getNamedItem(NAME);
				
				if (nameNode != null)
				{
					parseXMLmachine(w, capsule, nameNode.getNodeValue());
				}
			}
		}
	}
	
	private String generateName(String name)
	{
		String newName = name.replaceAll("[^a-zA-Z0-9_ ]", "");
		int add = 0;
		if (this.names.containsKey(newName))
		{
			while (this.names.containsKey(newName + Integer.toString(add)))
			{
				add = add + 1;
			}
			newName = newName + Integer.toString(add);
		}
		
		this.names.put(name, newName);
		
		return newName;
	}
	
	private String getName(String name)
	{
		if (!this.names.containsKey(name))
		{
			System.err.println("Parser tried to obtain a name that didn't exist");
			return generateName(name);
		}
		
		return this.names.get(name);
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
