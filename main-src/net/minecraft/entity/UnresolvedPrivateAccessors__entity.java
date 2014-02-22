package net.minecraft.entity;

/*
--filenotes-placeholder
*/

@Deprecated
public class UnresolvedPrivateAccessors__entity
{
	private static final UnresolvedPrivateAccessors__entity instance = new UnresolvedPrivateAccessors__entity();
	
	private UnresolvedPrivateAccessors__entity()
	{
		System.err.println(UnresolvedPrivateAccessors__entity.class.toString() + " WON'T WORK");
	}
	
	public static UnresolvedPrivateAccessors__entity getInstance()
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
