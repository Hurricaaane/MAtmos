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
import eu.ha3.matmos.engine0.requirem.CollationOfRequirements;

/* x-placeholder */

public class ExpansionManager implements VolumeUpdatable
{
	private final SoundAccessor accessor;
	private final File userconfigFolder;
	
	private final MAtResourcePackDealer dealer = new MAtResourcePackDealer();
	private Map<String, Expansion> expansions;
	
	private boolean isActivated;
	private Data data;
	
	private Collation collation;
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
		
		this.collation = new CollationOfRequirements();
	}
	
	public void loadExpansions()
	{
		clearExpansions();
		
		List<ExpressedExpansion> expressions = new ArrayList<ExpressedExpansion>();
		gatherOffline(expressions);
		createExpansionEntries(expressions);
		for (ExpressedExpansion exp : expressions)
		{
			addExpansionFromExpression(exp);
		}
		
	}
	
	private void gatherOffline(List<ExpressedExpansion> expressions)
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
						ExpressedExpansion exp =
							new ExpressedExpansion(uniqueName, friendlyName, pack.getResourcePack(), location);
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
	
	private void createExpansionEntries(List<ExpressedExpansion> offline)
	{
		for (ExpressedExpansion exp : offline)
		{
			MAtmosConvLogger.info("ExpansionLoader found offline " + exp.getUniqueName() + ".");
			createExpansionEntry(exp.getUniqueName());
		}
		
	}
	
	private void createExpansionEntry(String uniqueName)
	{
		Expansion expansion =
			new Expansion(this, this.accessor, uniqueName, new File(this.userconfigFolder, uniqueName + ".cfg"));
		this.expansions.put(uniqueName, expansion);
		
		expansion.setData(this.data);
		expansion.setCollation(this.collation);
	}
	
	private void addExpansionFromExpression(ExpressedExpansion exp)
	{
		try
		{
			addExpansion(exp.getUniqueName(), exp.getPack().getInputStream(exp.getLocation()));
		}
		catch (IOException e)
		{
			MAtmosConvLogger.warning("Error on ExpansionLoader (on expression " + exp.getUniqueName() + ").");
		}
	}
	
	private void addExpansion(String uniqueName, InputStream stream)
	{
		if (!this.expansions.containsKey(uniqueName))
		{
			MAtmosConvLogger.severe("Tried to add an expansion that has no entry!");
			return;
		}
		
		Expansion expansion = this.expansions.get(uniqueName);
		expansion.inputStructure(stream);
		
		tryTurnOn(expansion);
	}
	
	private void tryTurnOn(Expansion expansion)
	{
		if (expansion == null)
			return;
		
		turnOnOrOff(expansion);
		
	}
	
	private void turnOnOrOff(Expansion expansion)
	{
		if (expansion == null)
			return;
		
		if (this.isActivated)
		{
			if (expansion.getVolume() > 0)
			{
				expansion.turnOn();
			}
		}
		else
		{
			expansion.turnOff();
		}
		
	}
	
	public void activate()
	{
		if (this.isActivated)
			return;
		
		this.isActivated = true;
		
		resync();
	}
	
	public void deactivate()
	{
		if (!this.isActivated)
			return;
		
		this.isActivated = false;
		
		resync();
	}
	
	private void resync()
	{
		for (Expansion expansion : this.expansions.values())
		{
			turnOnOrOff(expansion);
		}
	}
	
	public Map<String, Expansion> getExpansions()
	{
		return this.expansions;
		
	}
	
	public void soundRoutine()
	{
		for (Expansion expansion : this.expansions.values())
		{
			expansion.soundRoutine();
		}
	}
	
	public void dataRoutine()
	{
		for (Expansion expansion : this.expansions.values())
		{
			expansion.dataRoutine();
		}
	}
	
	public void clearExpansions()
	{
		for (Expansion expansion : this.expansions.values())
		{
			expansion.clear();
		}
		this.expansions.clear();
	}
	
	public void setData(Data data)
	{
		this.data = data;
		
	}
	
	public void interrupt()
	{
		for (Expansion exp : this.expansions.values())
		{
			exp.interrupt();
		}
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
	
	public void dispose()
	{
		for (Expansion e : this.expansions.values())
		{
			e.cleanUp();
		}
	}
}
