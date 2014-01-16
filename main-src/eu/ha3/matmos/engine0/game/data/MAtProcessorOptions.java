package eu.ha3.matmos.engine0.game.data;

import eu.ha3.matmos.engine0.core.implem.SelfGeneratingData;
import eu.ha3.matmos.engine0.game.data.abstractions.processor.MAtProcessorModel;
import eu.ha3.matmos.engine0.game.system.MAtMod;

/* x-placeholder */

public class MAtProcessorOptions extends MAtProcessorModel
{
	public MAtProcessorOptions(MAtMod modIn, SelfGeneratingData dataIn, String normalNameIn, String deltaNameIn)
	{
		super(modIn, dataIn, normalNameIn, deltaNameIn);
	}
	
	@Override
	protected void doProcess()
	{
		setValueLegacyIntIndexes(0, mod().getConfig().getBoolean("useroptions.altitudes.high") ? 1 : 0);
		setValueLegacyIntIndexes(1, mod().getConfig().getBoolean("useroptions.altitudes.low") ? 1 : 0);
	}
	
}
