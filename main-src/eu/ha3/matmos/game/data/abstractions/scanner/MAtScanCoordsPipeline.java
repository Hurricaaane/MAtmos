package eu.ha3.matmos.game.data.abstractions.scanner;

import eu.ha3.matmos.engine0.core.interfaces.Data;

/* x-placeholder */

@Deprecated
public abstract class MAtScanCoordsPipeline implements MAtScanCoordsOps
{
	private Data data;
	private MAtScanCoordsPipeline next;
	
	public MAtScanCoordsPipeline(Data dataIn)
	{
		this.data = dataIn;
		this.next = null;
	}
	
	public Data data()
	{
		return this.data;
	}
	
	public abstract void doBegin();
	
	public abstract void doInput(int x, int y, int z);
	
	public abstract void doFinish();
	
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
