package eu.ha3.matmos.engine0.core.implem;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import eu.ha3.matmos.engine0.core.interfaces.Sheet;

/* x-placeholder */

public class GenericSheet<T> implements Sheet<T>
{
	private final Map<String, T> values;
	private final Map<String, Integer> versions;
	
	public GenericSheet(T defaultValue)
	{
		this.values = new LinkedHashMap<String, T>();
		this.versions = new LinkedHashMap<String, Integer>();
	}
	
	@Override
	public T get(String pos)
	{
		return this.values.get(pos);
	}
	
	@Override
	public void set(String pos, T value)
	{
		if (!value.equals(this.values.get(pos)))
		{
			this.values.put(pos, value);
			this.versions.put(pos, this.versions.get(pos) + 1);
		}
	}
	
	@Override
	public int getVersionOf(String pos)
	{
		return this.versions.get(pos);
	}
	
	@Override
	public boolean containsKey(String key)
	{
		return this.values.containsKey(key);
	}
	
	@Override
	public Set<String> keySet()
	{
		return this.values.keySet();
	}
}
