package eu.ha3.matmos.v170helper;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/* x-placeholder */

public class Version170Helper
{
	/**
	 * Gets the block at a certain location in the current world. This method is
	 * not safe against locations in undefined space.
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public static Block getBlockAt(int x, int y, int z)
	{
		return getBlockAt(Minecraft.getMinecraft().theWorld, x, y, z);
	}
	
	/**
	 * Gets the name of the block at a certain location in the current world. If
	 * the location is in an undefined space (lower than zero or higher than the
	 * current world getHeight(), or throws any exception during evaluation), it
	 * will return a default string.
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @param defaultIfFail
	 * @return
	 */
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
	
	/**
	 * Gets the block at a certain location in the given world. This method is
	 * not safe against locations in undefined space.
	 * 
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	private static Block getBlockAt(World world, int x, int y, int z)
	{
		Block block = world.func_147439_a(x, y, z);
		return block;
	}
	
	/**
	 * Gets the name of the block at a certain location in the given world. This
	 * method is not safe against locations in undefined space.
	 * 
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	private static String getNameAt(World world, int x, int y, int z)
	{
		return nameOf(getBlockAt(world, x, y, z));
	}
	
	//
	
	/**
	 * Gets the unique name of a given block, defined by its interoperability
	 * identifier.
	 * 
	 * @param block
	 * @return
	 */
	public static String nameOf(Block block)
	{
		// RegistryNamespaced
		return Block.field_149771_c.func_148750_c(block);
	}
	
	/**
	 * Gets the unique name of a given itemstack's item.
	 * 
	 * @param itemStack
	 * @return
	 */
	public static String nameOf(ItemStack itemStack)
	{
		// RegistryNamespaced
		return Block.field_149771_c.func_148750_c(itemStack.getItem());
	}
	
	/**
	 * Gets the unique name of a given item.
	 * 
	 * @param item
	 * @return
	 */
	public static String nameOf(Item item)
	{
		// RegistryNamespaced
		return Block.field_149771_c.func_148750_c(item);
	}
	
	/**
	 * Returns the Minecraft sound volume as a scalar value.
	 * 
	 * @param item
	 * @return
	 */
	public static float getSoundVolume()
	{
		return Minecraft.getMinecraft().???;
	}
	
	/**
	 * Play a sound.
	 * 
	 * @param equivalent
	 * @param nx
	 * @param ny
	 * @param nz
	 * @param soundEffectiveVolume
	 * @param pitch
	 * @param attenuationNone
	 * @param f
	 */
	public static void playSound(
		String name, float nx, float ny, float nz, float volume, float pitch, int attenuation, float rollf)
	{
	}
}
