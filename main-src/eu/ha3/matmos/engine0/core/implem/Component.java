package eu.ha3.matmos.engine0.core.implem;

import eu.ha3.matmos.engine0.core.interfaces.Named;
import eu.ha3.matmos.engine0.core.interfaces.Versionned;

/* x-placeholder */

public class Component implements Named, Versionned
{
	private final String name;
	private int version;
	
	public Component(String name)
	{
		this.name = name;
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
	}
}
