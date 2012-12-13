package net.minecraft.src;

import net.minecraft.client.Minecraft;
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

public class MAtProcessorContact extends MAtProcessorModel
{
	private int contactSum[];
	
	MAtProcessorContact(MAtMod modIn, Data dataIn, String normalNameIn, String deltaNameIn)
	{
		super(modIn, dataIn, normalNameIn, deltaNameIn);
		this.contactSum = new int[MAtDataGatherer.COUNT_WORLD_BLOCKS];
		
	}
	
	private void emptyContact()
	{
		for (int i = 0; i < this.contactSum.length; i++)
		{
			this.contactSum[i] = 0;
		}
		
	}
	
	@Override
	void doProcess()
	{
		Minecraft mc = mod().manager().getMinecraft();
		int x = (int) Math.floor(mc.thePlayer.posX);
		int y = (int) Math.floor(mc.thePlayer.posY) - 1; //FIXME: Player position is different from Half Life, this is fixed with a -1
		int z = (int) Math.floor(mc.thePlayer.posZ);
		
		int nx;
		int ny;
		int nz;
		
		emptyContact();
		
		for (int k = 0; k < 12; k++)
		{
			ny = y + (k > 7 ? k - 9 : k % 2);
			if (ny >= 0 && ny < mod().util().getWorldHeight())
			{
				nx = x + (k < 4 ? k < 2 ? -1 : 1 : 0);
				nz = z + (k > 3 && k < 8 ? k < 6 ? -1 : 1 : 0);
				
				int id = mod().manager().getMinecraft().theWorld.getBlockId(nx, ny, nz);
				if (id < this.contactSum.length || id >= 0)
				{
					this.contactSum[id] = this.contactSum[id] + 1;
				}
				
			}
			
		}
		
		for (int i = 0; i < this.contactSum.length; i++)
		{
			setValue(i, this.contactSum[i]);
		}
		
	}
	
}
