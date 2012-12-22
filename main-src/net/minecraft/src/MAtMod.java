package net.minecraft.src;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Locale;
import java.util.logging.Level;

import net.minecraft.client.Minecraft;
import eu.ha3.easy.TimeStatistic;
import eu.ha3.matmos.engine.MAtmosLogger;
import eu.ha3.mc.convenience.Ha3Signal;
import eu.ha3.mc.convenience.Ha3StaticUtilities;
import eu.ha3.mc.haddon.SupportsEverythingReady;
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
	implements SupportsFrameEvents, SupportsTickEvents, SupportsKeyEvents, SupportsEverythingReady /*, Ha3Personalizable*/
{
	final static public MAtLogger LOGGER = new MAtLogger();
	final static public int VERSION = 20; // Remember to change the thing on mod_Matmos
	
	final static private boolean KNOWLEDGE_IS_SLOW = false;
	
	private MAtModPhase phase;
	private ConfigProperty config;
	
	private Ha3SoundCommunicator sndComm;
	private MAtUserControl userControl;
	private MAtDataGatherer dataGatherer;
	private MAtExpansionManager expansionManager;
	private MAtSoundManagerMaster soundManagerMaster;
	private MAtUpdateNotifier updateNotifier;
	
	private boolean isFatalError;
	private boolean isRunning;
	
	private boolean firstTickPassed;
	private TimeStatistic timeStatistic;
	private boolean hasSentSignalToTurnOn;
	private boolean everythingIsReady;
	
	public MAtMod()
	{
		// This is the constructor, so don't do anything
		// related to Minecraft.
		
		// Haddon constructors don't have superclass constructor calls
		// for convenience, so nothing is initialized.
		
		this.phase = MAtModPhase.NOT_INITIALIZED;
		
		setModLogger(Level.INFO);
		setEngineLogger(Level.OFF);
	}
	
	/**
	 * Sets the logger level for MAtmos mod.
	 * 
	 * @param lvl
	 */
	public void setModLogger(Level lvl)
	{
		//MAtMod.LOGGER.setLevel(lvl);
		//this.conMod.setLevel(lvl);
		
	}
	
	/**
	 * Sets the logger level for MAtmos Engine (minecraft independent)
	 * 
	 * @param lvl
	 */
	public void setEngineLogger(Level lvl)
	{
		MAtmosLogger.LOGGER.setLevel(lvl);
		
	}
	
	@Override
	public void onLoad()
	{
		// Look for installation errors (1)
		if (!new File(Minecraft.getMinecraftDir(), "matmos/").exists())
		{
			this.isFatalError = true;
			manager().hookTickEvents(true);
			return;
			
		}
		
		this.timeStatistic = new TimeStatistic(Locale.ENGLISH);
		
		this.sndComm = new Ha3SoundCommunicator(this, "MAtmos_");
		
		this.userControl = new MAtUserControl(this);
		this.dataGatherer = new MAtDataGatherer(this);
		this.expansionManager = new MAtExpansionManager(this);
		this.updateNotifier = new MAtUpdateNotifier(this);
		
		this.soundManagerMaster = new MAtSoundManagerMaster(this);
		
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
			this.config.setSource(new File(Minecraft.getMinecraftDir(), "matmos/userconfig.cfg").getCanonicalPath());
			this.config.load();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			throw new RuntimeException("Error caused config not to work: " + e.getMessage());
		}
		
		this.soundManagerMaster.setVolume(this.config.getFloat("globalvolume.scale"));
		this.updateNotifier.loadConfig(this.config);
		
		MAtMod.LOGGER.info("Took " + this.timeStatistic.getSecondsAsString(1) + " seconds to setup MAtmos base.");
		
		//
		
		MAtMod.LOGGER.info("Pre-loading.");
		
		// This registers stuff to Minecraft (key bindings...)
		this.userControl.load();
		
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
		
		this.timeStatistic = new TimeStatistic(Locale.ENGLISH);
		
		this.phase = MAtModPhase.CONSTRUCTING;
		
		MAtMod.LOGGER.info("Constructing.");
		
		this.dataGatherer.load();
		// note: soundManager needs to be loaded post sndcomms
		
		this.sndComm.load(new Ha3Signal() {
			@Override
			public void signal()
			{
				loadResourcesPhase();
				
			}
		}, new Ha3Signal() {
			
			@Override
			public void signal()
			{
				sndCommFailed();
				
			}
		});
		
		this.expansionManager.loadExpansions();
		MAtMod.LOGGER.info("Took " + this.timeStatistic.getSecondsAsString(1) + " seconds to enable MAtmos.");
		
	}
	
	private void sndCommFailed()
	{
		this.phase = MAtModPhase.SOUNDCOMMUNICATOR_FAILURE;
		MAtMod.LOGGER.severe("CRITICAL Error with SoundCommunicator (after "
			+ this.timeStatistic.getSecondsAsString(3) + " s.). Will not load.");
		
		this.isFatalError = true;
		this.phase = MAtModPhase.SOUNDCOMMUNICATOR_FAILURE;
		
	}
	
	private String getFirstBlocker()
	{
		File folder = new File(Minecraft.getMinecraftDir(), "matmos/reloader_blacklist/");
		
		if (!folder.exists())
			return null;
		
		for (File file : folder.listFiles())
		{
			if (file.getName().endsWith(".list"))
			{
				BufferedReader reader;
				try
				{
					reader = new BufferedReader(new FileReader(file));
					try
					{
						String line;
						while ((line = reader.readLine()) != null)
						{
							String[] contents = line.split("\t");
							if (contents.length > 0 && contents[0].length() > 0)
							{
								if (Ha3StaticUtilities.classExists(contents[0], this))
								{
									if (contents.length > 1)
										return contents[1];
									else
										return "A blocker was detected.";
									
								}
								
							}
							
						}
						
					}
					catch (IOException e)
					{
					}
					finally
					{
						closeAndShutUp(reader);
					}
				}
				catch (FileNotFoundException e1)
				{
					// welp
					e1.printStackTrace();
				}
				
			}
			
		}
		
		return null;
		
	}
	
	private void closeAndShutUp(Closeable closeable)
	{
		try
		{
			closeable.close();
		}
		catch (IOException e)
		{
		}
	}
	
	private void loadResourcesPhase()
	{
		this.phase = MAtModPhase.RESOURCE_LOADER;
		
		MAtMod.LOGGER.info("SoundCommunicator loaded (after " + this.timeStatistic.getSecondsAsString(3) + " s.).");
		
		String firstBlocker = getFirstBlocker();
		if (firstBlocker != null)
		{
			MAtMod.LOGGER.warning(firstBlocker);
			MAtMod.LOGGER.warning("MAtmos will not attempt load sounds on its own at all.");
			loadFinalPhase();
			
		}
		else
		{
			MAtMod.LOGGER.info("Bypassing Resource Reloader threaded wait. This may cause issues.");
			
			try
			{
				new MAtResourceReloader(this, null).reloadResources();
			}
			catch (Exception e)
			{
				MAtMod.LOGGER.severe("A severe error has occured while trying to reload resources.");
				MAtMod.LOGGER.severe("MAtmos may not function properly.");
				e.printStackTrace();
				
				try
				{
					Writer writer = new FileWriter(new File(Minecraft.getMinecraftDir(), "matmos_error.log"), true);
					PrintWriter pw = new PrintWriter(writer);
					e.printStackTrace(pw);
					pw.close();
					
				}
				catch (Exception eee)
				{
				}
			}
			loadFinalPhase();
			
		}
		
	}
	
	private void loadFinalPhase()
	{
		this.phase = MAtModPhase.FINAL_PHASE;
		
		MAtMod.LOGGER.info("ResourceReloader finished (after " + this.timeStatistic.getSecondsAsString(3) + " s.).");
		
		this.phase = MAtModPhase.READY;
		
		MAtMod.LOGGER.info("Ready.");
		
		startRunning();
		
		// Do not do that when it's ready
		// Forge loads the Sound module before mods load up, so
		// Forge gets stuck in the loading screen for 10 seconds building the knowledge
		
		//this.expansionManager.signalReadyToTurnOn();
		if (this.everythingIsReady)
		{
			trySendSignalToTurnOn();
		}
	}
	
	@Override
	public void onEverythingReady()
	{
		if (!trySendSignalToTurnOn())
		{
			MAtMod.LOGGER.info("MAtmos is not yet enabled and mods are loaded: Knowledge will be built later...");
		}
		this.everythingIsReady = true;
	}
	
	private boolean trySendSignalToTurnOn()
	{
		if (this.phase != MAtModPhase.READY)
			return false;
		
		if (!this.hasSentSignalToTurnOn)
		{
			this.hasSentSignalToTurnOn = true;
			
			MAtMod.LOGGER.info("Now building knowledge...");
			if (KNOWLEDGE_IS_SLOW)
			{
				new Thread() {
					@Override
					public void run()
					{
						MAtMod.this.expansionManager.signalReadyToTurnOn();
					}
				}.start();
			}
			else
			{
				this.expansionManager.signalReadyToTurnOn();
			}
		}
		return true;
		
	}
	
	public void reloadAndStart()
	{
		if (!isReady())
			return;
		
		if (this.isRunning)
			return;
		
		// prevent the expansions from running before the thread could even start
		this.expansionManager.clearExpansions();
		
		if (KNOWLEDGE_IS_SLOW)
		{
			new Thread() {
				@Override
				public void run()
				{
					TimeStatistic stat = new TimeStatistic(Locale.ENGLISH);
					MAtMod.this.expansionManager.loadExpansions();
					MAtMod.LOGGER.info("Expansions loaded (" + stat.getSecondsAsString(1) + "s).");
					
				}
			}.start();
		}
		else
		{
			TimeStatistic stat = new TimeStatistic(Locale.ENGLISH);
			MAtMod.this.expansionManager.loadExpansions();
			MAtMod.LOGGER.info("Expansions loaded (" + stat.getSecondsAsString(1) + "s).");
		}
		
		startRunning();
		
	}
	
	public void startRunning()
	{
		if (!isReady())
			return;
		
		if (this.isRunning)
			return;
		
		this.isRunning = true;
		
		MAtMod.LOGGER.fine("Loading...");
		this.expansionManager.modWasTurnedOnOrOff();
		MAtMod.LOGGER.fine("Loaded.");
		
	}
	
	public void stopRunning()
	{
		if (!isReady())
			return;
		
		if (!this.isRunning)
			return;
		
		this.isRunning = false;
		
		MAtMod.LOGGER.fine("Stopping...");
		this.expansionManager.modWasTurnedOnOrOff();
		MAtMod.LOGGER.fine("Stopped.");
		
		createDataDump();
		
	}
	
	private void createDataDump()
	{
		if (!this.config.getBoolean("dump.enabled"))
			return;
		
		MAtMod.LOGGER.fine("Dumping data.");
		
		try
		{
			File file = new File(Minecraft.getMinecraftDir(), "data_dump.xml");
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
		final Object[] in = new Object[] { Ha3Utility.COLOR_WHITE, "MAtmos: " };
		
		Object[] dest = new Object[in.length + args.length];
		System.arraycopy(in, 0, dest, 0, in.length);
		System.arraycopy(args, 0, dest, in.length, args.length);
		
		util().printChat(dest);
		
	}
	
	public void printChatShort(Object... args)
	{
		final Object[] in = new Object[] { Ha3Utility.COLOR_WHITE, "" };
		
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
	public MAtExpansionManager getExpansionManager()
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
			if (!new File(Minecraft.getMinecraftDir(), "matmos/").exists())
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
		
		// We must try this, because when onEverythingReady was triggered,
		// it is not guaranteed that MAtMod was ready.
		//trySendSignalToTurnOn();
		//    moved with a boolean check
		
		this.userControl.tickRoutine();
		if (this.isRunning)
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
			MAtMod.LOGGER.info("Saving configuration...");
			
			// Write changes on disk.
			this.config.save();
		}
		
	}
	
	public ConfigProperty getConfig()
	{
		return this.config;
		
	}
	/*
	public boolean isStartEnabled()
	{
		return this.config.getBoolean("start.enabled");
	}
	
	public void setStartEnabled(boolean startEnabled)
	{
		this.config.setProperty("start.enabled", startEnabled);
		
	}*/
	
}
