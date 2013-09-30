package eu.ha3.matmos.engine0.game.data;

import net.minecraft.src.Minecraft;
import eu.ha3.matmos.engine0.core.implem.GenericSheet;
import eu.ha3.matmos.engine0.core.implem.StringData;
import eu.ha3.matmos.engine0.core.interfaces.Sheet;
import eu.ha3.matmos.engine0.game.system.MAtMod;

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
		MAtMod mod, StringData dataIn, String normalNameIn, String proportionnalNameIn, int proportionnalTotalIn)
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
			// 1.7 DERAIL
			this.tempnormal.set(Integer.toString(i), 0);
		}
	}
	
	@Override
	void doInput(long x, long y, long z)
	{
		int id = Minecraft.getMinecraft().theWorld.getBlockId((int) x, (int) y, (int) z);
		if (id >= this.tempnormal.getSize() || id < 0)
			return; /// Do not count
			
		// 1.7 DERAIL
		this.tempnormal.set(Integer.toString(id), this.tempnormal.get(Integer.toString(id)) + 1);
		
		this.count++;
		
	}
	
	@Override
	void doFinish()
	{
		Sheet<String> normal = null;
		Sheet<String> proportionnal = null;
		
		normal = data().getSheet(this.normalName);
		
		if (this.proportionnalName != null)
		{
			proportionnal = data().getSheet(this.proportionnalName);
		}
		
		for (int i = 0; i < this.tempnormal.getSize(); i++)
		{
			String iS = Integer.toString(i);
			normal.set(iS, Integer.toString(this.tempnormal.get(iS)));
			
			if (this.proportionnalName != null)
			{
				proportionnal.set(
					iS,
					Integer.toString((int) (this.proportionnalTotal * this.tempnormal.get(iS) / (float) this.count)));
			}
			
		}
	}
}
