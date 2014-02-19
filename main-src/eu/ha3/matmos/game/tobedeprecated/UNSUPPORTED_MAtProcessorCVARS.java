package eu.ha3.matmos.game.tobedeprecated;

import java.io.File;
import java.io.IOException;

import eu.ha3.matmos.engine0.core.interfaces.Data;
import eu.ha3.matmos.game.data.abstractions.processor.MAtProcessorModel;
import eu.ha3.matmos.game.system.MAtMod;
import eu.ha3.util.property.simple.ConfigProperty;
import eu.ha3.util.property.simple.PropertyMissingException;
import eu.ha3.util.property.simple.PropertyTypeException;

/* x-placeholder */

@Deprecated
public class UNSUPPORTED_MAtProcessorCVARS extends MAtProcessorModel
{
	private File defaultsConfig;
	private File userConfig;
	
	private ConfigProperty config;
	
	public UNSUPPORTED_MAtProcessorCVARS(MAtMod modIn, Data dataIn, String normalNameIn, String deltaNameIn)
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
				setValueLegacyIntIndexes(index, this.config.getInteger(key));
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
