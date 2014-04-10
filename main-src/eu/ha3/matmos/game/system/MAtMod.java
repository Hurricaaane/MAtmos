package eu.ha3.matmos.game.system;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import paulscode.sound.SoundSystem;
import net.minecraft.src.FolderResourcePack;
import eu.ha3.mc.haddon.implem.Ha3SoundCommunicator;
import eu.ha3.mc.haddon.implem.Ha3Utility;
import eu.ha3.mc.haddon.implem.HaddonImpl;
import eu.ha3.mc.haddon.implem.HaddonIdentity;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityLivingBase;
import net.minecraft.src.EntityPlayerSP;
import net.minecraft.src.KeyBinding;
import net.minecraft.src.Minecraft;
import net.minecraft.src.ReloadableResourceManager;
import net.minecraft.src.ResourceManager;
import net.minecraft.src.ResourceManagerReloadListener;
import net.minecraft.src.ResourcePack;
import net.minecraft.src.SoundManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mumfrey.liteloader.core.LiteLoader;

import eu.ha3.easy.TimeStatistic;
import eu.ha3.matmos.conv.CustomVolume;
import eu.ha3.matmos.conv.Expansion;
import eu.ha3.matmos.conv.ExpansionManager;
import eu.ha3.matmos.conv.MAtmosConvLogger;
import eu.ha3.matmos.engine.interfaces.Sheet;
import eu.ha3.matmos.game.data.MAtCatchAllRequirements;
import eu.ha3.matmos.game.data.MAtDataGatherer;
import eu.ha3.matmos.game.user.MAtUpdateNotifier;
import eu.ha3.matmos.game.user.MAtUserControl;
import eu.ha3.mc.haddon.Identity;
import eu.ha3.mc.haddon.PrivateAccessException;
import eu.ha3.mc.haddon.SupportsFrameEvents;
import eu.ha3.mc.haddon.SupportsKeyEvents;
import eu.ha3.mc.haddon.SupportsTickEvents;
import eu.ha3.mc.haddon.OperatorCaster;
import eu.ha3.util.property.simple.ConfigProperty;

/* x-placeholder-wtfplv2 */

public class MAtMod extends HaddonImpl
	implements SupportsFrameEvents, SupportsTickEvents, SupportsKeyEvents, ResourceManagerReloadListener
{
	final static public int VERSION = 26; // Remember to change the thing on mod_Matmos
	final static public String FOR = "1.6.4";
	final static public String MOD_RAW_NAME = "MAtmos";
	final static public String MOD_VERSIONNED_NAME = MOD_RAW_NAME + " r" + VERSION + " for " + FOR;
	protected final String ADDRESS = "http://matmos.ha3.eu";
    protected final Identity identity = new HaddonIdentity(this.MOD_RAW_NAME, this.VERSION, this.FOR, this.ADDRESS);
	
	final static public MAtmosConvLogger LOGGER = new MAtmosConvLogger();
	
	private File matmosFolder;
	private File packsFolder;
//	private String usingTotalConversion;
	
	private MAtModPhase phase;
	private ConfigProperty config;
	
	private ExpansionManager expansionManager;
	
	private boolean dataRoll;
	private Ha3SoundCommunicator sndComm;
	private MAtUserControl userControl;
	private MAtDataGatherer dataGatherer;
	private MAtSoundManagerMaster soundManagerMaster;
	private MAtUpdateNotifier updateNotifier;
	
	private boolean isFatalError;
	private boolean isRunning;
	
	private boolean firstTickPassed;
	private TimeStatistic timeStatistic;
	
	private boolean dumpReady = false;
	
	private Chatter chatter;
	
	public MAtMod()
	{
		// This is the constructor, so don't do anything
		// related to Minecraft.
		
		// Haddon constructors don't have superclass constructor calls
		// for convenience, so nothing is initialized.
		
		this.phase = MAtModPhase.NOT_INITIALIZED;
		
		MAtmosConvLogger.setRefinedness(MAtmosConvLogger.INFO);
		
	}
	
	@Override
	public void onLoad()
	{      
        util().registerPrivateGetter("currentServerData", Minecraft.class, -1, "currentServerData", "field_71422_O", "M");

        util().registerPrivateGetter("sndSystem", SoundManager.class, -1, "sndSystem", "field_77381_a", "b");
        util().registerPrivateGetter("soundPoolSounds", SoundManager.class, -1, "soundPoolSounds", "field_77379_b", "d");

        util().registerPrivateGetter("isJumping", EntityPlayerSP.class, -1, "isJumping", "field_70703_bu", "bd");
        util().registerPrivateGetter("isInWeb", Entity.class, -1, "isInWeb", "field_70134_J", "K");

        this.chatter = new Chatter(this, MOD_RAW_NAME);
        ((OperatorCaster) op()).setTickEnabled(true);
        ((OperatorCaster) op()).setFrameEnabled(true);
        
        this.matmosFolder = new File(util().getModsFolder(), "matmos/");
		// Look for installation errors
		if (!this.matmosFolder.exists())
		{
			this.isFatalError = true;
			return;
		}

    	this.packsFolder = new File(this.matmosFolder, "packs/");
		// Look for installation errors
		if (!this.packsFolder.exists())
		{
			this.isFatalError = true;
			return;
		}
		
		this.timeStatistic = new TimeStatistic(Locale.ENGLISH);
		this.sndComm = new Ha3SoundCommunicator(this, "MAtmos_");
		this.userControl = new MAtUserControl(this);
		this.dataGatherer = new MAtDataGatherer(this);
		this.expansionManager =
			new ExpansionManager("expansions_r26/",
					new File(this.matmosFolder, "expansions_r26_userconfig/"),
					this.packsFolder, new MAtCacheRegistry());
		this.updateNotifier = new MAtUpdateNotifier(this);
		
		// Create default configuration
		this.config = new ConfigProperty();
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
		this.config.setProperty("update_found.enabled", true);
		this.config.setProperty("update_found.version", MAtMod.VERSION);
		this.config.setProperty("update_found.display.remaining.value", 0);
		this.config.setProperty("update_found.display.count.value", 3);
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

		appendResourcePacks();
		createSoundManagerMaster();
		
		// This registers stuff to Minecraft (key bindings...)
		this.userControl.load();
		
		MAtmosConvLogger.info("Took " + this.timeStatistic.getSecondsAsString(3) + " seconds to setup MAtmos base.");
		
		this.phase = MAtModPhase.NOT_YET_ENABLED;
		if (this.config.getBoolean("start.enabled"))
		{
			initializeAndEnable();
		}
		
	}
	
	private void appendResourcePacks()
	{
		for (File file : this.packsFolder.listFiles())
		{
			if (file.isDirectory())
			{
				MAtmosConvLogger.info("Adding resource pack at " + file.getAbsolutePath());
				LiteLoader.getInstance().registerModResourcePack(new FolderResourcePack(file));
			}
		}
		Minecraft.getMinecraft().refreshResources();
	}
	
	public void initializeAndEnable()
	{
		if (this.phase != MAtModPhase.NOT_YET_ENABLED)
			return;
		
		this.phase = MAtModPhase.CONSTRUCTING;
		
		this.timeStatistic = new TimeStatistic(Locale.ENGLISH);
		
		MAtmosConvLogger.info("Constructing.");
		
		if (!this.config.getBoolean("dump.sheets.enabled"))
		{
			this.dataGatherer.load(this.expansionManager.getCollation());
		}
		else
		{
			MAtCatchAllRequirements catchall = new MAtCatchAllRequirements();
			this.dataGatherer.load(catchall);
			catchall.setData(this.dataGatherer.getData());
			
			this.dumpReady = true;
		}
		
		this.expansionManager.setMaster(this.soundManagerMaster);
		this.expansionManager.setData(this.dataGatherer.getData());
		
		this.expansionManager.loadExpansions();
		
		ResourceManager resMan = Minecraft.getMinecraft().getResourceManager();
		if (resMan instanceof ReloadableResourceManager)
		{
			MAtmosConvLogger.info("Adding resource reloading listener");
			((ReloadableResourceManager) resMan).registerReloadListener(this);
		}
		else
		{
			MAtmosConvLogger.severe("The base Resource Manager is not a reloadable instance. "
				+ "Unpredictable results will be caused by switching resource packs.");
		}
		
		this.phase = MAtModPhase.READY;
		MAtmosConvLogger.info("Ready.");
		
		startRunning();
		
		MAtmosConvLogger.info("Took " + this.timeStatistic.getSecondsAsString(3) + " seconds to enable MAtmos.");
		
	}
	
	private void createSoundManagerMaster()
	{
		this.soundManagerMaster = new MAtSoundManagerMaster(this);
		this.soundManagerMaster.setVolume(this.config.getFloat("globalvolume.scale"));
	}
	
	public void reloadAndStart()
	{
		if (!isReady())
			return;
		
		if (this.isRunning)
			return;
		
		// prevent the expansions from running before the thread could even start
		this.expansionManager.clearExpansions();
		
		TimeStatistic stat = new TimeStatistic(Locale.ENGLISH);
		MAtMod.this.expansionManager.loadExpansions();
		MAtmosConvLogger.info("Expansions loaded (" + stat.getSecondsAsString(1) + "s).");
		
		startRunning();
	}
	
	public void startRunning()
	{
		if (!isReady())
			return;
		
		if (this.isRunning)
			return;
		
		this.isRunning = true;
		
		MAtmosConvLogger.fine("Loading...");
		this.expansionManager.activate();
		MAtmosConvLogger.fine("Loaded.");
	}
	
	public void stopRunning()
	{
		if (!isReady())
			return;
		
		if (!this.isRunning)
			return;
		
		this.isRunning = false;
		
		MAtmosConvLogger.fine("Stopping...");
		this.expansionManager.deactivate();
		MAtmosConvLogger.fine("Stopped.");
		
		createDataDump(false);
	}
	
	public boolean isDumpReady()
	{
		return this.dumpReady;
	}
	
	@SuppressWarnings("unchecked")
	public void createDataDump(boolean force)
	{
		if (!force && !isDumpReady())
		{
			if (this.config.getBoolean("dump.sheets.enabled"))
			{
				this.chatter.printChat("Warning: Data dumps requires Minecraft to be restarted, "
					+ "because data dumps must be already enabled when Minecraft starts to work.");
			}
			return;
		}
		
		if (force)
		{
			this.chatter.printChat(
				Ha3Utility.COLOR_RED, "Warning: Generating PARTIAL data dumps will normally yield the value 0 "
					+ "for every unused data by the currently loaded expansions."
					+ " Only use PARTIAL data dumps to debug errors!");
		}
		
		MAtmosConvLogger.fine("Dumping data.");
		
		@SuppressWarnings("rawtypes")
		Map toJsonify = new LinkedHashMap();
		@SuppressWarnings("rawtypes")
		Map sheets = new LinkedHashMap();
		for (String name : this.dataGatherer.getData().getSheetNames())
		{
			List<Integer> integers = new ArrayList<Integer>();
			
			Sheet<Integer> sheet = this.dataGatherer.getData().getSheet(name);
			for (int i = 0; i < sheet.getSize(); i++)
			{
				integers.add(sheet.get(i));
			}
			sheets.put(name, integers);
		}
		toJsonify.put("sheets", sheets);
		
		Gson gson = new GsonBuilder().create();
		String jason = gson.toJson(toJsonify);
		
		try
		{
			File file = new File(this.matmosFolder, "matmos_dump.json");
			file.createNewFile();
			
			FileWriter fw = new FileWriter(file);
			fw.write(jason);
			fw.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
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
	
	// Events
	
	@Override
	public void onKey(KeyBinding event)
	{
		this.userControl.communicateKeyBindingEvent(event);
	}
	
	@Override
	public void onFrame(float semi)
	{
		if (this.isFatalError)
			return;
		
		if (!this.isRunning)
			return;
		
		this.expansionManager.soundRoutine();
		this.soundManagerMaster.routine();
		
		this.userControl.onFrame(semi);
	}
	
	@Override
	public void onTick()
	{
		// Inform the user of fatal errors and shut down MAtmos (2)
		if (this.isFatalError)
		{
			this.chatter.printChat(Ha3Utility.COLOR_YELLOW, "A fatal error has occured. MAtmos will not load.");
			if (!new File(util().getModsFolder(), "matmos/").exists())
			{
				this.chatter.printChat(Ha3Utility.COLOR_WHITE, "Are you sure you installed MAtmos correctly?");
				this.chatter.printChat(
					Ha3Utility.COLOR_WHITE, "The folder at (.minecraft/)", Ha3Utility.COLOR_YELLOW,
					Minecraft.getMinecraft().mcDataDir.toURI().relativize(this.matmosFolder.toURI()).getPath(),
					Ha3Utility.COLOR_YELLOW, " was NOT found. This folder should exist on a normal installation.");
				
			}
	        ((OperatorCaster) op()).setTickEnabled(false);
			return;
		}
		
		this.userControl.onTick();
		if (this.isRunning)
		{
			if (!this.dataRoll)
			{
				this.dataRoll = true;
				this.dataGatherer.dataRoll();
			}
			
			this.dataGatherer.tickRoutine();
			this.expansionManager.dataRoutine();
		}
		
		if (!this.firstTickPassed)
		{
			this.firstTickPassed = true;
			this.updateNotifier.attempt();
		}
	}

	// ResourceManagerReloadListener
	@Override
	public void onResourceManagerReload(ResourceManager var1)
	{
		MAtmosConvLogger.warning("ResourceManager has changed. Unintended side-effects results may happen.");
		
		// Initiate hot reload
		if (isReady() && isRunning())
		{
			// Set a NullObject to all SoundManagers to dispose of all streams safely
			this.expansionManager.neutralizeSoundManagers();
			
			// Stop the mod to clear all reserved streams
			stopRunning();
			
			// Create a new master and set it
			createSoundManagerMaster();
			this.expansionManager.setMaster(this.soundManagerMaster);
			
			// Restart the mod from scratch
			reloadAndStart();
		}
	}
	
	// Status getters
	
	public MAtModPhase getPhase()
	{
		return this.phase;
	}
	
	public boolean isFatalError()
	{
		return this.isFatalError;
	}
	
	public boolean isReady()
	{
		return this.phase == MAtModPhase.READY;
	}
	
	public boolean isRunning()
	{
		return this.isRunning;
	}
	
	// Getters
	
	public ConfigProperty getConfig()
	{
		return this.config;
	}
	
	public CustomVolume getGlobalVolumeControl()
	{
		return this.soundManagerMaster;
	}
	
	public Map<String, Expansion> getExpansionList()
	{
		return this.expansionManager.getExpansions();
	}
	
	public Ha3SoundCommunicator getSoundCommunicator()
	{
		return this.sndComm;
	}
	
	@Override
	public Identity getIdentity()
	{
		return this.identity;
	}
	
	public Chatter getChatter()
	{
		return this.chatter;
	}
	
}
