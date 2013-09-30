package eu.ha3.matmos.engine0.conv;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.ha3.matmos.engine0.core.interfaces.Data;
import eu.ha3.matmos.engine0.core.interfaces.SoundRelay;
import eu.ha3.matmos.engine0.requirem.Collation;
import eu.ha3.matmos.engine0.requirem.CollationOfRequirements;

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

public class ExpansionManager
{
	private Map<String, Expansion> expansions;
	//private List<MAtSoundManagerChild> soundManagers;
	
	private String expansionsSubdir;
	private File userconfigFolder;
	private boolean isActivated;
	private ReplicableSoundRelay master;
	private Data data;
	
	private Collation collation;
	private CacheRegistry cacheRegistry;
	private File packsFolder;
	
	public ExpansionManager(String expansionsSubdir, File userconfigFolder, CacheRegistry registry)
	{
		this.expansions = new HashMap<String, Expansion>();
		this.cacheRegistry = registry;
		
		this.expansionsSubdir = expansionsSubdir;
		this.userconfigFolder = userconfigFolder;
		
		if (!this.userconfigFolder.exists())
		{
			this.userconfigFolder.mkdirs();
		}
		
		this.collation = new CollationOfRequirements();
		
	}
	
	public void setPacksFolder(File modsFolder)
	{
		this.packsFolder = modsFolder;
	}
	
	public void createExpansionEntry(String userDefinedIdentifier)
	{
		Expansion expansion =
			new Expansion(userDefinedIdentifier, new File(this.userconfigFolder, userDefinedIdentifier + ".cfg"));
		this.expansions.put(userDefinedIdentifier, expansion);
		
		SoundRelay soundManager = this.master.createChild();
		//this.soundManagers.add(soundManager);
		
		expansion.setSoundManager(soundManager);
		expansion.setData(this.data);
		expansion.setCollation(this.collation);
		
	}
	
	public void addExpansionFromFile(String userDefinedIdentifier, File file)
	{
		try
		{
			addExpansion(userDefinedIdentifier, new FileInputStream(file));
			
		}
		catch (FileNotFoundException e)
		{
			MAtmosConvLogger.warning("Error with FileNotFound on ExpansionLoader (on file "
				+ file.getAbsolutePath() + ").");
			
		}
		
	}
	
	public void addExpansion(String userDefinedIdentifier, InputStream stream)
	{
		if (!this.expansions.containsKey(userDefinedIdentifier))
		{
			MAtmosConvLogger.severe("Tried to add an expansion that has no entry!");
			return;
			
		}
		
		Expansion expansion = this.expansions.get(userDefinedIdentifier);
		expansion.inputStructure(stream);
		
		tryTurnOn(expansion);
		
	}
	
	private void tryTurnOn(Expansion expansion)
	{
		if (expansion == null)
			return;
		
		turnOnOrOff(expansion);
		
	}
	
	private void turnOnOrOff(Expansion expansion)
	{
		if (expansion == null)
			return;
		
		if (this.isActivated)
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
	
	public void activate()
	{
		if (this.isActivated)
			return;
		
		this.isActivated = true;
		
		resync();
	}
	
	public void deactivate()
	{
		if (!this.isActivated)
			return;
		
		this.isActivated = false;
		
		resync();
	}
	
	private void resync()
	{
		for (Expansion expansion : this.expansions.values())
		{
			turnOnOrOff(expansion);
		}
	}
	
	public Map<String, Expansion> getExpansions()
	{
		return this.expansions;
		
	}
	
	public void soundRoutine()
	{
		for (Expansion expansion : this.expansions.values())
		{
			expansion.soundRoutine();
			
		}
		
	}
	
	public void dataRoutine()
	{
		for (Expansion expansion : this.expansions.values())
		{
			expansion.dataRoutine();
			
		}
		
	}
	
	public void clearExpansions()
	{
		for (Expansion expansion : this.expansions.values())
		{
			expansion.clear();
		}
		this.expansions.clear();
		
	}
	
	public void loadExpansions()
	{
		clearExpansions();
		
		List<File> offline = new ArrayList<File>();
		gatherOffline(this.packsFolder, offline);
		
		createExpansionEntries(offline);
		
		for (File file : offline)
		{
			addExpansionFromFile(file.getName(), file);
		}
		
	}
	
	private void createExpansionEntries(List<File> offline)
	{
		for (File file : offline)
		{
			MAtmosConvLogger.info("ExpansionLoader found offline " + file.getName() + ".");
			createExpansionEntry(file.getName());
		}
		
	}
	
	private void gatherOffline(File modsDir, List<File> files)
	{
		if (!modsDir.exists())
			return;
		
		for (File mod : modsDir.listFiles())
		{
			if (mod.isDirectory()) //&& mod.getName().startsWith("matmos_"))
			{
				File expansionFolder = new File(mod, this.expansionsSubdir);
				if (expansionFolder.isDirectory() && expansionFolder.exists())
				{
					for (File individual : expansionFolder.listFiles())
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
				
				File soundFolder = new File(mod, "assets/minecraft/sound/");
				if (soundFolder.exists())
				{
					loadResource(soundFolder, "");
				}
			}
		}
		
	}
	
	private void loadResource(File par1File, String root)
	{
		File[] filesInThisDir = par1File.listFiles();
		int fileCount = filesInThisDir.length;
		
		for (int i = 0; i < fileCount; ++i)
		{
			File file = filesInThisDir[i];
			
			if (file.isDirectory())
			{
				loadResource(file, root + file.getName() + "/");
			}
			else
			{
				try
				{
					this.cacheRegistry.cacheSound(root + file.getName());
				}
				catch (Exception e)
				{
				}
			}
		}
	}
	
	public void setMaster(ReplicableSoundRelay master)
	{
		this.master = master;
	}
	
	public void setData(Data data)
	{
		this.data = data;
		
	}
	
	public void neutralizeSoundManagers()
	{
		this.master = new SRCVNullObject();
		for (Expansion exp : this.expansions.values())
		{
			exp.setSoundManager(new SRCVNullObject());
		}
	}
	
	public Collation getCollation()
	{
		return this.collation;
	}
}
