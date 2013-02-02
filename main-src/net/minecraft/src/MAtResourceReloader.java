package net.minecraft.src;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import eu.ha3.matmos.conv.AnyLogger;
import eu.ha3.mc.convenience.Ha3Signal;
import eu.ha3.mc.haddon.PrivateAccessException;

public class MAtResourceReloader
{
	private MAtMod mod;
	
	public MAtResourceReloader(MAtMod modIn, Ha3Signal onSuccess)
	{
		this.mod = modIn;
		
	}
	
	private SoundPool sps;
	private List<String> myStack;
	private List<String> myAddedFiles;
	
	public void reloadResources()
	{
		// soundPoolSounds
		// XXX Get rid of private value getting on runtime
		try
		{
			this.sps =
				(SoundPool) this.mod
					.getManager()
					.getUtility()
					.getPrivateValueLiteral(
						net.minecraft.src.SoundManager.class, this.mod.getSoundCommunicator().getSoundManager(), "b", 1);
			
			this.myStack = new ArrayList<String>();
			this.myAddedFiles = new ArrayList<String>();
			
			cpy_reloadResources();
			
			StringBuilder sb = new StringBuilder().append("Loaded files:");
			for (String afile : this.myAddedFiles)
			{
				sb.append(" ").append(afile);
				
			}
			AnyLogger.info(sb.toString());
		}
		catch (PrivateAccessException e)
		{
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Reloads the resource folder and passes the resources to Minecraft to
	 * install.
	 */
	private void cpy_reloadResources()
	{
		File[] filesInThisDir = new File(Minecraft.getMinecraftDir(), "resources/sound3/").listFiles();
		
		if (filesInThisDir != null)
		{
			for (File file : filesInThisDir)
			{
				if (file.getName().startsWith("matmos_"))
				{
					loadResource(file, "sound3/" + file.getName() + "/");
					
				}
				
			}
			
		}
	}
	
	/**
	 * Loads a resource and passes it to Minecraft to install.
	 */
	private void loadResource(File par1File, String par2Str)
	{
		File[] filesInThisDir = par1File.listFiles();
		int fileCount = filesInThisDir.length;
		
		for (int i = 0; i < fileCount; ++i)
		{
			File file = filesInThisDir[i];
			
			if (file.isDirectory())
			{
				loadResource(file, par2Str + file.getName() + "/");
			}
			else
			{
				try
				{
					String fileRep = par2Str + file.getName();
					fileRep = fileRep.substring(fileRep.indexOf("/") + 1);
					fileRep = fileRep.substring(0, fileRep.indexOf("."));
					while (Character.isDigit(fileRep.charAt(fileRep.length() - 1)))
					{
						fileRep = fileRep.substring(0, fileRep.length() - 1);
					}
					fileRep = fileRep.replaceAll("/", ".");
					
					if (this.myStack.contains(fileRep))
					{
						this.mod.getManager().getMinecraft().installResource(par2Str + file.getName(), file);
						this.myAddedFiles.add(par2Str + file.getName());
					}
					else if (this.sps.getRandomSoundFromSoundPool(fileRep) == null)
					{
						this.myStack.add(fileRep);
						this.mod.getManager().getMinecraft().installResource(par2Str + file.getName(), file);
						this.myAddedFiles.add(par2Str + file.getName());
					}
				}
				catch (Exception var9)
				{
					System.out.println("Failed to add " + par2Str + file.getName());
				}
			}
		}
	}
	
}
