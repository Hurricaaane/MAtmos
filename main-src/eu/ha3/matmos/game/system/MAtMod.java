package eu.ha3.matmos.game.system;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import net.minecraft.src.FolderResourcePack;
import net.minecraft.src.Ha3SoundCommunicator;
import net.minecraft.src.Ha3Utility;
import net.minecraft.src.HaddonImpl;
import net.minecraft.src.KeyBinding;
import net.minecraft.src.Minecraft;
import net.minecraft.src.ReloadableResourceManager;
import net.minecraft.src.ResourceManager;
import net.minecraft.src.ResourceManagerReloadListener;
import net.minecraft.src.ResourcePack;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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
import eu.ha3.mc.haddon.PrivateAccessException;
import eu.ha3.mc.haddon.SupportsFrameEvents;
import eu.ha3.mc.haddon.SupportsKeyEvents;
import eu.ha3.mc.haddon.SupportsTickEvents;
import eu.ha3.util.property.simple.ConfigProperty;

/*
            DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE
                    Version 2, December 2004

 Copyright (C) 2004 Sam Hocevar <sam@hocevar.net>

 Everyone is permitted to copy and distribute verbatim or modified
 copies of this license document, and changing it is allowed as long
 as the name is changed.

            DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE
   TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION

  0. You just DO WHAT THE FUCK YOU WANT TO.
 */

public class MAtMod extends HaddonImpl
	implements SupportsFrameEvents, SupportsTickEvents, SupportsKeyEvents, ResourceManagerReloadListener
{
	final static public MAtmosConvLogger LOGGER = new MAtmosConvLogger();
	final static public int VERSION = 26; // Remember to change the thing on mod_Matmos
	final static public String FOR = "1.6.2";
	
	private File matmosFolder;
	private File packsFolder;
	private String usingSet;
	
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
		this.matmosFolder = new File(util().getModsFolder(), "matmos/");
		
		// Look for installation errors (1)
		if (!this.matmosFolder.exists())
		{
			this.isFatalError = true;
			manager().hookTickEvents(true);
			return;
		}
		
		this.timeStatistic = new TimeStatistic(Locale.ENGLISH);
		
		this.sndComm = new Ha3SoundCommunicator(this, "MAtmos_");
		
		this.userControl = new MAtUserControl(this);
		this.dataGatherer = new MAtDataGatherer(this);
		this.expansionManager =
			new ExpansionManager("expansions_r25/", new File(
				util().getModsFolder(), "matmos/expansions_r25_userconfig/"), new MAtCacheRegistry());
		this.updateNotifier = new MAtUpdateNotifier(this);
		
		manager().hookFrameEvents(true);
		manager().hookTickEvents(true);
		
		// Create default configuration
		this.config = new ConfigProperty();
		this.config.setProperty("world.height", 256);
		this.config.setProperty("dump.sheets.enabled", false);
		this.config.setProperty("start.enabled", true);
		this.config.setProperty("reversed.controls", false);
		this.config.setProperty("sound.autopreview", true);
		this.config.setProperty("globalvolume.scale", 1f);
		this.config.setProperty("useroptions.altitudes.high", true);
		this.config.setProperty("useroptions.altitudes.low", true);
		this.config.setProperty("useroptions.biome.override", -1);
		this.config.setProperty("update_found.enabled", true);
		this.config.setProperty("update_found.version", MAtMod.VERSION);
		this.config.setProperty("update_found.display.remaining.value", 0);
		this.config.setProperty("update_found.display.count.value", 3);
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
		
		for (File file : new File(this.matmosFolder, "sets/").listFiles())
		{
			if (file.isDirectory()
				&& new File(file, "mat_set.json").exists() && new File(file, "autostart.token").exists()
				&& new File(file, "autostart.token").isFile())
			{
				MAtmosConvLogger.info("Found autostart token in valid expansion set: " + file.getName());
				
				new File(file, "autostart.token").delete();
				
				this.config.setProperty("totalconversion.name", file.getName());
				saveConfig();
			}
		}
		
		if (!this.config.getString("totalconversion.name").equals("default"))
		{
			this.usingSet = this.config.getString("totalconversion.name");
			this.packsFolder = new File(this.matmosFolder, "sets/" + this.usingSet + "/");
		}
		
		if (this.packsFolder == null || !this.packsFolder.exists() || !this.packsFolder.isDirectory())
		{
			this.usingSet = "default";
			this.packsFolder = new File(this.matmosFolder, "sets/default/");
			this.config.setProperty("totalconversion.name", "default");
		}
		
		this.expansionManager.setPacksFolder(this.packsFolder);
		try
		{
			@SuppressWarnings("unchecked")
			List<ResourcePack> resourcePacks =
				(List<ResourcePack>) util().getPrivateValueLiteral(Minecraft.class, Minecraft.getMinecraft(), "aq", 63);
			
			for (File file : this.packsFolder.listFiles())
			{
				if (file.isDirectory())
				{
					MAtmosConvLogger.info("Adding resource pack at " + file.getAbsolutePath());
					resourcePacks.add(new FolderResourcePack(file));
				}
			}
		}
		catch (PrivateAccessException e)
		{
			e.printStackTrace();
		}
		
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
	
	public File getMAtmosFolder()
	{
		return this.matmosFolder;
	}
	
	public String getLoadedSet()
	{
		return this.usingSet;
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
		//if (!this.config.getBoolean("dump.sheets.enabled"))
		//	return;
		
		if (!force && !isDumpReady())
		{
			if (this.config.getBoolean("dump.sheets.enabled"))
			{
				this.printChat("Warning: Data dumps requires Minecraft to be restarted, "
					+ "because data dumps must be already enabled when Minecraft starts to work.");
			}
			return;
		}
		
		if (force)
		{
			this.printChat(
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
		
		this.userControl.frameRoutine(semi);
	}
	
	@Override
	public void onTick()
	{
		// Inform the user of fatal errors and shut down MAtmos (2)
		if (this.isFatalError)
		{
			printChat(Ha3Utility.COLOR_YELLOW, "A fatal error has occured. MAtmos will not load.");
			if (!new File(util().getModsFolder(), "matmos/").exists())
			{
				printChat(Ha3Utility.COLOR_WHITE, "Are you sure you installed MAtmos correctly?");
				printChat(
					Ha3Utility.COLOR_WHITE, "The folder called ", Ha3Utility.COLOR_YELLOW, ".minecraft/matmos/",
					Ha3Utility.COLOR_YELLOW, " was NOT found. This folder should exist on a normal installation.");
				
			}
			manager().hookTickEvents(false);
			manager().hookFrameEvents(false);
			return;
			
		}
		
		this.userControl.tickRoutine();
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
	public void onReload(ResourceManager var1)
	{
		MAtmosConvLogger.warning("ResourceManager has changed. Unintended side-effects results may happen.");
		
		// Initiate hot reload
		if (isReady() && isRunning())
		{
			// Set a NullObject to all SoundManagers to dispose of all streams safely
			this.expansionManager.neutralizeSoundManagers();
			
			// Stop the mod to clear all reserved streams
			stopRunning();
			
			// Sreate a new master and set it
			createSoundManagerMaster();
			this.expansionManager.setMaster(this.soundManagerMaster);
			
			// Restart the mod from scratch
			reloadAndStart();
		}
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
	
	// Utility functions
	
	public void printChat(Object... args)
	{
		printChat(new Object[] { Ha3Utility.COLOR_WHITE, "MAtmos: " }, args);
	}
	
	public void printChatShort(Object... args)
	{
		printChat(new Object[] { Ha3Utility.COLOR_WHITE, "" }, args);
	}
	
	protected void printChat(final Object[] in, Object... args)
	{
		Object[] dest = new Object[in.length + args.length];
		System.arraycopy(in, 0, dest, 0, in.length);
		System.arraycopy(args, 0, dest, in.length, args.length);
		
		util().printChat(dest);
	}
	
}
