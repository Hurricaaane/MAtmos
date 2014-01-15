package eu.ha3.matmos.engine0.core.implem;

import java.util.Map;

import eu.ha3.matmos.engine0.core.interfaces.Provider;
import eu.ha3.matmos.engine0.core.interfaces.Versionned;

/*
--filenotes-placeholder
*/

public class MappedProvider<T extends Versionned> implements Provider<T>
{
	private Map<String, T> map;
	
	public MappedProvider(Map<String, T> map)
	{
		this.map = map;
	}
	
	@Override
	public boolean exists(String name)
	{
		return this.map.containsKey(name);
	}
	
	@Override
	public int version(String name)
	{
		return this.map.containsKey(name) ? this.map.get(name).version() : -1;
	}
	
	@Override
	public T get(String name)
	{
		return this.map.get(name);
	}
	
	@Override
	public T instance()
	{
		return null;
	}
}
