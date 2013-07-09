package net.minecraft.src;

import eu.ha3.matmos.engine.implem.GenericSheet;
import eu.ha3.matmos.engine.implem.IntegerData;
import eu.ha3.matmos.engine.interfaces.Sheet;

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
	private Sheet<Integer> tempnormal;
	private int count;
	
	private String normalName;
	private String proportionnalName;
	
	private int proportionnalTotal;
	
	public MAtPipelineIDAccumulator(
		MAtMod mod, IntegerData dataIn, String normalNameIn, String proportionnalNameIn, int proportionnalTotalIn)
	{
		super(mod, dataIn);
		this.tempnormal = new GenericSheet<Integer>(MAtDataGatherer.COUNT_WORLD_BLOCKS, 0);
		
		this.normalName = normalNameIn;
		this.proportionnalName = proportionnalNameIn;
		this.proportionnalTotal = proportionnalTotalIn;
		
	}
	
	@Override
	void doBegin()
	{
		this.count = 0;
		for (int i = 0; i < this.tempnormal.getSize(); i++)
		{
			this.tempnormal.set(i, 0);
		}
	}
	
	@Override
	void doInput(long x, long y, long z)
	{
		int id = mod().manager().getMinecraft().theWorld.getBlockId((int) x, (int) y, (int) z);
		if (id >= this.tempnormal.getSize() || id < 0)
			return; /// Do not count
			
		this.tempnormal.set(id, this.tempnormal.get(id) + 1);
		
		this.count++;
		
	}
	
	@Override
	void doFinish()
	{
		Sheet<Integer> normal = null;
		Sheet<Integer> proportionnal = null;
		
		normal = data().getSheet(this.normalName);
		
		if (this.proportionnalName != null)
		{
			proportionnal = data().getSheet(this.proportionnalName);
		}
		
		for (int i = 0; i < this.tempnormal.getSize(); i++)
		{
			normal.set(i, this.tempnormal.get(i));
			
			if (this.proportionnalName != null)
			{
				proportionnal.set(i, (int) (this.proportionnalTotal * this.tempnormal.get(i) / (float) this.count));
			}
			
		}
	}
}
