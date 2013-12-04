package eu.ha3.matmos.engine0.game.data;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import eu.ha3.matmos.engine0.core.interfaces.Data;
import eu.ha3.matmos.engine0.requirem.Collation;
import eu.ha3.matmos.engine0.requirem.Requirements;

/* x-placeholder */

/**
 * Doesn't work since the introduction of arbitrary strings; the sheets can't be
 * iterated over them as they aren't initialized.
 * 
 * @author Hurry
 * 
 */
@Deprecated
public class MAtCatchAllRequirements implements Collation
{
	private Data data;
	private Map<String, Set<String>> sheetCache;
	
	public MAtCatchAllRequirements()
	{
		this.sheetCache = new HashMap<String, Set<String>>();
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
	public Set<String> getRequirementsFor(String sheet)
	{
		if (this.sheetCache.containsKey(sheet))
			return this.sheetCache.get(sheet);
		
		LinkedHashSet<String> all = new LinkedHashSet<String>();
		//for (int i = 0; i < this.data.getSheet(sheet).getSize(); i++) /// crap!!!! how can it work with arbitrary strings???!
		//{
		//	all.add(Integer.toString(i));
		//}
		
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
