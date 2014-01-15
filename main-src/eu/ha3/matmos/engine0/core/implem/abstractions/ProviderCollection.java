package eu.ha3.matmos.engine0.core.implem.abstractions;

import eu.ha3.matmos.engine0.core.interfaces.Provider;

/*
--filenotes-placeholder
*/

public interface ProviderCollection
{
	public Provider getCondition();
	
	public Provider getJunction();
	
	public Provider getMachine();
	
	public Provider getEvent();
}
