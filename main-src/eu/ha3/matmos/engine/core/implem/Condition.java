package eu.ha3.matmos.engine.core.implem;

import eu.ha3.matmos.engine.core.implem.abstractions.DependableComponent;
import eu.ha3.matmos.engine.core.interfaces.Operator;
import eu.ha3.matmos.engine.core.interfaces.SheetCommander;
import eu.ha3.matmos.engine.core.interfaces.SheetIndex;
import eu.ha3.matmos.engine.core.visualize.Visualized;
import eu.ha3.matmos.log.MAtLog;

import java.util.Collection;
import java.util.HashSet;

/* x-placeholder */

public class Condition extends DependableComponent implements Visualized
{
	private final SheetIndex indexX;
	private final Operator operatorX;
	private final String constantX;
	private final Long constantLongX;
	//private final Float constantFloatX;
	
	private final SheetCommander sheetCommander;
	
	// Fixes a bug where conditions don't evaluate for sheet indexes that don't exist
	// Required for ALWAYS_TRUE / ALWAYS_FALSE
	// This was caused by default value for undefined sheet indexes being -1 (equal to initial siVersion)
	private int siVersion = Integer.MIN_VALUE;
	
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
			
			MAtLog.fine("C: " + getName() + " -> " + this.isActive);
		}
	}
	
	private boolean testIfTrue()
	{
		try
		{
			String value = (String) this.sheetCommander.get(this.indexX);
			Long longValue = LongFloatSimplificator.longOf(value);
			//Float floatValue = LongFloatSimplificator.floatOf(this.constant);
			
			if (this.operatorX == Operator.ALWAYS_TRUE)
				return true;
			
			else if (this.operatorX == Operator.IN_LIST)
				return this.sheetCommander.listHas(this.constantX, value);
			
			else if (this.operatorX == Operator.NOT_IN_LIST)
				return !this.sheetCommander.listHas(this.constantX, value);
			
			else if (longValue != null && this.constantLongX != null)
			{
				// if (both values are integers), then
				
				//
				
				// We treat the EQUAL operator separately if it's a number
				// because we want to make sure the string resolve to
				// the same number, even though they are different in string format.
				
				// Also unbox them so we can use the not equal / equal operators safely.
				long lv = longValue;
				long clx = this.constantLongX;
				
				if (this.operatorX == Operator.NOT_EQUAL)
					return lv != clx;
				
				else if (this.operatorX == Operator.EQUAL)
					return lv == clx;
				
				else if (this.operatorX == Operator.GREATER)
					return lv > clx;
				
				else if (this.operatorX == Operator.GREATER_OR_EQUAL)
					return lv >= clx;
				
				else if (this.operatorX == Operator.LESSER)
					return lv < clx;
				
				else if (this.operatorX == Operator.LESSER_OR_EQUAL)
					return lv <= clx;
				
				else
					return false;
			}
			
			else if (this.operatorX == Operator.NOT_EQUAL)
				return !value.equals(this.constantX);
			
			else if (this.operatorX == Operator.EQUAL)
				return value.equals(this.constantX);
			
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
		String op = this.operatorX.getSymbol();
		
		return this.indexX.getSheet() + ">" + this.indexX.getIndex() + ":[" + value + "] " + op + " " + this.constantX;
	}
}
