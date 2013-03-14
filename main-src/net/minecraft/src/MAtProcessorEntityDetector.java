package net.minecraft.src;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

public class MAtProcessorEntityDetector extends MAtProcessorModel
{
	private AxisAlignedBB bbox;
	private int abrad;
	
	private Map<Integer, Integer> mappy;
	private int max;
	
	public MAtProcessorEntityDetector(
		MAtMod modIn, Data dataIn, String normalNameIn, String deltaNameIn, int abrad, int max)
	{
		super(modIn, dataIn, normalNameIn, deltaNameIn);
		this.bbox = AxisAlignedBB.getBoundingBox(0, 0, 0, 0, 0, 0);
		this.abrad = abrad;
		
		this.mappy = new HashMap<Integer, Integer>();
		this.max = max;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void doProcess()
	{
		Minecraft mc = mod().manager().getMinecraft();
		
		double x = mc.thePlayer.posX;
		double y = mc.thePlayer.posY;
		double z = mc.thePlayer.posZ;
		
		this.mappy.clear();
		
		List<Entity> entityList =
			mc.theWorld.getEntitiesWithinAABB(
				net.minecraft.src.Entity.class,
				this.bbox.setBounds(x - this.abrad, y - this.abrad, z - this.abrad, x + this.abrad, y + this.abrad, z
					+ this.abrad));
		
		for (Entity e : entityList)
		{
			if (e != mc.thePlayer)
			{
				if (e instanceof EntityPlayer)
				{
					add(0, 1);
				}
				else
				{
					int eID = EntityList.getEntityID(e);
					if (eID != 0)
					{
						add(eID, 1);
					}
				}
			}
		}
		
		for (int i = 0; i < this.max; i++)
		{
			if (this.mappy.containsKey(i))
			{
				setValue(i, this.mappy.get(i));
			}
			else
			{
				setValue(i, 0);
			}
			
		}
	}
	
	protected void add(int key, int count)
	{
		if (this.mappy.containsKey(key))
		{
			this.mappy.put(key, this.mappy.get(key) + count);
		}
		else
		{
			this.mappy.put(key, count);
		}
	}
	
}
