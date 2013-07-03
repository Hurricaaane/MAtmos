package net.minecraft.src;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.src.Minecraft;
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

public class MAtProcessorEntityDetector
{
	private MAtMod mod;
	
	private AxisAlignedBB bbox;
	
	private int max;
	
	private MAtProcessorModel mindistModel;
	private Map<Integer, Double> mindistMappy;
	
	private MAtProcessorModel[] radiModels;
	private int[] radi;
	private Map<Integer, Integer>[] mappies;
	
	private int maxel;
	
	public MAtProcessorEntityDetector(
		MAtMod modIn, Data dataIn, String mindist, String prefix, String deltaSuffix, int max, int... radiis)
	{
		this.mod = modIn;
		
		this.mindistModel = new MAtProcessorModel(modIn, dataIn, mindist, mindist + deltaSuffix) {
			@Override
			protected void doProcess()
			{
			}
		};
		this.mindistMappy = new HashMap<Integer, Double>();
		
		this.radiModels = new MAtProcessorModel[radiis.length];
		this.mappies = new Map[radiis.length];
		
		this.radi = Arrays.copyOf(radiis, radiis.length);
		Arrays.sort(this.radi);
		this.maxel = this.radi[this.radi.length - 1] + 10;
		
		for (int i = 0; i < this.radi.length; i++)
		{
			int radiNum = this.radi[i];
			this.radiModels[i] =
				new MAtProcessorModel(modIn, dataIn, prefix + radiNum, prefix + radiNum + deltaSuffix) {
					@Override
					protected void doProcess()
					{
					}
				};
			this.mappies[i] = new HashMap<Integer, Integer>();
		}
		
		this.bbox = AxisAlignedBB.getBoundingBox(0, 0, 0, 0, 0, 0);
		
		this.max = max;
	}
	
	public void refresh()
	{
		this.mindistModel.process();
		for (MAtProcessorModel processor : this.radiModels)
		{
			processor.process();
		}
		for (Map mappy : this.mappies)
		{
			mappy.clear();
		}
		this.mindistMappy.clear();
	}
	
	@SuppressWarnings("unchecked")
	public void process()
	{
		refresh();
		
		Minecraft mc = this.mod.manager().getMinecraft();
		
		double x = mc.thePlayer.posX;
		double y = mc.thePlayer.posY;
		double z = mc.thePlayer.posZ;
		
		this.bbox.setBounds(x - this.maxel, y - this.maxel, z - this.maxel, x + this.maxel, y + this.maxel, z
			+ this.maxel);
		
		List<Entity> entityList = mc.theWorld.getEntitiesWithinAABB(net.minecraft.src.Entity.class, this.bbox);
		
		for (Entity e : entityList)
		{
			if (e != null && e != mc.thePlayer)
			{
				double dx = e.posX - x;
				double dy = e.posY - y;
				double dz = e.posZ - z;
				
				double dd = Math.sqrt(dx * dx + dy * dy + dz * dz);
				
				if (e instanceof EntityPlayer)
				{
					mindist(0, dd);
				}
				else
				{
					int eID = EntityList.getEntityID(e);
					if (eID != 0)
					{
						mindist(eID, dd);
					}
				}
				
				int i = 0;
				while (i < this.radi.length)
				{
					if (dd <= this.radi[i])
					{
						for (int j = i; j < this.radi.length; j++)
						{
							if (e instanceof EntityPlayer)
							{
								add(j, 0, 1);
							}
							else
							{
								int eID = EntityList.getEntityID(e);
								if (eID != 0)
								{
									add(j, eID, 1);
								}
							}
						}
						i = this.radi.length;
					}
					else
					{
						i++;
					}
				}
			}
		}
		
		for (int i = 0; i < this.max; i++)
		{
			for (int rr = 0; rr < this.radi.length; rr++)
			{
				if (this.mappies[rr].containsKey(i))
				{
					this.radiModels[rr].setValue(i, this.mappies[rr].get(i));
				}
				else
				{
					this.radiModels[rr].setValue(i, 0);
				}
			}
			if (this.mindistMappy.containsKey(i))
			{
				this.mindistModel.setValue(i, (int) Math.floor(this.mindistMappy.get(i) * 1000));
			}
			else
			{
				this.mindistModel.setValue(i, Integer.MAX_VALUE);
			}
			
		}
	}
	
	protected void add(int radiIndex, int key, int count)
	{
		if (this.mappies[radiIndex].containsKey(key))
		{
			this.mappies[radiIndex].put(key, this.mappies[radiIndex].get(key) + count);
		}
		else
		{
			this.mappies[radiIndex].put(key, count);
		}
	}
	
	protected void mindist(int key, double distance)
	{
		if (!this.mindistMappy.containsKey(key) || this.mindistMappy.get(key) > distance)
		{
			this.mindistMappy.put(key, distance);
		}
	}
	
}
