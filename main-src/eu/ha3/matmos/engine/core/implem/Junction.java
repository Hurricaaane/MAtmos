package eu.ha3.matmos.engine.core.implem;

import eu.ha3.matmos.engine.core.implem.abstractions.DependableComponent;
import eu.ha3.matmos.engine.core.implem.abstractions.Provider;
import eu.ha3.matmos.engine.core.visualize.VisualizedSpecialDependencies;
import eu.ha3.matmos.log.MAtLog;

import java.util.*;

/* x-placeholder */

public class Junction extends DependableComponent implements VisualizedSpecialDependencies
{
	private final List<String> yes;
	private final List<String> no;
	
	private final Provider<Condition> provider;
	
	private final Collection<String> dependencies;
	
	public Junction(String name, Provider<Condition> provider, List<String> yes, List<String> no)
	{
		super(name);
		this.provider = provider;
		
		this.yes = yes;
		this.no = no;
		
		this.dependencies = new TreeSet<String>();
		this.dependencies.addAll(yes);
		this.dependencies.addAll(no);
	}
	
	@Override
	public void evaluate()
	{
		boolean pre = this.isActive;
		this.isActive = testIfTrue();
		
		if (pre != this.isActive)
		{
			MAtLog.fine("S: " + getName() + " -> " + this.isActive);
			
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
			if (!this.provider.exists(yes) || !this.provider.get(yes).isActive())
			{
				isTrue = false;
			}
		}
		
		if (!isTrue)
			return false;
		
		Iterator<String> iterNo = this.no.iterator();
		while (isTrue && iterNo.hasNext())
		{
			String no = iterNo.next();
			if (!this.provider.exists(no) || this.provider.get(no).isActive())
			{
				isTrue = false;
			}
		}
		return isTrue;
	}
	
	@Override
	public Collection<String> getDependencies()
	{
		return this.dependencies;
	}
	
	@Override
	public String getFeed()
	{
		return "";
	}
	
	@Override
	public Collection<String> getSpecialDependencies(String type)
	{
		if (type.equals("yes"))
			return this.yes;
		else if (type.equals("no"))
			return this.no;
		
		return new HashSet<String>();
	}
}
