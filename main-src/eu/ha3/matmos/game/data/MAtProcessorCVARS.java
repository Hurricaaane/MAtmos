package eu.ha3.matmos.game.data;

import java.io.File;
import java.io.IOException;

import eu.ha3.matmos.engine.implem.IntegerData;
import eu.ha3.matmos.game.system.MAtMod;
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

public class MAtProcessorCVARS extends MAtProcessorModel
{
	private File defaultsConfig;
	private File userConfig;
	
	private ConfigProperty config;
	
	public MAtProcessorCVARS(MAtMod modIn, IntegerData dataIn, String normalNameIn, String deltaNameIn)
	{
		super(modIn, dataIn, normalNameIn, deltaNameIn);
		this.defaultsConfig = new File(mod().util().getModsFolder(), "matmos/dataconfigvars_defaults.cfg");
		this.userConfig = new File(mod().util().getModsFolder(), "matmos/dataconfigvars.cfg");
		
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
	protected void doProcess()
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
