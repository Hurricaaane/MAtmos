package eu.ha3.matmos.engine0.game.system;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.audio.SoundManager;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import paulscode.sound.SoundSystem;
import eu.ha3.easy.TimeStatistic;
import eu.ha3.matmos.engine0.conv.Expansion;
import eu.ha3.matmos.engine0.conv.ExpansionManager;
import eu.ha3.matmos.engine0.conv.MAtmosConvLogger;
import eu.ha3.matmos.engine0.conv.Stable;
import eu.ha3.matmos.engine0.conv.volume.VolumeUpdatable;
import eu.ha3.matmos.engine0.game.data.MAtDataGatherer;
import eu.ha3.matmos.engine0.game.user.MAtUserControl;
import eu.ha3.mc.haddon.Identity;
import eu.ha3.mc.haddon.OperatorCaster;
import eu.ha3.mc.haddon.PrivateAccessException;
import eu.ha3.mc.haddon.implem.HaddonIdentity;
import eu.ha3.mc.haddon.implem.HaddonImpl;
import eu.ha3.mc.haddon.supporting.SupportsFrameEvents;
import eu.ha3.mc.haddon.supporting.SupportsTickEvents;
import eu.ha3.mc.quick.chat.Chatter;
import eu.ha3.mc.quick.update.NotifiableHaddon;
import eu.ha3.mc.quick.update.UpdateNotifier;
import eu.ha3.util.property.simple.ConfigProperty;

/* x-placeholder */

public class MAtMod extends HaddonImpl
	implements SupportsFrameEvents, SupportsTickEvents, NotifiableHaddon, IResourceManagerReloadListener,
	SoundAccessor, Stable
{
	// Identity
	protected final String NAME = "MAtmos";
	protected final int VERSION = 26;
	protected final String FOR = "1.6.2";
	protected final String ADDRESS = "http://matmos.ha3.eu";
	protected final Identity identity = new HaddonIdentity(this.NAME, this.VERSION, this.FOR, this.ADDRESS);
	
	// NotifiableHaddon and UpdateNotifier
	private final ConfigProperty config = new ConfigProperty();
	private final Chatter chatter = new Chatter(this, this.NAME);
	private final UpdateNotifier updateNotifier = new UpdateNotifier(
		this, "http://q.mc.ha3.eu/query/matmos-main-version-vn.json?ver=%d");
	
	// State
	private boolean isInitialized;
	private boolean isActivated;
	
	// Components
	private ExpansionManager expansionManager;
	private MAtUserControl userControl;
	private MAtDataGatherer dataGatherer;
	
	// Use once
	private boolean hasFirstTickPassed;
	
	public MAtMod()
	{
		MAtmosConvLogger.setRefinedness(MAtmosConvLogger.FINE);
	}
	
	@Override
	public void onLoad()
	{
		util().registerPrivateGetter("getSoundManager", SoundHandler.class, 5, "field_147694_f");
		util().registerPrivateGetter("getSoundSystem", SoundManager.class, 4, "field_148620_e");
		
		((OperatorCaster) op()).setTickEnabled(true);
		
		TimeStatistic timeMeasure = new TimeStatistic(Locale.ENGLISH);
		this.userControl = new MAtUserControl(this);
		this.expansionManager =
			new ExpansionManager(new File(util().getModsFolder(), "matmos/expansions_r27_userconfig/"), this);
		
		// Create default configuration
		this.updateNotifier.fillDefaults(this.config);
		this.config.setProperty("world.height", 256);
		this.config.setProperty("dump.sheets.enabled", false);
		this.config.setProperty("start.enabled", true);
		this.config.setProperty("reversed.controls", false);
		this.config.setProperty("sound.autopreview", true);
		this.config.setProperty("globalvolume.scale", 1f);
		this.config.setProperty("key.code", 65);
		this.config.setProperty("useroptions.altitudes.high", true);
		this.config.setProperty("useroptions.altitudes.low", true);
		this.config.setProperty("useroptions.biome.override", -1);
		this.config.setProperty("totalconversion.name", "default");
		this.config.commit();
		
		// Load configuration from source
		try
		{
			this.config.setSource(new File(util().getModsFolder(), "matmos/userconfig.cfg").getCanonicalPath());
			this.config.load();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			throw new RuntimeException("Error caused config not to work: " + e.getMessage());
		}
		
		this.updateNotifier.loadConfig(this.config);
		
		// This registers stuff to Minecraft (key bindings...)
		this.userControl.load();
		
		MAtmosConvLogger.info("Took " + timeMeasure.getSecondsAsString(3) + " seconds to setup MAtmos base.");
		
		if (this.config.getBoolean("start.enabled"))
		{
			initializeAndEnable();
		}
	}
	
	public void initializeAndEnable()
	{
		if (this.isInitialized)
			return;
		
		this.isInitialized = true;
		
		((OperatorCaster) op()).setFrameEnabled(true);
		
		IResourceManager resMan = Minecraft.getMinecraft().getResourceManager();
		if (resMan instanceof IReloadableResourceManager)
		{
			((IReloadableResourceManager) resMan).registerReloadListener(this);
		}
		
		reloadEverything();
		activate();
	}
	
	public void reloadEverything()
	{
		if (!this.isInitialized)
			return;
		
		this.expansionManager.deactivate();
		this.expansionManager.dispose();
		
		TimeStatistic stat = new TimeStatistic(Locale.ENGLISH);
		
		this.dataGatherer = new MAtDataGatherer(this);
		this.dataGatherer.load();
		this.expansionManager.setData(this.dataGatherer.getData());
		this.expansionManager.loadExpansions();
		
		MAtmosConvLogger.info("Expansions loaded (" + stat.getSecondsAsString(1) + "s).");
	}
	
	@Override
	public void activate()
	{
		if (!this.isInitialized)
			return;
		
		if (this.isActivated)
			return;
		
		this.isActivated = true;
		
		MAtmosConvLogger.fine("Loading...");
		this.expansionManager.activate();
		MAtmosConvLogger.fine("Loaded.");
	}
	
	@Override
	public void deactivate()
	{
		if (!this.isInitialized)
			return;
		
		if (!this.isActivated)
			return;
		
		this.isActivated = false;
		
		MAtmosConvLogger.fine("Stopping...");
		this.expansionManager.deactivate();
		MAtmosConvLogger.fine("Stopped.");
	}
	
	// Events
	
	@Override
	public void onFrame(float semi)
	{
		if (!this.isActivated)
			return;
		
		this.expansionManager.onFrame(semi);
		this.userControl.onFrame(semi);
	}
	
	@Override
	public void onTick()
	{
		this.userControl.onTick();
		if (this.isActivated)
		{
			this.dataGatherer.process();
			this.expansionManager.onTick();
		}
		
		if (!this.hasFirstTickPassed)
		{
			this.hasFirstTickPassed = true;
			this.updateNotifier.attempt();
		}
	}
	
	@Override
	public void dispose()
	{
	}
	
	@Override
	public void interrupt()
	{
		this.expansionManager.interrupt();
	}
	
	@Override
	public void onResourceManagerReload(IResourceManager var1)
	{
		MAtmosConvLogger.warning("ResourceManager has changed. Unintended side-effects may happen.");
		
		interrupt();
		
		// Initiate hot reload
		if (this.isActivated)
		{
			deactivate();
			reloadEverything();
			activate();
		}
		else
		{
			reloadEverything();
		}
	}
	
	public Map<String, Expansion> getExpansionList()
	{
		return this.expansionManager.getExpansions();
	}
	
	public boolean isInitialized()
	{
		return this.isInitialized;
	}
	
	@Override
	public boolean isActivated()
	{
		return this.isActivated;
	}
	
	@Override
	public Identity getIdentity()
	{
		return this.identity;
	}
	
	@Override
	public Chatter getChatter()
	{
		return this.chatter;
	}
	
	@Override
	public ConfigProperty getConfig()
	{
		return this.config;
	}
	
	@Override
	public void saveConfig()
	{
		// If there were changes...
		if (this.config.commit())
		{
			MAtmosConvLogger.info("Saving configuration...");
			
			// Write changes on disk.
			this.config.save();
		}
	}
	
	@Override
	public SoundManager getSoundManager()
	{
		try
		{
			return (SoundManager) util().getPrivate(Minecraft.getMinecraft().getSoundHandler(), "getSoundManager");
		}
		catch (PrivateAccessException e)
		{
			throw new RuntimeException();
		}
	}
	
	@Override
	public SoundSystem getSoundSystem()
	{
		try
		{
			return (SoundSystem) util().getPrivate(getSoundManager(), "getSoundSystem");
		}
		catch (PrivateAccessException e)
		{
			throw new RuntimeException();
		}
	}
	
	public VolumeUpdatable getGlobalVolumeControl()
	{
		return this.expansionManager;
	}
	
	public void synchronize()
	{
		this.expansionManager.synchronize();
	}
	
	public void saveExpansions()
	{
		this.expansionManager.saveConfig();
	}
}
