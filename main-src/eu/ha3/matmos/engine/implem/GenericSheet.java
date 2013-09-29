package eu.ha3.matmos.engine.implem;

import java.util.LinkedHashMap;
import java.util.Map;

import eu.ha3.matmos.engine.interfaces.Sheet;

/*
            DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE 
                    Version 2, December 2004 

 Copyright (C) 2004 Sam Hocevar <sam@hocevar.net> 

 Everyone is permitted to copy and distribute verbatim or modified 
 copies of this license document, and changing it is allowed as long 
 as the name is changed. 

            DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE 
   TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION 

  0. You just DO WHAT THE FUCK YOU WANT TO. 
*/

public class GenericSheet<T> implements Sheet<T>
{
	private final Map<String, T> values;
	private final int count;
	
	private final Map<String, Integer> versions;
	
	public GenericSheet(int count, T defaultValue)
	{
		this.values = new LinkedHashMap<String, T>(count);
		this.count = count;
		
		this.versions = new LinkedHashMap<String, Integer>(count);
		
		// 1.7 DERAIL
		for (int i = 0; i < count; i++)
		{
			this.values.put(Integer.toString(i), defaultValue);
			this.versions.put(Integer.toString(i), 0);
		}
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
	public int getSize()
	{
		return this.count;
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
}
