package eu.ha3.matmos.engine0.conv;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraft.util.ResourceLocation;

import org.apache.commons.io.IOUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import eu.ha3.matmos.engine0.conv.volume.VolumeUpdatable;
import eu.ha3.matmos.engine0.core.interfaces.Data;
import eu.ha3.matmos.engine0.game.system.MAtResourcePackDealer;
import eu.ha3.matmos.engine0.game.system.SoundAccessor;
import eu.ha3.matmos.engine0.requirem.Collation;
import eu.ha3.mc.haddon.supporting.SupportsFrameEvents;
import eu.ha3.mc.haddon.supporting.SupportsTickEvents;

/* x-placeholder */

public class ExpansionManager implements VolumeUpdatable, Stable, SupportsTickEvents, SupportsFrameEvents
{
	private final SoundAccessor accessor;
	private final File userconfigFolder;
	
	private final MAtResourcePackDealer dealer = new MAtResourcePackDealer();
	private Map<String, Expansion> expansions;
	
	private boolean isActivated;
	private Data data;
	
	private float volume = 1f;
	
	public ExpansionManager(File userconfigFolder, SoundAccessor accessor)
	{
		this.userconfigFolder = userconfigFolder;
		this.accessor = accessor;
		
		this.expansions = new HashMap<String, Expansion>();
		
		if (!this.userconfigFolder.exists())
		{
			this.userconfigFolder.mkdirs();
		}
	}
	
	public void loadExpansions()
	{
		dispose();
		
		List<ExpansionIdentity> expressions = new ArrayList<ExpansionIdentity>();
		findExpansions(expressions);
		for (ExpansionIdentity exp : expressions)
		{
			addExpansionFromExpression(exp);
		}
	}
	
	private void findExpansions(List<ExpansionIdentity> expressions)
	{
		List<ResourcePackRepository.Entry> resourcePacks = this.dealer.findResourcePacks();
		for (ResourcePackRepository.Entry pack : resourcePacks)
		{
			try
			{
				InputStream is = this.dealer.openExpansionsPointerFile(pack.getResourcePack());
				String jasonString = IOUtils.toString(is, "UTF-8");
				
				JsonObject jason = new JsonParser().parse(jasonString).getAsJsonObject();
				JsonArray versions = jason.get("expansions").getAsJsonArray();
				for (JsonElement element : versions)
				{
					JsonObject o = element.getAsJsonObject();
					String uniqueName = o.get("uniquename").getAsString();
					String friendlyName = o.get("friendlyname").getAsString();
					String pointer = o.get("pointer").getAsString();
					ResourceLocation location = new ResourceLocation("matmos", pointer);
					if (pack.getResourcePack().resourceExists(location))
					{
						ExpansionIdentity exp =
							new ExpansionIdentity(uniqueName, friendlyName, pack.getResourcePack(), location);
						expressions.add(exp);
					}
					else
					{
						MAtmosConvLogger.warning("An expansion pointer doesn't exist: " + pointer);
					}
				}
			}
			catch (Exception e)
			{
				MAtmosConvLogger.warning(pack.getResourcePackName()
					+ " " + "has failed with an error: " + e.getMessage());
			}
		}
	}
	
	private void addExpansionFromExpression(ExpansionIdentity exp)
	{
		try
		{
			String uniqueName = exp.getUniqueName();
			
			Expansion expansion =
				new Expansion(this, this.accessor, exp, new File(this.userconfigFolder, uniqueName + ".cfg"));
			this.expansions.put(uniqueName, expansion);
			
			expansion.setData(this.data);
			
			expansion.inputStructure(exp.getPack().getInputStream(exp.getLocation()));
			expansion.updateVolume();
		}
		catch (IOException e)
		{
			MAtmosConvLogger.warning("Error on ExpansionLoader (on expression " + exp.getUniqueName() + ").");
		}
	}
	
	private void synchronizeStable(Expansion expansion)
	{
		if (expansion == null)
			return;
		
		if (this.isActivated)
		{
			if (expansion.isActivated())
			{
				if (expansion.getVolume() <= 0f)
				{
					expansion.deactivate();
				}
			}
			else
			{
				if (expansion.getVolume() > 0f)
				{
					expansion.activate();
				}
			}
		}
		else
		{
			expansion.deactivate();
		}
	}
	
	public void synchronize()
	{
		for (Expansion expansion : this.expansions.values())
		{
			synchronizeStable(expansion);
		}
	}
	
	public Map<String, Expansion> getExpansions()
	{
		return this.expansions;
	}
	
	@Override
	public void onFrame(float f)
	{
		for (Expansion expansion : this.expansions.values())
		{
			expansion.soundRoutine();
		}
	}
	
	@Override
	public void onTick()
	{
		for (Expansion expansion : this.expansions.values())
		{
			expansion.dataRoutine();
		}
	}
	
	public void setData(Data data)
	{
		this.data = data;
		
	}
	
	public Collation getCollation()
	{
		return this.collation;
	}
	
	@Override
	public float getVolume()
	{
		return this.volume;
	}
	
	@Override
	public void setVolumeAndUpdate(float volume)
	{
		this.volume = volume;
		updateVolume();
	}
	
	@Override
	public void updateVolume()
	{
		for (Expansion e : this.expansions.values())
		{
			e.updateVolume();
		}
	}
	
	@Override
	public void activate()
	{
		if (this.isActivated)
			return;
		
		this.isActivated = true;
		
		synchronize();
	}
	
	@Override
	public void deactivate()
	{
		if (!this.isActivated)
			return;
		
		this.isActivated = false;
		
		synchronize();
	}
	
	@Override
	public void interrupt()
	{
		for (Expansion exp : this.expansions.values())
		{
			exp.interrupt();
		}
	}
	
	@Override
	public void dispose()
	{
		for (Expansion expansion : this.expansions.values())
		{
			expansion.dispose();
		}
		this.expansions.clear();
	}
	
	@Override
	public boolean isActivated()
	{
		return this.isActivated;
	}
	
	public void saveConfig()
	{
		for (Expansion expansion : this.expansions.values())
		{
			expansion.saveConfig();
		}
	}
}
