package eu.ha3.matmos.engine.implem;

import java.util.ArrayList;
import java.util.List;

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
	private final List<T> values;
	private final int count;
	
	private final List<Integer> versions;
	
	public GenericSheet(int count, T defaultValue)
	{
		this.values = new ArrayList<T>(count);
		this.count = count;
		
		this.versions = new ArrayList<Integer>(count);
		
		for (int i = 0; i < count; i++)
		{
			this.values.add(defaultValue);
			this.versions.add(0);
		}
	}
	
	@Override
	public T get(int pos)
	{
		return this.values.get(pos);
	}
	
	@Override
	public void set(int pos, T value)
	{
		if (!value.equals(this.values.get(pos)))
		{
			this.values.set(pos, value);
			this.versions.set(pos, this.versions.get(pos) + 1);
		}
	}
	
	@Override
	public int getSize()
	{
		return this.count;
	}
	
	@Override
	public int getVersionOf(int pos)
	{
		return this.versions.get(pos);
	}
}
