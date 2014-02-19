package eu.ha3.matmos.game.tobedeprecated;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.UnresolvedPrivateAccessors__entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityMinecartEmpty;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldInfo;
import eu.ha3.matmos.engine0.core.interfaces.Data;
import eu.ha3.matmos.game.data.MAT_HARD_LIMITS;
import eu.ha3.matmos.game.data.abstractions.processor.MAtProcessorModel;
import eu.ha3.matmos.game.system.MAtMod;
import eu.ha3.matmos.game.system.MAtmosUtility;

/* x-placeholder */

@Deprecated
public class MAtProcessorFrequent extends MAtProcessorModel
{
	@Deprecated
	public MAtProcessorFrequent(MAtMod modIn, Data dataIn, String normalNameIn, String deltaNameIn)
	{
		super(modIn, dataIn, normalNameIn, deltaNameIn);
	}
	
	@Override
	protected void doProcess()
	{
		Minecraft mc = Minecraft.getMinecraft();
		World w = mc.theWorld;
		WorldInfo worldinfo = w.getWorldInfo();
		EntityPlayerSP player = mc.thePlayer;
		
		int x = (int) Math.floor(player.posX);
		int y = (int) Math.floor(player.posY);
		int z = (int) Math.floor(player.posZ);
		
		boolean mouseOverATile = mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectType.BLOCK;
		
		// Get the Sky Light value
		conversionComplete(0, w.getSavedLightValue(EnumSkyBlock.Sky, x, y, z));
		
		// Get the Artificial Light value
		conversionComplete(1, w.getSavedLightValue(EnumSkyBlock.Block, x, y, z));
		
		// Get the Block Light value
		conversionComplete(2, w.getBlockLightValue(x, y, z));
		
		// Get the World Time modulo 24KL
		conversionComplete(3, (int) (worldinfo.getWorldTime() % 24000L));
		
		// Get the Altitude (Y)
		conversionComplete(4, y);
		
		// 5 : (RELAXED) 
		
		// Get if Is in Water
		conversionComplete(6, player.isInWater() ? 1 : 0);
		
		// Get if It's Raining
		conversionComplete(7, worldinfo.isRaining() ? 1 : 0);
		
		// Get if It's Thundering (regardless of rain)
		conversionComplete(8, worldinfo.isThundering() ? 1 : 0);
		
		// Get if the Current block player is on is exposed to the sky
		conversionComplete(9, w.canBlockSeeTheSky(x, y, z) ? 1 : 0);
		
		// Get if Player is un the Nether
		conversionComplete(10, player.dimension == -1 ? 1 : 0);
		
		// Get the Skylight level subtracted (the amount of light stripped from Skylight)
		conversionComplete(11, w.skylightSubtracted);
		
		// [12,18] (RELAXED)
		
		// Get if Player is inside of Water material
		conversionComplete(19, player.isWet() ? 1 : 0);
		
		// Get if Player X (Floored (Double) Player X, casted into to Integer)
		conversionComplete(20, x);
		
		// Get if Player Z (Floored (Double) Player Z, casted into to Integer)
		conversionComplete(21, z);
		
		// Get if Player is on Ground
		conversionComplete(22, player.onGround ? 1 : 0);
		
		// Get Player oxygen amount (Integer)
		conversionComplete(23, player.getAir());
		
		// Get Player health amount (Integer)
		conversionComplete(24, (int) Math.ceil(player.getHealth())); // HEALTH is now a float.
		
		// Get Player dimension (Integer)
		conversionComplete(25, player.dimension);
		
		setValueLegacyIntIndexes(26, w.canBlockSeeTheSky(x, y, z) && !(w.getTopSolidOrLiquidBlock(x, z) > y) ? 1 : 0);
		
		conversionComplete(27, w.getTopSolidOrLiquidBlock(x, z));
		
		conversionComplete(28, w.getTopSolidOrLiquidBlock(x, z) - y);
		
		// [29,31] : (RELAXED) 
		
		conversionComplete(
			32, player.inventory.getCurrentItem() != null
				? MAtmosUtility.nameOf(player.inventory.getCurrentItem()) : MAT_HARD_LIMITS.NO_ITEM);
		//setValue( 32, player.getHeldItem() != null ? player.getHeldItem().itemID : -1 );
		
		conversionComplete(33, (int) Math.round(player.motionX * 1000));
		
		conversionComplete(34, (int) Math.round(player.motionY * 1000));
		
		conversionComplete(35, (int) Math.round(player.motionZ * 1000));
		
		//setValueLegacyIntIndexes(
		//	36, y >= 1 && y < MAT_HARD_LIMITS.USE_MAX_HEIGHT_LIMIT
		//		? Version170Helper.getNameAt(x, y - 1, z) : MAT_HARD_LIMITS.NO_BLOCK_OUT_OF_BOUNDS);
		conversionComplete(36, MAtmosUtility.getNameAt(x, y - 1, z, MAT_HARD_LIMITS.NO_BLOCK_OUT_OF_BOUNDS));
		
		//setValueLegacyIntIndexes(
		//	37, y >= 2 && y < MAT_HARD_LIMITS.USE_MAX_HEIGHT_LIMIT
		//		? Version170Helper.getNameAt(x, y - 2, z) : MAT_HARD_LIMITS.NO_BLOCK_OUT_OF_BOUNDS);
		conversionComplete(37, MAtmosUtility.getNameAt(x, y - 2, z, MAT_HARD_LIMITS.NO_BLOCK_OUT_OF_BOUNDS));
		
		conversionComplete(38, (int) mod().util().getClientTick());
		
		conversionComplete(39, player.isBurning() ? 1 : 0); // XXX ERROR NOW IS A PRIVATE VALUE
		
		conversionComplete(40, (int) Math.floor(player.swingProgress * 16));
		
		conversionComplete(41, player.swingProgress != 0 ? 1 : 0); // is swinging
		
		conversionComplete(42, UnresolvedPrivateAccessors__entity.getInstance().isJumping(player) ? 1 : 0);
		
		conversionComplete(43, (int) (player.fallDistance * 1000));
		
		conversionComplete(44, UnresolvedPrivateAccessors__entity.getInstance().isInWeb(player) ? 1 : 0);
		
		int mxx = (int) Math.round(player.motionX * 1000);
		int mzz = (int) Math.round(player.motionZ * 1000);
		
		conversionComplete(45, (int) Math.floor(Math.sqrt(mxx * mxx + mzz * mzz)));
		
		conversionComplete(46, player.inventory.currentItem);
		
		conversionComplete(47, mc.objectMouseOver != null ? 1 : 0);
		
		conversionComplete(48, mc.objectMouseOver != null ? mc.objectMouseOver.typeOfHit.ordinal() : -1);
		
		conversionComplete(49, player.isBurning() ? 1 : 0);
		
		conversionComplete(50, player.getTotalArmorValue());
		
		conversionComplete(51, player.getFoodStats().getFoodLevel());
		
		conversionComplete(52, (int) (player.getFoodStats().getSaturationLevel() * 1000));
		
		conversionComplete(53, 0); // TODO (Food Exhaustion Level)
		
		conversionComplete(54, (int) (player.experience * 1000));
		
		conversionComplete(55, player.experienceLevel);
		
		conversionComplete(56, player.experienceTotal);
		
		conversionComplete(57, player.isOnLadder() ? 1 : 0);
		
		conversionComplete(58, player.getItemInUseDuration());
		
		// ---- / --- // / / setValue( 59, player.inventory.func_35157_d(Item.arrow.shiftedIndex) ? 1 : 0);
		setValueLegacyIntIndexes(59, 0);
		
		conversionComplete(60, player.isBlocking() ? 1 : 0);
		
		conversionComplete(61, 72000 - player.getItemInUseDuration());
		
		conversionComplete(62, player.inventory.getCurrentItem() == null ? -1 : player.inventory
			.getCurrentItem().getItemDamage());
		
		conversionComplete(63, player.isSprinting() ? 1 : 0);
		
		conversionComplete(64, player.isSneaking() ? 1 : 0);
		
		conversionComplete(65, player.isAirBorne ? 1 : 0);
		
		conversionComplete(66, player.isUsingItem() ? 1 : 0);
		
		conversionComplete(67, player.isRiding() ? 1 : 0);
		
		conversionComplete(68, player.ridingEntity != null
			&& player.ridingEntity.getClass() == EntityMinecartEmpty.class ? 1 : 0);
		
		conversionComplete(69, player.ridingEntity != null && player.ridingEntity.getClass() == EntityBoat.class
			? 1 : 0);
		
		conversionComplete(70, mc.playerController != null && mc.playerController.isInCreativeMode() ? 1 : 0);
		
		int rmx = player.ridingEntity != null ? (int) Math.round(player.ridingEntity.motionX * 1000) : 0;
		conversionComplete(71, rmx);
		
		int rmy = player.ridingEntity != null ? (int) Math.round(player.ridingEntity.motionY * 1000) : 0;
		conversionComplete(72, rmy);
		
		int rmz = player.ridingEntity != null ? (int) Math.round(player.ridingEntity.motionZ * 1000) : 0;
		conversionComplete(73, rmz);
		
		int rmxx = player.ridingEntity != null ? (int) Math.round(player.ridingEntity.motionX * 1000) : 0;
		int rmzz = player.ridingEntity != null ? (int) Math.round(player.ridingEntity.motionZ * 1000) : 0;
		conversionComplete(74, player.ridingEntity != null ? (int) Math.floor(Math.sqrt(rmxx * rmxx + rmzz * rmzz)) : 0);
		
		// 75-85 relaxed server
		
		conversionComplete(
			86,
			mouseOverATile ? MAtmosUtility.getNameAt(
				mc.objectMouseOver.blockX, mc.objectMouseOver.blockY, mc.objectMouseOver.blockZ,
				MAT_HARD_LIMITS.NO_BLOCK_IN_THIS_CONTEXT) : MAT_HARD_LIMITS.NO_BLOCK_IN_THIS_CONTEXT);
		
		conversionComplete(
			87,
			mouseOverATile ? w.getBlockMetadata(
				mc.objectMouseOver.blockX, mc.objectMouseOver.blockY, mc.objectMouseOver.blockZ) : 0);
		
		// 88 - moon phase
		
		conversionComplete(
			89,
			player.inventory.armorInventory[0] != null
				? MAtmosUtility.nameOf(player.inventory.armorInventory[0]) : MAT_HARD_LIMITS.NO_ITEM);
		
		conversionComplete(
			90,
			player.inventory.armorInventory[1] != null
				? MAtmosUtility.nameOf(player.inventory.armorInventory[1]) : MAT_HARD_LIMITS.NO_ITEM);
		
		conversionComplete(
			91,
			player.inventory.armorInventory[2] != null
				? MAtmosUtility.nameOf(player.inventory.armorInventory[2]) : MAT_HARD_LIMITS.NO_ITEM);
		
		conversionComplete(
			92,
			player.inventory.armorInventory[3] != null
				? MAtmosUtility.nameOf(player.inventory.armorInventory[3]) : MAT_HARD_LIMITS.NO_ITEM);
		
		// 93 - ME BiomeID
		
		///setValueLegacyIntIndexes(
		///	94, y >= 0 && y < MAT_HARD_LIMITS.USE_MAX_HEIGHT_LIMIT
		///		? Version170Helper.getNameAt(x, y, z) : MAT_HARD_LIMITS.NO_BLOCK_OUT_OF_BOUNDS); 
		conversionComplete(94, MAtmosUtility.getNameAt(x, y, z, MAT_HARD_LIMITS.NO_BLOCK_OUT_OF_BOUNDS));
		
		///setValueLegacyIntIndexes(
		///	95, y >= 0 && y < mod().util().getWorldHeight() - 1
		///		? Version170Helper.getNameAt(x, y + 1, z) : MAT_HARD_LIMITS.NO_BLOCK_OUT_OF_BOUNDS);
		conversionComplete(95, MAtmosUtility.getNameAt(x, y + 1, z, MAT_HARD_LIMITS.NO_BLOCK_OUT_OF_BOUNDS));
		
		ItemStack currentItem = player.inventory.getCurrentItem();
		setValueLegacyIntIndexes(96, currentItem != null
			? MAtmosUtility.nameOf(currentItem) : MAT_HARD_LIMITS.NO_ITEM);
		
		conversionComplete(97, mc.currentScreen != null && mc.currentScreen instanceof GuiContainer ? 1 : 0);
		
		// 98 99 UNUSED
		
		conversionComplete(100, player.ridingEntity != null && player.ridingEntity instanceof EntityHorse ? 1 : 0);
		
	}
	
}
