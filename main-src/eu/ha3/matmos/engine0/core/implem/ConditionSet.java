package eu.ha3.matmos.engine0.core.implem;

import java.util.Iterator;
import java.util.List;

import eu.ha3.matmos.engine0.conv.MAtmosConvLogger;
import eu.ha3.matmos.engine0.core.interfaces.Provider;

/* x-placeholder */

public class ConditionSet extends MultistateComponent
{
	private final List<String> yes;
	private final List<String> no;
	
	private final Provider<Condition> x;
	
	public ConditionSet(String name, Provider<Condition> provider, List<String> yes, List<String> no)
	{
		super(name, provider);
		this.x = provider;
		
		this.yes = yes;
		this.no = no;
	}
	
	@Override
	public void evaluate()
	{
		boolean pre = this.isActive;
		this.isActive = testIfTrue();
		
		if (pre != this.isActive)
		{
			MAtmosConvLogger.fine("S: " + getName() + " -> " + this.isActive);
			
			incrementVersion();
		}
	}
	
	private boolean testIfTrue()
	{
		boolean isTrue = true;
		
		Iterator<String> iterYes = this.yes.iterator();
		while (isTrue && iterYes.hasNext())
		{
			String yes = iterYes.next();
			if (!this.x.exists(yes) || !this.x.get(yes).isActive())
			{
				isTrue = false;
			}
		}
		
		Iterator<String> iterNo = this.no.iterator();
		while (isTrue && iterNo.hasNext())
		{
			String no = iterNo.next();
			if (!this.x.exists(no) || this.x.get(no).isActive())
			{
				isTrue = false;
			}
		}
		return isTrue;
	}
}
