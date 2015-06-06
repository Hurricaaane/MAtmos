package eu.ha3.matmos.game.data.modules;

import eu.ha3.matmos.engine.core.interfaces.Data;
import eu.ha3.matmos.game.data.abstractions.module.Module;
import eu.ha3.matmos.game.data.abstractions.module.ModuleProcessor;
import eu.ha3.matmos.game.system.MAtmosUtility;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;

/*
--filenotes-placeholder
*/

public class L__legacy_column extends ModuleProcessor implements Module
{
	public L__legacy_column(Data data)
	{
		super(data, "legacy_column");
	}
	
	@Override
	protected void doProcess()
	{
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		
		int x = (int) Math.floor(player.posX);
		int y = (int) Math.floor(player.posY);
		int z = (int) Math.floor(player.posZ);
		
		setValue("y-1_as_number", MAtmosUtility.legacyOf(MAtmosUtility.getBlockAt(x, y - 1, z)));
		setValue("y-2_as_number", MAtmosUtility.legacyOf(MAtmosUtility.getBlockAt(x, y - 2, z)));
		setValue("y0_as_number", MAtmosUtility.legacyOf(MAtmosUtility.getBlockAt(x, y + 0, z)));
		setValue("y1_as_number", MAtmosUtility.legacyOf(MAtmosUtility.getBlockAt(x, y + 1, z)));
	}
}