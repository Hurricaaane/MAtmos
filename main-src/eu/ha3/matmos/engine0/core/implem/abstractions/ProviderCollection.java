package eu.ha3.matmos.engine0.core.implem.abstractions;

import eu.ha3.matmos.engine0.core.implem.Condition;
import eu.ha3.matmos.engine0.core.implem.Dynamic;
import eu.ha3.matmos.engine0.core.implem.Event;
import eu.ha3.matmos.engine0.core.implem.Junction;
import eu.ha3.matmos.engine0.core.implem.Machine;
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
	
	public Provider<Condition> getCondition();
	
	public Provider<Junction> getJunction();
	
	public Provider<Machine> getMachine();
	
	public Provider<Event> getEvent();
	
	public Provider<Dynamic> getDynamic();
}
