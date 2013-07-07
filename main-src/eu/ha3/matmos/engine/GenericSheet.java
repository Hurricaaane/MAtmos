package eu.ha3.matmos.engine;

import java.util.ArrayList;
import java.util.List;

import eu.ha3.matmos.engineinterfaces.Sheet;

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
	
	public GenericSheet(int count, T defaultValue)
	{
		this.values = new ArrayList<T>(count);
		this.count = count;
		
		for (int i = 0; i < count; i++)
		{
			this.values.add(defaultValue);
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
		this.values.set(pos, value);
	}
	
	@Override
	public int getSize()
	{
		return this.count;
	}
}
