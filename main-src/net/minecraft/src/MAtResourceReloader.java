package net.minecraft.src;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import eu.ha3.matmos.conv.MAtmosConvLogger;

public class MAtResourceReloader
{
	private MAtMod mod;
	
	public MAtResourceReloader(MAtMod modIn)
	{
		this.mod = modIn;
	}
	
	private List<String> myAddedFiles;
	
	public void reloadResources()
	{
		this.myAddedFiles = new ArrayList<String>();
		
		findAndLoadResourcesFromLocation();
		
		MAtmosConvLogger.info("Loaded " + this.myAddedFiles.size() + " files.");
	}
	
	/**
	 * Reloads the resource folder and passes the resources to Minecraft to
	 * install.
	 */
	private void findAndLoadResourcesFromLocation()
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
					
					this.mod.getManager().getMinecraft().installResource(par2Str + file.getName(), file);
					this.myAddedFiles.add(par2Str + file.getName());
				}
				catch (Exception var9)
				{
					System.out.println("Failed to add " + par2Str + file.getName());
				}
			}
		}
	}
	
}
