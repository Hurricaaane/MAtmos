package eu.ha3.matmos.engine0.core.implem;

import java.util.Iterator;
import java.util.List;

import eu.ha3.matmos.engine0.conv.MAtmosConvLogger;
import eu.ha3.matmos.engine0.core.interfaces.Overrided;
import eu.ha3.matmos.engine0.core.interfaces.Provider;
import eu.ha3.matmos.engine0.core.interfaces.Simulated;

/* x-placeholder */

public class Machine extends MultistateComponent implements Simulated, Overrided
{
	private final List<String> allow;
	private final List<String> restrict;
	private final TimedEventInformation timed;
	private final StreamInformation stream;
	
	private boolean powered;
	private boolean switchedOn;
	
	//
	
	private final Provider<ConditionSet> x;
	
	public Machine(
		String name, Provider<ConditionSet> provider, List<String> allow, List<String> restrict,
		TimedEventInformation timed, StreamInformation stream)
	{
		super(name, provider);
		this.x = this.provider;
		
		this.allow = allow;
		this.restrict = restrict;
		this.timed = timed;
		this.stream = stream;
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
		this.anyallows.add(name);
		
		return;
	}
	
	public void addRestrict(String name)
	{
		this.anyrestricts.add(name);
		
		return;
	}
	
	public void removeSet(String name)
	{
		this.anyallows.remove(name);
		this.anyrestricts.remove(name);
		
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
	public boolean evaluate()
	{
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
			
			MAtmosConvLogger.fine(new StringBuilder("M:")
				.append(this.name).append(this.switchedOn ? " now On." : " now Off.").toString());
			
		}
		
		return this.switchedOn;
		
	}
	
	public boolean testIfTrue()
	{
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
	
	public List<TimedEvent> getTimedEvents()
	{
		return this.etimes;
	}
	
	@Override
	public void simulate()
	{
		// TODO Auto-generated method stub
		
	}
	
}
