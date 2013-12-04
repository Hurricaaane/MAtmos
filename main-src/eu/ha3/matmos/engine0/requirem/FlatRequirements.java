package eu.ha3.matmos.engine0.requirem;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/* x-placeholder */

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
