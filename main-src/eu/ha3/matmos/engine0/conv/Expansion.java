package eu.ha3.matmos.engine0.conv;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;

import eu.ha3.easy.TimeStatistic;
import eu.ha3.matmos.engine0.conv.volume.VolumeContainer;
import eu.ha3.matmos.engine0.conv.volume.VolumeUpdatable;
import eu.ha3.matmos.engine0.core.implem.Knowledge;
import eu.ha3.matmos.engine0.core.implem.SystemClock;
import eu.ha3.matmos.engine0.core.interfaces.Data;
import eu.ha3.matmos.engine0.core.interfaces.Evaluated;
import eu.ha3.matmos.engine0.core.interfaces.EventInterface;
import eu.ha3.matmos.engine0.core.interfaces.ReferenceTime;
import eu.ha3.matmos.engine0.core.interfaces.Simulated;
import eu.ha3.matmos.engine0.game.data.ModularDataGatherer;
import eu.ha3.matmos.engine0.game.data.abstractions.Collector;
import eu.ha3.matmos.engine0.game.system.SoundAccessor;
import eu.ha3.matmos.engine0.game.system.SoundHelperRelay;
import eu.ha3.matmos.engine0tools.LegacyXMLExpansions_Engine1;
import eu.ha3.util.property.simple.ConfigProperty;

/* x-placeholder */

public class Expansion implements VolumeUpdatable, Stable, Simulated, Evaluated
{
	private static ReferenceTime TIME = new SystemClock();
	
	private DocumentBuilder documentBuilder;
	private Document document;
	private Knowledge knowledge;
	
	private boolean initializedContents;
	
	private ConfigProperty myConfiguration;
	private boolean isBuilding;
	
	private float volume;
	
	//
	
	private final ExpansionIdentity identity;
	private final VolumeContainer masterVolume;
	private final SoundHelperRelay capabilities;
	
	private boolean isReady;
	private boolean isActive;
	private boolean reliesOnLegacyModules;
	
	private final Data data;
	private final Collector collector;
	
	public Expansion(
		ExpansionIdentity identity, Data data, Collector collector, SoundAccessor accessor,
		VolumeContainer masterVolume, File configurationSource)
	{
		this.identity = identity;
		this.masterVolume = masterVolume;
		this.capabilities = new SoundHelperRelay(accessor);
		this.data = data;
		this.collector = collector;
		
		this.knowledge = new Knowledge(this.capabilities, TIME);
		this.knowledge.setData(data);
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		
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
		
		try
		{
			this.documentBuilder = dbf.newDocumentBuilder();
		}
		catch (ParserConfigurationException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public void inputStructure(InputStream stream)
	{
		this.initializedContents = false;
		try
		{
			this.document = this.documentBuilder.parse(stream);
			this.initializedContents = true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			this.document = null;
			this.initializedContents = false;
		}
	}
	
	private void buildKnowledge()
	{
		if (this.document == null)
			return;
		
		if (!this.initializedContents)
			return;
		
		this.isReady = new LegacyXMLExpansions_Engine1().loadKnowledge(this.knowledge, this.document);
	}
	
	@Override
	public void simulate()
	{
		if (!this.isReady)
			return;
		
		if (!this.isActive)
			return;
		
		this.knowledge.simulate();
	}
	
	@Override
	public void evaluate()
	{
		if (!this.isReady)
			return;
		
		if (!this.isActive)
			return;
		
		this.knowledge.evaluate();
	}
	
	public void playSample()
	{
		if (!isActivated())
			return;
		
		EventInterface event = (EventInterface) this.knowledge.obtainProviders().getEvent().get("__SAMPLE");
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
	
	public boolean isReady()
	{
		return this.isReady;
	}
	
	public boolean reliesOnLegacyModules()
	{
		return this.reliesOnLegacyModules;
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
		
		if (this.isBuilding)
			return;
		
		if (!this.isReady && this.initializedContents)
		{
			this.isBuilding = true;
			
			TimeStatistic stat = new TimeStatistic(Locale.ENGLISH);
			buildKnowledge();
			this.knowledge.cacheSounds();
			
			MAtmosConvLogger.info("Expansion "
				+ this.identity.getUniqueName() + " loaded (" + stat.getSecondsAsString(3) + "s).");
			this.isBuilding = false;
		}
		
		if (this.isReady)
		{
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
		}
		
		this.isActive = true;
		
	}
	
	@Override
	public void deactivate()
	{
		if (!this.isReady)
			return;
		
		if (!this.isActive)
			return;
		
		if (this.data instanceof Collector)
		{
			((Collector) this.data).removeModuleStack(this.identity.getUniqueName());
		}
		
		this.isActive = false;
	}
	
	@Override
	public void dispose()
	{
		deactivate();
		this.capabilities.cleanUp();
		
		this.isReady = false;
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
}
