package eu.ha3.matmos.game.data;

import eu.ha3.matmos.engine.implem.IntegerData;
import eu.ha3.matmos.game.system.MAtMod;

/*
            DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE 
                    Version 2, December 2004 

 Copyright (C) 2004 Sam Hocevar <sam@hocevar.net> 

 Everyone is permitted to copy and distribute verbatim or modified 
 copies of this license document, and changing it is allowed as long 
 as the name is changed. 

            DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE 
   TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION 

  0. You just DO WHAT THE FUCK YOU WANT TO. 
*/

public abstract class MAtScanCoordsPipeline implements MAtScanCoordsOps
{
	private MAtMod mod;
	private IntegerData data;
	
	private MAtScanCoordsPipeline next;
	
	MAtScanCoordsPipeline(MAtMod mod2, IntegerData dataIn)
	{
		this.mod = mod2;
		this.data = dataIn;
		this.next = null;
		
	}
	
	public MAtMod mod()
	{
		return this.mod;
		
	}
	
	public IntegerData data()
	{
		return this.data;
		
	}
	
	abstract void doBegin();
	
	abstract void doInput(long x, long y, long z);
	
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
	public void input(long x, long y, long z)
	{
		doInput(x, y, z);
		
		if (this.next != null)
		{
			this.next.input(x, y, z);
		}
		
	}
	
}
