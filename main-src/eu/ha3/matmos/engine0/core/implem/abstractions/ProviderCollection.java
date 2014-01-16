package eu.ha3.matmos.engine0.core.implem.abstractions;

import eu.ha3.matmos.engine0.core.interfaces.Provider;
import eu.ha3.matmos.engine0.core.interfaces.ReferenceTime;
import eu.ha3.matmos.engine0.core.interfaces.SheetCommander;

/*
--filenotes-placeholder
*/

public interface ProviderCollection
{
	public ReferenceTime getReferenceTime();
	
	public SheetCommander getSheetCommander();
	
	public Provider getCondition();
	
	public Provider getJunction();
	
	public Provider getMachine();
	
	public Provider getEvent();
}
