package eu.ha3.matmos.game.data.modules;

import eu.ha3.matmos.engine.core.interfaces.Data;
import eu.ha3.matmos.game.data.abstractions.module.Module;
import eu.ha3.matmos.game.data.abstractions.module.ModuleProcessor;
import eu.ha3.matmos.game.system.MAtmosUtility;

/*
--filenotes-placeholder
*/

public class M__cb_pos extends ModuleProcessor implements Module
{
	public M__cb_pos(Data data)
	{
		super(data, "cb_pos");
	}
	
	@Override
	protected void doProcess()
	{
		setValue("x", MAtmosUtility.getPlayerX());
		setValue("y", MAtmosUtility.getPlayerY());
		setValue("z", MAtmosUtility.getPlayerZ());
	}
}
