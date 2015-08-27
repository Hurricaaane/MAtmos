package eu.ha3.matmos.expansions;

import eu.ha3.easy.TimeStatistic;
import eu.ha3.matmos.engine.core.implem.Knowledge;
import eu.ha3.matmos.engine.core.implem.ProviderCollection;
import eu.ha3.matmos.engine.core.implem.SystemClock;
import eu.ha3.matmos.engine.core.interfaces.Data;
import eu.ha3.matmos.engine.core.interfaces.Evaluated;
import eu.ha3.matmos.engine.core.interfaces.EventInterface;
import eu.ha3.matmos.engine.core.interfaces.ReferenceTime;
import eu.ha3.matmos.engine.core.interfaces.Simulated;
import eu.ha3.matmos.expansions.agents.LoadingAgent;
import eu.ha3.matmos.expansions.agents.RawJasonLoadingAgent;
import eu.ha3.matmos.expansions.debugunit.ExpansionDebugUnit;
import eu.ha3.matmos.expansions.debugunit.FolderResourcePackEditableEDU;
import eu.ha3.matmos.expansions.debugunit.ReadOnlyJasonStringEDU;
import eu.ha3.matmos.expansions.volume.VolumeContainer;
import eu.ha3.matmos.expansions.volume.VolumeUpdatable;
import eu.ha3.matmos.game.data.ModularDataGatherer;
import eu.ha3.matmos.game.data.abstractions.Collector;
import eu.ha3.matmos.game.system.SoundAccessor;
import eu.ha3.matmos.game.system.SoundHelperRelay;
import eu.ha3.matmos.log.MAtLog;
import eu.ha3.util.property.simple.ConfigProperty;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.FolderResourcePack;
import net.minecraft.util.ResourceLocation;

/* x-placeholder */

public class Expansion implements VolumeUpdatable, Stable, Simulated, Evaluated
{
	private static ReferenceTime TIME = new SystemClock();
	
	private final ExpansionIdentity identity;
	private final Data data;
	private final Collector collector;
	private final SoundHelperRelay capabilities;
	private final VolumeContainer masterVolume;
	private final ConfigProperty myConfiguration;
	
	private float volume;
	private boolean isSuccessfullyBuilt;
	private boolean isActive;
	private boolean reliesOnLegacyModules;
	
	//
	
	private Knowledge knowledge; // Knowledge is not final
	private LoadingAgent agent;
	
	//
	private LoadingAgent jasonDebugPush;
	
	public Expansion(ExpansionIdentity identity, Data data, Collector collector, SoundAccessor accessor,
                     VolumeContainer masterVolume, File configurationSource)
	{
		this.identity = identity;
		this.masterVolume = masterVolume;
		this.capabilities = new SoundHelperRelay(accessor);
		this.data = data;
		this.collector = collector;
		
		newKnowledge();
		
		this.myConfiguration = new ConfigProperty();
		this.myConfiguration.setProperty("volume", 1f);
		this.myConfiguration.commit();
		try
		{
			this.myConfiguration.setSource(configurationSource.getCanonicalPath());
			this.myConfiguration.load();
		}
		catch (IOException e1)
		{
			e1.printStackTrace();
		}
		
		setVolumeAndUpdate(this.myConfiguration.getFloat("volume"));
	}
	
	public void setLoadingAgent(LoadingAgent agent)
	{
		this.agent = agent;
	}
	
	public void refreshKnowledge()
	{
		boolean reactivate = isActivated();
		deactivate();
		
		newKnowledge();
		this.isSuccessfullyBuilt = false;
		
		if (reactivate)
		{
			activate();
		}
	}
	
	public void pushDebugJasonAndRefreshKnowledge(String jasonString)
	{
		this.jasonDebugPush = new RawJasonLoadingAgent(jasonString);
		refreshKnowledge();
	}
	
	private void newKnowledge()
	{
		this.knowledge = new Knowledge(this.capabilities, TIME);
		this.knowledge.setData(this.data);
	}
	
	private void buildKnowledge()
	{
		if (this.agent == null)
			return;
		
		newKnowledge();
		
		if (this.jasonDebugPush == null)
		{
			this.isSuccessfullyBuilt = this.agent.load(this.identity, this.knowledge);
		}
		else
		{
			this.isSuccessfullyBuilt = this.jasonDebugPush.load(this.identity, this.knowledge);
			this.jasonDebugPush = null;
		}
		
		if (!this.isSuccessfullyBuilt)
		{
			newKnowledge();
		}
		
		this.knowledge.cacheSounds(this.identity);
	}
	
	public void playSample()
	{
		if (!isActivated())
			return;
		
		EventInterface event = this.knowledge.obtainProviders().getEvent().get("__SAMPLE");
		if (event != null)
		{
			event.playSound(1f, 1f);
		}
	}
	
	public String getName()
	{
		return this.identity.getUniqueName();
	}
	
	public String getFriendlyName()
	{
		return this.identity.getFriendlyName();
	}
	
	public void saveConfig()
	{
		this.myConfiguration.setProperty("volume", this.volume);
		if (this.myConfiguration.commit())
		{
			this.myConfiguration.save();
		}
	}
	
	public boolean reliesOnLegacyModules()
	{
		return this.reliesOnLegacyModules;
	}
	
	@Override
	public void simulate()
	{
		if (!this.isActive)
			return;
		
		try
		{
			this.knowledge.simulate();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			deactivate();
		}
	}
	
	@Override
	public void evaluate()
	{
		if (!this.isActive)
			return;
		
		try
		{
			this.knowledge.evaluate();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			deactivate();
		}
	}
	
	@Override
	public boolean isActivated()
	{
		return this.isActive;
	}
	
	@Override
	public void activate()
	{
		if (this.isActive)
			return;
		
		if (getVolume() <= 0f)
			return;
		
		if (!this.isSuccessfullyBuilt && this.agent != null)
		{
			MAtLog.info("Building expansion " + getName() + "...");
			TimeStatistic stat = new TimeStatistic(Locale.ENGLISH);
			buildKnowledge();
			if (this.isSuccessfullyBuilt)
			{
				MAtLog.info("Expansion " + getName() + " built (" + stat.getSecondsAsString(3) + "s).");
			}
			else
			{
				MAtLog.warning("Expansion "
					+ getName() + " failed to build!!! (" + stat.getSecondsAsString(3) + "s).");
			}
		}
		
		if (this.collector != null)
		{
			Set<String> requiredModules = this.knowledge.calculateRequiredModules();
			this.collector.addModuleStack(this.identity.getUniqueName(), requiredModules);
			
			MAtLog.info("Expansion "
				+ this.identity.getUniqueName() + " requires " + requiredModules.size() + " found modules: "
				+ Arrays.toString(requiredModules.toArray()));
			
			List<String> legacyModules = new ArrayList<String>();
			for (String module : requiredModules)
			{
				if (module.startsWith(ModularDataGatherer.LEGACY_PREFIX))
				{
					legacyModules.add(module);
				}
			}
			if (legacyModules.size() > 0)
			{
				Collections.sort(legacyModules);
				MAtLog.warning("Expansion "
					+ this.identity.getUniqueName() + " uses LEGACY modules: "
					+ Arrays.toString(legacyModules.toArray()));
				this.reliesOnLegacyModules = true;
			}
		}
		
		this.isActive = true;
		
	}
	
	@Override
	public void deactivate()
	{
		if (!this.isActive)
			return;
		
		if (this.collector != null)
		{
			this.collector.removeModuleStack(this.identity.getUniqueName());
		}
		
		this.isActive = false;
	}
	
	@Override
	public void dispose()
	{
		deactivate();
		this.capabilities.cleanUp();
		newKnowledge();
		setLoadingAgent(null);
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
		this.capabilities.applyVolume(this.masterVolume.getVolume() * getVolume());
	}
	
	/**
	 * Interrupt this expansion brutally, without calling cleanup calls.
	 */
	@Override
	public void interrupt()
	{
		this.capabilities.interrupt();
	}
	
	/**
	 * Obtain the providers of the knowledge, for debugging purposes.
	 * 
	 * @return
	 */
	public ProviderCollection obtainProvidersForDebugging()
	{
		return this.knowledge.obtainProviders();
	}
	
	public ExpansionDebugUnit obtainDebugUnit()
	{
		try
		{
			if (this.identity.getPack() instanceof FolderResourcePack)
			{
				FolderResourcePack frp = (FolderResourcePack) this.identity.getPack();
				String folderName = frp.getPackName();
				// XXX: getPackName might not be specified to return the folder name?
				
				final File folder = new File(Minecraft.getMinecraft().mcDataDir, "resourcepacks/" + folderName);
				
				if (folder.exists() && folder.isDirectory())
				{
					System.out.println(this.identity.getLocation().getResourcePath());
					final File file =
						new File(folder, "assets/matmos/" + this.identity.getLocation().getResourcePath());
					
					return new FolderResourcePackEditableEDU() {
						@Override
						public Knowledge obtainKnowledge()
						{
							return Expansion.this.knowledge;
						}
						
						@Override
						public Data obtainData()
						{
							return Expansion.this.data;
						}
						
						@Override
						public File obtainExpansionFile()
						{
							return file;
						}
						
						@Override
						public File obtainExpansionFolder()
						{
							return folder;
						}
					};
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return new ReadOnlyJasonStringEDU() {
			
			@Override
			public Knowledge obtainKnowledge()
			{
				return Expansion.this.knowledge;
			}
			
			@Override
			public Data obtainData()
			{
				return Expansion.this.data;
			}
			
			@Override
			public String obtainJasonString()
			{
				try
				{
					// XXX does not handle XML 
					return new Scanner(Expansion.this.identity.getPack().getInputStream(
						Expansion.this.identity.getLocation())).useDelimiter("\\Z").next();
				}
				catch (IOException e)
				{
					e.printStackTrace();
					System.err.println("Jason unavailable.");
					return "{}";
				}
			}
		};
	}
	
	public boolean hasMoreInfo()
	{
		return this.identity.getPack().resourceExists(new ResourceLocation("matmos", "info.txt"));
	}
	
	public String getInfo()
	{
		try
		{
			return new Scanner(this.identity.getPack().getInputStream(new ResourceLocation("matmos", "info.txt")))
				.useDelimiter("\\Z").next();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return "Error while fetching info.txt";
		}
	}
}
