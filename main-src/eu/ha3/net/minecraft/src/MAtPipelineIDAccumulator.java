package net.minecraft.src;

import java.util.ArrayList;

import eu.ha3.matmos.engine.Data;

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

public class MAtPipelineIDAccumulator extends MAtScanCoordsPipeline
{
	private int[] tempnormal;
	private int count;
	
	private String normalName;
	private String proportionnalName;
	
	private int proportionnalTotal;
	
	MAtPipelineIDAccumulator(
		MAtMod mod, Data dataIn, String normalNameIn, String proportionnalNameIn, int proportionnalTotalIn)
	{
		super(mod, dataIn);
		this.tempnormal = new int[MAtDataGatherer.COUNT_WORLD_BLOCKS];
		
		this.normalName = normalNameIn;
		this.proportionnalName = proportionnalNameIn;
		this.proportionnalTotal = proportionnalTotalIn;
		
	}
	
	@Override
	void doBegin()
	{
		this.count = 0;
		for (int i = 0; i < this.tempnormal.length; i++)
		{
			this.tempnormal[i] = 0;
			
		}
		
	}
	
	@Override
	void doInput(long x, long y, long z)
	{
		int id = mod().manager().getMinecraft().theWorld.getBlockId((int) x, (int) y, (int) z);
		if (id >= this.tempnormal.length || id < 0)
			return; /// Do not count
			
		this.tempnormal[id] = this.tempnormal[id] + 1;
		
		this.count++;
		
	}
	
	@Override
	void doFinish()
	{
		ArrayList<Integer> normal = null;
		ArrayList<Integer> proportionnal = null;
		
		normal = data().sheets.get(this.normalName);
		
		if (this.proportionnalName != null)
		{
			proportionnal = data().sheets.get(this.proportionnalName);
		}
		
		for (int i = 0; i < this.tempnormal.length; i++)
		{
			normal.set(i, this.tempnormal[i]);
			
			if (this.proportionnalName != null)
			{
				proportionnal.set(i, (int) (this.proportionnalTotal * this.tempnormal[i] / (float) this.count));
			}
			
		}
		
		//data().flagUpdate(); // TODO Is this a good place to do it ?
		
	}
	
}
