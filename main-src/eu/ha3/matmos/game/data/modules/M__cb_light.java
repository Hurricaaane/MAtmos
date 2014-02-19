package eu.ha3.matmos.game.data.modules;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import eu.ha3.matmos.engine0.core.interfaces.Data;
import eu.ha3.matmos.game.data.abstractions.module.Module;
import eu.ha3.matmos.game.data.abstractions.module.ModuleProcessor;

/*
--filenotes-placeholder
*/

public class M__cb_light extends ModuleProcessor implements Module
{
	public M__cb_light(Data data)
	{
		super(data, "cb_light");
	}
	
	@Override
	protected void doProcess()
	{
		World w = Minecraft.getMinecraft().theWorld;
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		
		int x = (int) Math.floor(player.posX);
		int y = (int) Math.floor(player.posY);
		int z = (int) Math.floor(player.posZ);
		
		setValue("sky", w.getSavedLightValue(EnumSkyBlock.Sky, x, y, z));
		setValue("lamp", w.getSavedLightValue(EnumSkyBlock.Block, x, y, z));
		setValue("final", w.getBlockLightValue(x, y, z));
		setValue("see_sky", w.canBlockSeeTheSky(x, y, z));
	}
}