package eu.ha3.matmos.engine0.core.implem;

import eu.ha3.matmos.engine0.conv.MAtmosConvLogger;
import eu.ha3.matmos.engine0.core.interfaces.Provider;
import eu.ha3.matmos.engine0.core.interfaces.SheetCommander;

/* x-placeholder */

public class Condition extends MultistateComponent
{
	private final SheetIndex indexX;
	private final ConditionType operatorX;
	private final String constantX;
	private final Long constantLongX;
	//private final Float constantFloatX;
	
	private final Provider<SheetCommander> x;
	
	private int siVersion = -1;
	
	public Condition(
		String name, Provider<SheetCommander> sheetCommander, SheetIndex index, ConditionType operator, String constant)
	{
		super(name, sheetCommander);
		this.x = this.provider;
		
		this.indexX = index;
		this.operatorX = operator;
		this.constantX = constant;
		
		this.constantLongX = LongFloatSimplificator.longOf(constant);
		//this.constantFloatX = LongFloatSimplificator.floatOf(constant);
	}
	
	@Override
	public void evaluate()
	{
		if (!this.x.instance().exists(this.indexX))
			return;
		
		if (this.x.instance().version(this.indexX) == this.siVersion)
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
			String value = (String) this.x.instance().get(this.indexX);
			Long longValue = LongFloatSimplificator.longOf(value);
			//Float floatValue = LongFloatSimplificator.floatOf(this.constant);
			
			if (this.operatorX == ConditionType.NOT_EQUAL)
				return !value.equals(this.constantX);
			
			else if (this.operatorX == ConditionType.EQUAL)
				return value.equals(this.constantX);
			
			else if (this.operatorX == ConditionType.IN_LIST)
				return this.x.instance().listHas(this.constantX, value);
			
			else if (this.operatorX == ConditionType.NOT_IN_LIST)
				return !this.x.instance().listHas(this.constantX, value);
			
			else if (longValue != null && this.constantLongX != null)
			{
				// if (both values are integers), then
				
				if (this.operatorX == ConditionType.GREATER)
					return longValue > this.constantLongX;
				
				else if (this.operatorX == ConditionType.GREATER_OR_EQUAL)
					return longValue >= this.constantLongX;
				
				else if (this.operatorX == ConditionType.LESSER_)
					return longValue < this.constantLongX;
				
				else if (this.operatorX == ConditionType.LESSER_OR_EQUAL)
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
}
