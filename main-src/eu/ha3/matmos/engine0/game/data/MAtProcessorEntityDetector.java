package eu.ha3.matmos.engine0.game.data;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import eu.ha3.matmos.engine0.conv.Processor;
import eu.ha3.matmos.engine0.core.implem.StringData;
import eu.ha3.matmos.engine0.game.system.MAtMod;

/* x-placeholder */

public class MAtProcessorEntityDetector implements Processor
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
	
	private boolean isRequired;
	
	@SuppressWarnings("unchecked")
	public MAtProcessorEntityDetector(
		MAtMod modIn, StringData dataIn, String mindist, String prefix, String deltaSuffix, int max, int... radiis)
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
		this.isRequired = false;
		
		this.mindistModel.process();
		this.isRequired = this.mindistModel.isRequired();
		
		for (MAtProcessorModel processor : this.radiModels)
		{
			processor.process();
			this.isRequired = this.isRequired || processor.isRequired();
		}
		for (Map<?, ?> mappy : this.mappies)
		{
			mappy.clear();
		}
		this.mindistMappy.clear();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void process()
	{
		refresh();
		
		if (!this.isRequired)
			return;
		
		Minecraft mc = Minecraft.getMinecraft();
		
		double x = mc.thePlayer.posX;
		double y = mc.thePlayer.posY;
		double z = mc.thePlayer.posZ;
		
		this.bbox.setBounds(x - this.maxel, y - this.maxel, z - this.maxel, x + this.maxel, y + this.maxel, z
			+ this.maxel);
		
		List<Entity> entityList = mc.theWorld.getEntitiesWithinAABB(Entity.class, this.bbox);
		
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
				if (this.radiModels[rr].isRequired())
					if (this.mappies[rr].containsKey(i))
					{
						this.radiModels[rr].setValueLegacyIntIndexes(i, this.mappies[rr].get(i));
					}
					else
					{
						this.radiModels[rr].setValueLegacyIntIndexes(i, 0);
					}
			}
			if (this.mindistModel.isRequired())
				if (this.mindistMappy.containsKey(i))
				{
					this.mindistModel.setValueLegacyIntIndexes(i, (int) Math.floor(this.mindistMappy.get(i) * 1000));
				}
				else
				{
					this.mindistModel.setValueLegacyIntIndexes(i, Integer.MAX_VALUE);
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
