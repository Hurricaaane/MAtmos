package eu.ha3.matmos.game.system;

import eu.ha3.easy.StopWatchStatistic;
import eu.ha3.easy.TimeStatistic;
import eu.ha3.matmos.expansions.Expansion;
import eu.ha3.matmos.expansions.ExpansionManager;
import eu.ha3.matmos.expansions.Stable;
import eu.ha3.matmos.expansions.volume.VolumeUpdatable;
import eu.ha3.matmos.game.data.ModularDataGatherer;
import eu.ha3.matmos.game.user.UserControl;
import eu.ha3.matmos.game.user.VisualDebugger;
import eu.ha3.matmos.log.MAtLog;
import eu.ha3.matmos.pluggable.PluggableIntoMinecraft;
import eu.ha3.mc.haddon.Identity;
import eu.ha3.mc.haddon.OperatorCaster;
import eu.ha3.mc.haddon.PrivateAccessException;
import eu.ha3.mc.haddon.implem.HaddonIdentity;
import eu.ha3.mc.haddon.implem.HaddonImpl;
import eu.ha3.mc.haddon.supporting.SupportsFrameEvents;
import eu.ha3.mc.haddon.supporting.SupportsTickEvents;
import eu.ha3.mc.quick.chat.ChatColorsSimple;
import eu.ha3.mc.quick.chat.Chatter;
import eu.ha3.mc.quick.update.NotifiableHaddon;
import eu.ha3.mc.quick.update.UpdateNotifier;
import eu.ha3.util.property.simple.ConfigProperty;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.audio.SoundManager;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.entity.Entity;
import paulscode.sound.SoundSystem;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.*;

/* x-placeholder */

public class MAtMod extends HaddonImpl
	implements SupportsFrameEvents, SupportsTickEvents, NotifiableHaddon, IResourceManagerReloadListener,
	SoundAccessor, Stable
{
	private static final boolean _COMPILE_IS_UNSTABLE = true;
	
	// Identity
	protected final String NAME = "MAtmos-UnofficialBeta";
	protected final int VERSION = 29;
	protected final String FOR = "1.8";
	protected final String ADDRESS = "http://matmos.ha3.eu";
	protected final Date DATE = new Date(1394610076);
	protected final Identity identity = new HaddonIdentity(this.NAME, this.VERSION, this.FOR, this.ADDRESS);
	
	// NotifiableHaddon and UpdateNotifier
	private final ConfigProperty config = new ConfigProperty();
	private final Chatter chatter = new Chatter(this, this.NAME);
	private final UpdateNotifier updateNotifier = new UpdateNotifier(
		this, "http://q.mc.ha3.eu/query/matmos-main-version-vn.json?ver=%d");
	
	// State
	private boolean isInitialized;
	private boolean isActivated;
	private boolean isUnderwaterMode;
	
	// Components
	private ExpansionManager expansionManager;
	private UserControl userControl;
	private ModularDataGatherer dataGatherer;
	private VisualDebugger visualDebugger;
	
	// Use once
	private boolean hasFirstTickPassed;
	private boolean hasResourcePacks;
	private boolean hasDisabledResourcePacks;
	private boolean hasResourcePacks_FixMe;
	
	// Debug
	private StopWatchStatistic timeStat = new StopWatchStatistic();
	
	// Debug queue
	private Object queueLock = new Object();
	private List<Runnable> queue = new ArrayList<Runnable>();
	
	public MAtMod()
	{
		MAtLog.setRefinedness(MAtLog.INFO);
	}
	
	@Override
	public void onLoad()
	{
		util().registerPrivateGetter("getSoundManager", SoundHandler.class, 5, "field_147694_f", "f");
		util().registerPrivateGetter("getSoundSystem", SoundManager.class, 4, "field_148620_e", "e");

        // dag edit - update to 1.8 obf stuff
		util().registerPrivateGetter("isInWeb", Entity.class, -1, "isInWeb", "field_70134_J", "H");
		
		((OperatorCaster) op()).setTickEnabled(true);
		
		TimeStatistic timeMeasure = new TimeStatistic(Locale.ENGLISH);
		this.userControl = new UserControl(this);
		this.expansionManager =
			new ExpansionManager(new File(util().getModsFolder(), "matmos/expansions_r29_userconfig/"), this);
		
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
		this.config.setProperty("debug.mode", 0);
		this.config.setProperty("minecraftsound.ambient.volume", 1f);
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
		
		this.expansionManager.setVolumeAndUpdate(this.config.getFloat("globalvolume.scale"));
		resetAmbientVolume();
		
		this.updateNotifier.loadConfig(this.config);
		
		// This registers stuff to Minecraft (key bindings...)
		this.userControl.load();
		
		MAtLog.info("Took " + timeMeasure.getSecondsAsString(3) + " seconds to setup MAtmos base.");
		
		if (this.config.getBoolean("start.enabled"))
		{
			initializeAndEnable();
		}
	}
	
	private void resetAmbientVolume()
	{
		// For some reason it has to be set twice to validate it (???!)
		Minecraft.getMinecraft().gameSettings.setSoundLevel(
			SoundCategory.AMBIENT, this.config.getFloat("minecraftsound.ambient.volume"));
		Minecraft.getMinecraft().gameSettings.setSoundLevel(
			SoundCategory.AMBIENT, this.config.getFloat("minecraftsound.ambient.volume"));
	}
	
	private void overrideAmbientVolume()
	{
		if (this.config.getFloat("minecraftsound.ambient.volume") <= 0f)
			return;
		
		// For some reason it has to be set twice to validate it (???!)
		Minecraft.getMinecraft().gameSettings.setSoundLevel(SoundCategory.AMBIENT, 0.01f);
		Minecraft.getMinecraft().gameSettings.setSoundLevel(SoundCategory.AMBIENT, 0.01f);
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
		
		this.dataGatherer = new ModularDataGatherer(this);
		this.dataGatherer.load();
		this.visualDebugger = new VisualDebugger(this, this.dataGatherer);
		this.expansionManager.setData(this.dataGatherer.getData());
		this.expansionManager.setCollector(this.dataGatherer);
		this.expansionManager.loadExpansions();
		
		this.hasResourcePacks = true;
		if (this.expansionManager.getExpansions().size() == 0)
		{
			MAtResourcePackDealer dealer = new MAtResourcePackDealer();
			if (dealer.findResourcePacks().size() == 0)
			{
				this.hasResourcePacks = false;
				this.hasDisabledResourcePacks = dealer.findDisabledResourcePacks().size() > 0;
			}
		}
		
		MAtLog.info("Expansions loaded (" + stat.getSecondsAsString(1) + "s).");
	}
	
	@Override
	public void activate()
	{
		if (!this.isInitialized)
			return;
		
		if (this.isActivated)
			return;
		
		this.isActivated = true;
		
		MAtLog.fine("Loading...");
		this.expansionManager.activate();
		MAtLog.fine("Loaded.");
	}
	
	@Override
	public void deactivate()
	{
		if (!this.isInitialized)
			return;
		
		if (!this.isActivated)
			return;
		
		this.isActivated = false;
		
		MAtLog.fine("Stopping...");
		this.expansionManager.deactivate();
		MAtLog.fine("Stopped.");
	}
	
	// Events
	
	@Override
	public void onFrame(float semi)
	{
		if (!this.isActivated)
			return;
		
		this.expansionManager.onFrame(semi);
		this.userControl.onFrame(semi);
		this.visualDebugger.onFrame(semi);
		
	}
	
	@Override
	public void onTick()
	{
		this.userControl.onTick();
		if (this.isActivated)
		{
			if (!this.queue.isEmpty())
			{
				synchronized (this.queueLock)
				{
					while (!this.queue.isEmpty())
					{
						this.queue.remove(0).run();
					}
				}
			}
			
			this.timeStat.reset();
			this.dataGatherer.process();
			this.expansionManager.onTick();
			this.timeStat.stop();
			
			if (MAtmosUtility.isUnderwaterAnyGamemode())
			{
				if (!this.isUnderwaterMode)
				{
					this.isUnderwaterMode = true;
					overrideAmbientVolume();
				}
			}
			else
			{
				if (this.isUnderwaterMode)
				{
					this.isUnderwaterMode = false;
					resetAmbientVolume();
				}
			}
		}
		else
		{
			if (this.isUnderwaterMode)
			{
				this.isUnderwaterMode = false;
				resetAmbientVolume();
			}
		}
		
		if (!this.hasFirstTickPassed)
		{
			this.hasFirstTickPassed = true;
			
			this.updateNotifier.attempt();
			
			if (MAtMod._COMPILE_IS_UNSTABLE)
			{
				getChatter().printChatShort("http://matmos.ha3.eu/");
				getChatter().printChat(
					ChatColorsSimple.COLOR_RED
						+ "You are using an " + ChatColorsSimple.COLOR_YELLOW + "Unofficial Beta" + ChatColorsSimple.COLOR_RED
						+ " version of MAtmos.");
				getChatter().printChatShort(
					"By using this version, you understand that this mod isn't intended for"
						+ " actual game sessions, MAtmos may not work, might crash, the sound"
						+ " ambience is incomplete, etc.");
				getChatter().printChatShort(
					"Use at your own risk. " + "Please check regularly for updates and resource pack updates.");
			}
			
			if (isDebugMode())
			{
				getChatter().printChat(
					ChatColorsSimple.COLOR_GOLD + "Developer mode is enabled in the Advanced options.");
				getChatter().printChatShort("This affects performance. Your game may run slower.");
			}
			
			if (!this.hasResourcePacks)
			{
				this.hasResourcePacks_FixMe = true;
				if (this.hasDisabledResourcePacks)
				{
					this.chatter.printChat(ChatColorsSimple.COLOR_RED, "Resource Pack not enabled yet!");
					this.chatter.printChatShort(ChatColorsSimple.COLOR_WHITE, "You need to activate "
						+ "\"MAtmos Resource Pack\" in the Minecraft Options menu for it to run.");
				}
				else
				{
					this.chatter.printChat(ChatColorsSimple.COLOR_RED, "Resource Pack missing from resourcepacks/!");
					this.chatter.printChatShort(
						ChatColorsSimple.COLOR_WHITE,
						"You may have forgotten to put the Resource Pack file into your resourcepacks/ folder.");
				}
			}
		}
		if (this.hasResourcePacks_FixMe && this.hasResourcePacks)
		{
			this.hasResourcePacks_FixMe = false;
			this.chatter.printChat(ChatColorsSimple.COLOR_BRIGHTGREEN, "It should work now!");
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
		MAtLog.warning("ResourceManager has changed. Unintended side-effects may happen.");
		
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
			MAtLog.info("Saving configuration...");
			
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
	
	public boolean hasResourcePacksLoaded()
	{
		return this.hasResourcePacks;
	}
	
	public boolean hasNonethelessResourcePacksInstalled()
	{
		return this.hasDisabledResourcePacks;
	}
	
	public void synchronize()
	{
		this.expansionManager.synchronize();
	}
	
	public void saveExpansions()
	{
		this.expansionManager.saveConfig();
	}
	
	public VisualDebugger getVisualDebugger()
	{
		return this.visualDebugger;
	}
	
	public StopWatchStatistic getLag()
	{
		return this.timeStat;
	}
	
	public void queueForNextTick(Runnable runnable)
	{
		synchronized (this.queueLock)
		{
			this.queue.add(runnable);
		}
	}
	
	public boolean isDebugMode()
	{
		return this.config.getInteger("debug.mode") > 0;
	}
	
	public void changedDebugMode()
	{
		if (isDebugMode())
		{
			getChatter().printChat(ChatColorsSimple.COLOR_GOLD + "Dev/Editor mode enabled.");
			getChatter().printChatShort("Enabling this mode may cause Minecraft to run slower.");
		}
		else
		{
			getChatter().printChat(ChatColorsSimple.COLOR_GOLD + "Dev/Editor mode disabled.");
		}
		
		this.dataGatherer.forceRecomputeModuleStack_debugModeChanged();
	}
	
	public boolean isEditorAvailable()
	{
		try
		{
			return Class.forName("eu.ha3.matmos.editor.EditorMaster", false, this.getClass().getClassLoader()) != null;
		}
		catch (Exception e)
		{
			return false;
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Runnable instantiateRunnableEditor(PluggableIntoMinecraft pluggable)
	{
		try
		{
			Class editorClass =
				Class.forName("eu.ha3.matmos.editor.EditorMaster", false, this.getClass().getClassLoader());
			Constructor ctor = editorClass.getDeclaredConstructor(PluggableIntoMinecraft.class);
			ctor.setAccessible(true);
			
			return (Runnable) ctor.newInstance(pluggable);
		}
		catch (Exception e)
		{
			return null;
		}
	}
}
