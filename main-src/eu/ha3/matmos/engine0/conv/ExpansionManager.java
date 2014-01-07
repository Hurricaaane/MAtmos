package eu.ha3.matmos.engine0.conv;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraft.util.ResourceLocation;

import org.apache.commons.io.IOUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import eu.ha3.matmos.engine0.core.interfaces.Data;
import eu.ha3.matmos.engine0.core.interfaces.SoundRelay;
import eu.ha3.matmos.engine0.game.system.MAtResourcePackDealer;
import eu.ha3.matmos.engine0.requirem.Collation;
import eu.ha3.matmos.engine0.requirem.CollationOfRequirements;

/* x-placeholder */

public class ExpansionManager
{
	private final MAtResourcePackDealer dealer = new MAtResourcePackDealer();
	private Map<String, Expansion> expansions;
	
	private File userconfigFolder;
	private boolean isActivated;
	private ReplicableSoundRelay master;
	private Data data;
	
	private Collation collation;
	
	public ExpansionManager(File userconfigFolder)
	{
		this.expansions = new HashMap<String, Expansion>();
		
		this.userconfigFolder = userconfigFolder;
		
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
				StringWriter writer = new StringWriter();
				IOUtils.copy(this.dealer.openExpansionsPointerFile(pack.getResourcePack()), writer);
				String jasonString = writer.toString();
				
				JsonObject jason = new JsonParser().parse(jasonString).getAsJsonObject();
				JsonObject versions = jason.getAsJsonObject("expansions");
				for (JsonElement element : versions.getAsJsonArray())
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
		Expansion expansion = new Expansion(uniqueName, new File(this.userconfigFolder, uniqueName + ".cfg"));
		this.expansions.put(uniqueName, expansion);
		
		SoundRelay soundManager = this.master.createChild();
		
		expansion.setSoundManager(soundManager);
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
	
	public void setMaster(ReplicableSoundRelay master)
	{
		this.master = master;
	}
	
	public void setData(Data data)
	{
		this.data = data;
		
	}
	
	public void neutralizeSoundManagers()
	{
		this.master = new SRCVNullObject();
		for (Expansion exp : this.expansions.values())
		{
			exp.setSoundManager(new SRCVNullObject());
		}
	}
	
	public Collation getCollation()
	{
		return this.collation;
	}
}
