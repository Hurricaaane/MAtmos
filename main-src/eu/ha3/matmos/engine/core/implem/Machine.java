package eu.ha3.matmos.engine.core.implem;

import eu.ha3.matmos.engine.core.implem.abstractions.DependableComponent;
import eu.ha3.matmos.engine.core.implem.abstractions.Provider;
import eu.ha3.matmos.engine.core.interfaces.Overrided;
import eu.ha3.matmos.engine.core.interfaces.Simulated;
import eu.ha3.matmos.engine.core.visualize.VisualizedSpecialDependencies;
import eu.ha3.matmos.log.MAtLog;

import java.util.*;

/* x-placeholder */

public class Machine extends DependableComponent implements Simulated, Overrided, VisualizedSpecialDependencies
{
	private final List<String> allow;
	private final List<String> restrict;
	private final TimedEventInformation timed;
	private final StreamInformation stream;
	
	private final Provider<Junction> provider;
	
	private boolean overrideUnderway;
	private boolean overrideState;
	
	private final Collection<String> dependencies;
	
	public Machine(
		String name, Provider<Junction> provider, List<String> allow, List<String> restrict,
		TimedEventInformation timed, StreamInformation stream)
	{
		super(name);
		this.provider = provider;
		
		this.allow = allow;
		this.restrict = restrict;
		this.timed = timed;
		this.stream = stream;
		
		this.dependencies = new TreeSet<String>();
		this.dependencies.addAll(allow);
		this.dependencies.addAll(restrict);
	}
	
	@Override
	public void simulate()
	{
		if (this.timed != null)
		{
			this.timed.simulate();
		}
		if (this.stream != null)
		{
			this.stream.simulate();
		}
	}
	
	@Override
	public void evaluate()
	{
		boolean previous = this.isActive;
		this.isActive = testIfTrue();
		
		if (previous != this.isActive)
		{
			incrementVersion();
			if (this.timed != null)
			{
				this.timed.evaluate();
			}
			if (this.stream != null)
			{
				this.stream.evaluate();
			}
			
			MAtLog.fine("M: " + getName() + " -> " + this.isActive);
		}
	}
	
	private boolean testIfTrue()
	{
		if (this.overrideUnderway)
			return this.overrideState;
		
		boolean isTrue = false;
		
		Iterator<String> iterAllow = this.allow.iterator();
		while (!isTrue && iterAllow.hasNext())
		{
			String junction = iterAllow.next();
			
			if (this.provider.get(junction).isActive())
			{
				// If any Allows is true, it's true (exit the loop)
				isTrue = true;
			}
		}
		
		if (!isTrue)
			return false;
		
		/// Unless...
		
		Iterator<String> iterRestrict = this.restrict.iterator();
		while (isTrue && iterRestrict.hasNext())
		{
			String junction = iterRestrict.next();
			
			if (this.provider.get(junction).isActive())
			{
				// If any Restrict is true, it's false
				isTrue = false;
			}
		}
		
		return isTrue;
	}
	
	@Override
	public void overrideForceOn()
	{
		this.overrideUnderway = true;
		this.overrideState = true;
		evaluate();
	}
	
	@Override
	public void overrideForceOff()
	{
		this.overrideUnderway = true;
		this.overrideState = false;
		evaluate();
	}
	
	@Override
	public void overrideFinish()
	{
		this.overrideUnderway = false;
		evaluate();
	}
	
	@Override
	public Collection<String> getDependencies()
	{
		return this.dependencies;
	}
	
	@Override
	public String getFeed()
	{
		return "";
	}
	
	@Override
	public Collection<String> getSpecialDependencies(String type)
	{
		if (type.equals("allow"))
			return this.allow;
		else if (type.equals("restrict"))
			return this.restrict;
		
		return new HashSet<String>();
	}
}
