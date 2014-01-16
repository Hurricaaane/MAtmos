package eu.ha3.matmos.engine0.game.data.modules;

import eu.ha3.matmos.engine0.core.interfaces.Data;
import eu.ha3.matmos.engine0.game.data.MAtDataGatherer;
import eu.ha3.matmos.engine0.game.data.abstractions.processor.ProcessorModel;

/*
--filenotes-placeholder
*/

public class ModuleBlockCount extends ProcessorModel implements Module
{
	public static String NAME = "block_curadius8";
	
	public ModuleBlockCount(Data data)
	{
		super(data, NAME, NAME + MAtDataGatherer.DELTA_SUFFIX);
		data.getSheet(NAME).setDefaultValue("0");
	}
	
	@Override
	protected void doProcess()
	{
		//Entity e = Minecraft.getMinecraft().thePlayer;
	}
	
	@Override
	public String getModuleName()
	{
		return NAME;
	}
}