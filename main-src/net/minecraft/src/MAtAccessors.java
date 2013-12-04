package net.minecraft.src;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.FoodStats;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldInfo;
import eu.ha3.mc.haddon.PrivateAccessException;
import eu.ha3.mc.haddon.Utility;

/* x-placeholder */

public class MAtAccessors
{
	@Deprecated
	public static FoodStats getFoodStatsOf(EntityPlayerSP player)
	{
		return player.getFoodStats();
	}
	
	public static boolean getIsJumpingOf(EntityPlayerSP player)
	{
		return player.isJumping;
	}
	
	public static boolean getIsInWebOf(EntityPlayerSP player)
	{
		return player.isInWeb;
	}
	
	@Deprecated
	public static WorldInfo getWorldInfoOf(World world)
	{
		return world.getWorldInfo();
	}
	
	public static SoundPool getSoundPoolSounds(Utility util)
	{
		try
		{
			return (SoundPool) util.getPrivateValueLiteral(
				net.minecraft.src.SoundManager.class, Minecraft.getMinecraft().sndManager, "d", 3);
		}
		catch (PrivateAccessException e)
		{
			throwMismatchingReflection(e);
			return null;
		}
	}
	
	private static void throwMismatchingReflection(Exception e)
	{
		throw new RuntimeException("Mismatching reflection " + e.getMessage());
	}
}
