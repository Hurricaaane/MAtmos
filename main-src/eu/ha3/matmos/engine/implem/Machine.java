package eu.ha3.matmos.engine.implem;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;

import eu.ha3.matmos.conv.MAtmosConvLogger;

/* x-placeholder */

/**
 * 
 * @author Hurry A Machine is an indexed entity in a Knowledge.
 * 
 * 
 *         The purpose of a Machine is to generate noises by the storage of
 *         EventTimed.
 * 
 *         A Machine can be powered on/off and turned on/off.
 * 
 *         Powering a machine on allows its routine to execute events that
 *         happen while the Machine is turned off.
 * 
 *         A Machine is turned on whenever it is powered and its routine
 *         executes, and all of the Restricts are false while any of the Allows
 *         is true.
 * 
 * 
 *         For a Machine to be valid, it needs to have at least one Allow.
 * 
 */

public class Machine extends Switchable
{
	private List<String> anyallows;
	private List<String> anyrestricts;
	
	private List<TimedEvent> etimes;
	private List<Stream> streams;
	
	private boolean powered;
	private boolean switchedOn;
	
	Machine(Knowledge knowledgeIn)
	{
		super(knowledgeIn);
		
		this.etimes = new ArrayList<TimedEvent>();
		this.streams = new ArrayList<Stream>();
		
		this.anyallows = new ArrayList<String>();
		this.anyrestricts = new ArrayList<String>();
		
		this.powered = false;
		this.switchedOn = false;
		
	}
	
	public void routine()
	{
		if (this.switchedOn)
		{
			for (Iterator<TimedEvent> iter = this.etimes.iterator(); iter.hasNext();)
			{
				TimedEvent etime = iter.next();
				etime.routine();
				
			}
			
		}
		if (this.powered && !this.streams.isEmpty())
		{
			for (Iterator<Stream> iter = this.streams.iterator(); iter.hasNext();)
			{
				iter.next().routine();
			}
			
		}
		
	}
	
	/**
	 * Turns the machine on.
	 */
	public void turnOn()
	{
		if (!this.powered)
			return;
		
		if (this.switchedOn)
			return;
		
		this.switchedOn = true;
		for (Iterator<TimedEvent> iter = this.etimes.iterator(); iter.hasNext();)
		{
			iter.next().restart();
		}
		
		for (Iterator<Stream> iter = this.streams.iterator(); iter.hasNext();)
		{
			iter.next().signalPlayable();
		}
		
	}
	
	/**
	 * Turns the machine off.
	 */
	public void turnOff()
	{
		if (!this.powered)
			return;
		
		if (!this.switchedOn)
			return;
		
		this.switchedOn = false;
		
		for (Iterator<Stream> iter = this.streams.iterator(); iter.hasNext();)
		{
			iter.next().signalStoppable();
		}
		
	}
	
	/**
	 * Allows the machine to be turned on.
	 */
	public void powerOn()
	{
		this.powered = true;
		
	}
	
	/**
	 * Disallows the machine to be turned on, and turns it off.
	 */
	public void powerOff()
	{
		for (Iterator<Stream> iter = this.streams.iterator(); iter.hasNext();)
		{
			iter.next().clearToken();
		}
		
		turnOff();
		this.powered = false;
		
	}
	
	public boolean isPowered()
	{
		return this.powered;
		
	}
	
	public boolean isOn()
	{
		return this.switchedOn;
		
	}
	
	public List<String> getAllows()
	{
		return this.anyallows;
	}
	
	public List<String> getRestricts()
	{
		return this.anyrestricts;
	}
	
	public void addAllow(String name)
	{
		/*if (anyrestricts.contains(name))
			return;

		if (anyallows.contains(name))
			return;
		 */
		
		this.anyallows.add(name);
		flagNeedsTesting();
		
		return;
	}
	
	public void addRestrict(String name)
	{
		/*if (anyallows.contains(name))
			return;
		
		if (anyrestricts.contains(name))
			return;
		 */
		
		this.anyrestricts.add(name);
		flagNeedsTesting();
		
		return;
	}
	
	public void removeSet(String name)
	{
		this.anyallows.remove(name);
		this.anyrestricts.remove(name);
		flagNeedsTesting();
		
		return;
		
	}
	
	public void replaceSetName(String name, String newName)
	{
		if (this.anyallows.contains(name))
		{
			this.anyallows.add(newName);
			this.anyallows.remove(name);
		}
		if (this.anyrestricts.contains(name))
		{
			this.anyrestricts.add(newName);
			this.anyrestricts.remove(name);
		}
		flagNeedsTesting();
		
	}
	
	public List<TimedEvent> getEventsTimed()
	{
		return this.etimes;
		
	}
	
	public int addEventTimed()
	{
		this.etimes.add(new TimedEvent(this));
		
		return this.etimes.size();
		
	}
	
	public int removeEventTimed(int index)
	{
		this.etimes.remove(index);
		
		return this.etimes.size();
		
	}
	
	public TimedEvent getEventTimed(int index)
	{
		return this.etimes.get(index);
		
	}
	
	public List<Stream> getStreams()
	{
		return this.streams;
		
	}
	
	public int addStream()
	{
		this.streams.add(new Stream(this));
		
		return this.streams.size();
		
	}
	
	public int removeStream(int index)
	{
		this.streams.remove(index);
		
		return this.streams.size();
		
	}
	
	public Stream getStream(int index)
	{
		return this.streams.get(index);
		
	}
	
	@Override
	protected boolean testIfValid()
	{
		if (this.anyallows.size() == 0)
			return false;
		
		Iterator<String> iterAnyallows = this.anyallows.iterator();
		while (iterAnyallows.hasNext())
		{
			String cset = iterAnyallows.next();
			
			if (!this.knowledge.getConditionSetsKeySet().contains(cset))
				return false;
			
		}
		
		Iterator<String> iterAnyrestricts = this.anyrestricts.iterator();
		while (iterAnyrestricts.hasNext())
		{
			String cset = iterAnyrestricts.next();
			
			if (!this.knowledge.getConditionSetsKeySet().contains(cset))
				return false;
			
		}
		
		return true;
		
	}
	
	public boolean evaluate()
	{
		if (!isValid())
			return false;
		
		if (!this.powered)
			return false;
		
		boolean pre = this.switchedOn;
		boolean shallBeOn = testIfTrue();
		
		if (pre != shallBeOn)
		{
			if (shallBeOn)
			{
				turnOn();
			}
			else
			{
				turnOff();
			}
			
			//MAtmosEngine.logger; //TODO Logger
			MAtmosConvLogger.fine(new StringBuilder("M:")
				.append(this.nickname).append(this.switchedOn ? " now On." : " now Off.").toString());
			
		}
		
		return this.switchedOn;
		
	}
	
	@Override
	public boolean isActive()
	{
		return isTrue();
		
	}
	
	public boolean isTrue()
	{
		return this.switchedOn;
		
	}
	
	public boolean testIfTrue()
	{
		if (!isValid())
			return false;
		
		boolean isTrue = false;
		
		Iterator<String> iterAnyallows = this.anyallows.iterator();
		while (!isTrue && iterAnyallows.hasNext())
		{
			String cset = iterAnyallows.next();
			
			if (this.knowledge.getConditionSet(cset).isTrue())
			{
				isTrue = true; // If any Allows is true, it's true (exit the loop)
				
			}
			
		}
		
		/// Unless...
		
		Iterator<String> iterAnyrestricts = this.anyrestricts.iterator();
		while (isTrue && iterAnyrestricts.hasNext())
		{
			String cset = iterAnyrestricts.next();
			
			if (this.knowledge.getConditionSet(cset).isTrue())
			{
				isTrue = false; // If any Restrict is true, it's false
				
			}
			
		}
		
		return isTrue;
		
	}
	
	@Override
	public String serialize(XMLEventWriter eventWriter) throws XMLStreamException
	{
		buildDescriptibleSerialized(eventWriter);
		
		for (Iterator<String> iter = this.anyallows.iterator(); iter.hasNext();)
		{
			createNode(eventWriter, "allow", iter.next());
		}
		
		for (Iterator<String> iter = this.anyrestricts.iterator(); iter.hasNext();)
		{
			createNode(eventWriter, "restrict", iter.next());
		}
		
		for (Iterator<TimedEvent> iter = this.etimes.iterator(); iter.hasNext();)
		{
			iter.next().serialize(eventWriter);
		}
		
		for (Iterator<Stream> iter = this.streams.iterator(); iter.hasNext();)
		{
			iter.next().serialize(eventWriter);
		}
		
		return "";
		
	}
	
	public List<TimedEvent> getTimedEvents()
	{
		return this.etimes;
	}
	
}
