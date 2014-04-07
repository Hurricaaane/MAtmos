package eu.ha3.matmos.game.data;

import net.minecraft.src.EntityPlayerSP;
import net.minecraft.src.FoodStats;
import net.minecraft.src.Minecraft;
import net.minecraft.src.SoundManager;
import net.minecraft.src.SoundPool;
import net.minecraft.src.World;
import net.minecraft.src.WorldInfo;
import eu.ha3.mc.haddon.PrivateAccessException;
import eu.ha3.mc.haddon.Utility;

/*
            DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE 
                    Version 2, December 2004 

 Copyright (C) 2004 Sam Hocevar <sam@hocevar.net> 

 Everyone is permitted to copy and distribute verbatim or modified 
 copies of this license document, and changing it is allowed as long 
 as the name is changed. 

            DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE 
   TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION 

  0. You just DO WHAT THE FUCK YOU WANT TO. 
*/

public class MAtAccessors
{
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
