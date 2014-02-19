package eu.ha3.matmos.game.data.abstractions.processor;

import eu.ha3.matmos.engine0.core.interfaces.Data;
import eu.ha3.matmos.game.system.MAtMod;

/* x-placeholder */

public abstract class MAtProcessorModel extends ProcessorModel
{
	private MAtMod mod;
	
	public MAtProcessorModel(MAtMod mod, Data data, String normalName, String deltaName)
	{
		super(data, normalName, deltaName);
		this.mod = mod;
	}
	
	public MAtMod mod()
	{
		return this.mod;
	}
}
