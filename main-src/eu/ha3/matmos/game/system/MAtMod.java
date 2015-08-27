package eu.ha3.matmos.game.system;

import com.google.common.base.Optional;
import eu.ha3.easy.StopWatchStatistic;
import eu.ha3.easy.TimeStatistic;
import eu.ha3.matmos.expansions.Expansion;
import eu.ha3.matmos.expansions.Stable;
import eu.ha3.matmos.expansions.volume.VolumeUpdatable;
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
import eu.ha3.mc.quick.chat.Chatter;
import eu.ha3.mc.quick.update.NotifiableHaddon;
import eu.ha3.mc.quick.update.UpdateNotifier;
import eu.ha3.util.property.simple.ConfigProperty;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.audio.SoundManager;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumChatFormatting;
import paulscode.sound.SoundSystem;

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
	protected final Identity identity = new HaddonIdentity(this.NAME, this.VERSION, this.FOR, this.ADDRESS).setPrefix("r");
	
	// NotifiableHaddon and UpdateNotifier
	private final ConfigProperty config = new ConfigProperty();
	private final Chatter chatter = new Chatter(this, this.NAME + ": ");
	private final UpdateNotifier updateNotifier = new UpdateNotifier(
		this, "http://q.mc.ha3.eu/query/matmos-main-version-vn.json?ver=%d");
	
	// State
	private boolean isListenerInstalled; 
	private Optional<Simulacrum> simulacrum = Optional.absent();
	private boolean isUnderwaterMode;
	
	// Components
	private UserControl userControl;
	
	// Use once
	private boolean hasFirstTickPassed;
	
	// Debug
	private StopWatchStatistic timeStat = new StopWatchStatistic();
	
	// Debug queue
	private Object queueLock = new Object();
	private List<Runnable> queue = new ArrayList<Runnable>();
	private boolean hasResourcePacks_FixMe;
	
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
		((OperatorCaster) op()).setFrameEnabled(true);
		
		TimeStatistic timeMeasure = new TimeStatistic(Locale.ENGLISH);
		this.userControl = new UserControl(this);

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
		
		resetAmbientVolume();
		
		this.updateNotifier.loadConfig(this.config);
		
		// This registers stuff to Minecraft (key bindings...)
		this.userControl.load();
		
		MAtLog.info("Took " + timeMeasure.getSecondsAsString(3) + " seconds to setup MAtmos base.");
		
		if (this.config.getBoolean("start.enabled"))
		{
			start();
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
	
	public void start()
	{
		if (!this.isListenerInstalled)
		{
			this.isListenerInstalled = true;

			IResourceManager resMan = Minecraft.getMinecraft().getResourceManager();
			if (resMan instanceof IReloadableResourceManager)
			{
				((IReloadableResourceManager) resMan).registerReloadListener(this);
			}
		}

		refresh();
	}

	public void refresh()
	{
		deactivate();
		activate();
	}
	
	public boolean isActivated()
	{
		return this.simulacrum.isPresent();
	}

	@Override
	public void activate()
	{
		if (isActivated())
			return;
		
		
		MAtLog.fine("Loading...");
		this.simulacrum = Optional.of(new Simulacrum(this));
		MAtLog.fine("Loaded.");
	}
	
	@Override
	public void deactivate()
	{
		if (!isActivated())
			return;

		MAtLog.fine("Stopping...");
		this.simulacrum.get().dispose();
		this.simulacrum = Optional.absent();
		MAtLog.fine("Stopped.");
	}
	
	// Events
	
	@Override
	public void onFrame(float semi)
	{
		if (!isActivated())
			return;
		
		this.simulacrum.get().onFrame(semi);
		this.userControl.onFrame(semi);
	}
	
	@Override
	public void onTick()
	{
		this.userControl.onTick();
		if (this.isActivated())
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
			this.simulacrum.get().onTick();
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
                        EnumChatFormatting.RED,
                        "You are using an ",
                        EnumChatFormatting.YELLOW,
                        "Unofficial Beta",
                        EnumChatFormatting.RED,
                        " version of MAtmos.");

				getChatter().printChatShort("By using this version, you understand that this mod isn't intended for " +
                        "actual game sessions, MAtmos may not work, might crash, the sound ambience is incomplete, etc.");

				getChatter().printChatShort("Use at your own risk. Please check regularly for updates and resource pack updates.");
			}
			
			if (isDebugMode())
			{
				getChatter().printChat(EnumChatFormatting.GOLD, "Developer mode is enabled in the Advanced options.");
				getChatter().printChatShort("This affects performance. Your game may run slower.");
			}
			
			if (!this.simulacrum.get().hasResourcePacks())
			{
				this.hasResourcePacks_FixMe = true;
				if (this.simulacrum.get().hasDisabledResourcePacks())
				{
					this.chatter.printChat(EnumChatFormatting.RED, "Resource Pack not enabled yet!");
					this.chatter.printChatShort(EnumChatFormatting.WHITE, "You need to activate \"MAtmos Resource Pack\" in the Minecraft Options menu for it to run.");
				}
				else
				{
					this.chatter.printChat(EnumChatFormatting.RED, "Resource Pack missing from resourcepacks/!");
					this.chatter.printChatShort(EnumChatFormatting.WHITE,"You may have forgotten to put the Resource Pack file into your resourcepacks/ folder.");
				}
			}
		}

		if (this.isActivated())
		{
			if (this.hasResourcePacks_FixMe && this.simulacrum.get().hasResourcePacks())
			{
				this.hasResourcePacks_FixMe = false;
				this.chatter.printChat(EnumChatFormatting.GREEN, "It should work now!");
			}
		}
	}
	
	@Override
	public void dispose()
	{
		if (isActivated())
			this.simulacrum.get().dispose();
	}
	
	@Override
	public void interrupt()
	{
		if (isActivated())
			this.simulacrum.get().interruptBrutally();
	}
	
	@Override
	public void onResourceManagerReload(IResourceManager var1)
	{
		MAtLog.warning("ResourceManager has changed. Unintended side-effects may happen.");
		
		interrupt();
		
		// Initiate hot reload
		if (this.isActivated())
		{
			simulacrum.get().interruptBrutally();
			deactivate();
			activate();
		}
	}

	@SuppressWarnings("unchecked")
	public Map<String, Expansion> getExpansionList()
	{
		if (isActivated())
			return this.simulacrum.get().getExpansions();
		else
			return (Map<String, Expansion>) Collections.EMPTY_MAP;
	}
	
	public boolean isInitialized()
	{
		return this.isListenerInstalled;
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
		return this.simulacrum.get().getGlobalVolumeControl();
	}
	
	public boolean hasResourcePacksLoaded()
	{
		if (!isActivated())
			return false;

		return this.simulacrum.get().hasResourcePacks();
	}
	
	public boolean hasNonethelessResourcePacksInstalled()
	{
		if (!isActivated())
			return false;

		return this.simulacrum.get().hasDisabledResourcePacks();
	}
	
	public void synchronize()
	{
		if (isActivated())
			this.simulacrum.get().synchronize();
	}
	
	public void saveExpansions()
	{
		if (isActivated())
			this.simulacrum.get().saveConfig();
	}
	
	public VisualDebugger getVisualDebugger()
	{
		// UNCHECKED!
		return this.simulacrum.get().getVisualDebugger();
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
			getChatter().printChat(EnumChatFormatting.GOLD, "Dev/Editor mode enabled.");
			getChatter().printChatShort("Enabling this mode may cause Minecraft to run slower.");
		}
		else
		{
			getChatter().printChat(EnumChatFormatting.GOLD, "Dev/Editor mode disabled.");
		}
		refresh();
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

	public Optional<Expansion> getExpansionEffort(String expansionName)
	{
		if (!isActivated())
			return Optional.absent();

		if (!simulacrum.get().getExpansions().containsKey(expansionName))
			return Optional.absent();

		return Optional.of(simulacrum.get().getExpansions().get(expansionName));
	}
}
