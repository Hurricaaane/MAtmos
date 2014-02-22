package eu.ha3.matmos.expansions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import eu.ha3.easy.TimeStatistic;
import eu.ha3.matmos.engine0.core.implem.Knowledge;
import eu.ha3.matmos.engine0.core.implem.SystemClock;
import eu.ha3.matmos.engine0.core.implem.abstractions.ProviderCollection;
import eu.ha3.matmos.engine0.core.interfaces.Data;
import eu.ha3.matmos.engine0.core.interfaces.Evaluated;
import eu.ha3.matmos.engine0.core.interfaces.EventInterface;
import eu.ha3.matmos.engine0.core.interfaces.ReferenceTime;
import eu.ha3.matmos.engine0.core.interfaces.Simulated;
import eu.ha3.matmos.expansions.agents.LoadingAgent;
import eu.ha3.matmos.expansions.volume.VolumeContainer;
import eu.ha3.matmos.expansions.volume.VolumeUpdatable;
import eu.ha3.matmos.game.data.ModularDataGatherer;
import eu.ha3.matmos.game.data.abstractions.Collector;
import eu.ha3.matmos.game.system.SoundAccessor;
import eu.ha3.matmos.game.system.SoundHelperRelay;
import eu.ha3.util.property.simple.ConfigProperty;

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
	
	public Expansion(
		ExpansionIdentity identity, Data data, Collector collector, SoundAccessor accessor,
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
		
		if (true || reactivate)
		{
			activate();
		}
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
		
		this.isSuccessfullyBuilt = this.agent.load(this.identity, this.knowledge);
		if (!this.isSuccessfullyBuilt)
		{
			newKnowledge();
		}
		
		this.knowledge.cacheSounds();
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
			MAtmosConvLogger.info("Building expansion " + getName() + "...");
			TimeStatistic stat = new TimeStatistic(Locale.ENGLISH);
			buildKnowledge();
			if (this.isSuccessfullyBuilt)
			{
				MAtmosConvLogger.info("Expansion " + getName() + " built (" + stat.getSecondsAsString(3) + "s).");
			}
			else
			{
				MAtmosConvLogger.warning("Expansion "
					+ getName() + " failed to build!!! (" + stat.getSecondsAsString(3) + "s).");
			}
		}
		
		if (this.collector != null)
		{
			Set<String> requiredModules = this.knowledge.calculateRequiredModules();
			this.collector.addModuleStack(this.identity.getUniqueName(), requiredModules);
			
			MAtmosConvLogger.info("Expansion "
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
				MAtmosConvLogger.warning("Expansion "
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
}
