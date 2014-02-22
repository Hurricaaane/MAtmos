package eu.ha3.matmos.game.data.modules;

import eu.ha3.matmos.engine0.core.interfaces.Data;
import eu.ha3.matmos.game.data.abstractions.module.Module;
import eu.ha3.matmos.game.data.abstractions.module.ModuleProcessor;
import eu.ha3.matmos.game.system.MAtMod;

/*
--filenotes-placeholder
*/

public class R__meta_option extends ModuleProcessor implements Module
{
	private final MAtMod mod;
	
	public R__meta_option(Data data, MAtMod mod)
	{
		super(data, "meta_option", true);
		this.mod = mod;
	}
	
	@Override
	protected void doProcess()
	{
		setValue("altitudes_high", this.mod.getConfig().getBoolean("useroptions.altitudes.high") ? 1 : 0);
		setValue("altitudes_low", this.mod.getConfig().getBoolean("useroptions.altitudes.low") ? 1 : 0);
	}
}