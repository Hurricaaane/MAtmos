package eu.ha3.matmos.engine0tools;

import static eu.ha3.matmos.jsonformat.JaF.CONDITION_SYMBOL;
import static eu.ha3.matmos.jsonformat.JaF.CONDITION_VALUE;
import static eu.ha3.matmos.jsonformat.JaF.EVENT_DISTANCE;
import static eu.ha3.matmos.jsonformat.JaF.EVENT_PATH;
import static eu.ha3.matmos.jsonformat.JaF.EVENT_PITCH_MAX;
import static eu.ha3.matmos.jsonformat.JaF.EVENT_PITCH_MIN;
import static eu.ha3.matmos.jsonformat.JaF.EVENT_VOL_MAX;
import static eu.ha3.matmos.jsonformat.JaF.EVENT_VOL_MIN;
import static eu.ha3.matmos.jsonformat.JaF.GENERIC_DELAY_FADEIN;
import static eu.ha3.matmos.jsonformat.JaF.GENERIC_DELAY_FADEOUT;
import static eu.ha3.matmos.jsonformat.JaF.GENERIC_ENTRIES;
import static eu.ha3.matmos.jsonformat.JaF.GENERIC_FADEIN;
import static eu.ha3.matmos.jsonformat.JaF.GENERIC_FADEOUT;
import static eu.ha3.matmos.jsonformat.JaF.GENERIC_INDEX;
import static eu.ha3.matmos.jsonformat.JaF.GENERIC_SHEET;
import static eu.ha3.matmos.jsonformat.JaF.MACHINE_ALLOW;
import static eu.ha3.matmos.jsonformat.JaF.MACHINE_EVENT;
import static eu.ha3.matmos.jsonformat.JaF.MACHINE_RESTRICT;
import static eu.ha3.matmos.jsonformat.JaF.MACHINE_STREAM;
import static eu.ha3.matmos.jsonformat.JaF.ROOT_CONDITION;
import static eu.ha3.matmos.jsonformat.JaF.ROOT_DYNAMIC;
import static eu.ha3.matmos.jsonformat.JaF.ROOT_EVENT;
import static eu.ha3.matmos.jsonformat.JaF.ROOT_LIST;
import static eu.ha3.matmos.jsonformat.JaF.ROOT_MACHINE;
import static eu.ha3.matmos.jsonformat.JaF.ROOT_SET;
import static eu.ha3.matmos.jsonformat.JaF.SET_NO;
import static eu.ha3.matmos.jsonformat.JaF.SET_YES;
import static eu.ha3.matmos.jsonformat.JaF.STREAM_LOOPING;
import static eu.ha3.matmos.jsonformat.JaF.STREAM_PATH;
import static eu.ha3.matmos.jsonformat.JaF.STREAM_PAUSE;
import static eu.ha3.matmos.jsonformat.JaF.STREAM_PITCH;
import static eu.ha3.matmos.jsonformat.JaF.STREAM_VOL;
import static eu.ha3.matmos.jsonformat.JaF.TIMED_DELAY_MAX;
import static eu.ha3.matmos.jsonformat.JaF.TIMED_DELAY_MIN;
import static eu.ha3.matmos.jsonformat.JaF.TIMED_DELAY_START;
import static eu.ha3.matmos.jsonformat.JaF.TIMED_EVENT;
import static eu.ha3.matmos.jsonformat.JaF.TIMED_PITCH_MOD;
import static eu.ha3.matmos.jsonformat.JaF.TIMED_VOL_MOD;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.xpath.XPathExpressionException;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.sf.practicalxml.DomUtil;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import eu.ha3.matmos.engine0.core.implem.Condition;
import eu.ha3.matmos.engine0.core.implem.Dynamic;
import eu.ha3.matmos.engine0.core.implem.Event;
import eu.ha3.matmos.engine0.core.implem.Junction;
import eu.ha3.matmos.engine0.core.implem.Knowledge;
import eu.ha3.matmos.engine0.core.implem.LongFloatSimplificator;
import eu.ha3.matmos.engine0.core.implem.Machine;
import eu.ha3.matmos.engine0.core.implem.Possibilities;
import eu.ha3.matmos.engine0.core.implem.SheetEntry;
import eu.ha3.matmos.engine0.core.implem.StreamInformation;
import eu.ha3.matmos.engine0.core.implem.TimedEvent;
import eu.ha3.matmos.engine0.core.implem.TimedEventInformation;
import eu.ha3.matmos.engine0.core.implem.abstractions.ProviderCollection;
import eu.ha3.matmos.engine0.core.interfaces.Named;
import eu.ha3.matmos.engine0.core.interfaces.Operator;
import eu.ha3.matmos.engine0.core.interfaces.SheetIndex;
import eu.ha3.matmos.engine0tools.Jason.Blob;
import eu.ha3.matmos.engine0tools.Jason.Plot;
import eu.ha3.matmos.engine0tools.Jason.Uniq;
import eu.ha3.matmos.game.system.MAtmosUtility;

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
	
	private String UID;
	private Knowledge knowledgeWorkstation;
	private List<Named> elements;
	private ProviderCollection providers;
	
	private Blob o_json;
	
	public LegacyXMLExpansions_Engine1()
	{
		this.scanDicts = new HashMap<String, String>();
		this.scanDicts.put("LargeScan", "scan_large");
		this.scanDicts.put("LargeScanPerMil", "scan_large_p1k");
		this.scanDicts.put("SmallScan", "scan_small");
		this.scanDicts.put("SmallScanPerMil", "scan_small_p1k");
		this.scanDicts.put("ContactScan", "scan_contact");
	}
	
	public boolean loadKnowledge_andConvertToJason(
		String UID, Knowledge original, Document doc, File whereToPutTheJsonFile)
	{
		try
		{
			parseXML(UID, original, doc, whereToPutTheJsonFile);
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
	private String asBlock(int il)
	{
		Block block = Block.func_149729_e(il);
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
		Item item = Item.func_150899_d(il);
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
	
	private String dynamicSheetHash(String name)
	{
		return this.UID.hashCode() % 1000 + "_" + name;
	}
	
	private String eltString(String tagName, Element ele)
	{
		return textOf(DomUtil.getChild(ele, tagName));
	}
	
	private TimedEvent inscriptXMLeventTimed(Element specs, Blob eventBlob)
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
		
		eventBlob.blob(
			TIMED_EVENT, eventname, TIMED_VOL_MOD, vol_mod, TIMED_PITCH_MOD, pitch_mod, TIMED_DELAY_MIN, delay_min,
			TIMED_DELAY_MAX, delay_max, TIMED_DELAY_START, delay_start);
		
		return new TimedEvent(
			eventname, this.providers.getEvent(), vol_mod, pitch_mod, delay_min, delay_max, delay_start);
	}
	
	private StreamInformation inscriptXMLstream(Element specs, String machineName, Blob streamBlob)
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
		
		String path = _PATH;
		float vol = _VOLUME != null ? Float.parseFloat(_VOLUME) : 1f;
		float pitch = _PITCH != null ? Float.parseFloat(_PITCH) : 1f;
		float fadein = _FADEINTIME != null ? Float.parseFloat(_FADEINTIME) : 1f; // 1f is dummy
		float fadeout = _FADEOUTTIME != null ? Float.parseFloat(_FADEOUTTIME) : 1f; // 1f is dummy
		float delay_fadein = _DELAYBEFOREFADEIN != null ? Float.parseFloat(_DELAYBEFOREFADEIN) : 1f; // 1f is dummy
		float delay_fadeout = _DELAYBEFOREFADEOUT != null ? Float.parseFloat(_DELAYBEFOREFADEOUT) : 1f; // 1f is dummy
		boolean looping = toInt(_ISLOOPING) == 1;
		boolean pause = toInt(_ISUSINGPAUSE) == 1;
		
		streamBlob.blob(
			STREAM_PATH, path, STREAM_VOL, vol, STREAM_PITCH, pitch, GENERIC_FADEIN, fadein, GENERIC_FADEOUT, fadeout,
			GENERIC_DELAY_FADEIN, delay_fadein, GENERIC_DELAY_FADEOUT, delay_fadeout, STREAM_LOOPING, looping,
			STREAM_PAUSE, pause);
		
		return new StreamInformation(
			machineName, this.providers.getMachine(), this.providers.getReferenceTime(),
			this.providers.getSoundRelay(), path, vol, pitch, delay_fadein, delay_fadeout, fadein, fadeout, looping,
			pause);
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
	
	private String nameOf(Element element)
	{
		if (element == null)
			return null;
		
		Node nameNode = element.getAttributes().getNamedItem(NAME);
		if (nameNode == null)
			return null;
		
		return nameNode.getNodeValue();
	}
	
	private void parseXML(String UID, Knowledge original, Document doc, File whereToPutTheJsonFile)
		throws XPathExpressionException, DOMException
	{
		Element elt = doc.getDocumentElement();
		DomUtil.removeEmptyTextRecursive(elt);
		
		this.UID = UID;
		this.knowledgeWorkstation = original;
		this.elements = new ArrayList<Named>();
		this.providers = this.knowledgeWorkstation.obtainProviders();
		
		this.o_json = Jason.blob();
		
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
		
		// Uncleaned field order
		System.out.println(Jason.toJson(this.o_json));
		
		//SerialRoot cleanedFieldOrder =
		//	new JasonExpansions_Engine1Deserializer2000().fromJson(Jason.toJson(this.o_json));
		if (whereToPutTheJsonFile != null)
		{
			try
			{
				if (!whereToPutTheJsonFile.exists())
				{
					whereToPutTheJsonFile.createNewFile();
				}
				
				FileWriter write = new FileWriter(whereToPutTheJsonFile);
				write.append(Jason.toJsonPretty(this.o_json));
				//write.append(Jason.toJsonPretty(this.cleanedFieldOrder));
				write.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		
		this.knowledgeWorkstation.addKnowledge(this.elements);
		this.knowledgeWorkstation.compile();
	}
	
	private void parseXML_1_dynamic(Element capsule, String name)
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
			
			SheetIndex si = new LegacySheetIndex_Engine0to1(sheet, index);
			
			entries.add(Jason.blob(GENERIC_SHEET, si.getSheet(), GENERIC_INDEX, si.getIndex()));
			sheetIndexes.add(si);
		}
		joson(ROOT_DYNAMIC).put(name, Jason.blob(GENERIC_ENTRIES, entries));
		
		Named element = new Dynamic(dynamicSheetHash(name), this.providers.getSheetCommander(), sheetIndexes);
		this.elements.add(element);
	}
	
	private void parseXML_2_list(Element capsule, String name)
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
				if (asBlock(il) != null)
				{
					asBlock.add(asBlock(il));
				}
				if (asItem(il) != null)
				{
					asItem.add(asItem(il));
				}
			}
		}
		joson(ROOT_LIST).put(name, Jason.blob(GENERIC_ENTRIES, list));
		joson(ROOT_LIST).put(name + AS_BLOCK, Jason.blob(GENERIC_ENTRIES, asBlock));
		joson(ROOT_LIST).put(name + AS_ITEM, Jason.blob(GENERIC_ENTRIES, asItem));
		
		Named element = new Possibilities(name, list);
		this.elements.add(element);
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
				sheetNotComputed, dynamicSheetHash(indexNotComputed));
		
		// Get the sheet and index directly from the SheetIndex, because
		// the LegacySheetIndex computed the new values for us.
		
		String jasonIndexExcludeDynamic = dynamic ? indexNotComputed : si.getIndex();
		{
			joson(ROOT_CONDITION).put(
				name,
				Jason.blob(
					GENERIC_SHEET, si.getSheet(), GENERIC_INDEX, jasonIndexExcludeDynamic, CONDITION_SYMBOL, Operator
						.fromSymbol(symbol).getSerializedForm(), CONDITION_VALUE, value));
		}
		if (si instanceof LegacySheetIndex_Engine0to1)
		{
			if (((LegacySheetIndex_Engine0to1) si).isBlock() && asBlock(value) != null)
			{
				joson(ROOT_CONDITION).put(
					name + AS_BLOCK,
					Jason.blob(
						GENERIC_SHEET, si.getSheet(), GENERIC_INDEX, jasonIndexExcludeDynamic, CONDITION_SYMBOL,
						Operator.fromSymbol(symbol).getSerializedForm(), CONDITION_VALUE, asBlock(value)));
			}
			
			if (((LegacySheetIndex_Engine0to1) si).isItem() && asItem(value) != null)
			{
				joson(ROOT_CONDITION).put(
					name + AS_ITEM,
					Jason.blob(
						GENERIC_SHEET, si.getSheet(), GENERIC_INDEX, jasonIndexExcludeDynamic, CONDITION_SYMBOL,
						Operator.fromSymbol(symbol).getSerializedForm(), CONDITION_VALUE, asItem(value)));
			}
		}
		
		Named element = new Condition(name, this.providers.getSheetCommander(), si, Operator.fromSymbol(symbol), value);
		this.elements.add(element);
	}
	
	private void parseXML_4_set(Element capsule, String name)
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
		joson(ROOT_SET).put(name, Jason.blob(SET_YES, yes, SET_NO, no));
		
		Named element = new Junction(name, this.providers.getCondition(), yes, no);
		this.elements.add(element);
	}
	
	private void parseXML_5_event(Element capsule, String name)
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
		joson(ROOT_EVENT).put(
			name,
			Jason.blob(
				EVENT_VOL_MIN, vol_min, EVENT_VOL_MAX, vol_max, EVENT_PITCH_MIN, pitch_min, EVENT_PITCH_MAX, pitch_max,
				EVENT_DISTANCE, distance, EVENT_PATH, paths));
		
		Named element =
			new Event(name, this.providers.getSoundRelay(), paths, vol_min, vol_max, pitch_min, pitch_max, distance);
		this.elements.add(element);
	}
	
	private void parseXML_6_machine(Element capsule, String name)
	{
		Blob blob = Jason.blob();
		
		StreamInformation stream = null;
		Blob streamBlob = Jason.blob();
		for (Element eelt : DomUtil.getChildren(capsule, STREAM))
		{
			stream = inscriptXMLstream(eelt, name, streamBlob);
		}
		if (stream != null)
		{
			// Remove these information from the stream to put these on the machine
			
			blob.put(GENERIC_DELAY_FADEIN, streamBlob.remove(GENERIC_DELAY_FADEIN));
			blob.put(GENERIC_DELAY_FADEOUT, streamBlob.remove(GENERIC_DELAY_FADEOUT));
			blob.put(GENERIC_FADEIN, streamBlob.remove(GENERIC_FADEIN));
			blob.put(GENERIC_FADEOUT, streamBlob.remove(GENERIC_FADEOUT));
			
			blob.put(MACHINE_STREAM, streamBlob);
		}
		else
		{
			blob.put(GENERIC_DELAY_FADEIN, 0f);
			blob.put(GENERIC_DELAY_FADEOUT, 0f);
			blob.put(GENERIC_FADEIN, 0f);
			blob.put(GENERIC_FADEOUT, 0f);
		}
		
		List<TimedEvent> events = new ArrayList<TimedEvent>();
		Plot eventPlot = Jason.plot();
		for (Element eelt : DomUtil.getChildren(capsule, EVENTTIMED))
		{
			Blob eventBlob = Jason.blob();
			events.add(inscriptXMLeventTimed(eelt, eventBlob));
			eventPlot.add(eventBlob);
		}
		if (events.size() > 0)
		{
			blob.put(MACHINE_EVENT, eventPlot);
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
			// Ignore fade/delay values for XML expansions
			tie =
				new TimedEventInformation(
					name, this.providers.getMachine(), this.providers.getReferenceTime(), events, 0f, 0f, 0f, 0f);
		}
		
		blob.blob(MACHINE_ALLOW, allow, MACHINE_RESTRICT, restrict);
		joson(ROOT_MACHINE).put(name, blob);
		
		Named element = new Machine(name, this.providers.getJunction(), allow, restrict, tie, stream);
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
