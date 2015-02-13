package eu.ha3.matmos.game.data.modules;

import eu.ha3.matmos.engine.core.interfaces.Data;
import eu.ha3.matmos.game.data.abstractions.module.Module;
import eu.ha3.matmos.game.data.abstractions.module.ModuleProcessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;

/*
--filenotes-placeholder
*/

public class M__ply_motion extends ModuleProcessor implements Module
{
	public M__ply_motion(Data data)
	{
		super(data, "ply_motion");
	}
	
	@Override
	protected void doProcess()
	{
        // dag edit EntityClientPlayerMP -> EntityPlayerSP
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		
		int mxx = (int) Math.round(player.motionX * 1000);
		int mzz = (int) Math.round(player.motionZ * 1000);
		
		setValue("x_1k", mxx);
		setValue("y_1k", (int) Math.round(player.motionY * 1000));
		setValue("z_1k", mzz);
		setValue("sqrt_xx_zz", (int) Math.floor(Math.sqrt(mxx * mxx + mzz * mzz)));
	}
}
