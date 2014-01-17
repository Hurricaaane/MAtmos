package eu.ha3.matmos.engine0.game.data;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.MAtAccessor_NetMinecraftEntity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityMinecartEmpty;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldInfo;
import eu.ha3.matmos.engine0.core.interfaces.Data;
import eu.ha3.matmos.engine0.game.data.abstractions.processor.MAtProcessorModel;
import eu.ha3.matmos.engine0.game.system.MAtMod;
import eu.ha3.matmos.v170helper.Version170Helper;

/* x-placeholder */

public class MAtProcessorFrequent extends MAtProcessorModel
{
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
		setValueLegacyIntIndexes(0, w.getSavedLightValue(EnumSkyBlock.Sky, x, y, z));
		
		// Get the Artificial Light value
		setValueLegacyIntIndexes(1, w.getSavedLightValue(EnumSkyBlock.Block, x, y, z));
		
		// Get the Block Light value
		setValueLegacyIntIndexes(2, w.getBlockLightValue(x, y, z));
		
		// Get the World Time modulo 24KL
		setValueLegacyIntIndexes(3, (int) (worldinfo.getWorldTime() % 24000L));
		
		// Get the Altitude (Y)
		setValueLegacyIntIndexes(4, y);
		
		// 5 : (RELAXED) 
		
		// Get if Is in Water
		setValueLegacyIntIndexes(6, player.isInWater() ? 1 : 0);
		
		// Get if It's Raining
		setValueLegacyIntIndexes(7, worldinfo.isRaining() ? 1 : 0);
		
		// Get if It's Thundering (regardless of rain)
		setValueLegacyIntIndexes(8, worldinfo.isThundering() ? 1 : 0);
		
		// Get if the Current block player is on is exposed to the sky
		setValueLegacyIntIndexes(9, w.canBlockSeeTheSky(x, y, z) ? 1 : 0);
		
		// Get if Player is un the Nether
		setValueLegacyIntIndexes(10, player.dimension == -1 ? 1 : 0);
		
		// Get the Skylight level subtracted (the amount of light stripped from Skylight)
		setValueLegacyIntIndexes(11, w.skylightSubtracted);
		
		// [12,18] (RELAXED)
		
		// Get if Player is inside of Water material
		setValueLegacyIntIndexes(19, player.isWet() ? 1 : 0);
		
		// Get if Player X (Floored (Double) Player X, casted into to Integer)
		setValueLegacyIntIndexes(20, x);
		
		// Get if Player Z (Floored (Double) Player Z, casted into to Integer)
		setValueLegacyIntIndexes(21, z);
		
		// Get if Player is on Ground
		setValueLegacyIntIndexes(22, player.onGround ? 1 : 0);
		
		// Get Player oxygen amount (Integer)
		setValueLegacyIntIndexes(23, player.getAir());
		
		// Get Player health amount (Integer)
		setValueLegacyIntIndexes(24, (int) Math.ceil(player.getHealth())); // HEALTH is now a float.
		
		// Get Player dimension (Integer)
		setValueLegacyIntIndexes(25, player.dimension);
		
		setValueLegacyIntIndexes(26, w.canBlockSeeTheSky(x, y, z) && !(w.getTopSolidOrLiquidBlock(x, z) > y) ? 1 : 0);
		
		setValueLegacyIntIndexes(27, w.getTopSolidOrLiquidBlock(x, z));
		
		setValueLegacyIntIndexes(28, w.getTopSolidOrLiquidBlock(x, z) - y);
		
		// [29,31] : (RELAXED) 
		
		setValueLegacyIntIndexes(
			32, player.inventory.getCurrentItem() != null
				? Version170Helper.nameOf(player.inventory.getCurrentItem()) : MAT_HARD_LIMITS.NO_ITEM);
		//setValue( 32, player.getHeldItem() != null ? player.getHeldItem().itemID : -1 );
		
		setValueLegacyIntIndexes(33, (int) Math.round(player.motionX * 1000));
		
		setValueLegacyIntIndexes(34, (int) Math.round(player.motionY * 1000));
		
		setValueLegacyIntIndexes(35, (int) Math.round(player.motionZ * 1000));
		
		//setValueLegacyIntIndexes(
		//	36, y >= 1 && y < MAT_HARD_LIMITS.USE_MAX_HEIGHT_LIMIT
		//		? Version170Helper.getNameAt(x, y - 1, z) : MAT_HARD_LIMITS.NO_BLOCK_OUT_OF_BOUNDS);
		setValueLegacyIntIndexes(36, Version170Helper.getNameAt(x, y - 1, z, MAT_HARD_LIMITS.NO_BLOCK_OUT_OF_BOUNDS));
		
		//setValueLegacyIntIndexes(
		//	37, y >= 2 && y < MAT_HARD_LIMITS.USE_MAX_HEIGHT_LIMIT
		//		? Version170Helper.getNameAt(x, y - 2, z) : MAT_HARD_LIMITS.NO_BLOCK_OUT_OF_BOUNDS);
		setValueLegacyIntIndexes(37, Version170Helper.getNameAt(x, y - 2, z, MAT_HARD_LIMITS.NO_BLOCK_OUT_OF_BOUNDS));
		
		setValueLegacyIntIndexes(38, (int) mod().util().getClientTick());
		
		setValueLegacyIntIndexes(39, player.isBurning() ? 1 : 0); // XXX ERROR NOW IS A PRIVATE VALUE
		
		setValueLegacyIntIndexes(40, (int) Math.floor(player.swingProgress * 16));
		
		setValueLegacyIntIndexes(41, player.swingProgress != 0 ? 1 : 0); // is swinging
		
		setValueLegacyIntIndexes(42, MAtAccessor_NetMinecraftEntity.getInstance().isJumping(player) ? 1 : 0);
		
		setValueLegacyIntIndexes(43, (int) (player.fallDistance * 1000));
		
		setValueLegacyIntIndexes(44, MAtAccessor_NetMinecraftEntity.getInstance().isInWeb(player) ? 1 : 0);
		
		int mxx = (int) Math.round(player.motionX * 1000);
		int mzz = (int) Math.round(player.motionZ * 1000);
		
		setValueLegacyIntIndexes(45, (int) Math.floor(Math.sqrt(mxx * mxx + mzz * mzz)));
		
		setValueLegacyIntIndexes(46, player.inventory.currentItem);
		
		setValueLegacyIntIndexes(47, mc.objectMouseOver != null ? 1 : 0);
		
		setValueLegacyIntIndexes(48, mc.objectMouseOver != null ? mc.objectMouseOver.typeOfHit.ordinal() : -1);
		
		setValueLegacyIntIndexes(49, player.isBurning() ? 1 : 0);
		
		setValueLegacyIntIndexes(50, player.getTotalArmorValue());
		
		setValueLegacyIntIndexes(51, player.getFoodStats().getFoodLevel());
		
		setValueLegacyIntIndexes(52, (int) (player.getFoodStats().getSaturationLevel() * 1000));
		
		setValueLegacyIntIndexes(53, 0); // TODO (Food Exhaustion Level)
		
		setValueLegacyIntIndexes(54, (int) (player.experience * 1000));
		
		setValueLegacyIntIndexes(55, player.experienceLevel);
		
		setValueLegacyIntIndexes(56, player.experienceTotal);
		
		setValueLegacyIntIndexes(57, player.isOnLadder() ? 1 : 0);
		
		setValueLegacyIntIndexes(58, player.getItemInUseDuration());
		
		// ---- / --- // / / setValue( 59, player.inventory.func_35157_d(Item.arrow.shiftedIndex) ? 1 : 0);
		setValueLegacyIntIndexes(59, 0);
		
		setValueLegacyIntIndexes(60, player.isBlocking() ? 1 : 0);
		
		setValueLegacyIntIndexes(61, 72000 - player.getItemInUseDuration());
		
		setValueLegacyIntIndexes(62, player.inventory.getCurrentItem() == null ? -1 : player.inventory
			.getCurrentItem().getItemDamage());
		
		setValueLegacyIntIndexes(63, player.isSprinting() ? 1 : 0);
		
		setValueLegacyIntIndexes(64, player.isSneaking() ? 1 : 0);
		
		setValueLegacyIntIndexes(65, player.isAirBorne ? 1 : 0);
		
		setValueLegacyIntIndexes(66, player.isUsingItem() ? 1 : 0);
		
		setValueLegacyIntIndexes(67, player.isRiding() ? 1 : 0);
		
		setValueLegacyIntIndexes(68, player.ridingEntity != null
			&& player.ridingEntity.getClass() == EntityMinecartEmpty.class ? 1 : 0);
		
		setValueLegacyIntIndexes(69, player.ridingEntity != null && player.ridingEntity.getClass() == EntityBoat.class
			? 1 : 0);
		
		setValueLegacyIntIndexes(70, mc.playerController != null && mc.playerController.isInCreativeMode() ? 1 : 0);
		
		int rmx = player.ridingEntity != null ? (int) Math.round(player.ridingEntity.motionX * 1000) : 0;
		setValueLegacyIntIndexes(71, rmx);
		
		int rmy = player.ridingEntity != null ? (int) Math.round(player.ridingEntity.motionY * 1000) : 0;
		setValueLegacyIntIndexes(72, rmy);
		
		int rmz = player.ridingEntity != null ? (int) Math.round(player.ridingEntity.motionZ * 1000) : 0;
		setValueLegacyIntIndexes(73, rmz);
		
		int rmxx = player.ridingEntity != null ? (int) Math.round(player.ridingEntity.motionX * 1000) : 0;
		int rmzz = player.ridingEntity != null ? (int) Math.round(player.ridingEntity.motionZ * 1000) : 0;
		setValueLegacyIntIndexes(
			74, player.ridingEntity != null ? (int) Math.floor(Math.sqrt(rmxx * rmxx + rmzz * rmzz)) : 0);
		
		// 75-85 relaxed server
		
		setValueLegacyIntIndexes(
			86,
			mouseOverATile ? Version170Helper.getNameAt(
				mc.objectMouseOver.blockX, mc.objectMouseOver.blockY, mc.objectMouseOver.blockZ,
				MAT_HARD_LIMITS.NO_BLOCK_IN_THIS_CONTEXT) : MAT_HARD_LIMITS.NO_BLOCK_IN_THIS_CONTEXT);
		
		setValueLegacyIntIndexes(
			87,
			mouseOverATile ? w.getBlockMetadata(
				mc.objectMouseOver.blockX, mc.objectMouseOver.blockY, mc.objectMouseOver.blockZ) : 0);
		
		// 88 - moon phase
		
		setValueLegacyIntIndexes(
			89,
			player.inventory.armorInventory[0] != null
				? Version170Helper.nameOf(player.inventory.armorInventory[0]) : MAT_HARD_LIMITS.NO_ITEM);
		
		setValueLegacyIntIndexes(
			90,
			player.inventory.armorInventory[1] != null
				? Version170Helper.nameOf(player.inventory.armorInventory[1]) : MAT_HARD_LIMITS.NO_ITEM);
		
		setValueLegacyIntIndexes(
			91,
			player.inventory.armorInventory[2] != null
				? Version170Helper.nameOf(player.inventory.armorInventory[2]) : MAT_HARD_LIMITS.NO_ITEM);
		
		setValueLegacyIntIndexes(
			92,
			player.inventory.armorInventory[3] != null
				? Version170Helper.nameOf(player.inventory.armorInventory[3]) : MAT_HARD_LIMITS.NO_ITEM);
		
		// 93 - ME BiomeID
		
		///setValueLegacyIntIndexes(
		///	94, y >= 0 && y < MAT_HARD_LIMITS.USE_MAX_HEIGHT_LIMIT
		///		? Version170Helper.getNameAt(x, y, z) : MAT_HARD_LIMITS.NO_BLOCK_OUT_OF_BOUNDS); 
		setValueLegacyIntIndexes(94, Version170Helper.getNameAt(x, y, z, MAT_HARD_LIMITS.NO_BLOCK_OUT_OF_BOUNDS));
		
		///setValueLegacyIntIndexes(
		///	95, y >= 0 && y < mod().util().getWorldHeight() - 1
		///		? Version170Helper.getNameAt(x, y + 1, z) : MAT_HARD_LIMITS.NO_BLOCK_OUT_OF_BOUNDS);
		setValueLegacyIntIndexes(95, Version170Helper.getNameAt(x, y + 1, z, MAT_HARD_LIMITS.NO_BLOCK_OUT_OF_BOUNDS));
		
		ItemStack currentItem = player.inventory.getCurrentItem();
		setValueLegacyIntIndexes(96, currentItem != null
			? Version170Helper.nameOf(currentItem) : MAT_HARD_LIMITS.NO_ITEM);
		
		setValueLegacyIntIndexes(97, mc.currentScreen != null && mc.currentScreen instanceof GuiContainer ? 1 : 0);
		
		// 98 99 UNUSED
		
		setValueLegacyIntIndexes(100, player.ridingEntity != null && player.ridingEntity instanceof EntityHorse ? 1 : 0);
		
	}
	
}
