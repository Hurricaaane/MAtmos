package eu.ha3.matmos.game.data.abstractions.module;

import java.util.Map.Entry;

import eu.ha3.matmos.engine.core.interfaces.Data;

/*
--filenotes-placeholder
*/

// XXX: This is a terrible use of extends.
public class ThousandStringCountModule extends ExternalStringCountModule
{
	private int total = 0;
	
	public ThousandStringCountModule(Data data, String name, boolean doNotUseDelta)
	{
		super(data, name, doNotUseDelta);
	}
	
	public ThousandStringCountModule(Data data, String name)
	{
		super(data, name);
	}
	
	@Override
	public void increment(String name)
	{
		super.increment(name);
		this.total = this.total + 1;
	}
	
	@Override
	public void apply()
	{
		for (Entry<String, Integer> entry : this.things.entrySet())
		{
			entry.setValue((int) Math.ceil((float) entry.getValue() / this.total * 1000f));
		}
		super.apply();
		this.total = 0;
	}
}
