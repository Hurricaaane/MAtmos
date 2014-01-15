package eu.ha3.matmos.engine0.core.implem;

import eu.ha3.matmos.engine0.core.implem.abstractions.ProviderCollection;
import eu.ha3.matmos.engine0.core.interfaces.Provider;

/*
--filenotes-placeholder
*/

public class Providers implements ProviderCollection
{
	private final Provider<Condition> conditionProvider;
	private final Provider<Junction> junctionProvider;
	private final Provider<Machine> machineProvider;
	private final Provider<Event> eventProvider;
	
	public Providers(
		Provider<Condition> conditionProvider, Provider<Junction> junctionProvider, Provider<Machine> machineProvider,
		Provider<Event> eventProvider)
	{
		this.conditionProvider = conditionProvider;
		this.junctionProvider = junctionProvider;
		this.machineProvider = machineProvider;
		this.eventProvider = eventProvider;
	}
	
	@Override
	public Provider getCondition()
	{
		return this.conditionProvider;
	}
	
	@Override
	public Provider getJunction()
	{
		return this.junctionProvider;
	}
	
	@Override
	public Provider getMachine()
	{
		return this.machineProvider;
	}
	
	@Override
	public Provider getEvent()
	{
		return this.eventProvider;
	}
	
}
