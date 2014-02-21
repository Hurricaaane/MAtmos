package eu.ha3.matmos.engine0.core.implem;

import java.util.Collection;
import java.util.HashSet;

import eu.ha3.matmos.engine0.core.implem.abstractions.DependableComponent;
import eu.ha3.matmos.engine0.core.interfaces.SheetCommander;
import eu.ha3.matmos.engine0.core.interfaces.SheetIndex;
import eu.ha3.matmos.engine0.core.visualize.VisualizeOperator;
import eu.ha3.matmos.engine0.core.visualize.Visualized;
import eu.ha3.matmos.expansions.MAtmosConvLogger;

/* x-placeholder */

public class Condition extends DependableComponent implements Visualized
{
	private final SheetIndex indexX;
	private final Operator operatorX;
	private final String constantX;
	private final Long constantLongX;
	//private final Float constantFloatX;
	
	private final SheetCommander sheetCommander;
	
	private int siVersion = -1;
	
	private final Collection<String> dependencies;
	
	public Condition(String name, SheetCommander sheetCommander, SheetIndex index, Operator operator, String constant)
	{
		super(name);
		this.sheetCommander = sheetCommander;
		
		this.indexX = index;
		this.operatorX = operator;
		this.constantX = constant;
		
		this.constantLongX = LongFloatSimplificator.longOf(constant);
		//this.constantFloatX = LongFloatSimplificator.floatOf(constant);
		
		this.dependencies = new HashSet<String>();
		this.dependencies.add(index.getSheet());
	}
	
	@Override
	public void evaluate()
	{
		// Bypass exists: We want sheets to return their default value
		//if (!this.sheetCommander.exists(this.indexX))
		//	return;
		
		//System.out.println(getName()
		//	+ " -> " + this.indexX.getSheet() + " " + this.indexX.getIndex() + ": "
		//	+ this.sheetCommander.get(this.indexX));
		
		if (this.sheetCommander.version(this.indexX) == this.siVersion)
			return;
		
		boolean pre = this.isActive;
		this.isActive = testIfTrue();
		
		if (pre != this.isActive)
		{
			incrementVersion();
			
			MAtmosConvLogger.fine("C: " + getName() + " -> " + this.isActive);
		}
	}
	
	private boolean testIfTrue()
	{
		try
		{
			String value = (String) this.sheetCommander.get(this.indexX);
			Long longValue = LongFloatSimplificator.longOf(value);
			//Float floatValue = LongFloatSimplificator.floatOf(this.constant);
			
			if (this.operatorX == Operator.NOT_EQUAL)
				return !value.equals(this.constantX);
			
			else if (this.operatorX == Operator.EQUAL)
				return value.equals(this.constantX);
			
			else if (this.operatorX == Operator.IN_LIST)
				return this.sheetCommander.listHas(this.constantX, value);
			
			else if (this.operatorX == Operator.NOT_IN_LIST)
				return !this.sheetCommander.listHas(this.constantX, value);
			
			else if (longValue != null && this.constantLongX != null)
			{
				// if (both values are integers), then
				
				if (this.operatorX == Operator.GREATER)
					return longValue > this.constantLongX;
				
				else if (this.operatorX == Operator.GREATER_OR_EQUAL)
					return longValue >= this.constantLongX;
				
				else if (this.operatorX == Operator.LESSER)
					return longValue < this.constantLongX;
				
				else if (this.operatorX == Operator.LESSER_OR_EQUAL)
					return longValue <= this.constantLongX;
				
				else
					return false;
			}
			
			else
				return false;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Returns the required sheet modules of this condition.
	 */
	@Override
	public Collection<String> getDependencies()
	{
		return this.dependencies;
	}
	
	@Override
	public String getFeed()
	{
		String value = (String) this.sheetCommander.get(this.indexX);
		String op = VisualizeOperator.get(this.operatorX);
		
		return this.indexX.getSheet() + ">" + this.indexX.getIndex() + ":[" + value + "] " + op + " " + this.constantX;
	}
}
