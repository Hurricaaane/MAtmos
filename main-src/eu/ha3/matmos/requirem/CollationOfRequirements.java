package eu.ha3.matmos.requirem;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import eu.ha3.matmos.conv.MAtmosConvLogger;

/* x-placeholder */

public class CollationOfRequirements extends FlatRequirements implements Collation
{
	private final Map<String, Requirements> collation;
	
	public CollationOfRequirements()
	{
		this.collation = new HashMap<String, Requirements>();
	}
	
	@Override
	public void addRequirements(String owner, Requirements req)
	{
		this.collation.put(owner, req);
		recomputeRequirements();
		
		MAtmosConvLogger.info("Adding requirements: " + owner);
	}
	
	@Override
	public void removeRequirements(String owner)
	{
		if (!this.collation.containsKey(owner))
			return;
		
		this.collation.remove(owner);
		recomputeRequirements();
		
		MAtmosConvLogger.info("Removing requirements: " + owner);
	}
	
	private void recomputeRequirements()
	{
		this.sheets.clear();
		
		for (Requirements ownedRequirements : this.collation.values())
		{
			Set<String> registeredSheets = ownedRequirements.getRegisteredSheets();
			for (String registered : registeredSheets)
			{
				ensureSheetExists(registered);
				this.sheets.get(registered).addAll(ownedRequirements.getRequirementsFor(registered));
			}
		}
	}
	
}
