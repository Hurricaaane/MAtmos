package eu.ha3.matmos.game.data.modules;

import eu.ha3.matmos.engine.core.interfaces.Data;
import eu.ha3.matmos.game.data.abstractions.module.Module;
import eu.ha3.matmos.game.data.abstractions.module.ModuleProcessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;

/*
--filenotes-placeholder
*/

public class M__ply_stats extends ModuleProcessor implements Module
{
	public M__ply_stats(Data data)
	{
		super(data, "ply_stats");
	}
	
	@Override
	protected void doProcess()
	{
        // dag edit EntityClientPlayerMP -> EntityPlayerSP
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		
		setValue("health1k", (int) (player.getHealth() * 1000));
		setValue("oxygen", player.getAir());
		setValue("armor", player.getTotalArmorValue());
		setValue("food", player.getFoodStats().getFoodLevel());
		setValue("saturation1k", (int) (player.getFoodStats().getSaturationLevel() * 1000));
		setValue("exhaustion1k", 0x00); // FIXME ^^^^ (fixme or not) Exhaustion Level is an internal value
		setValue("experience1k", (int) (player.experience * 1000));
		setValue("experience_level", player.experienceLevel);
		setValue("experience_total", player.experienceTotal);
	}
}
