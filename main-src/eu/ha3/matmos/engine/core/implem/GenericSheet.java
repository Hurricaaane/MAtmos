package eu.ha3.matmos.engine.core.implem;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import eu.ha3.matmos.engine.core.interfaces.Sheet;

/* x-placeholder */

public class GenericSheet implements Sheet
{
	protected final Map<String, String> values;
	protected final Map<String, Integer> versions;
	private String def = "_ENTRY_NOT_DEFINED";
	
	public GenericSheet()
	{
		this.values = new TreeMap<String, String>();
		this.versions = new TreeMap<String, Integer>();
	}
	
	@Override
	public String get(String key)
	{
		return this.values.containsKey(key) ? this.values.get(key) : this.def;
	}
	
	@Override
	public void set(String key, String value)
	{
		if (!value.equals(this.values.get(key)))
		{
			int ver = this.versions.containsKey(key) ? this.versions.get(key) : -1;
			this.values.put(key, value);
			this.versions.put(key, ver + 1);
		}
	}
	
	@Override
	public int version(String pos)
	{
		if (this.versions.containsKey(pos))
			return this.versions.get(pos);
		
		return -1;
	}
	
	@Override
	public boolean exists(String key)
	{
		return this.values.containsKey(key);
	}
	
	@Override
	public Set<String> keySet()
	{
		return this.values.keySet();
	}
	
	@Override
	public void clear()
	{
		this.values.clear();
	}
	
	@Override
	public void setDefaultValue(String def)
	{
		this.def = def;
	}
	
	@Override
	public String getDefaultValue()
	{
		return this.def;
	}
}
