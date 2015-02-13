package eu.ha3.matmos.game.system;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

/* x-placeholder */

public class MAtmosUtility
{
    // dag edit - added getPlayerPosition()
    public static BlockPos getPlayerPosition()
    {
        return Minecraft.getMinecraft().thePlayer.getPosition();
    }

	public static int getPlayerX()
	{
		return (int) Math.floor(Minecraft.getMinecraft().thePlayer.posX);
	}
	
	public static int getPlayerY()
	{
		return (int) Math.floor(Minecraft.getMinecraft().thePlayer.posY);
	}
	
	public static int getPlayerZ()
	{
		return (int) Math.floor(Minecraft.getMinecraft().thePlayer.posZ);
	}
	
	public static boolean isUnderwaterAnyGamemode()
	{
		return Minecraft.getMinecraft().thePlayer.isInsideOfMaterial(Material.water);
	}
	
	/**
	 * Tells if y is within the height boundaries of the current world, where
	 * blocks can exist.
	 * 
	 * @param y
	 * @return
	 */
	public static boolean isWithinBounds(int y)
	{
		return y >= 0 && y < Minecraft.getMinecraft().theWorld.getHeight();
	}
	
	/**
	 * Clamps the y value to something that is within the current worlds'
	 * boundaries.
	 * 
	 * @param y
	 * @return
	 */
	public static int clampToBounds(int y)
	{
		if (y < 0)
			return 0;
		
		if (y >= Minecraft.getMinecraft().theWorld.getHeight())
			return Minecraft.getMinecraft().theWorld.getHeight() - 1;
		
		return y;
	}
	
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
		if (!isWithinBounds(y))
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
        // dag edit getBlock(...) -> getBlockState(...).getBlock()
		Block block = world.getBlockState(new BlockPos(x, y, z)).getBlock();
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
        // dag edit + .toString() [check]
		return Block.blockRegistry.getNameForObject(block).toString();
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
		return nameOf(itemStack.getItem());
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
        // dag edit + .toString() [check]
		return Item.itemRegistry.getNameForObject(item).toString();
	}
	
	public static boolean isSoundMasterEnabled()
	{
		return Minecraft.getMinecraft().gameSettings.getSoundLevel(SoundCategory.MASTER) > 0f;
	}
	
	public static boolean isSoundAmbientEnabled()
	{
		return Minecraft.getMinecraft().gameSettings.getSoundLevel(SoundCategory.AMBIENT) > 0f;
	}
	
	/**
	 * Play a sound.
	 * 
	 * @param name
	 * @param nx
	 * @param ny
	 * @param nz
	 * @param volume
	 * @param pitch
	 * @param attenuation
	 * @param rollf
	 */
	public static void playSound(
		String name, float nx, float ny, float nz, float volume, float pitch, int attenuation, float rollf)
	{
		Minecraft.getMinecraft().theWorld.playSound(nx, ny, nz, name, volume, pitch, false);
	}
	
	/**
	 * Play a sound.
	 * 
	 * @param name
	 * @param nx
	 * @param ny
	 * @param nz
	 * @param volume
	 * @param pitch
	 */
	public static void playSound(String name, float nx, float ny, float nz, float volume, float pitch)
	{
		Minecraft.getMinecraft().theWorld.playSound(nx, ny, nz, name, volume, pitch, false);
	}
	
	/**
	 * Returns the PowerMeta of the block at the specified coordinates.<br>
	 * The PowerMeta is a string that combines the block name and the metadata
	 * of a certain block.
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @param defaultIfFail
	 * @return
	 */
	public static String getPowerMetaAt(int x, int y, int z, String defaultIfFail)
	{
		if (!isWithinBounds(y))
			return defaultIfFail;
		
		try
		{
            // dag edit
            IBlockState blockState = Minecraft.getMinecraft().theWorld.getBlockState(new BlockPos(x, y, z));
			return asPowerMeta(
				getNameAt(Minecraft.getMinecraft().theWorld, x, y, z),
                blockState.getBlock().getMetaFromState(blockState));
		}
		catch (Exception e)
		{
			return defaultIfFail;
		}
	}
	
	/**
	 * Returns the PowerMeta, a string that combines the item name and the
	 * metadata of a certain block.
	 * 
	 * @param item
	 * @return
	 */
	public static String asPowerMeta(ItemStack item)
	{
		return asPowerMeta(nameOf(item.getItem()), item.getMetadata());
	}
	
	/**
	 * Returns the PowerMeta, a string that combines the item name and the
	 * metadata of a certain block.
	 * 
	 * @param item
	 * @param meta
	 * @return
	 */
	public static String asPowerMeta(Item item, int meta)
	{
		return asPowerMeta(nameOf(item), meta);
	}
	
	/**
	 * Returns the PowerMeta, a string that combines the block name and the
	 * metadata of a certain block.
	 * 
	 * @param block
	 * @param meta
	 * @return
	 */
	public static String asPowerMeta(Block block, int meta)
	{
		return asPowerMeta(nameOf(block), meta);
	}
	
	/**
	 * Returns the PowerMeta, a string that combines the item/block name and its
	 * metadata.
	 * 
	 * @param block
	 * @param meta
	 * @return
	 */
	public static String asPowerMeta(String block, int meta)
	{
		return block + "^" + Integer.toString(meta);
	}
	
	/**
	 * Returns the metadata of a certain block at the specified coordinates.
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @param defaultIfFail
	 * @return
	 */
	public static int getMetaAt(int x, int y, int z, int defaultIfFail)
	{
		if (!isWithinBounds(y))
			return defaultIfFail;
		
		try
		{
            // dag edit - use BlockState
            IBlockState blockState = Minecraft.getMinecraft().theWorld.getBlockState(new BlockPos(x, y, z));
			return blockState.getBlock().getMetaFromState(blockState);
		}
		catch (Exception e)
		{
			return defaultIfFail;
		}
	}
	
	/**
	 * Returns the metadata of a certain block at the specified coordinates.
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @param defaultIfFail
	 * @return
	 */
	public static String getMetaAsStringAt(int x, int y, int z, String defaultIfFail)
	{
		if (!isWithinBounds(y))
			return defaultIfFail;
		
		try
		{
            // dag edit - use BlockState
            IBlockState blockState = Minecraft.getMinecraft().theWorld.getBlockState(new BlockPos(x, y, z));
			return Integer.toString(blockState.getBlock().getMetaFromState(blockState));
		}
		catch (Exception e)
		{
			return defaultIfFail;
		}
	}
	
	/**
	 * Returns the legacy number value of an item stack.
	 * 
	 * @param itemStack
	 * @return
	 */
	public static int legacyOf(ItemStack itemStack)
	{
		// RegistryNamespaced
		return Item.itemRegistry.getIDForObject(itemStack.getItem());
	}
	
	/**
	 * Returns the legacy number value of a block.
	 * 
	 * @param block
	 * @return
	 */
	public static int legacyOf(Block block)
	{
		// RegistryNamespaced
		return Block.blockRegistry.getIDForObject(block);
	}
	
	public static String sanitizeUniqueName(String name)
	{
		return name.replaceAll("[^a-zA-Z0-9.-_]", "");
	}
}
