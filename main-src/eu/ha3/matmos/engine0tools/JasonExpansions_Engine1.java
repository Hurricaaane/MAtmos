package eu.ha3.matmos.engine0tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import eu.ha3.matmos.engine0.core.implem.Condition;
import eu.ha3.matmos.engine0.core.implem.Dynamic;
import eu.ha3.matmos.engine0.core.implem.Event;
import eu.ha3.matmos.engine0.core.implem.Junction;
import eu.ha3.matmos.engine0.core.implem.Knowledge;
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
import eu.ha3.matmos.expansions.ExpansionIdentity;

/*
--filenotes-placeholder
*/

public class JasonExpansions_Engine1
{
	public static final String CONDITION_SYMBOL = "symbol";
	public static final String CONDITION_VALUE = "value";
	public static final String EVENT_DISTANCE = "distance";
	public static final String EVENT_PATH = "path";
	public static final String EVENT_PITCH_MAX = "pitch_max";
	public static final String EVENT_PITCH_MIN = "pitch_min";
	public static final String EVENT_VOL_MAX = "vol_max";
	public static final String EVENT_VOL_MIN = "vol_min";
	public static final String GENERIC_ENTRIES = "entries";
	public static final String GENERIC_INDEX = "index";
	public static final String GENERIC_SHEET = "sheet";
	public static final String MACHINE_ALLOW = "allow";
	public static final String MACHINE_EVENT = "event";
	public static final String MACHINE_RESTRICT = "restrict";
	public static final String MACHINE_STREAM = "stream";
	public static final String ROOT_CONDITION = "condition";
	public static final String ROOT_DYNAMIC = "dynamic";
	
	public static final String ROOT_EVENT = "event";
	public static final String ROOT_LIST = "list";
	public static final String ROOT_MACHINE = "machine";
	public static final String ROOT_SET = "set";
	public static final String SET_NO = "no";
	public static final String SET_YES = "yes";
	public static final String GENERIC_DELAY_FADEIN = "delay_fadein";
	public static final String GENERIC_DELAY_FADEOUT = "delay_fadeout";
	public static final String GENERIC_FADEIN = "fadein";
	public static final String GENERIC_FADEOUT = "fadeout";
	public static final String STREAM_LOOPING = "looping";
	public static final String STREAM_PATH = "path";
	public static final String STREAM_PAUSE = "pause";
	public static final String STREAM_PITCH = "pitch";
	public static final String STREAM_VOL = "vol";
	public static final String TIMED_DELAY_MAX = "delay_max";
	public static final String TIMED_DELAY_MIN = "delay_min";
	public static final String TIMED_DELAY_START = "delay_start";
	public static final String TIMED_EVENT = "event";
	public static final String TIMED_PITCH_MOD = "pitch_mod";
	public static final String TIMED_VOL_MOD = "vol_mod";
	
	private ArrayList<Named> elements;
	private Knowledge knowledgeWorkstation;
	private ProviderCollection providers;
	
	private Map<Operator, String> serializedSymbols;
	private Map<String, Operator> inverseSymbols;
	
	private String UID;
	
	public JasonExpansions_Engine1()
	{
		this.serializedSymbols = new HashMap<Operator, String>();
		this.inverseSymbols = new HashMap<String, Operator>();
		
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
		
		for (Entry<Operator, String> is : this.serializedSymbols.entrySet())
		{
			this.inverseSymbols.put(is.getValue(), is.getKey());
		}
	}
	
	public boolean parseJson(String jasonString, ExpansionIdentity identity, Knowledge knowledge)
	{
		try
		{
			parseJSONUnsafe(jasonString, identity, knowledge);
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
	public void parseJSONUnsafe(String jasonString, ExpansionIdentity identity, Knowledge knowledge)
	{
		this.UID = identity.getUniqueName();
		this.knowledgeWorkstation = knowledge;
		this.elements = new ArrayList<Named>();
		this.providers = this.knowledgeWorkstation.obtainProviders();
		
		JsonObject jason = new JsonParser().parse(jasonString).getAsJsonObject();
		
		if (jason.has(ROOT_DYNAMIC))
		{
			for (Entry<String, JsonElement> entry : iterObject(ROOT_DYNAMIC, jason))
			{
				parseXML_1_dynamic(entry.getValue().getAsJsonObject(), entry.getKey());
			}
		}
		if (jason.has(ROOT_LIST))
		{
			for (Entry<String, JsonElement> entries : iterObject(ROOT_LIST, jason))
			{
				parseXML_2_list(entries.getValue().getAsJsonObject(), entries.getKey());
			}
		}
		if (jason.has(ROOT_CONDITION))
		{
			for (Entry<String, JsonElement> entries : iterObject(ROOT_CONDITION, jason))
			{
				parseXML_3_condition(entries.getValue().getAsJsonObject(), entries.getKey());
			}
		}
		if (jason.has(ROOT_SET))
		{
			for (Entry<String, JsonElement> entries : iterObject(ROOT_SET, jason))
			{
				parseXML_4_set(entries.getValue().getAsJsonObject(), entries.getKey());
			}
		}
		if (jason.has(ROOT_EVENT))
		{
			for (Entry<String, JsonElement> entries : iterObject(ROOT_EVENT, jason))
			{
				parseXML_5_event(entries.getValue().getAsJsonObject(), entries.getKey());
			}
		}
		if (jason.has(ROOT_MACHINE))
		{
			for (Entry<String, JsonElement> entries : iterObject(ROOT_MACHINE, jason))
			{
				parseXML_6_machine(entries.getValue().getAsJsonObject(), entries.getKey());
			}
		}
		
		this.knowledgeWorkstation.addKnowledge(this.elements);
		this.knowledgeWorkstation.compile();
	}
	
	private String dynamicSheetHash(String name)
	{
		return this.UID.hashCode() % 1000 + "_" + name;
	}
	
	private String eltString(String member, JsonElement capsule)
	{
		return eltString(member, capsule.getAsJsonObject());
	}
	
	private String eltString(String member, JsonObject capsule)
	{
		return capsule.get(member).getAsString();
	}
	
	private TimedEvent inscriptXMLeventTimed(JsonObject specs)
	{
		String eventname = eltString(TIMED_EVENT, specs);
		float vol_mod = Float.parseFloat(eltString(TIMED_VOL_MOD, specs));
		float pitch_mod = Float.parseFloat(eltString(TIMED_PITCH_MOD, specs));
		float delay_min = Float.parseFloat(eltString(TIMED_DELAY_MIN, specs));
		float delay_max = Float.parseFloat(eltString(TIMED_DELAY_MAX, specs));
		float delay_start = Float.parseFloat(eltString(TIMED_DELAY_START, specs));
		
		return new TimedEvent(
			eventname, this.providers.getEvent(), vol_mod, pitch_mod, delay_min, delay_max, delay_start);
	}
	
	private StreamInformation inscriptXMLstream(
		JsonObject specs, String machineName, float delayBeforeFadeIn, float delayBeforeFadeOut, float fadeInTime,
		float fadeOutTime)
	{
		String path = eltString(STREAM_PATH, specs);
		float vol = Float.parseFloat(eltString(STREAM_VOL, specs));
		float pitch = Float.parseFloat(eltString(STREAM_PITCH, specs));
		boolean looping = Boolean.parseBoolean(eltString(STREAM_LOOPING, specs));
		boolean pause = Boolean.parseBoolean(eltString(STREAM_PAUSE, specs));
		
		return new StreamInformation(
			machineName, this.providers.getMachine(), this.providers.getReferenceTime(),
			this.providers.getSoundRelay(), path, vol, pitch, delayBeforeFadeIn, delayBeforeFadeOut, fadeInTime,
			fadeOutTime, looping, pause);
	}
	
	private JsonArray iterArray(String category, JsonObject capsule)
	{
		return capsule.getAsJsonArray(category);
	}
	
	private Set<Entry<String, JsonElement>> iterObject(String category, JsonObject capsule)
	{
		return capsule.getAsJsonObject(category).entrySet();
	}
	
	private void parseXML_1_dynamic(JsonObject capsule, String name)
	{
		List<SheetIndex> sheetIndexes = new ArrayList<SheetIndex>();
		
		for (JsonElement eelt : iterArray(GENERIC_ENTRIES, capsule))
		{
			String sheet = eltString(GENERIC_SHEET, eelt);
			String index = eltString(GENERIC_INDEX, eelt);
			
			sheetIndexes.add(new SheetEntry(sheet, index));
		}
		
		Named element = new Dynamic(dynamicSheetHash(name), this.providers.getSheetCommander(), sheetIndexes);
		this.elements.add(element);
	}
	
	private void parseXML_2_list(JsonObject capsule, String name)
	{
		List<String> list = new ArrayList<String>();
		
		for (JsonElement eelt : iterArray(GENERIC_ENTRIES, capsule))
		{
			list.add(eelt.getAsString());
		}
		
		Named element = new Possibilities(name, list);
		this.elements.add(element);
	}
	
	private void parseXML_3_condition(JsonObject capsule, String name)
	{
		String sheetNotComputed = eltString(GENERIC_SHEET, capsule);
		String indexNotComputed = eltString(GENERIC_INDEX, capsule);
		String serializedSymbol = eltString(CONDITION_SYMBOL, capsule);
		String value = eltString(CONDITION_VALUE, capsule);
		
		if (sheetNotComputed.equals(Dynamic.DEDICATED_SHEET))
		{
			indexNotComputed = dynamicSheetHash(indexNotComputed);
		}
		
		Named element =
			new Condition(
				name, this.providers.getSheetCommander(), new SheetEntry(sheetNotComputed, indexNotComputed),
				this.inverseSymbols.get(serializedSymbol), value);
		this.elements.add(element);
	}
	
	private void parseXML_4_set(JsonObject capsule, String name)
	{
		List<String> yes = new ArrayList<String>();
		List<String> no = new ArrayList<String>();
		
		for (JsonElement eelt : iterArray(SET_YES, capsule))
		{
			yes.add(eelt.getAsString());
		}
		
		for (JsonElement eelt : iterArray(SET_NO, capsule))
		{
			no.add(eelt.getAsString());
		}
		
		Named element = new Junction(name, this.providers.getCondition(), yes, no);
		this.elements.add(element);
	}
	
	private void parseXML_5_event(JsonObject capsule, String name)
	{
		List<String> paths = new ArrayList<String>();
		
		float vol_min = Float.parseFloat(eltString(EVENT_VOL_MIN, capsule));
		float vol_max = Float.parseFloat(eltString(EVENT_VOL_MAX, capsule));
		float pitch_min = Float.parseFloat(eltString(EVENT_PITCH_MIN, capsule));
		float pitch_max = Float.parseFloat(eltString(EVENT_PITCH_MAX, capsule));
		int distance = toInt(eltString(EVENT_DISTANCE, capsule));
		
		for (JsonElement eelt : iterArray(EVENT_PATH, capsule))
		{
			paths.add(eelt.getAsString());
		}
		
		Named element =
			new Event(name, this.providers.getSoundRelay(), paths, vol_min, vol_max, pitch_min, pitch_max, distance);
		this.elements.add(element);
	}
	
	private void parseXML_6_machine(JsonObject capsule, String name)
	{
		List<TimedEvent> events = new ArrayList<TimedEvent>();
		
		float fadein = Float.parseFloat(eltString(GENERIC_FADEIN, capsule));
		float fadeout = Float.parseFloat(eltString(GENERIC_FADEOUT, capsule));
		float delay_fadein = Float.parseFloat(eltString(GENERIC_DELAY_FADEIN, capsule));
		float delay_fadeout = Float.parseFloat(eltString(GENERIC_DELAY_FADEOUT, capsule));
		
		if (capsule.has(MACHINE_EVENT))
		{
			for (JsonElement eelt : iterArray(MACHINE_EVENT, capsule))
			{
				events.add(inscriptXMLeventTimed(eelt.getAsJsonObject()));
			}
		}
		
		StreamInformation stream = null;
		if (capsule.has(MACHINE_STREAM))
		{
			stream =
				inscriptXMLstream(
					capsule.getAsJsonObject(MACHINE_STREAM), name, delay_fadein, delay_fadeout, fadein, fadeout);
		}
		
		List<String> allow = new ArrayList<String>();
		for (JsonElement eelt : iterArray(MACHINE_ALLOW, capsule))
		{
			allow.add(eelt.getAsString());
		}
		
		List<String> restrict = new ArrayList<String>();
		for (JsonElement eelt : iterArray(MACHINE_RESTRICT, capsule))
		{
			restrict.add(eelt.getAsString());
		}
		
		TimedEventInformation tie = null;
		if (events.size() > 0)
		{
			tie =
				new TimedEventInformation(
					name, this.providers.getMachine(), this.providers.getReferenceTime(), events, delay_fadein,
					delay_fadeout, fadein, fadeout);
		}
		
		Named element = new Machine(name, this.providers.getJunction(), allow, restrict, tie, stream);
		this.elements.add(element);
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
