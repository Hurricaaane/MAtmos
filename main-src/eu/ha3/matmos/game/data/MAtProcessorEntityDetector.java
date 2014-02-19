package eu.ha3.matmos.game.data;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import eu.ha3.matmos.engine0.core.interfaces.Data;
import eu.ha3.matmos.game.data.abstractions.Collector;
import eu.ha3.matmos.game.data.abstractions.Processor;
import eu.ha3.matmos.game.data.abstractions.module.ModuleProcessor;
import eu.ha3.matmos.game.data.abstractions.module.PassOnceModule;

/* x-placeholder */

public class MAtProcessorEntityDetector implements Processor, PassOnceModule
{
	private final Set<String> submodules;
	private final Collector collector;
	
	private AxisAlignedBB bbox;
	
	private int max;
	
	private ModuleProcessor mindistModel;
	private Map<Integer, Double> mindistMappy;
	
	private ModuleProcessor[] radiModels;
	private int[] radi;
	private Map<Integer, Integer>[] mappies;
	
	private int maxel;
	
	private boolean isRequired;
	
	@SuppressWarnings("unchecked")
	public MAtProcessorEntityDetector(
		Data dataIn, Collector collector, String minDistModule, String radiModulePrefix, int max, int... radiis)
	{
		this.collector = collector;
		this.submodules = new LinkedHashSet<String>();
		
		this.mindistModel = new ModuleProcessor(dataIn, minDistModule) {
			@Override
			protected void doProcess()
			{
			}
		};
		dataIn.getSheet(minDistModule).setDefaultValue("0");
		this.submodules.add(minDistModule);
		this.mindistMappy = new HashMap<Integer, Double>();
		
		this.radiModels = new ModuleProcessor[radiis.length];
		this.mappies = new Map[radiis.length];
		
		this.radi = Arrays.copyOf(radiis, radiis.length);
		Arrays.sort(this.radi);
		this.maxel = this.radi[this.radi.length - 1] + 10;
		
		for (int i = 0; i < this.radi.length; i++)
		{
			int radiNum = this.radi[i];
			this.radiModels[i] = new ModuleProcessor(dataIn, radiModulePrefix + radiNum) {
				@Override
				protected void doProcess()
				{
				}
			};
			dataIn.getSheet(radiModulePrefix + radiNum).setDefaultValue("0");
			this.submodules.add(radiModulePrefix + radiNum);
			this.mappies[i] = new HashMap<Integer, Integer>();
		}
		
		this.bbox = AxisAlignedBB.getBoundingBox(0, 0, 0, 0, 0, 0);
		
		this.max = max;
	}
	
	private void refresh()
	{
		this.isRequired = false;
		
		this.isRequired = this.collector.requires(this.mindistModel.getModuleName());
		
		for (ModuleProcessor processor : this.radiModels)
		{
			this.isRequired = this.isRequired || this.collector.requires(processor.getModuleName());
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
		
		// XXX: Normally, process() should only run if it's required
		if (!this.isRequired)
			return;
		
		else
		{
			System.err.println("EntityDetector is running but not required. Logic error?");
		}
		
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
		
		for (int rr = 0; rr < this.radi.length; rr++)
		{
			if (this.collector.requires(this.radiModels[rr].getModuleName()))
			{
				for (int i = 0; i < this.max; i++)
				{
					if (this.mappies[rr].containsKey(i))
					{
						this.radiModels[rr].setValueLegacyIntIndexes(i, this.mappies[rr].get(i));
					}
					else
					{
						this.radiModels[rr].setValueLegacyIntIndexes(i, 0);
					}
				}
			}
		}
		if (this.collector.requires(this.mindistModel.getModuleName()))
		{
			for (int i = 0; i < this.max; i++)
			{
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
		
		// Apply the virtual sheets
		if (this.collector.requires(this.mindistModel.getModuleName()))
		{
			this.mindistModel.process();
		}
		for (int rr = 0; rr < this.radi.length; rr++)
		{
			if (this.collector.requires(this.radiModels[rr].getModuleName()))
			{
				this.radiModels[rr].process();
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
	
	@Override
	public String getModuleName()
	{
		return "_POM__entity_detector";
	}
	
	@Override
	public Set<String> getSubModules()
	{
		return this.submodules;
	}
	
}
