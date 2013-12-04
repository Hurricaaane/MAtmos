package eu.ha3.matmos.v170helper;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/* x-placeholder */

public class Version170Helper
{
	public static Block getBlockAt(int x, int y, int z)
	{
		return getBlockAt(Minecraft.getMinecraft().theWorld, x, y, z);
	}
	
	public static String getNameAt(int x, int y, int z, String defaultIfFail)
	{
		if (y <= 0 || y >= Minecraft.getMinecraft().theWorld.getHeight())
			return defaultIfFail;
		
		try
		{
			return getNameAt(Minecraft.getMinecraft().theWorld, x, y, z);
		}
		catch (Exception e)
		{
			return defaultIfFail;
		}
	}
	
	private static Block getBlockAt(World world, int x, int y, int z)
	{
		Block block = world.func_147439_a(x, y, z);
		return block;
	}
	
	private static String getNameAt(World world, int x, int y, int z)
	{
		return nameOf(getBlockAt(world, x, y, z));
	}
	
	//
	
	public static String nameOf(Block block)
	{
		return null;
	}
	
	public static String nameOf(ItemStack itemStack)
	{
		return null;
	}
	
	public static String nameOf(Item item)
	{
		return null;
	}
	
}
