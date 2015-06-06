package eu.ha3.matmos.tools;

import eu.ha3.matmos.engine.core.implem.Dynamic;
import eu.ha3.matmos.engine.core.implem.LongFloatSimplificator;
import eu.ha3.matmos.engine.core.implem.SheetEntry;
import eu.ha3.matmos.engine.core.interfaces.Operator;
import eu.ha3.matmos.engine.core.interfaces.SheetIndex;
import eu.ha3.matmos.game.system.MAtmosUtility;
import eu.ha3.matmos.jsonformat.serializable.expansion.*;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.sf.practicalxml.DomUtil;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.xpath.XPathExpressionException;
import java.util.HashMap;
import java.util.Map;

/* x-placeholder */

public class LegacyXMLExpansions_Engine1
{
	private static final String AS_ITEM = "__AS_ITEM";
	private static final String AS_BLOCK = "__AS_BLOCK";
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
	
	private Map<String, String> scanDicts;
	
	private SerialRoot root;
	
	public LegacyXMLExpansions_Engine1()
	{
		this.scanDicts = new HashMap<String, String>();
		this.scanDicts.put("LargeScan", "scan_large");
		this.scanDicts.put("LargeScanPerMil", "scan_large_p1k");
		this.scanDicts.put("SmallScan", "scan_small");
		this.scanDicts.put("SmallScanPerMil", "scan_small_p1k");
		this.scanDicts.put("ContactScan", "scan_contact");
	}
	
	public SerialRoot loadXMLtoSerial(Document doc)
	{
		this.root = new SerialRoot();
		
		try
		{
			parseXMLtoSerial(doc);
			return this.root;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return new SerialRoot();
		}
	}
	
	private String asBlock(int il)
	{
		Block block = Block.getBlockById(il);
		if (block == null)
			return null;
		
		return MAtmosUtility.nameOf(block);
	}
	
	private String asBlock(String longFloatSimplificated)
	{
		if (longFloatSimplificated == null)
			return null;
		
		Long l = LongFloatSimplificator.longOf(longFloatSimplificated);
		if (l == null)
			return null;
		
		return asBlock((int) (long) l);
	}
	
	private String asItem(int il)
	{
		Item item = Item.getItemById(il);
		if (item == null)
			return null;
		
		return MAtmosUtility.nameOf(item);
	}
	
	private String asItem(String longFloatSimplificated)
	{
		if (longFloatSimplificated == null)
			return null;
		
		Long l = LongFloatSimplificator.longOf(longFloatSimplificated);
		if (l == null)
			return null;
		
		return asItem((int) (long) l);
	}
	
	private String eltString(String tagName, Element ele)
	{
		return textOf(DomUtil.getChild(ele, tagName));
	}
	
	private SerialMachineEvent inscriptXMLeventTimed(Element specs)
	{
		SerialMachineEvent sme = new SerialMachineEvent();
		
		String eventname = eltString(EVENTNAME, specs);
		String volmod = eltString(VOLMOD, specs);
		String pitchmod = eltString(PITCHMOD, specs);
		String delaymin = eltString(DELAYMIN, specs);
		String delaymax = eltString(DELAYMAX, specs);
		String delaystart = eltString(DELAYSTART, specs);
		
		sme.event = eventname;
		sme.vol_mod = volmod != null ? Float.parseFloat(volmod) : 1f;
		sme.pitch_mod = pitchmod != null ? Float.parseFloat(pitchmod) : 1f;
		sme.delay_min = delaymin != null ? Float.parseFloat(delaymin) : 1f; // 1f is dummy
		sme.delay_max = delaymax != null ? Float.parseFloat(delaymax) : 1f; // 1f is dummy
		sme.delay_start = delaystart != null ? Float.parseFloat(delaystart) : 1f; // 1f is dummy
		
		return sme;
	}
	
	private SerialMachineStream inscriptXMLstream(Element specs, SerialMachine machine)
	{
		SerialMachineStream sms = new SerialMachineStream();
		
		String _PATH = eltString(PATH, specs);
		String _VOLUME = eltString(VOLUME, specs);
		String _PITCH = eltString(PITCH, specs);
		String _FADEINTIME = eltString(FADEINTIME, specs);
		String _FADEOUTTIME = eltString(FADEOUTTIME, specs);
		String _DELAYBEFOREFADEIN = eltString(DELAYBEFOREFADEIN, specs);
		String _DELAYBEFOREFADEOUT = eltString(DELAYBEFOREFADEOUT, specs);
		String _ISLOOPING = eltString(ISLOOPING, specs);
		String _ISUSINGPAUSE = eltString(ISUSINGPAUSE, specs);
		
		sms.path = _PATH;
		sms.vol = _VOLUME != null ? Float.parseFloat(_VOLUME) : 1f;
		sms.pitch = _PITCH != null ? Float.parseFloat(_PITCH) : 1f;
		machine.fadein = _FADEINTIME != null ? Float.parseFloat(_FADEINTIME) : 1f; // 1f is dummy
		machine.fadeout = _FADEOUTTIME != null ? Float.parseFloat(_FADEOUTTIME) : 1f; // 1f is dummy
		machine.delay_fadein = _DELAYBEFOREFADEIN != null ? Float.parseFloat(_DELAYBEFOREFADEIN) : 1f; // 1f is dummy
		machine.delay_fadeout = _DELAYBEFOREFADEOUT != null ? Float.parseFloat(_DELAYBEFOREFADEOUT) : 1f; // 1f is dummy
		sms.looping = toInt(_ISLOOPING) == 1;
		sms.pause = toInt(_ISUSINGPAUSE) == 1;
		
		return sms;
	}
	
	private String nameOf(Element element)
	{
		if (element == null)
			return null;
		
		Node nameNode = element.getAttributes().getNamedItem(NAME);
		if (nameNode == null)
			return null;
		
		return nameNode.getNodeValue();
	}
	
	private void parseXMLtoSerial(Document doc) throws XPathExpressionException, DOMException
	{
		Element elt = doc.getDocumentElement();
		DomUtil.removeEmptyTextRecursive(elt);
		
		for (Element capsule : DomUtil.getChildren(elt, DYNAMIC))
		{
			if (nameOf(capsule) != null)
			{
				parseXML_1_dynamic(capsule, nameOf(capsule));
			}
		}
		for (Element capsule : DomUtil.getChildren(elt, LIST))
		{
			if (nameOf(capsule) != null)
			{
				parseXML_2_list(capsule, nameOf(capsule));
			}
		}
		for (Element capsule : DomUtil.getChildren(elt, CONDITION))
		{
			if (nameOf(capsule) != null)
			{
				parseXML_3_condition(capsule, nameOf(capsule));
			}
		}
		for (Element capsule : DomUtil.getChildren(elt, SET))
		{
			if (nameOf(capsule) != null)
			{
				parseXML_4_set(capsule, nameOf(capsule));
			}
		}
		for (Element capsule : DomUtil.getChildren(elt, EVENT))
		{
			if (nameOf(capsule) != null)
			{
				parseXML_5_event(capsule, nameOf(capsule));
			}
		}
		for (Element capsule : DomUtil.getChildren(elt, MACHINE))
		{
			if (nameOf(capsule) != null)
			{
				parseXML_6_machine(capsule, nameOf(capsule));
			}
		}
	}
	
	private void parseXML_1_dynamic(Element capsule, String name)
	{
		SerialDynamic dynamic = new SerialDynamic();
		
		for (Element eelt : DomUtil.getChildren(capsule, ENTRY))
		{
			SerialDynamicSheetIndex sdsi = new SerialDynamicSheetIndex();
			
			String sheet = eelt.getAttributes().getNamedItem(SHEET).getNodeValue();
			String index = textOf(eelt);
			
			if (sheet.contains("Scan"))
			{
				index = recomputeBlockName(index);
				sheet = recomputeScanSheetName(sheet);
			}
			
			SheetIndex si = new LegacySheetIndex_Engine0to1(sheet, index);
			
			sdsi.sheet = si.getSheet();
			sdsi.index = si.getIndex();
			dynamic.entries.add(sdsi);
		}
		
		this.root.dynamic.put(name, dynamic);
	}
	
	private void parseXML_2_list(Element capsule, String name)
	{
		SerialList list = new SerialList();
		SerialList asItem = new SerialList();
		SerialList asBlock = new SerialList();
		
		for (Element eelt : DomUtil.getChildren(capsule, CONSTANT))
		{
			list.entries.add(textOf(eelt));
			
			Long l = LongFloatSimplificator.longOf(textOf(eelt));
			if (l != null)
			{
				int il = (int) (long) l;
				if (asBlock(il) != null)
				{
					asBlock.entries.add(asBlock(il));
				}
				if (asItem(il) != null)
				{
					asItem.entries.add(asItem(il));
				}
			}
		}
		this.root.list.put(name, list);
		this.root.list.put(name + AS_BLOCK, asBlock);
		this.root.list.put(name + AS_ITEM, asItem);
	}
	
	private void parseXML_3_condition(Element capsule, String name)
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
				sheetNotComputed, indexNotComputed);
		
		{
			SerialCondition condition = new SerialCondition();
			
			condition.sheet = si.getSheet();
			condition.index = si.getIndex();
			condition.symbol = Operator.fromSymbol(symbol).getSerializedForm();
			condition.value = value;
			
			this.root.condition.put(name, condition);
		}
		if (si instanceof LegacySheetIndex_Engine0to1)
		{
			if (((LegacySheetIndex_Engine0to1) si).isBlock() && asBlock(value) != null)
			{
				SerialCondition condition = new SerialCondition();
				
				condition.sheet = si.getSheet();
				condition.index = si.getIndex();
				condition.symbol = Operator.fromSymbol(symbol).getSerializedForm();
				condition.value = asBlock(value);
				
				this.root.condition.put(name + AS_BLOCK, condition);
			}
			
			if (((LegacySheetIndex_Engine0to1) si).isItem() && asItem(value) != null)
			{
				SerialCondition condition = new SerialCondition();
				
				condition.sheet = si.getSheet();
				condition.index = si.getIndex();
				condition.symbol = Operator.fromSymbol(symbol).getSerializedForm();
				condition.value = asItem(value);
				
				this.root.condition.put(name + AS_ITEM, condition);
			}
		}
	}
	
	private void parseXML_4_set(Element capsule, String name)
	{
		SerialSet set = new SerialSet();
		
		for (Element eelt : DomUtil.getChildren(capsule, TRUEPART))
		{
			set.yes.add(textOf(eelt));
		}
		
		for (Element eelt : DomUtil.getChildren(capsule, FALSEPART))
		{
			set.no.add(textOf(eelt));
		}
		
		this.root.set.put(name, set);
	}
	
	private void parseXML_5_event(Element capsule, String name)
	{
		SerialEvent event = new SerialEvent();
		
		String volmin = eltString(VOLMIN, capsule);
		String volmax = eltString(VOLMAX, capsule);
		String pitchmin = eltString(PITCHMIN, capsule);
		String pitchmax = eltString(PITCHMAX, capsule);
		String metasound = eltString(METASOUND, capsule);
		
		event.vol_min = volmin != null ? Float.parseFloat(volmin) : 1f;
		event.vol_max = volmax != null ? Float.parseFloat(volmax) : 1f;
		event.pitch_min = pitchmin != null ? Float.parseFloat(pitchmin) : 1f;
		event.pitch_max = pitchmax != null ? Float.parseFloat(pitchmax) : 1f;
		event.distance = metasound != null ? toInt(metasound) : 0;
		
		for (Element eelt : DomUtil.getChildren(capsule, PATH))
		{
			event.path.add(textOf(eelt));
		}
		
		this.root.event.put(name, event);
	}
	
	private void parseXML_6_machine(Element capsule, String name)
	{
		SerialMachine machine = new SerialMachine();
		
		SerialMachineStream stream = null;
		for (Element eelt : DomUtil.getChildren(capsule, STREAM))
		{
			stream = inscriptXMLstream(eelt, machine);
		}
		if (stream != null)
		{
			machine.stream = stream;
		}
		else
		{
			machine.delay_fadein = 0f;
			machine.delay_fadeout = 0f;
			machine.fadein = 0f;
			machine.fadeout = 0f;
		}
		
		for (Element eelt : DomUtil.getChildren(capsule, EVENTTIMED))
		{
			machine.event.add(inscriptXMLeventTimed(eelt));
		}
		
		for (Element eelt : DomUtil.getChildren(capsule, ALLOW))
		{
			machine.allow.add(textOf(eelt));
		}
		
		for (Element eelt : DomUtil.getChildren(capsule, RESTRICT))
		{
			machine.restrict.add(textOf(eelt));
		}
		
		this.root.machine.put(name, machine);
	}
	
	private String recomputeBlockName(String index)
	{
		Long l = LongFloatSimplificator.longOf(index);
		if (l != null && l < 256)
		{
			Object o = Block.blockRegistry.getObjectById((int) (long) l);
			if (o != null && o instanceof Block)
			{
				index = MAtmosUtility.nameOf((Block) o);
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
	
	private String textOf(Element ele)
	{
		if (ele == null || ele.getFirstChild() == null)
			return null;
		
		return ele.getFirstChild().getNodeValue();
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
	
}
