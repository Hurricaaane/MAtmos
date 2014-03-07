package eu.ha3.matmos.tools;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import com.google.gson.Gson;
import com.google.gson.JsonParser;

import eu.ha3.matmos.engine.core.implem.Condition;
import eu.ha3.matmos.engine.core.implem.Dynamic;
import eu.ha3.matmos.engine.core.implem.Event;
import eu.ha3.matmos.engine.core.implem.Junction;
import eu.ha3.matmos.engine.core.implem.Knowledge;
import eu.ha3.matmos.engine.core.implem.Machine;
import eu.ha3.matmos.engine.core.implem.Possibilities;
import eu.ha3.matmos.engine.core.implem.SheetEntry;
import eu.ha3.matmos.engine.core.implem.StreamInformation;
import eu.ha3.matmos.engine.core.implem.TimedEvent;
import eu.ha3.matmos.engine.core.implem.TimedEventInformation;
import eu.ha3.matmos.engine.core.implem.abstractions.ProviderCollection;
import eu.ha3.matmos.engine.core.interfaces.Named;
import eu.ha3.matmos.engine.core.interfaces.Operator;
import eu.ha3.matmos.engine.core.interfaces.SheetIndex;
import eu.ha3.matmos.expansions.ExpansionIdentity;
import eu.ha3.matmos.jsonformat.serializable.expansion.SerialCondition;
import eu.ha3.matmos.jsonformat.serializable.expansion.SerialDynamic;
import eu.ha3.matmos.jsonformat.serializable.expansion.SerialDynamicSheetIndex;
import eu.ha3.matmos.jsonformat.serializable.expansion.SerialEvent;
import eu.ha3.matmos.jsonformat.serializable.expansion.SerialList;
import eu.ha3.matmos.jsonformat.serializable.expansion.SerialMachine;
import eu.ha3.matmos.jsonformat.serializable.expansion.SerialMachineEvent;
import eu.ha3.matmos.jsonformat.serializable.expansion.SerialRoot;
import eu.ha3.matmos.jsonformat.serializable.expansion.SerialSet;

/*
--filenotes-placeholder
*/

public class JasonExpansions_Engine1Deserializer2000
{
	private List<Named> elements;
	private Knowledge knowledgeWorkstation;
	private ProviderCollection providers;
	
	private String UID;
	
	public JasonExpansions_Engine1Deserializer2000()
	{
	}
	
	public boolean loadJson(String jasonString, ExpansionIdentity identity, Knowledge knowledge)
	{
		try
		{
			parseJsonUnsafe(jasonString, identity, knowledge);
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
	public void loadSerial(SerialRoot root, ExpansionIdentity identity, Knowledge knowledge)
	{
		prepare(identity, knowledge);
		continueFromSerial(root, identity, knowledge);
	}
	
	public SerialRoot jsonToSerial(String jasonString)
	{
		return new Gson().fromJson(new JsonParser().parse(jasonString).getAsJsonObject(), SerialRoot.class);
	}
	
	private void prepare(ExpansionIdentity identity, Knowledge knowledge)
	{
		this.UID = identity.getUniqueName();
		this.knowledgeWorkstation = knowledge;
		this.elements = new ArrayList<Named>();
		this.providers = this.knowledgeWorkstation.obtainProviders();
	}
	
	private void parseJsonUnsafe(String jasonString, ExpansionIdentity identity, Knowledge knowledge)
	{
		prepare(identity, knowledge);
		SerialRoot root = new Gson().fromJson(new JsonParser().parse(jasonString).getAsJsonObject(), SerialRoot.class);
		continueFromSerial(root, identity, knowledge);
	}
	
	private void continueFromSerial(SerialRoot root, ExpansionIdentity identity, Knowledge knowledge)
	{
		if (root.dynamic != null)
		{
			for (Entry<String, SerialDynamic> entry : root.dynamic.entrySet())
			{
				List<SheetIndex> sheetIndexes = new ArrayList<SheetIndex>();
				for (SerialDynamicSheetIndex eelt : entry.getValue().entries)
				{
					sheetIndexes.add(new SheetEntry(eelt.sheet, eelt.index));
				}
				this.elements.add(new Dynamic(
					dynamicSheetHash(entry.getKey()), this.providers.getSheetCommander(), sheetIndexes));
			}
		}
		if (root.list != null)
		{
			for (Entry<String, SerialList> entry : root.list.entrySet())
			{
				this.elements.add(new Possibilities(entry.getKey(), asList(entry.getValue().entries)));
			}
		}
		if (root.condition != null)
		{
			for (Entry<String, SerialCondition> entry : root.condition.entrySet())
			{
				String indexNotComputed = entry.getValue().index;
				if (entry.getValue().sheet.equals(Dynamic.DEDICATED_SHEET))
				{
					indexNotComputed = dynamicSheetHash(indexNotComputed);
				}
				
				this.elements.add(new Condition(
					entry.getKey(), this.providers.getSheetCommander(), new SheetEntry(
						entry.getValue().sheet, indexNotComputed),
					Operator.fromSerializedForm(entry.getValue().symbol), entry.getValue().value));
			}
		}
		if (root.set != null)
		{
			for (Entry<String, SerialSet> entry : root.set.entrySet())
			{
				this.elements.add(new Junction(
					entry.getKey(), this.providers.getCondition(), asList(entry.getValue().yes), asList(entry
						.getValue().no)));
			}
		}
		if (root.event != null)
		{
			for (Entry<String, SerialEvent> entry : root.event.entrySet())
			{
				this.elements.add(new Event(
					entry.getKey(), this.providers.getSoundRelay(), asList(entry.getValue().path),
					entry.getValue().vol_min, entry.getValue().vol_max, entry.getValue().pitch_min,
					entry.getValue().pitch_max, entry.getValue().distance));
			}
		}
		if (root.machine != null)
		{
			for (Entry<String, SerialMachine> entry : root.machine.entrySet())
			{
				SerialMachine serial = entry.getValue();
				
				List<TimedEvent> events = new ArrayList<TimedEvent>();
				
				if (serial.event != null)
				{
					for (SerialMachineEvent eelt : serial.event)
					{
						events.add(new TimedEvent(
							eelt.event, this.providers.getEvent(), eelt.vol_mod, eelt.pitch_mod, eelt.delay_min,
							eelt.delay_max, eelt.delay_start));
					}
				}
				
				StreamInformation stream = null;
				if (serial.stream != null)
				{
					stream =
						new StreamInformation(
							entry.getKey(), this.providers.getMachine(), this.providers.getReferenceTime(),
							this.providers.getSoundRelay(), serial.stream.path, serial.stream.vol, serial.stream.pitch,
							serial.delay_fadein, serial.delay_fadeout, serial.fadein, serial.fadeout,
							serial.stream.looping, serial.stream.pause);
				}
				
				TimedEventInformation tie = null;
				if (serial.event.size() > 0)
				{
					tie =
						new TimedEventInformation(
							entry.getKey(), this.providers.getMachine(), this.providers.getReferenceTime(), events,
							serial.delay_fadein, serial.delay_fadeout, serial.fadein, serial.fadeout);
				}
				
				Named element =
					new Machine(
						entry.getKey(), this.providers.getJunction(), asList(serial.allow), asList(serial.restrict),
						tie, stream);
				this.elements.add(element);
			}
		}
		
		this.knowledgeWorkstation.addKnowledge(this.elements);
	}
	
	private String dynamicSheetHash(String name)
	{
		return this.UID.hashCode() % 1000 + "_" + name;
	}
	
	private <T> List<T> asList(Collection<T> thing)
	{
		return new ArrayList<T>(thing);
	}
}
