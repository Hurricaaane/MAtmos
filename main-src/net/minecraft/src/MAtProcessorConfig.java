package net.minecraft.src;

import java.io.File;
import java.io.IOException;

import net.minecraft.client.Minecraft;
import eu.ha3.matmos.engine.Data;
import eu.ha3.util.property.simple.ConfigProperty;
import eu.ha3.util.property.simple.PropertyMissingException;
import eu.ha3.util.property.simple.PropertyTypeException;

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

public class MAtProcessorConfig extends MAtProcessorModel
{
	private File defaultsConfig;
	private File userConfig;
	
	private ConfigProperty config;
	
	MAtProcessorConfig(MAtMod modIn, Data dataIn, String normalNameIn, String deltaNameIn)
	{
		super(modIn, dataIn, normalNameIn, deltaNameIn);
		this.defaultsConfig = new File(Minecraft.getMinecraftDir(), "matmos/dataconfigvars_defaults.cfg");
		this.userConfig = new File(Minecraft.getMinecraftDir(), "matmos/dataconfigvars.cfg");
		
		this.config = new ConfigProperty();
		this.config.setSource(this.defaultsConfig.getAbsolutePath());
		this.config.load();
		
		this.config.setSource(this.userConfig.getAbsolutePath());
		if (!this.userConfig.exists())
		{
			try
			{
				this.userConfig.createNewFile();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		
	}
	
	@Override
	void doProcess()
	{
		this.config.load();
		for (String key : this.config.getAllProperties().keySet())
		{
			try
			{
				int index = Integer.parseInt(key);
				setValue(index, this.config.getInteger(key));
			}
			catch (NumberFormatException e)
			{
			}
			catch (PropertyTypeException e)
			{
			}
			catch (PropertyMissingException e)
			{
				// This error shouldn't happen...
				e.printStackTrace();
			}
			
		}
		
	}
	
}
