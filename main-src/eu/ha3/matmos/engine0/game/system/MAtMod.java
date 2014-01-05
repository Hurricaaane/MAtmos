package eu.ha3.matmos.engine0.game.system;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;

import net.minecraft.client.settings.KeyBinding;
import eu.ha3.easy.TimeStatistic;
import eu.ha3.matmos.engine0.conv.CustomVolume;
import eu.ha3.matmos.engine0.conv.Expansion;
import eu.ha3.matmos.engine0.conv.ExpansionManager;
import eu.ha3.matmos.engine0.conv.MAtmosConvLogger;
import eu.ha3.matmos.engine0.game.data.MAtCatchAllRequirements;
import eu.ha3.matmos.engine0.game.data.MAtDataGatherer;
import eu.ha3.matmos.engine0.game.user.MAtUpdateNotifier;
import eu.ha3.matmos.engine0.game.user.MAtUserControl;
import eu.ha3.mc.haddon.OperatorCaster;
import eu.ha3.mc.haddon.implem.HaddonImpl;
import eu.ha3.mc.haddon.supporting.SupportsFrameEvents;
import eu.ha3.mc.haddon.supporting.SupportsKeyEvents;
import eu.ha3.mc.haddon.supporting.SupportsTickEvents;
import eu.ha3.mc.quick.ChatColorsSimple;
import eu.ha3.util.property.simple.ConfigProperty;

/* x-placeholder */

public class MAtMod extends HaddonImpl implements SupportsFrameEvents, SupportsTickEvents, SupportsKeyEvents
{
	final static public String MOD_RAW_NAME = "MAtmos";
	final static public int VERSION = 26; // Remember to change the thing on mod_Matmos
	final static public String FOR = "1.6.2";
	final static public String MOD_VERSIONNED_NAME = MOD_RAW_NAME + " r" + VERSION + " for " + FOR;
	final static public String ADDRESS = "http://matmos.ha3.eu";
	
	final static public MAtmosConvLogger LOGGER = new MAtmosConvLogger();
	
	//private File matmosFolder;
	
	private MAtModPhase phase;
	private ConfigProperty config;
	
	private ExpansionManager expansionManager;
	
	private boolean dataRoll;
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
		//this.matmosFolder = new File(util().getModsFolder(), "matmos/");
		this.chatter = new Chatter(this, MOD_RAW_NAME);
		
		// Required for the fatal error message to appear.
		((OperatorCaster) op()).setTickEnabled(true);
		
		// Look for installation errors
		/*if (!this.matmosFolder.exists())
		{
			this.isFatalError = true;
			return;
		}*/
		
		this.timeStatistic = new TimeStatistic(Locale.ENGLISH);
		this.userControl = new MAtUserControl(this);
		this.dataGatherer = new MAtDataGatherer(this);
		this.expansionManager =
			new ExpansionManager(new File(util().getModsFolder(), "matmos/expansions_r27_userconfig/"));
		this.updateNotifier = new MAtUpdateNotifier(this);
		
		((OperatorCaster) op()).setFrameEnabled(true);
		
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
		
		/*
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
		*/
		
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
	@Deprecated
	public void createDataDump(boolean force)
	{
		// Disabled functionnality due to arbitrary strings
		if (true)
			return;
		
		/*
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
			List<String> values = new ArrayList<String>();
			
			Sheet<String> sheet = this.dataGatherer.getData().getSheet(name);
			for (int i = 0; i < sheet.getSize(); i++)
			{
				// 1.7 DERAIL
				values.add(sheet.get(Integer.toString(i)));
			}
			sheets.put(name, values);
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
		*/
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
			this.chatter.printChat(ChatColorsSimple.COLOR_YELLOW, "A fatal error has occured. MAtmos will not load.");
			/*if (!new File(util().getModsFolder(), "matmos/").exists())
			{
				this.chatter.printChat(ChatColorsSimple.COLOR_WHITE, "Are you sure you installed MAtmos correctly?");
				this.chatter
					.printChat(
						ChatColorsSimple.COLOR_WHITE, "The folder at (.minecraft/)", ChatColorsSimple.COLOR_YELLOW,
						Minecraft.getMinecraft().mcDataDir.toURI().relativize(this.matmosFolder.toURI()).getPath(),
						ChatColorsSimple.COLOR_YELLOW,
						" was NOT found. This folder should exist on a normal installation.");
				
			}*/
			((OperatorCaster) op()).setTickEnabled(false);
			((OperatorCaster) op()).setFrameEnabled(false);
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
	/*
	public void onResourceManagerReload(Objectr var1) // ResourceManager
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
	*/
	
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
	
	public Chatter getChatter()
	{
		return this.chatter;
	}
	
	@Override
	public String getHaddonName()
	{
		return MAtMod.MOD_RAW_NAME;
	}
	
	@Override
	public String getHaddonVersion()
	{
		return "r" + MAtMod.VERSION + " for " + MAtMod.FOR;
	}
	
}
