package eu.ha3.matmos.engine.core.implem.abstractions;

import eu.ha3.matmos.engine.core.interfaces.Evaluated;
import eu.ha3.matmos.engine.core.interfaces.Stated;

/* x-placeholder */

public abstract class MultistateComponent extends Component implements Stated, Evaluated
{
	protected boolean isActive;
	
	public MultistateComponent(String name)
	{
		super(name);
	}
	
	@Override
	public boolean isActive()
	{
		return this.isActive;
	}
}
