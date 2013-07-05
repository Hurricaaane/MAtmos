package net.minecraft.src;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;
import java.util.logging.Level;

import eu.ha3.easy.TimeStatistic;
import eu.ha3.matmos.conv.ExpansionManager;
import eu.ha3.matmos.conv.MAtmosConvLogger;
import eu.ha3.matmos.engine.MAtmosLogger;
import eu.ha3.mc.convenience.Ha3Signal;
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
	final static public int VERSION = 24; // Remember to change the thing on mod_Matmos
	final static public String FOR = "1.5.2";
	
	private MAtModPhase phase;
	private ConfigProperty config;
	
	private ExpansionManager expansionManager;
	
	private Ha3SoundCommunicator sndComm;
	private MAtUserControl userControl;
	private MAtDataGatherer dataGatherer;
	private MAtSoundManagerMaster soundManagerMaster;
	private MAtUpdateNotifier updateNotifier;
	
	private boolean isFatalError;
	private boolean isRunning;
	
	private boolean firstTickPassed;
	private TimeStatistic timeStatistic;
	
	public MAtMod()
	{
		// This is the constructor, so don't do anything
		// related to Minecraft.
		
		// Haddon constructors don't have superclass constructor calls
		// for convenience, so nothing is initialized.
		
		this.phase = MAtModPhase.NOT_INITIALIZED;
		
		MAtmosLogger.LOGGER.setLevel(Level.OFF);
	}
	
	@Override
	public void onLoad()
	{
		// Look for installation errors (1)
		if (!new File(util().getMinecraftDir(), "matmos/").exists())
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
			new ExpansionManager(new File(util().getMinecraftDir(), "matmos/expansions_r25/"), new File(util()
				.getMinecraftDir(), "matmos/expansions_r12_userconfig/"));
		this.updateNotifier = new MAtUpdateNotifier(this);
		
		manager().hookFrameEvents(true);
		manager().hookTickEvents(true);
		
		// Create default configuration
		this.config = new ConfigProperty();
		this.config.setProperty("dump.enabled", true);
		this.config.setProperty("start.enabled", true);
		this.config.setProperty("reversed.controls", false);
		this.config.setProperty("sound.autopreview", true);
		this.config.setProperty("globalvolume.scale", 1f);
		this.config.setProperty("update_found.enabled", true);
		this.config.setProperty("update_found.version", MAtMod.VERSION);
		this.config.setProperty("update_found.display.remaining.value", 0);
		this.config.setProperty("update_found.display.count.value", 3);
		this.config.commit();
		
		// Load configuration from source
		try
		{
			this.config.setSource(new File(util().getMinecraftDir(), "matmos/userconfig.cfg").getCanonicalPath());
			this.config.load();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			throw new RuntimeException("Error caused config not to work: " + e.getMessage());
		}
		
		this.updateNotifier.loadConfig(this.config);
		createMaster();
		
		MAtmosConvLogger.info("Took " + this.timeStatistic.getSecondsAsString(3) + " seconds to setup MAtmos base.");
		
		//
		
		MAtmosConvLogger.info("Pre-loading.");
		
		// This registers stuff to Minecraft (key bindings...)
		this.userControl.load();
		
		this.phase = MAtModPhase.NOT_YET_ENABLED;
		if (this.config.getBoolean("start.enabled"))
		{
			initializeAndEnable();
		}
		
	}
	
	private void createMaster()
	{
		this.soundManagerMaster = new MAtSoundManagerMaster(this);
		this.soundManagerMaster.setVolume(this.config.getFloat("globalvolume.scale"));
	}
	
	public void initializeAndEnable()
	{
		if (this.phase != MAtModPhase.NOT_YET_ENABLED)
			return;
		
		this.timeStatistic = new TimeStatistic(Locale.ENGLISH);
		
		this.phase = MAtModPhase.CONSTRUCTING;
		
		MAtmosConvLogger.info("Constructing.");
		
		this.dataGatherer.load();
		this.expansionManager.setMaster(this.soundManagerMaster);
		this.expansionManager.setData(this.dataGatherer.getData());
		
		this.sndComm.load(new Ha3Signal() {
			@Override
			public void signal()
			{
				sndCommSuccess();
			}
		}, new Ha3Signal() {
			@Override
			public void signal()
			{
				sndCommFailed();
			}
		});
		
		this.expansionManager.loadExpansions();
		
		// XXX: not reloading resources anymore!!!
		//TimeStatistic stat = new TimeStatistic();
		//MAtmosConvLogger.info("Loading resources...");
		//new MAtResourceReloader(this).reloadResources();
		//MAtmosConvLogger.info("Took " + stat.getSecondsAsString(3) + " seconds to load resources");
		
		ResourceManager resMan = manager().getMinecraft().func_110442_L();
		if (resMan instanceof ReloadableResourceManager)
		{
			MAtmosConvLogger.info("Adding resource reloading listener");
			((ReloadableResourceManager) resMan).func_110542_a(this);
		}
		else
		{
			MAtmosConvLogger.warning("The base Resource Manager is not a reloadable instance. "
				+ "Unpredictable results will be caused by switching resource packs.");
		}
		
		this.phase = MAtModPhase.READY;
		MAtmosConvLogger.info("Ready.");
		
		startRunning();
		
		MAtmosConvLogger.info("Took " + this.timeStatistic.getSecondsAsString(3) + " seconds to enable MAtmos.");
		
	}
	
	private void sndCommSuccess()
	{
		MAtmosConvLogger.info("SoundCommunicator loaded (after " + this.timeStatistic.getSecondsAsString(3) + " s.).");
	}
	
	private void sndCommFailed()
	{
		this.phase = MAtModPhase.SOUNDCOMMUNICATOR_FAILURE;
		MAtmosConvLogger.severe("CRITICAL Error with SoundCommunicator (after "
			+ this.timeStatistic.getSecondsAsString(3) + " s.). Will not load.");
		
		this.isFatalError = true;
		
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
	
	public void reloadWhileRunning()
	{
		if (isReady() && isRunning())
		{
			// Set a NullObject to the Master to dispose of all streams
			this.expansionManager.setMaster(new MAtSoundManagerNullObject());
			
			// Stop the mod to clear all reserved streams
			stopRunning();
			
			// Sreate a new master and set it
			createMaster();
			this.expansionManager.setMaster(this.soundManagerMaster);
			
			// Restart the mod from scratch
			reloadAndStart();
		}
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
		
		createDataDump();
		
	}
	
	private void createDataDump()
	{
		if (!this.config.getBoolean("dump.enabled"))
			return;
		
		MAtmosConvLogger.fine("Dumping data.");
		
		try
		{
			File file = new File(util().getMinecraftDir(), "data_dump.xml");
			file.createNewFile();
			
			FileWriter fw = new FileWriter(file);
			fw.write(getDataGatherer().getData().createXML());
			fw.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
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
	
	public MAtSoundManagerMaster getSoundManagerMaster()
	{
		return this.soundManagerMaster;
		
	}
	
	public MAtDataGatherer getDataGatherer()
	{
		return this.dataGatherer;
		
	}
	
	// XXX Blatant design.
	public ExpansionManager getExpansionManager()
	{
		return this.expansionManager;
		
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
		
		if (this.sndComm.isUseable())
		{
			this.expansionManager.soundRoutine();
			this.soundManagerMaster.routine();
		}
		
		this.userControl.frameRoutine(semi);
		
	}
	
	@Override
	public void onTick()
	{
		// Inform the user of fatal errors and shut down MAtmos (2)
		if (this.isFatalError)
		{
			printChat(Ha3Utility.COLOR_YELLOW, "A fatal error has occured. MAtmos will not load.");
			if (!new File(util().getMinecraftDir(), "matmos/").exists())
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
		if (this.isRunning && this.sndComm.isUseable())
		{
			this.dataGatherer.tickRoutine();
			this.expansionManager.dataRoutine();
			
		}
		
		if (!this.firstTickPassed)
		{
			this.firstTickPassed = true;
			this.updateNotifier.attempt();
			
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
	
	public ConfigProperty getConfig()
	{
		return this.config;
		
	}
	
	// ResourceManagerReloadListener
	@Override
	public void func_110549_a(ResourceManager var1)
	{
		MAtmosConvLogger.info("Resource Manager needs to reload. Unpredictable effects");
		reloadWhileRunning();
	}
}
