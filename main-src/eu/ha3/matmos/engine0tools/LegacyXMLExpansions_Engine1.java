package eu.ha3.matmos.engine0tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import javax.xml.xpath.XPathExpressionException;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.sf.practicalxml.DomUtil;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import eu.ha3.matmos.engine0.core.implem.Condition;
import eu.ha3.matmos.engine0.core.implem.Dynamic;
import eu.ha3.matmos.engine0.core.implem.Event;
import eu.ha3.matmos.engine0.core.implem.Junction;
import eu.ha3.matmos.engine0.core.implem.Knowledge;
import eu.ha3.matmos.engine0.core.implem.LongFloatSimplificator;
import eu.ha3.matmos.engine0.core.implem.Machine;
import eu.ha3.matmos.engine0.core.implem.Operator;
import eu.ha3.matmos.engine0.core.implem.Possibilities;
import eu.ha3.matmos.engine0.core.implem.SheetEntry;
import eu.ha3.matmos.engine0.core.implem.StreamInformation;
import eu.ha3.matmos.engine0.core.implem.TimedEvent;
import eu.ha3.matmos.engine0.core.implem.TimedEventInformation;
import eu.ha3.matmos.engine0.core.implem.abstractions.ProviderCollection;
import eu.ha3.matmos.engine0.core.interfaces.Named;
import eu.ha3.matmos.engine0.core.interfaces.SheetIndex;
import eu.ha3.matmos.game.system.MAtmosUtility;
import eu.ha3.matmos.jason.Jason;
import eu.ha3.matmos.jason.Jason.Blob;
import eu.ha3.matmos.jason.Jason.Plot;
import eu.ha3.matmos.jason.Jason.Uniq;

/* x-placeholder */

public class LegacyXMLExpansions_Engine1
{
	private static final String NAME = "name";
	private static final String LIST = "list";
	
	private static final String CONDITION = "condition";
	private static final String SHEET = "sheet";
	private static final String KEY = "key";
	private static final String DYNAMICKEY = "dynamickey";
	private static final String SYMBOL = "symbol";
	private static final String CONSTANT = "constant";
	
	private static final String SET = "set";
	private static final String TRUEPART = "truepart";
	private static final String FALSEPART = "falsepart";
	
	private static final String EVENT = "event";
	private static final String VOLMIN = "volmin";
	private static final String VOLMAX = "volmax";
	private static final String PITCHMIN = "pitchmin";
	private static final String PITCHMAX = "pitchmax";
	private static final String METASOUND = "metasound";
	private static final String PATH = "path";
	
	private static final String MACHINE = "machine";
	private static final String ALLOW = "allow";
	private static final String RESTRICT = "restrict";
	
	private static final String DYNAMIC = "dynamic";
	private static final String ENTRY = "entry";
	
	private static final String EVENTTIMED = "eventtimed";
	private static final String EVENTNAME = "eventname";
	private static final String VOLMOD = "volmod";
	private static final String PITCHMOD = "pitchmod";
	private static final String DELAYSTART = "delaystart";
	private static final String DELAYMIN = "delaymin";
	private static final String DELAYMAX = "delaymax";
	
	private static final String STREAM = "stream";
	//PATH already covered
	private static final String VOLUME = "volume";
	private static final String PITCH = "pitch";
	private static final String FADEINTIME = "fadeintime";
	private static final String FADEOUTTIME = "fadeouttime";
	private static final String DELAYBEFOREFADEIN = "delaybeforefadein";
	private static final String DELAYBEFOREFADEOUT = "delaybeforefadeout";
	private static final String ISLOOPING = "islooping";
	private static final String ISUSINGPAUSE = "isusingpause";
	
	private Map<String, Operator> inverseSymbols;
	private Map<Operator, String> serializedSymbols;
	private Map<String, String> scanDicts;
	
	private Knowledge knowledgeWorkstation;
	private List<Named> elements;
	private ProviderCollection providers;
	
	private Blob o_json;
	
	public LegacyXMLExpansions_Engine1()
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
		
		this.serializedSymbols = new HashMap<Operator, String>();
		this.serializedSymbols.put(Operator.NOT_EQUAL, "NOT_EQUAL");
		this.serializedSymbols.put(Operator.EQUAL, "EQUAL");
		this.serializedSymbols.put(Operator.GREATER, "GREATER");
		this.serializedSymbols.put(Operator.GREATER_OR_EQUAL, "GREATER_OR_EQUAL");
		this.serializedSymbols.put(Operator.LESSER, "LESSER");
		this.serializedSymbols.put(Operator.LESSER_OR_EQUAL, "LESSER_OR_EQUAL");
		this.serializedSymbols.put(Operator.IN_LIST, "IN_LIST");
		this.serializedSymbols.put(Operator.NOT_IN_LIST, "NOT_IN_LIST");
		this.serializedSymbols.put(Operator.ALWAYS_FALSE, "ALWAYS_FALSE");
		
		this.scanDicts = new HashMap<String, String>();
		this.scanDicts.put("LargeScan", "scan_large");
		this.scanDicts.put("LargeScanPerMil", "scan_large_p1k");
		this.scanDicts.put("SmallScan", "scan_small");
		this.scanDicts.put("SmallScanPerMil", "scan_small_p1k");
		this.scanDicts.put("ContactScan", "scan_contact");
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
			return (int) Float.parseFloat(source);
		}
	}
	
	private Map<String, String> oxDynamic = new HashMap<String, String>();
	
	private void parseXMLdynamic(Element capsule, String name)
	{
		List<SheetIndex> sheetIndexes = new ArrayList<SheetIndex>();
		
		Plot entries = Jason.plot();
		for (Element eelt : DomUtil.getChildren(capsule, ENTRY))
		{
			String sheet = eelt.getAttributes().getNamedItem(SHEET).getNodeValue();
			String index = textOf(eelt);
			
			if (sheet.contains("Scan"))
			{
				index = recomputeBlockName(index);
				sheet = recomputeScanSheetName(sheet);
			}
			
			entries.add(Jason.blob("sheet", sheet, "index", index));
			sheetIndexes.add(new LegacySheetIndex_Engine0to1(sheet, index));
		}
		joson(JasonExpansions_Engine1.ROOT_DYNAMIC).put(
			name, Jason.blob(JasonExpansions_Engine1.GENERIC_ENTRIES, entries));
		
		this.oxDynamic.put(name, new Random().nextInt() + "__" + name);
		Named element = new Dynamic(this.oxDynamic.get(name), this.providers.getSheetCommander(), sheetIndexes);
		this.elements.add(element);
	}
	
	private void parseXMLlist(Element capsule, String name)
	{
		List<String> list = new ArrayList<String>();
		
		Uniq asItem = Jason.uniq();
		Uniq asBlock = Jason.uniq();
		for (Element eelt : DomUtil.getChildren(capsule, CONSTANT))
		{
			list.add(textOf(eelt));
			
			Long l = LongFloatSimplificator.longOf(textOf(eelt));
			if (l != null)
			{
				int il = (int) (long) l;
				if (Block.func_149729_e(il) != null)
				{
					asBlock.add(MAtmosUtility.nameOf(Block.func_149729_e(il)));
				}
				if (Item.func_150899_d(il) != null)
				{
					asItem.add(MAtmosUtility.nameOf(Item.func_150899_d(il)));
				}
			}
		}
		joson(JasonExpansions_Engine1.ROOT_LIST).put(name, Jason.blob(JasonExpansions_Engine1.GENERIC_ENTRIES, list));
		joson(JasonExpansions_Engine1.ROOT_LIST).put(
			name + "__as_block", Jason.blob(JasonExpansions_Engine1.GENERIC_ENTRIES, asBlock));
		joson(JasonExpansions_Engine1.ROOT_LIST).put(
			name + "__as_item", Jason.blob(JasonExpansions_Engine1.GENERIC_ENTRIES, asItem));
		
		Named element = new Possibilities(name, list);
		this.elements.add(element);
	}
	
	private void parseXMLcondition(Element capsule, String name)
	{
		String sheetNotComputed = eltString(SHEET, capsule);
		String indexNotComputed = eltString(KEY, capsule);
		String dynamicIndexXX = eltString(DYNAMICKEY, capsule);
		String symbol = eltString(SYMBOL, capsule);
		String value = eltString(CONSTANT, capsule);
		String listValueXX = eltString(LIST, capsule);
		
		boolean dynamic = false;
		
		if (dynamicIndexXX != null)
		{
			sheetNotComputed = Dynamic.DEDICATED_SHEET;
			indexNotComputed = dynamicIndexXX;
			dynamic = true;
		}
		if (listValueXX != null)
		{
			value = listValueXX;
		}
		
		if (sheetNotComputed.contains("Scan"))
		{
			indexNotComputed = recomputeBlockName(indexNotComputed);
			sheetNotComputed = recomputeScanSheetName(sheetNotComputed);
		}
		
		SheetIndex si =
			!dynamic ? new LegacySheetIndex_Engine0to1(sheetNotComputed, indexNotComputed) : new SheetEntry(
				sheetNotComputed, this.oxDynamic.containsKey(indexNotComputed)
					? this.oxDynamic.get(indexNotComputed) : "__INVALID__" + indexNotComputed);
		
		// Get the sheet and index directly from the SheetIndex, because
		// the LegacySheetIndex computed the new values for us. 
		Blob blob =
			Jason.blob(
				"sheet", si.getSheet(), "index", si.getIndex(), "symbol",
				this.serializedSymbols.get(this.inverseSymbols.get(symbol)), "value", value);
		
		joson("condition").put(name, blob);
		Named element =
			new Condition(name, this.providers.getSheetCommander(), si, this.inverseSymbols.get(symbol), value);
		this.elements.add(element);
	}
	
	private String recomputeBlockName(String index)
	{
		Long l = LongFloatSimplificator.longOf(index);
		if (l != null && l < 256)
		{
			Object o = Block.field_149771_c.func_148754_a((int) (long) l);
			if (o != null && o instanceof Block)
			{
				//String ocst = index;
				index = MAtmosUtility.nameOf((Block) o);
				//System.out.println("Converted index from " + ocst + " to " + index);
			}
			else
			{
				System.err.println("??? Failed to convert block with index " + index);
			}
		}
		else
		{
			System.err.println("??? Failed to convert block with index " + index + " out of bounds?");
		}
		
		return index;
	}
	
	private String recomputeScanSheetName(String sheet)
	{
		if (this.scanDicts.containsKey(sheet))
			return this.scanDicts.get(sheet);
		
		System.err.println("Scan sheet has no equivalent: " + this.scanDicts);
		return sheet;
		
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
		joson(JasonExpansions_Engine1.ROOT_SET).put(name, Jason.blob("yes", yes, "no", no));
		
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
		joson("event").put(
			name,
			Jason.blob(
				"vol_min", vol_min, "vol_max", vol_max, "pitch_min", pitch_min, "pitch_min", pitch_max, "distance",
				distance, "paths", paths));
		
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
		
		return new TimedEvent(
			eventname, this.providers.getEvent(), vol_mod, pitch_mod, delay_min, delay_max, delay_start);
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
	
	/**
	 * Returns the named blob from the root blob, creating it if it didn't
	 * exist.
	 * 
	 * @param klass
	 * @return
	 */
	private Blob joson(String klass)
	{
		if (!this.o_json.containsKey(klass))
		{
			this.o_json.put(klass, Jason.blob());
		}
		
		return (Blob) this.o_json.get(klass);
	}
	
	private void parseXML(Knowledge original, Document doc) throws XPathExpressionException, DOMException
	{
		Element elt = doc.getDocumentElement();
		DomUtil.removeEmptyTextRecursive(elt);
		
		this.knowledgeWorkstation = original;
		this.elements = new ArrayList<Named>();
		this.providers = this.knowledgeWorkstation.obtainProviders();
		
		this.o_json = Jason.blob();
		
		{
			NodeList cat = elt.getElementsByTagName(DYNAMIC);
			for (int i = 0; i < cat.getLength(); i++)
			{
				Element capsule = (Element) cat.item(i);
				Node nameNode = capsule.getAttributes().getNamedItem(NAME);
				
				if (nameNode != null)
				{
					parseXMLdynamic(capsule, nameNode.getNodeValue());
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
					parseXMLlist(capsule, nameNode.getNodeValue());
				}
			}
		}
		
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
		
		System.out.println(Jason.toJson(this.o_json));
		
		this.knowledgeWorkstation.addKnowledge(this.elements);
		this.knowledgeWorkstation.compile();
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
