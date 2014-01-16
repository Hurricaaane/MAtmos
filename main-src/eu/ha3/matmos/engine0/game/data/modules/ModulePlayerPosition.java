package eu.ha3.matmos.engine0.game.data.modules;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import eu.ha3.matmos.engine0.conv.ProcessorModel;
import eu.ha3.matmos.engine0.core.interfaces.Data;
import eu.ha3.matmos.engine0.game.data.MAtDataGatherer;

/*
--filenotes-placeholder
*/

public class ModulePlayerPosition extends ProcessorModel implements Module
{
	public static String NAME = "player_position";
	
	public ModulePlayerPosition(Data data)
	{
		super(data, NAME, NAME + MAtDataGatherer.DELTA_SUFFIX);
	}
	
	@Override
	protected void doProcess()
	{
		Entity e = Minecraft.getMinecraft().thePlayer;
		setValue("block_x", (long) Math.floor(e.posX));
		setValue("block_y", (long) Math.floor(e.posY));
		setValue("block_z", (long) Math.floor(e.posZ));
	}
	
	@Override
	public String getModuleName()
	{
		return NAME;
	}
}
