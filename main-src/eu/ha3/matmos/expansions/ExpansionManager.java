package eu.ha3.matmos.expansions;

import java.io.File;
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

import eu.ha3.matmos.engine0.core.interfaces.Data;
import eu.ha3.matmos.expansions.agents.JasonLoadingAgent;
import eu.ha3.matmos.expansions.agents.LegacyXMLLoadingAgent;
import eu.ha3.matmos.expansions.volume.VolumeUpdatable;
import eu.ha3.matmos.game.data.abstractions.Collector;
import eu.ha3.matmos.game.system.MAtResourcePackDealer;
import eu.ha3.matmos.game.system.MAtmosUtility;
import eu.ha3.matmos.game.system.SoundAccessor;
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
	private Collector collector;
	
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
		
		List<ExpansionIdentity> identities = new ArrayList<ExpansionIdentity>();
		findExpansions(identities);
		for (ExpansionIdentity identity : identities)
		{
			addExpansion(identity);
		}
	}
	
	private void findExpansions(List<ExpansionIdentity> identities)
	{
		List<ResourcePackRepository.Entry> resourcePacks = this.dealer.findResourcePacks();
		for (ResourcePackRepository.Entry pack : resourcePacks)
		{
			try
			{
				InputStream is = this.dealer.openExpansionsPointerFile(pack.getResourcePack());
				String jasonString = IOUtils.toString(is, "UTF-8");
				
				JsonObject jason = new JsonParser().parse(jasonString).getAsJsonObject();
				JsonArray expansions = jason.get("expansions").getAsJsonArray();
				for (JsonElement element : expansions)
				{
					JsonObject o = element.getAsJsonObject();
					String uniqueName = MAtmosUtility.sanitizeUniqueName(o.get("uniquename").getAsString());
					String friendlyName = o.get("friendlyname").getAsString();
					String pointer = o.get("pointer").getAsString();
					ResourceLocation location = new ResourceLocation("matmos", pointer);
					if (pack.getResourcePack().resourceExists(location))
					{
						ExpansionIdentity identity =
							new ExpansionIdentity(uniqueName, friendlyName, pack.getResourcePack(), location);
						identities.add(identity);
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
	
	private void addExpansion(ExpansionIdentity identity)
	{
		Expansion expansion =
			new Expansion(identity, this.data, this.collector, this.accessor, this, new File(
				this.userconfigFolder, identity.getUniqueName() + ".cfg"));
		this.expansions.put(identity.getUniqueName(), expansion);
		
		if (identity.getLocation().getResourcePath().endsWith(".xml"))
		{
			File folder = new File(this.userconfigFolder, "DO NOT EDIT UNLESS COPIED/");
			if (!folder.exists())
			{
				folder.mkdirs();
			}
			expansion.setLoadingAgent(new LegacyXMLLoadingAgent(new File(folder, identity.getUniqueName()
				+ ".json_NO_EDIT")));
		}
		else
		{
			expansion.setLoadingAgent(new JasonLoadingAgent());
		}
		
		expansion.updateVolume();
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
			expansion.simulate();
		}
	}
	
	@Override
	public void onTick()
	{
		for (Expansion expansion : this.expansions.values())
		{
			expansion.evaluate();
		}
	}
	
	public void setData(Data data)
	{
		this.data = data;
	}
	
	public void setCollector(Collector collector)
	{
		this.collector = collector;
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
