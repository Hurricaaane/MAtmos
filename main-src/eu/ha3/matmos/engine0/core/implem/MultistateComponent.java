package eu.ha3.matmos.engine0.core.implem;

import eu.ha3.matmos.engine0.core.interfaces.Evaluated;
import eu.ha3.matmos.engine0.core.interfaces.Provider;
import eu.ha3.matmos.engine0.core.interfaces.Stated;

/* x-placeholder */

public abstract class MultistateComponent extends Component implements Stated, Evaluated
{
	@SuppressWarnings("rawtypes")
	protected Provider provider;
	protected boolean isActive;
	
	@SuppressWarnings("rawtypes")
	public MultistateComponent(String name, Provider provider)
	{
		super(name);
		
		this.provider = provider;
	}
	
	@Override
	public boolean isActive()
	{
		return this.isActive;
	}
}
