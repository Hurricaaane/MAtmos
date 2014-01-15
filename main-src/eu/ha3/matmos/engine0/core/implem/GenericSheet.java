package eu.ha3.matmos.engine0.core.implem;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import eu.ha3.matmos.engine0.core.interfaces.Sheet;

/* x-placeholder */

public class GenericSheet implements Sheet
{
	private final Map<String, String> values;
	private final Map<String, Integer> versions;
	
	public GenericSheet(String defaultValue)
	{
		this.values = new TreeMap<String, String>();
		this.versions = new TreeMap<String, Integer>();
	}
	
	@Override
	public String get(String pos)
	{
		return this.values.get(pos);
	}
	
	@Override
	public void set(String pos, String value)
	{
		if (!value.equals(this.values.get(pos)))
		{
			int ver = this.versions.containsKey(pos) ? this.versions.get(pos) : 0;
			this.values.put(pos, value);
			this.versions.put(pos, ver + 1);
		}
	}
	
	@Override
	public int getVersionOf(String pos)
	{
		if (this.versions.containsKey(pos))
			return this.versions.get(pos);
		
		return -1;
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
