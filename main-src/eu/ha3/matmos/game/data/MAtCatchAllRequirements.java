package eu.ha3.matmos.game.data;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import eu.ha3.matmos.engine.interfaces.Data;
import eu.ha3.matmos.requirem.Collation;
import eu.ha3.matmos.requirem.Requirements;

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

public class MAtCatchAllRequirements implements Collation
{
	private Data data;
	private Map<String, Set<Integer>> sheetCache;
	
	public MAtCatchAllRequirements()
	{
		this.sheetCache = new HashMap<String, Set<Integer>>();
	}
	
	public void setData(Data data)
	{
		this.data = data;
	}
	
	@Override
	public Set<String> getRegisteredSheets()
	{
		return this.data.getSheetNames();
	}
	
	@Override
	public Set<Integer> getRequirementsFor(String sheet)
	{
		if (this.sheetCache.containsKey(sheet))
			return this.sheetCache.get(sheet);
		
		LinkedHashSet<Integer> all = new LinkedHashSet<Integer>();
		for (int i = 0; i < this.data.getSheet(sheet).getSize(); i++)
		{
			all.add(i);
		}
		
		this.sheetCache.put(sheet, all);
		return this.sheetCache.get(sheet);
	}
	
	@Override
	public boolean isRequired(String sheet)
	{
		return true;
	}
	
	@Override
	public void addRequirements(String owner, Requirements req)
	{
	}
	
	@Override
	public void removeRequirements(String owner)
	{
	}
}
