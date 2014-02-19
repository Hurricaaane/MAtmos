package net.minecraft.entity;

/*
--filenotes-placeholder
*/

@Deprecated
public class MAtAccessor_NetMinecraftEntity
{
	private static final MAtAccessor_NetMinecraftEntity instance = new MAtAccessor_NetMinecraftEntity();
	
	private MAtAccessor_NetMinecraftEntity()
	{
		System.err.println(MAtAccessor_NetMinecraftEntity.class.toString() + " WON'T WORK");
	}
	
	public static MAtAccessor_NetMinecraftEntity getInstance()
	{
		return instance;
	}
	
	@Deprecated
	public boolean isJumping(EntityLivingBase entityLiving)
	{
		return entityLiving.isJumping;
	}
	
	@Deprecated
	public boolean isInWeb(EntityLivingBase entityLiving)
	{
		return entityLiving.isInWeb;
	}
}
