package eu.ha3.matmos.requirem;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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

public class FlatRequirements implements Requirements
{
	protected final Map<String, Set<String>> sheets;
	protected final Set<String> emptySet;
	
	public FlatRequirements()
	{
		this.sheets = new HashMap<String, Set<String>>();
		this.emptySet = new HashSet<String>();
	}
	
	@Override
	public Set<String> getRequirementsFor(String sheet)
	{
		if (this.sheets.containsKey(sheet))
			return this.sheets.get(sheet);
		
		return this.emptySet;
	}
	
	@Override
	public boolean isRequired(String sheet)
	{
		return this.sheets.containsKey(sheet);
	}
	
	@Override
	public Set<String> getRegisteredSheets()
	{
		return this.sheets.keySet();
	}
	
	/**
	 * Convenience method to make sure a sheet exists in the map.
	 * 
	 * @param sheet
	 */
	protected void ensureSheetExists(String sheet)
	{
		if (!this.sheets.containsKey(sheet))
		{
			this.sheets.put(sheet, new HashSet<String>());
		}
	}
	
}
