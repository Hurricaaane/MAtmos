package eu.ha3.matmos.engine0.core.implem.abstractions;

import eu.ha3.matmos.engine0.core.interfaces.Provider;
import eu.ha3.matmos.engine0.core.interfaces.ReferenceTime;
import eu.ha3.matmos.engine0.core.interfaces.SheetCommander;
import eu.ha3.matmos.engine0.core.interfaces.SoundRelay;

/*
--filenotes-placeholder
*/

public interface ProviderCollection
{
	public ReferenceTime getReferenceTime();
	
	public SoundRelay getSoundRelay();
	
	public SheetCommander getSheetCommander();
	
	public Provider getCondition();
	
	public Provider getJunction();
	
	public Provider getMachine();
	
	public Provider getEvent();
}
