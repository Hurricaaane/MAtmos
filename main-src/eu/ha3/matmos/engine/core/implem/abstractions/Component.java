package eu.ha3.matmos.engine.core.implem.abstractions;

import java.util.HashSet;
import java.util.Set;

import eu.ha3.matmos.engine.core.interfaces.Named;
import eu.ha3.matmos.engine.core.interfaces.VersionListener;
import eu.ha3.matmos.engine.core.interfaces.Versionned;

/* x-placeholder */

public abstract class Component implements Named, Versionned
{
	private final String name;
	private final Set<VersionListener> listeners;
	private int version;
	
	public Component(String name)
	{
		this.name = name;
		this.listeners = new HashSet<VersionListener>();
		this.version = -1;
	}
	
	@Override
	public String getName()
	{
		return this.name;
	}
	
	@Override
	public String toString()
	{
		return "[(" + this.getClass().toString() + ") " + this.name + "]";
	}
	
	@Override
	public int version()
	{
		return this.version;
	}
	
	@Override
	public void incrementVersion()
	{
		this.version = this.version + 1;
		
		for (VersionListener listener : this.listeners)
		{
			listener.onIncrement(this);
		}
	}
	
	@Override
	public void registerVersionListener(VersionListener listener)
	{
		this.listeners.add(listener);
	}
}
