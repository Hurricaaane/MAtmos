package eu.ha3.matmos.game.data.abstractions.module;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import eu.ha3.matmos.engine.core.interfaces.Data;

/*
--filenotes-placeholder
*/

/**
 * An abstract module that specializes in counting things in one pass.
 * 
 * @author Hurry
 */
public abstract class AbstractStringCountModule extends ModuleProcessor implements Module
{
	private Set<String> oldThings = new LinkedHashSet<String>();
	private Set<String> newThings = new LinkedHashSet<String>();
	
	protected Map<String, Integer> things = new HashMap<String, Integer>();
	
	public AbstractStringCountModule(Data data, String name)
	{
		this(data, name, false);
	}
	
	public AbstractStringCountModule(Data data, String name, boolean doNotUseDelta)
	{
		super(data, name, doNotUseDelta);
		
		data.getSheet(name).setDefaultValue("0");
		if (!doNotUseDelta)
		{
			data.getSheet(name + DELTA_SUFFIX).setDefaultValue("0");
		}
	}
	
	@Override
	protected void doProcess()
	{
		count();
		apply();
	}
	
	public void increment(String thing)
	{
		this.things.put(thing, this.things.containsKey(thing) ? this.things.get(thing) + 1 : 1);
	}
	
	protected abstract void count();
	
	public void apply()
	{
		for (Entry<String, Integer> entry : this.things.entrySet())
		{
			this.setValue(entry.getKey(), entry.getValue());
		}
		
		this.newThings.addAll(this.things.keySet()); // add all new string
		this.things.clear();
		
		// Reset all missing string to zero
		this.oldThings.removeAll(this.newThings);
		for (String missing : this.oldThings)
		{
			setValue(missing, 0);
		}
		this.oldThings.clear();
		
		// The following code means
		// oldThings <- newThings
		// newThings <- (empty set)
		Set<String> anEmptySet = this.oldThings;
		this.oldThings = this.newThings;
		this.newThings = anEmptySet;
	}
	
}