package eu.ha3.matmos.game.data.modules;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldInfo;
import eu.ha3.matmos.engine.core.interfaces.Data;
import eu.ha3.matmos.game.data.abstractions.module.Module;
import eu.ha3.matmos.game.data.abstractions.module.ModuleProcessor;

/*
--filenotes-placeholder
*/

public class M__w_general extends ModuleProcessor implements Module
{
	public M__w_general(Data data)
	{
		super(data, "w_general");
	}
	
	@Override
	protected void doProcess()
	{
		World w = Minecraft.getMinecraft().theWorld;
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		WorldInfo info = w.getWorldInfo();
		
		setValue("time_modulo24k", (int) (info.getWorldTime() % 24000L));
		setValue("rain", info.isRaining());
		//setValue("thunder", info.isThundering());
		setValue("thunder", w.getWeightedThunderStrength(0f) > 0.9f);
		setValue("dimension", player.dimension);
		setValue("light_subtracted", w.skylightSubtracted);
		setValue("remote", w.isRemote);
		setValue("moon_phase", w.getMoonPhase());
		
		setValue("rain_force1k", Math.round(w.getRainStrength(0f) * 1000));
		setValue("thunder_force1k", Math.round(w.getWeightedThunderStrength(0f) * 1000));
		
	}
}