package eu.ha3.matmos.game.data.modules;

import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import eu.ha3.matmos.engine.core.interfaces.Data;
import eu.ha3.matmos.game.data.abstractions.module.Module;
import eu.ha3.matmos.game.data.abstractions.module.ModuleProcessor;
import eu.ha3.matmos.game.system.MAtmosUtility;

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
        BlockPos playerPos = MAtmosUtility.getPlayerPosition();

        /* dag edits
         * Use BlockPos
         * getSavedLightValue(..) -> getLightFor(..)
         * EnumSkyBlock.Sky -> EnumSkyBlock.SKY
         * EnumSkyBlock.Block -> EnumSkyBlock.BLOCK
         * getBlockLightValue(..) -> getLight(..)
         * canBlockSeeTheSky(..) -> canSeeSky(..)
         */
        setValue("sky", w.getLightFor(EnumSkyBlock.SKY, playerPos));
		setValue("lamp", w.getLightFor(EnumSkyBlock.BLOCK, playerPos));
		setValue("final", w.getLight(playerPos));
        setValue("see_sky", w.canSeeSky(playerPos));
	}
}