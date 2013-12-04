package eu.ha3.matmos.requirem;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/* x-placeholder */

public class FlatRequirements implements Requirements
{
	protected final Map<String, Set<Integer>> sheets;
	protected final Set<Integer> emptySet;
	
	public FlatRequirements()
	{
		this.sheets = new HashMap<String, Set<Integer>>();
		this.emptySet = new HashSet<Integer>();
	}
	
	@Override
	public Set<Integer> getRequirementsFor(String sheet)
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
			this.sheets.put(sheet, new HashSet<Integer>());
		}
	}
	
}
