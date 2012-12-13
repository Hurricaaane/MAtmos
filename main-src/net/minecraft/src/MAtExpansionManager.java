package net.minecraft.src;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.client.Minecraft;

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

public class MAtExpansionManager
{
	private MAtMod mod;
	private Map<String, MAtExpansion> expansions;
	private List<MAtSoundManagerChild> soundManagers;
	
	private File expansionsFolder;
	private File userconfigFolder;
	
	private boolean canBuildKnowledge;
	
	public MAtExpansionManager(MAtMod mAtmosHaddon)
	{
		this.mod = mAtmosHaddon;
		
		this.expansions = new ConcurrentHashMap<String, MAtExpansion>();
		this.soundManagers = new ArrayList<MAtSoundManagerChild>();
		
		this.expansionsFolder = new File(Minecraft.getMinecraftDir(), "matmos/expansions_r12/");
		this.userconfigFolder = new File(Minecraft.getMinecraftDir(), "matmos/expansions_r12_userconfig/");
		
		if (!this.expansionsFolder.exists())
		{
			this.expansionsFolder.mkdirs();
		}
		
		if (!this.userconfigFolder.exists())
		{
			this.userconfigFolder.mkdirs();
		}
		
	}
	
	private void renewExpansionProngs(MAtExpansion expansion)
	{
		MAtSoundManagerChild soundManager = new MAtSoundManagerChild(this.mod);
		this.soundManagers.add(soundManager);
		
		expansion.setSoundManager(soundManager);
		expansion.setData(this.mod.getDataGatherer().getData());
		
	}
	
	public void createExpansionEntry(String userDefinedIdentifier)
	{
		MAtExpansion expansion = new MAtExpansion(userDefinedIdentifier);
		this.expansions.put(userDefinedIdentifier, expansion);
		renewExpansionProngs(expansion);
		
	}
	
	public void addExpansionFromFile(String userDefinedIdentifier, File file)
	{
		try
		{
			addExpansion(userDefinedIdentifier, new FileInputStream(file));
			
		}
		catch (FileNotFoundException e)
		{
			MAtMod.LOGGER.warning("Error with FileNotFound on ExpansionLoader (on file "
				+ file.getAbsolutePath() + ").");
			
		}
		
	}
	
	/*public void removeExpansion(String userDefinedIdentifier)
	{
		if (this.expansions.containsKey(userDefinedIdentifier))
		{
			MAtExpansion expansion = this.expansions.get(userDefinedIdentifier);
			
			expansion.turnOff();
			
			this.expansions.remove(userDefinedIdentifier);
		}
		
	}*/
	
	public void addExpansion(String userDefinedIdentifier, InputStream stream)
	{
		if (!this.expansions.containsKey(userDefinedIdentifier))
		{
			MAtMod.LOGGER.severe("Tried to add an expansion that has no entry!");
			return;
			
		}
		
		MAtExpansion expansion = this.expansions.get(userDefinedIdentifier);
		expansion.inputStructure(stream);
		
		tryTurnOn(expansion);
		
	}
	
	private void tryTurnOn(MAtExpansion expansion)
	{
		if (expansion == null)
			return;
		
		if (!this.canBuildKnowledge)
			return;
		
		turnOnOrOff(expansion);
		
	}
	
	public void signalReadyToTurnOn()
	{
		this.canBuildKnowledge = true;
		
		// Try build the knowledge of expansions that were structured before buildknowledge was ready
		for (MAtExpansion expansion : this.expansions.values())
		{
			tryTurnOn(expansion);
		}
		
	}
	
	private void turnOnOrOff(MAtExpansion expansion)
	{
		if (expansion == null)
			return;
		
		if (this.mod.isRunning())
		{
			if (expansion.getVolume() > 0)
			{
				expansion.turnOn();
			}
		}
		else
		{
			expansion.turnOff();
		}
		
	}
	
	public synchronized void modWasTurnedOnOrOff()
	{
		for (MAtExpansion expansion : this.expansions.values())
		{
			turnOnOrOff(expansion);
			
		}
	}
	
	public Map<String, MAtExpansion> getExpansions()
	{
		return this.expansions;
		
	}
	
	public void soundRoutine()
	{
		for (MAtExpansion expansion : this.expansions.values())
		{
			expansion.soundRoutine();
			
		}
		
	}
	
	public void dataRoutine()
	{
		for (MAtExpansion expansion : this.expansions.values())
		{
			expansion.dataRoutine();
			
		}
		
	}
	
	public synchronized void clearExpansions()
	{
		for (MAtExpansion expansion : this.expansions.values())
		{
			expansion.clear();
		}
		this.expansions.clear();
		
	}
	
	public void loadExpansions()
	{
		clearExpansions();
		
		List<File> offline = new ArrayList<File>();
		gatherOffline(this.expansionsFolder, offline);
		
		createExpansionEntries(offline);
		
		for (File file : offline)
		{
			addExpansionFromFile(file.getName(), file);
		}
		
	}
	
	private synchronized void createExpansionEntries(List<File> offline)
	{
		for (File file : offline)
		{
			MAtMod.LOGGER.info("ExpansionLoader found offline " + file.getName() + ".");
			createExpansionEntry(file.getName());
		}
		
	}
	
	private void gatherOffline(File file, List<File> files)
	{
		if (!file.exists())
			return;
		
		for (File individual : file.listFiles())
		{
			if (individual.isDirectory())
			{
			}
			else if (individual.getName().endsWith(".xml"))
			{
				files.add(individual);
				
			}
			
		}
		
	}
	
}
