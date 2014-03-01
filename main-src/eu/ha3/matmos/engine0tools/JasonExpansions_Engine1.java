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

import java.util.ArrayList;
import java.util.List;
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
import eu.ha3.matmos.engine0.core.implem.Possibilities;
import eu.ha3.matmos.engine0.core.implem.SheetEntry;
import eu.ha3.matmos.engine0.core.implem.StreamInformation;
import eu.ha3.matmos.engine0.core.implem.TimedEvent;
import eu.ha3.matmos.engine0.core.implem.TimedEventInformation;
import eu.ha3.matmos.engine0.core.implem.abstractions.ProviderCollection;
import eu.ha3.matmos.engine0.core.interfaces.Named;
import eu.ha3.matmos.engine0.core.interfaces.Operator;
import eu.ha3.matmos.engine0.core.interfaces.SheetIndex;
import eu.ha3.matmos.expansions.ExpansionIdentity;

/*
--filenotes-placeholder
*/

public class JasonExpansions_Engine1
{
	private ArrayList<Named> elements;
	private Knowledge knowledgeWorkstation;
	private ProviderCollection providers;
	
	private String UID;
	
	public JasonExpansions_Engine1()
	{
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
	
	private Boolean eltBool(String member, JsonObject capsule)
	{
		return capsule.get(member).getAsBoolean();
	}
	
	private Integer eltInt(String member, JsonObject capsule)
	{
		return capsule.get(member).getAsInt();
	}
	
	private Float eltFloat(String member, JsonObject capsule)
	{
		return capsule.get(member).getAsFloat();
	}
	
	private String eltString(String member, JsonObject capsule)
	{
		return capsule.get(member).getAsString();
	}
	
	private TimedEvent inscriptXMLeventTimed(JsonObject specs)
	{
		String event = eltString(TIMED_EVENT, specs);
		float vol_mod = eltFloat(TIMED_VOL_MOD, specs);
		float pitch_mod = eltFloat(TIMED_PITCH_MOD, specs);
		float delay_min = eltFloat(TIMED_DELAY_MIN, specs);
		float delay_max = eltFloat(TIMED_DELAY_MAX, specs);
		float delay_start = eltFloat(TIMED_DELAY_START, specs);
		
		return new TimedEvent(event, this.providers.getEvent(), vol_mod, pitch_mod, delay_min, delay_max, delay_start);
	}
	
	private StreamInformation inscriptXMLstream(
		JsonObject specs, String machineName, float delayBeforeFadeIn, float delayBeforeFadeOut, float fadeInTime,
		float fadeOutTime)
	{
		String path = eltString(STREAM_PATH, specs);
		float vol = eltFloat(STREAM_VOL, specs);
		float pitch = eltFloat(STREAM_PITCH, specs);
		boolean looping = eltBool(STREAM_LOOPING, specs);
		boolean pause = eltBool(STREAM_PAUSE, specs);
		
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
			String sheet = eltString(GENERIC_SHEET, eelt.getAsJsonObject());
			String index = eltString(GENERIC_INDEX, eelt.getAsJsonObject());
			
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
		String sheet = eltString(GENERIC_SHEET, capsule);
		String indexNotComputed = eltString(GENERIC_INDEX, capsule);
		String serializedFormSymbol = eltString(CONDITION_SYMBOL, capsule);
		String value = eltString(CONDITION_VALUE, capsule);
		
		if (sheet.equals(Dynamic.DEDICATED_SHEET))
		{
			indexNotComputed = dynamicSheetHash(indexNotComputed);
		}
		
		Named element =
			new Condition(
				name, this.providers.getSheetCommander(), new SheetEntry(sheet, indexNotComputed),
				Operator.fromSerializedForm(serializedFormSymbol), value);
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
		
		float vol_min = eltFloat(EVENT_VOL_MIN, capsule);
		float vol_max = eltFloat(EVENT_VOL_MAX, capsule);
		float pitch_min = eltFloat(EVENT_PITCH_MIN, capsule);
		float pitch_max = eltFloat(EVENT_PITCH_MAX, capsule);
		int distance = eltInt(EVENT_DISTANCE, capsule);
		
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
		
		float fadein = eltFloat(GENERIC_FADEIN, capsule);
		float fadeout = eltFloat(GENERIC_FADEOUT, capsule);
		float delay_fadein = eltFloat(GENERIC_DELAY_FADEIN, capsule);
		float delay_fadeout = eltFloat(GENERIC_DELAY_FADEOUT, capsule);
		
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
}
