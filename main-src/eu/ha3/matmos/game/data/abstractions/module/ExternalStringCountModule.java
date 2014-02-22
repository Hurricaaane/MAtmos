package eu.ha3.matmos.game.data.abstractions.module;

import eu.ha3.matmos.engine0.core.interfaces.Data;
import eu.ha3.matmos.engine0.core.interfaces.VirtualSheet;

/*
--filenotes-placeholder
*/

/**
 * Call commit() to apply the changes.
 * 
 * @author Hurry
 */
public class ExternalStringCountModule extends AbstractStringCountModule
{
	public ExternalStringCountModule(Data data, String name)
	{
		super(data, name);
	}
	
	public ExternalStringCountModule(Data data, String name, boolean doNotUseDelta)
	{
		super(data, name, doNotUseDelta);
	}
	
	@Override
	public void process()
	{
		// Don't use
	}
	
	@Override
	public void doProcess()
	{
		// Don't use
	}
	
	@Override
	public void count()
	{
		// Don't use
	}
	
	protected void commit()
	{
		apply();
		if (this.sheet instanceof VirtualSheet)
		{
			((VirtualSheet) this.sheet).apply();
		}
	}
}
