package eu.ha3.matmos.engine0.game.data.modules;

import eu.ha3.matmos.engine0.core.interfaces.Data;
import eu.ha3.matmos.engine0.game.data.abstractions.module.Module;
import eu.ha3.matmos.engine0.game.data.abstractions.module.ModuleProcessor;
import eu.ha3.matmos.engine0.game.system.MAtMod;

/*
--filenotes-placeholder
*/

public class L__meta_mod extends ModuleProcessor implements Module
{
	private final MAtMod mod;
	
	public L__meta_mod(Data data, MAtMod mod)
	{
		super(data, "meta_mod");
		this.mod = mod;
	}
	
	@Override
	protected void doProcess()
	{
		setValue("mod_tick", this.mod.util().getClientTick());
	}
}