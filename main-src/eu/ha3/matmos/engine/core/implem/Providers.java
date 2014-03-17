package eu.ha3.matmos.engine.core.implem;

import eu.ha3.matmos.engine.core.implem.abstractions.Provider;
import eu.ha3.matmos.engine.core.interfaces.ReferenceTime;
import eu.ha3.matmos.engine.core.interfaces.SheetCommander;
import eu.ha3.matmos.engine.core.interfaces.SoundRelay;

/*
--filenotes-placeholder
*/

public class Providers implements ProviderCollection
{
	private final ReferenceTime time;
	private final SoundRelay soundRelay;
	private final SheetCommander commander;
	private final Provider<Condition> conditionProvider;
	private final Provider<Junction> junctionProvider;
	private final Provider<Machine> machineProvider;
	private final Provider<Event> eventProvider;
	private final Provider<Dynamic> dynamicProvider;
	
	public Providers(
		ReferenceTime time, SoundRelay soundRelay, SheetCommander commander, Provider<Condition> conditionProvider,
		Provider<Junction> junctionProvider, Provider<Machine> machineProvider, Provider<Event> eventProvider,
		Provider<Dynamic> dynamicProvider)
	{
		this.time = time;
		this.soundRelay = soundRelay;
		this.commander = commander;
		
		this.conditionProvider = conditionProvider;
		this.junctionProvider = junctionProvider;
		this.machineProvider = machineProvider;
		this.eventProvider = eventProvider;
		this.dynamicProvider = dynamicProvider;
	}
	
	@Override
	public ReferenceTime getReferenceTime()
	{
		return this.time;
	}
	
	@Override
	public SoundRelay getSoundRelay()
	{
		return this.soundRelay;
	}
	
	@Override
	public SheetCommander getSheetCommander()
	{
		return this.commander;
	}
	
	@Override
	public Provider<Condition> getCondition()
	{
		return this.conditionProvider;
	}
	
	@Override
	public Provider<Junction> getJunction()
	{
		return this.junctionProvider;
	}
	
	@Override
	public Provider<Machine> getMachine()
	{
		return this.machineProvider;
	}
	
	@Override
	public Provider<Event> getEvent()
	{
		return this.eventProvider;
	}
	
	@Override
	public Provider<Dynamic> getDynamic()
	{
		return this.dynamicProvider;
	}
	
}
