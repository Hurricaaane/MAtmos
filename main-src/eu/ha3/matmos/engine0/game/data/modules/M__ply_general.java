package eu.ha3.matmos.engine0.game.data.modules;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.MAtAccessor_NetMinecraftEntity;
import eu.ha3.matmos.engine0.core.interfaces.Data;
import eu.ha3.matmos.engine0.game.data.abstractions.module.Module;
import eu.ha3.matmos.engine0.game.data.abstractions.module.ModuleProcessor;

/*
--filenotes-placeholder
*/

public class M__ply_general extends ModuleProcessor implements Module
{
	public M__ply_general(Data data)
	{
		super(data, "ply_general");
	}
	
	@Override
	protected void doProcess()
	{
		EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
		
		setValue("in_water", player.isInWater());
		setValue("wet", player.isWet());
		setValue("on_ground", player.onGround);
		setValue("oxygen", player.getAir());
		setValue("burning", player.isBurning());
		setValue("jumping", MAtAccessor_NetMinecraftEntity.getInstance().isJumping(player));
		setValue("in_web", MAtAccessor_NetMinecraftEntity.getInstance().isInWeb(player));
		setValue("on_ladder", player.isOnLadder());
		
		setValue("blocking", player.isBlocking());
		setValue("sprinting", player.isSprinting());
		setValue("sneaking", player.isSneaking());
		setValue("airborne", player.isAirBorne);
		setValue("using_item", player.isUsingItem());
		setValue("riding", player.isRiding());
		setValue("creative", Minecraft.getMinecraft().playerController != null
			&& Minecraft.getMinecraft().playerController.isInCreativeMode());
	}
}
