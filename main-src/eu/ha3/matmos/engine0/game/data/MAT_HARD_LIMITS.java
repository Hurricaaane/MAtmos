package eu.ha3.matmos.engine0.game.data;

// TODO 2013-12 : Remove all hard limits
public class MAT_HARD_LIMITS
{
	@Deprecated
	/**
	 * Support for custom enchantments
	 */
	public static int ENCHANTMENTS_COUNT = 64;
	
	@Deprecated
	/**
	 * Support for custom or variable world heights
	 */
	public static int USE_MAX_HEIGHT_LIMIT = 256;
	
	/**
	 * This is a magic value to designate a block that is outside the world
	 * actual bounds
	 */
	public static String NO_BLOCK_OUT_OF_BOUNDS = "";
	
	/**
	 * This is a magic value to designate a failed detection of block that is
	 * unrelated to the world height.
	 */
	public static String NO_BLOCK_IN_THIS_CONTEXT = "";
	
	/**
	 * This is a magic value to designate the lack of equipped item.
	 */
	public static String NO_ITEM = "";
}
