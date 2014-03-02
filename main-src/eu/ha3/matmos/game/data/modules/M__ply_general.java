package eu.ha3.matmos.game.data.modules;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import eu.ha3.matmos.engine.core.interfaces.Data;
import eu.ha3.matmos.game.data.abstractions.module.Module;
import eu.ha3.matmos.game.data.abstractions.module.ModuleProcessor;
import eu.ha3.mc.haddon.PrivateAccessException;
import eu.ha3.mc.haddon.Utility;

/*
--filenotes-placeholder
*/

public class M__ply_general extends ModuleProcessor implements Module
{
	private final Utility util;
	
	public M__ply_general(Data data, Utility util)
	{
		super(data, "ply_general");
		this.util = util;
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
		//setValue("jumping", UnresolvedPrivateAccessors__entity.getInstance().isJumping(player));
		//setValue("in_web", UnresolvedPrivateAccessors__entity.getInstance().isInWeb(player));
		try
		{
			setValue("jumping", (Boolean) this.util.getPrivate(player, "isJumping"));
			setValue("in_web", (Boolean) this.util.getPrivate(player, "isInWeb"));
		}
		catch (PrivateAccessException e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}
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
