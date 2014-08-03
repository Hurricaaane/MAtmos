package eu.ha3.matmos.game.data.modules;

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
import eu.ha3.matmos.engine.core.interfaces.Data;
import eu.ha3.matmos.game.data.abstractions.Collector;
import eu.ha3.matmos.game.data.abstractions.Processor;
import eu.ha3.matmos.game.data.abstractions.module.ModuleProcessor;
import eu.ha3.matmos.game.data.abstractions.module.PassOnceModule;
import eu.ha3.matmos.game.system.IDontKnowHowToCode;

/* x-placeholder */

public class S__detect implements Processor, PassOnceModule
{
	private final Set<String> submodules;
	private final Collector collector;
	
	private AxisAlignedBB bbox;
	
	private int max;
	
	private ModuleProcessor mindistModel;
	private Map<Integer, Double> minimumDistanceReports;
	
	private ModuleProcessor[] radiusSheets;
	private int[] radiusValuesSorted;
	private Map<Integer, Integer>[] entityCount;
	
	private int maxel;
	
	private boolean isRequired;
	
	@SuppressWarnings("unchecked")
	public S__detect(
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
		this.minimumDistanceReports = new HashMap<Integer, Double>();
		
		this.radiusSheets = new ModuleProcessor[radiis.length];
		this.entityCount = (Map<Integer, Integer>[]) new Map<?, ?>[radiis.length];
		
		this.radiusValuesSorted = Arrays.copyOf(radiis, radiis.length);
		Arrays.sort(this.radiusValuesSorted);
		this.maxel = this.radiusValuesSorted[this.radiusValuesSorted.length - 1] + 10;
		
		for (int i = 0; i < this.radiusValuesSorted.length; i++)
		{
			int radiNum = this.radiusValuesSorted[i];
			this.radiusSheets[i] = new ModuleProcessor(dataIn, radiModulePrefix + radiNum) {
				@Override
				protected void doProcess()
				{
				}
			};
			dataIn.getSheet(radiModulePrefix + radiNum).setDefaultValue(Integer.toString(Integer.MAX_VALUE));
			this.submodules.add(radiModulePrefix + radiNum);
			this.entityCount[i] = new HashMap<Integer, Integer>();
		}
		
		this.bbox = AxisAlignedBB.getBoundingBox(0, 0, 0, 0, 0, 0);
		
		this.max = max;
	}
	
	private void refresh()
	{
		this.isRequired = false;
		
		this.isRequired = this.collector.requires(this.mindistModel.getModuleName());
		
		for (ModuleProcessor processor : this.radiusSheets)
		{
			this.isRequired = this.isRequired || this.collector.requires(processor.getModuleName());
		}
		for (Map<Integer, Integer> mappy : this.entityCount)
		{
			mappy.clear();
		}
		
		// Reset old things
		for (int entityID : this.minimumDistanceReports.keySet())
		{
			for (int i = 0; i < this.radiusValuesSorted.length; i++)
			{
				this.radiusSheets[i].setValueIntIndex(entityID, 0);
			}
			this.mindistModel.setValueIntIndex(entityID, Integer.MAX_VALUE);
		}
		this.minimumDistanceReports.clear();
	}
	
	@Override
	public void process()
	{
		refresh();
		
		// XXX: Normally, process() should only run if it's required
		if (!this.isRequired)
		{
			IDontKnowHowToCode.warnOnce("EntityDetector is running but not required. Logic error?");
			return;
		}
		
		Minecraft mc = Minecraft.getMinecraft();
		
		double x = mc.thePlayer.posX;
		double y = mc.thePlayer.posY;
		double z = mc.thePlayer.posZ;
		
		this.bbox.setBounds(x - this.maxel, y - this.maxel, z - this.maxel, x + this.maxel, y + this.maxel, z
			+ this.maxel);
		
		@SuppressWarnings("unchecked")
		List<Entity> entityList = mc.theWorld.getEntitiesWithinAABB(Entity.class, this.bbox);
		
		for (Entity e : entityList)
		{
			if (e != null && e != mc.thePlayer)
			{
				double dx = e.posX - x;
				double dy = e.posY - y;
				double dz = e.posZ - z;
				
				double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
				
				if (e instanceof EntityPlayer)
				{
					reportDistance(0, distance);
				}
				else
				{
					int entityID = EntityList.getEntityID(e);
					if (entityID != 0)
					{
						reportDistance(entityID, distance);
					}
				}
				
				int i = 0;
				boolean reported = false;
				while (i < this.radiusValuesSorted.length && !reported)
				{
					if (distance <= this.radiusValuesSorted[i])
					{
						// If something is within 1 meter, it certainly also is within 5 meters:
						// expand now and exit the loop.
						int eID = e instanceof EntityPlayer ? 0 : EntityList.getEntityID(e);
						if (eID >= 0)
						{
							for (int above = i; above < this.radiusValuesSorted.length; above++)
							{
								addToEntityCount(above, eID, 1);
							}
						}
						reported = true;
					}
					else
					{
						i++;
					}
				}
			}
		}
		
		for (int i = 0; i < this.radiusValuesSorted.length; i++)
		{
			if (this.collector.requires(this.radiusSheets[i].getModuleName()))
			{
				for (int entityID : this.entityCount[i].keySet())
				{
					this.radiusSheets[i].setValueIntIndex(entityID, this.entityCount[i].get(entityID));
				}
			}
		}
		if (this.collector.requires(this.mindistModel.getModuleName()))
		{
			for (int entityID : this.minimumDistanceReports.keySet())
			{
				this.mindistModel.setValueIntIndex(
					entityID, (int) Math.floor(this.minimumDistanceReports.get(entityID) * 1000));
			}
		}
		
		// Apply the virtual sheets
		if (this.collector.requires(this.mindistModel.getModuleName()))
		{
			this.mindistModel.process();
		}
		for (int i = 0; i < this.radiusValuesSorted.length; i++)
		{
			if (this.collector.requires(this.radiusSheets[i].getModuleName()))
			{
				this.radiusSheets[i].process();
			}
		}
	}
	
	protected void addToEntityCount(int radiIndex, int entityID, int count)
	{
		if (this.entityCount[radiIndex].containsKey(entityID))
		{
			this.entityCount[radiIndex].put(entityID, this.entityCount[radiIndex].get(entityID) + count);
		}
		else
		{
			this.entityCount[radiIndex].put(entityID, count);
		}
	}
	
	protected void reportDistance(int entityID, double distance)
	{
		if (!this.minimumDistanceReports.containsKey(entityID) || this.minimumDistanceReports.get(entityID) > distance)
		{
			this.minimumDistanceReports.put(entityID, distance);
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
