package eu.ha3.matmos.engine0.game.data;

import eu.ha3.matmos.engine0.conv.ProcessorModel;
import eu.ha3.matmos.engine0.core.implem.StringData;
import eu.ha3.matmos.engine0.game.system.MAtMod;

/* x-placeholder */

public abstract class MAtProcessorModel extends ProcessorModel
{
	private MAtMod mod;
	
	public MAtProcessorModel(MAtMod modIn, StringData dataIn, String normalNameIn, String deltaNameIn)
	{
		super(dataIn, normalNameIn, deltaNameIn);
		this.mod = modIn;
		
	}
	
	public MAtMod mod()
	{
		return this.mod;
	}
}
