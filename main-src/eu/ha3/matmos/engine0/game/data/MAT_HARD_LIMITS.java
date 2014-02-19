package eu.ha3.matmos.engine0.game.data;

// TODO 2013-12 : Remove all hard limits
public class MAT_HARD_LIMITS
{
	public static final int LEGACY_NO_ITEM = -1;
	public static final int LEGACY_NO_BLOCK_IN_THIS_CONTEXT = 0;
	public static final int LEGACY_NO_BLOCK_OUT_OF_BOUNDS = 0;
	
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
