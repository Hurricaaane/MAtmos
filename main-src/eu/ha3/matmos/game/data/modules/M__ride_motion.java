package eu.ha3.matmos.game.data.modules;

import eu.ha3.matmos.engine.core.interfaces.Data;
import eu.ha3.matmos.game.data.abstractions.module.Module;
import eu.ha3.matmos.game.data.abstractions.module.ModuleProcessor;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;

/*
--filenotes-placeholder
*/

public class M__ride_motion extends ModuleProcessor implements Module
{
	public M__ride_motion(Data data)
	{
		super(data, "ride_motion");
	}
	
	@Override
	protected void doProcess()
	{
		Entity ride = Minecraft.getMinecraft().thePlayer.ridingEntity;
		
		if (ride != null)
		{
			int mxx = (int) Math.round(ride.motionX * 1000);
			int mzz = (int) Math.round(ride.motionZ * 1000);
			
			setValue("x_1k", mxx);
			setValue("y_1k", (int) Math.round(ride.motionY * 1000));
			setValue("z_1k", mzz);
			setValue("sqrt_xx_zz", (int) Math.floor(Math.sqrt(mxx * mxx + mzz * mzz)));
		}
		else
		{
			setValue("x_1k", 0);
			setValue("y_1k", 0);
			setValue("z_1k", 0);
			setValue("sqrt_xx_zz", 0);
		}
	}
}
