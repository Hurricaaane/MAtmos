package eu.ha3.matmos.game.data.modules;

import eu.ha3.matmos.engine.core.interfaces.Data;
import eu.ha3.matmos.game.data.abstractions.module.Module;
import eu.ha3.matmos.game.data.abstractions.module.ModuleProcessor;
import eu.ha3.matmos.game.system.IDontKnowHowToCode;
import eu.ha3.matmos.game.system.MAtMod;
import eu.ha3.util.property.simple.ConfigProperty;

import java.io.File;
import java.io.IOException;

/*
--filenotes-placeholder
*/

public class R__legacy_configvars extends ModuleProcessor implements Module
{
	private final MAtMod mod;
	
	private File defaultsConfig;
	private File userConfig;
	private ConfigProperty config;
	
	public R__legacy_configvars(Data data, MAtMod mod)
	{
		super(data, "legacy_configvars", true);
		this.mod = mod;
		
		this.defaultsConfig = new File(mod.util().getModsFolder(), "matmos/dataconfigvars_defaults.cfg");
		this.userConfig = new File(mod.util().getModsFolder(), "matmos/dataconfigvars.cfg");
		
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
		for (String key : this.config.getAllProperties().keySet())
		{
			try
			{
				setValue(key, this.config.getInteger(key));
			}
			catch (Exception e)
			{
				IDontKnowHowToCode.whoops__printExceptionToChat(this.mod.getChatter(), e, this);
			}
		}
	}
}
