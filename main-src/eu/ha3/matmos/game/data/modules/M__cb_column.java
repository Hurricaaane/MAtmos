package eu.ha3.matmos.game.data.modules;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.world.World;
import eu.ha3.matmos.engine0.core.interfaces.Data;
import eu.ha3.matmos.game.data.MAT_HARD_LIMITS;
import eu.ha3.matmos.game.data.abstractions.module.Module;
import eu.ha3.matmos.game.data.abstractions.module.ModuleProcessor;
import eu.ha3.matmos.game.system.MAtmosUtility;

/*
--filenotes-placeholder
*/

public class M__cb_column extends ModuleProcessor implements Module
{
	public M__cb_column(Data data)
	{
		super(data, "cb_column");
	}
	
	@Override
	protected void doProcess()
	{
		World w = Minecraft.getMinecraft().theWorld;
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		
		int x = (int) Math.floor(player.posX);
		int y = (int) Math.floor(player.posY);
		int z = (int) Math.floor(player.posZ);
		
		setValue("y-1", MAtmosUtility.getNameAt(x, y - 1, z, MAT_HARD_LIMITS.NO_BLOCK_OUT_OF_BOUNDS));
		setValue("y-2", MAtmosUtility.getNameAt(x, y - 2, z, MAT_HARD_LIMITS.NO_BLOCK_OUT_OF_BOUNDS));
		setValue("y0", MAtmosUtility.getNameAt(x, y + 0, z, MAT_HARD_LIMITS.NO_BLOCK_OUT_OF_BOUNDS));
		setValue("y1", MAtmosUtility.getNameAt(x, y + 1, z, MAT_HARD_LIMITS.NO_BLOCK_OUT_OF_BOUNDS));
		setValue("topmost_block", w.getTopSolidOrLiquidBlock(x, z));
		setValue("thickness_overhead", w.getTopSolidOrLiquidBlock(x, z) - y);
		// FIXME: 26 is unresolved
	}
}