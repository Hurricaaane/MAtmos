package eu.ha3.matmos.engine.implem;

import javax.xml.xpath.XPathExpressionException;

import net.sf.practicalxml.DomUtil;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/* x-placeholder */

public class UtilityLoader
{
	Knowledge knowledgeWorkstation;
	
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
	
	private UtilityLoader()
	{
	}
	
	/**
	 * SingletonHolder is loaded on the first execution of
	 * Singleton.getInstance() or the first access to SingletonHolder.INSTANCE,
	 * not before.
	 */
	private static class MAtmosUtilityLoaderSingletonHolder
	{
		public static final UtilityLoader instance = new UtilityLoader();
	}
	
	public static UtilityLoader getInstance()
	{
		return MAtmosUtilityLoaderSingletonHolder.instance;
	}
	
	///
	
	public boolean loadKnowledge(Knowledge original, Document doc, boolean allowOverrides) throws MAtmosException
	{
		try
		{
			parseXML(original, doc, allowOverrides);
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
	
	private void extractXMLdescriptible(Knowledge original, Element capsule, Descriptible descriptible)
		throws XPathExpressionException
	{
		Element descElt = DomUtil.getChild(capsule, DESCRIPTIBLE);
		
		if (descElt != null)
		{
			parseXMLdescriptible(original, descElt, descriptible);
			
		}
		
	}
	
	private void parseXMLdescriptible(Knowledge original, Element descNode, Descriptible descriptible)
		throws XPathExpressionException
	{
		if (descNode == null)
			return;
		
		String nickname = eltString(NICKNAME, descNode);
		String description = eltString(DESCRIPTION, descNode);
		String icon = eltString(ICON, descNode);
		String meta = eltString(META, descNode);
		
		if (nickname != null)
		{
			descriptible.nickname = nickname;
		}
		if (description != null)
		{
			descriptible.description = description;
		}
		if (icon != null)
		{
			descriptible.icon = icon;
		}
		if (meta != null)
		{
			descriptible.meta = meta;
		}
		
	}
	
	private void parseXMLdynamic(Knowledge original, Element capsule, String name, boolean allowOverrides)
		throws XPathExpressionException
	{
		boolean exists = original.getDynamic(name) != null;
		if (exists && !allowOverrides)
			return;
		
		if (!exists)
		{
			original.addDynamic(name);
		}
		
		Dynamic descriptible = original.getDynamic(name);
		extractXMLdescriptible(original, capsule, descriptible);
		
		for (Element eelt : DomUtil.getChildren(capsule, ENTRY))
		{
			Node nameNode = eelt.getAttributes().getNamedItem(SHEET);
			
			if (nameNode != null)
			{
				descriptible.addCouple(nameNode.getNodeValue(), Integer.parseInt(eelt.getTextContent()));
				
			}
			
		}
		
	}
	
	private void parseXMLlist(Knowledge original, Element capsule, String name, boolean allowOverrides)
		throws XPathExpressionException
	{
		boolean exists = original.getList(name) != null;
		if (exists && !allowOverrides)
			return;
		
		if (!exists)
		{
			original.addList(name);
		}
		
		SugarList descriptible = original.getList(name);
		extractXMLdescriptible(original, capsule, descriptible);
		
		for (Element eelt : DomUtil.getChildren(capsule, CONSTANT))
		{
			String constant = textOf(eelt);
			
			descriptible.add(toInt(constant));
			
		}
		
	}
	
	private void parseXMLcondition(Knowledge original, Element capsule, String name, boolean allowOverrides)
		throws XPathExpressionException
	{
		boolean exists = original.getCondition(name) != null;
		if (exists && !allowOverrides)
			return;
		
		if (!exists)
		{
			original.addCondition(name);
		}
		
		Condition descriptible = original.getCondition(name);
		extractXMLdescriptible(original, capsule, descriptible);
		
		String sheet = eltString(SHEET, capsule);
		String key = eltString(KEY, capsule);
		String dynamickey = eltString(DYNAMICKEY, capsule);
		String symbol = eltString(SYMBOL, capsule);
		String constant = eltString(CONSTANT, capsule);
		String list = eltString(LIST, capsule);
		
		if (sheet != null)
		{
			descriptible.setSheet(sheet);
		}
		
		if (key != null)
		{
			descriptible.setKey(toInt(key));
		}
		
		if (dynamickey != null && !dynamickey.equals(""))
		{
			descriptible.setDynamic(dynamickey);
		}
		
		if (symbol != null)
		{
			descriptible.setSymbol(symbol);
		}
		
		if (constant != null)
		{
			descriptible.setConstant(toInt(constant));
		}
		
		if (list != null)
		{
			descriptible.setList(list);
		}
		
	}
	
	private void parseXMLset(Knowledge original, Element capsule, String name, boolean allowOverrides)
		throws XPathExpressionException
	{
		boolean exists = original.getConditionSet(name) != null;
		if (exists && !allowOverrides)
			return;
		
		if (!exists)
		{
			original.addConditionSet(name);
		}
		
		ConditionSet descriptible = original.getConditionSet(name);
		extractXMLdescriptible(original, capsule, descriptible);
		
		for (Element eelt : DomUtil.getChildren(capsule, TRUEPART))
		{
			String truepart = textOf(eelt);
			
			descriptible.addCondition(truepart, true);
			
		}
		
		for (Element eelt : DomUtil.getChildren(capsule, FALSEPART))
		{
			String falsepart = textOf(eelt);
			
			descriptible.addCondition(falsepart, false);
			
		}
		
	}
	
	private void parseXMLevent(Knowledge original, Element capsule, String name, boolean allowOverrides)
		throws XPathExpressionException
	{
		boolean exists = original.getEvent(name) != null;
		if (exists && !allowOverrides)
			return;
		
		if (!exists)
		{
			original.addEvent(name);
		}
		
		Event descriptible = original.getEvent(name);
		extractXMLdescriptible(original, capsule, descriptible);
		
		String volmin = eltString(VOLMIN, capsule);
		String volmax = eltString(VOLMAX, capsule);
		String pitchmin = eltString(PITCHMIN, capsule);
		String pitchmax = eltString(PITCHMAX, capsule);
		String metasound = eltString(METASOUND, capsule);
		
		if (volmin != null)
		{
			descriptible.volMin = Float.parseFloat(volmin);
		}
		
		if (volmax != null)
		{
			descriptible.volMax = Float.parseFloat(volmax);
		}
		
		if (pitchmin != null)
		{
			descriptible.pitchMin = Float.parseFloat(pitchmin);
		}
		
		if (pitchmax != null)
		{
			descriptible.pitchMax = Float.parseFloat(pitchmax);
		}
		
		if (metasound != null)
		{
			descriptible.metaSound = toInt(metasound);
		}
		
		for (Element eelt : DomUtil.getChildren(capsule, PATH))
		{
			String path = textOf(eelt);
			
			descriptible.paths.add(path);
			
		}
		
	}
	
	private void parseXMLmachine(Knowledge original, Element capsule, String name, boolean allowOverrides)
		throws XPathExpressionException
	{
		boolean exists = original.getMachine(name) != null;
		if (exists && !allowOverrides)
			return;
		
		if (!exists)
		{
			original.addMachine(name);
		}
		
		Machine descriptible = original.getMachine(name);
		extractXMLdescriptible(original, capsule, descriptible);
		
		for (Element eelt : DomUtil.getChildren(capsule, EVENTTIMED))
		{
			int size = descriptible.addEventTimed();
			inscriptXMLeventTimed(descriptible.getEventTimed(size - 1), eelt);
			
		}
		
		for (Element eelt : DomUtil.getChildren(capsule, STREAM))
		{
			int size = descriptible.addStream();
			inscriptXMLstream(descriptible.getStream(size - 1), eelt);
			
		}
		
		for (Element eelt : DomUtil.getChildren(capsule, ALLOW))
		{
			descriptible.addAllow(textOf(eelt));
			
		}
		
		for (Element eelt : DomUtil.getChildren(capsule, RESTRICT))
		{
			descriptible.addRestrict(textOf(eelt));
			
		}
		
	}
	
	private void inscriptXMLeventTimed(TimedEvent inscriptible, Element specs) throws XPathExpressionException
	{
		String eventname = eltString(EVENTNAME, specs);
		String volmod = eltString(VOLMOD, specs);
		String pitchmod = eltString(PITCHMOD, specs);
		String delaystart = eltString(DELAYSTART, specs);
		String delaymin = eltString(DELAYMIN, specs);
		String delaymax = eltString(DELAYMAX, specs);
		
		if (eventname != null)
		{
			inscriptible.event = eventname;
		}
		
		if (volmod != null)
		{
			inscriptible.volMod = Float.parseFloat(volmod);
		}
		
		if (pitchmod != null)
		{
			inscriptible.pitchMod = Float.parseFloat(pitchmod);
		}
		
		if (delaystart != null)
		{
			inscriptible.delayStart = Float.parseFloat(delaystart);
		}
		
		if (delaymin != null)
		{
			inscriptible.delayMin = Float.parseFloat(delaymin);
		}
		
		if (delaymax != null)
		{
			inscriptible.delayMax = Float.parseFloat(delaymax);
		}
		
	}
	
	private void inscriptXMLstream(Stream inscriptible, Element specs) throws XPathExpressionException
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
			inscriptible.isUsingPause = toInt(_ISUSINGPAUSE) == 1;
		}
		
	}
	
	private void parseXML(Knowledge original, Document doc, boolean allowOverrides)
		throws XPathExpressionException, DOMException
	{
		Element elt = doc.getDocumentElement();
		DomUtil.removeEmptyTextRecursive(elt);
		
		{
			NodeList cat = elt.getElementsByTagName(DYNAMIC);
			for (int i = 0; i < cat.getLength(); i++)
			{
				Element capsule = (Element) cat.item(i);
				Node nameNode = capsule.getAttributes().getNamedItem(NAME);
				
				if (nameNode != null)
				{
					parseXMLdynamic(original, capsule, nameNode.getNodeValue(), allowOverrides);
					
				}
				
			}
			
		}
		
		{
			NodeList cat = elt.getElementsByTagName(LIST);
			for (int i = 0; i < cat.getLength(); i++)
			{
				Element capsule = (Element) cat.item(i);
				Node nameNode = capsule.getAttributes().getNamedItem(NAME);
				
				if (nameNode != null)
				{
					parseXMLlist(original, capsule, nameNode.getNodeValue(), allowOverrides);
					
				}
				
			}
			
		}
		
		{
			for (Element capsule : DomUtil.getChildren(elt, CONDITION))
			{
				Node nameNode = capsule.getAttributes().getNamedItem(NAME);
				
				if (nameNode != null)
				{
					parseXMLcondition(original, capsule, nameNode.getNodeValue(), allowOverrides);
					
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
					parseXMLset(original, capsule, nameNode.getNodeValue(), allowOverrides);
					
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
					parseXMLevent(original, capsule, nameNode.getNodeValue(), allowOverrides);
					
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
					parseXMLmachine(original, capsule, nameNode.getNodeValue(), allowOverrides);
					
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
