package eu.ha3.matmos.engine0.game.data;

import eu.ha3.matmos.engine0.core.implem.StringData;

/* x-placeholder */

public abstract class MAtScanCoordsPipeline implements MAtScanCoordsOps
{
	private StringData data;
	private MAtScanCoordsPipeline next;
	
	public MAtScanCoordsPipeline(StringData dataIn)
	{
		this.data = dataIn;
		this.next = null;
	}
	
	public StringData data()
	{
		return this.data;
	}
	
	abstract void doBegin();
	
	abstract void doInput(int x, int y, int z);
	
	abstract void doFinish();
	
	public void append(MAtScanCoordsPipeline operator)
	{
		if (this.next == null)
		{
			this.next = operator;
		}
		else
		{
			this.next.append(operator);
		}
		
	}
	
	@Override
	public void begin()
	{
		doBegin();
		
		if (this.next != null)
		{
			this.next.begin();
		}
		
	}
	
	@Override
	public void finish()
	{
		doFinish();
		
		if (this.next != null)
		{
			this.next.finish();
		}
		
	}
	
	@Override
	public void input(int x, int y, int z)
	{
		doInput(x, y, z);
		
		if (this.next != null)
		{
			this.next.input(x, y, z);
		}
		
	}
	
}
