package eu.ha3.matmos.game.data;

import eu.ha3.matmos.conv.ProcessorModel;
import eu.ha3.matmos.engine.implem.IntegerData;
import eu.ha3.matmos.game.system.MAtMod;

/* x-placeholder */

public abstract class MAtProcessorModel extends ProcessorModel
{
	private MAtMod mod;
	
	public MAtProcessorModel(MAtMod modIn, IntegerData dataIn, String normalNameIn, String deltaNameIn)
	{
		super(dataIn, normalNameIn, deltaNameIn);
		this.mod = modIn;
		
	}
	
	public MAtMod mod()
	{
		return this.mod;
	}
}
