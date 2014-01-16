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
import eu.ha3.matmos.engine0.core.implem.SelfGeneratingData;
import eu.ha3.matmos.engine0.game.data.abstractions.processor.MAtProcessorModel;
import eu.ha3.matmos.engine0.game.system.MAtMod;
import eu.ha3.matmos.v170helper.Version170Helper;

/* x-placeholder */

public class MAtProcessorFrequent extends MAtProcessorModel
{
	public MAtProcessorFrequent(MAtMod modIn, SelfGeneratingData dataIn, String normalNameIn, String deltaNameIn)
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
		
		for (String sindex : getRequired())
		{
			// 1.7 DERAIL
			int index = Integer.parseInt(sindex);
			switch (index)
			{
			case 0:
				// Get the Sky Light value
				setValueLegacyIntIndexes(0, w.getSavedLightValue(EnumSkyBlock.Sky, x, y, z));
				break;
			
			case 1:
				// Get the Artificial Light value
				setValueLegacyIntIndexes(1, w.getSavedLightValue(EnumSkyBlock.Block, x, y, z));
				break;
			
			case 2:
				// Get the Block Light value
				setValueLegacyIntIndexes(2, w.getBlockLightValue(x, y, z));
				break;
			
			case 3:
				// Get the World Time modulo 24KL
				setValueLegacyIntIndexes(3, (int) (worldinfo.getWorldTime() % 24000L));
				break;
			
			case 4:
				// Get the Altitude (Y)
				setValueLegacyIntIndexes(4, y);
				break;
			
			// 5 : (RELAXED) 
			
			case 6:
				// Get if Is in Water
				setValueLegacyIntIndexes(6, player.isInWater() ? 1 : 0);
				break;
			
			case 7:
				// Get if It's Raining
				setValueLegacyIntIndexes(7, worldinfo.isRaining() ? 1 : 0);
				break;
			
			case 8:
				// Get if It's Thundering (regardless of rain)
				setValueLegacyIntIndexes(8, worldinfo.isThundering() ? 1 : 0);
				break;
			
			case 9:
				// Get if the Current block player is on is exposed to the sky
				setValueLegacyIntIndexes(9, w.canBlockSeeTheSky(x, y, z) ? 1 : 0);
				break;
			
			case 10:
				// Get if Player is un the Nether
				setValueLegacyIntIndexes(10, player.dimension == -1 ? 1 : 0);
				break;
			
			case 11:
				// Get the Skylight level subtracted (the amount of light stripped from Skylight)
				setValueLegacyIntIndexes(11, w.skylightSubtracted);
				break;
			
			// [12,18] (RELAXED)
			
			case 19:
				// Get if Player is inside of Water material
				setValueLegacyIntIndexes(19, player.isWet() ? 1 : 0);
				break;
			
			case 20:
				// Get if Player X (Floored (Double) Player X, casted into to Integer)
				setValueLegacyIntIndexes(20, x);
				break;
			
			case 21:
				// Get if Player Z (Floored (Double) Player Z, casted into to Integer)
				setValueLegacyIntIndexes(21, z);
				break;
			
			case 22:
				// Get if Player is on Ground
				setValueLegacyIntIndexes(22, player.onGround ? 1 : 0);
				break;
			
			case 23:
				// Get Player oxygen amount (Integer)
				setValueLegacyIntIndexes(23, player.getAir());
				break;
			
			case 24:
				// Get Player health amount (Integer)
				setValueLegacyIntIndexes(24, (int) Math.ceil(player.getHealth())); // HEALTH is now a float.
				break;
			
			case 25:
				// Get Player dimension (Integer)
				setValueLegacyIntIndexes(25, player.dimension);
				break;
			
			case 26:
				setValueLegacyIntIndexes(26, w.canBlockSeeTheSky(x, y, z) && !(w.getTopSolidOrLiquidBlock(x, z) > y)
					? 1 : 0);
				break;
			
			case 27:
				setValueLegacyIntIndexes(27, w.getTopSolidOrLiquidBlock(x, z));
				break;
			
			case 28:
				setValueLegacyIntIndexes(28, w.getTopSolidOrLiquidBlock(x, z) - y);
				break;
			
			// [29,31] : (RELAXED) 
			
			case 32:
				setValueLegacyIntIndexes(
					32,
					player.inventory.getCurrentItem() != null ? Version170Helper.nameOf(player.inventory
						.getCurrentItem()) : MAT_HARD_LIMITS.NO_ITEM);
				//setValue( 32, player.getHeldItem() != null ? player.getHeldItem().itemID : -1 );
				break;
			
			case 33:
				setValueLegacyIntIndexes(33, (int) Math.round(player.motionX * 1000));
				break;
			
			case 34:
				setValueLegacyIntIndexes(34, (int) Math.round(player.motionY * 1000));
				break;
			
			case 35:
				setValueLegacyIntIndexes(35, (int) Math.round(player.motionZ * 1000));
				break;
			
			case 36:
				//setValueLegacyIntIndexes(
				//	36, y >= 1 && y < MAT_HARD_LIMITS.USE_MAX_HEIGHT_LIMIT
				//		? Version170Helper.getNameAt(x, y - 1, z) : MAT_HARD_LIMITS.NO_BLOCK_OUT_OF_BOUNDS);
				setValueLegacyIntIndexes(
					36, Version170Helper.getNameAt(x, y - 1, z, MAT_HARD_LIMITS.NO_BLOCK_OUT_OF_BOUNDS));
				break;
			
			case 37:
				//setValueLegacyIntIndexes(
				//	37, y >= 2 && y < MAT_HARD_LIMITS.USE_MAX_HEIGHT_LIMIT
				//		? Version170Helper.getNameAt(x, y - 2, z) : MAT_HARD_LIMITS.NO_BLOCK_OUT_OF_BOUNDS);
				setValueLegacyIntIndexes(
					37, Version170Helper.getNameAt(x, y - 2, z, MAT_HARD_LIMITS.NO_BLOCK_OUT_OF_BOUNDS));
				break;
			
			case 38:
				setValueLegacyIntIndexes(38, (int) mod().util().getClientTick());
				break;
			
			case 39:
				setValueLegacyIntIndexes(39, player.isBurning() ? 1 : 0); // XXX ERROR NOW IS A PRIVATE VALUE
				break;
			
			case 40:
				setValueLegacyIntIndexes(40, (int) Math.floor(player.swingProgress * 16));
				break;
			
			case 41:
				setValueLegacyIntIndexes(41, player.swingProgress != 0 ? 1 : 0); // is swinging
				break;
			
			case 42:
				setValueLegacyIntIndexes(42, MAtAccessor_NetMinecraftEntity.getInstance().isJumping(player) ? 1 : 0);
				break;
			
			case 43:
				setValueLegacyIntIndexes(43, (int) (player.fallDistance * 1000));
				break;
			
			case 44:
				setValueLegacyIntIndexes(44, MAtAccessor_NetMinecraftEntity.getInstance().isInWeb(player) ? 1 : 0);
				break;
			
			case 45:
				int mxx = (int) Math.round(player.motionX * 1000);
				int mzz = (int) Math.round(player.motionZ * 1000);
				
				setValueLegacyIntIndexes(45, (int) Math.floor(Math.sqrt(mxx * mxx + mzz * mzz)));
				break;
			
			case 46:
				setValueLegacyIntIndexes(46, player.inventory.currentItem);
				break;
			
			case 47:
				setValueLegacyIntIndexes(47, mc.objectMouseOver != null ? 1 : 0);
				break;
			
			case 48:
				setValueLegacyIntIndexes(48, mc.objectMouseOver != null ? mc.objectMouseOver.typeOfHit.ordinal() : -1);
				break;
			
			case 49:
				setValueLegacyIntIndexes(49, player.isBurning() ? 1 : 0);
				break;
			
			case 50:
				setValueLegacyIntIndexes(50, player.getTotalArmorValue());
				break;
			
			case 51:
				setValueLegacyIntIndexes(51, player.getFoodStats().getFoodLevel());
				break;
			
			case 52:
				setValueLegacyIntIndexes(52, (int) (player.getFoodStats().getSaturationLevel() * 1000));
				break;
			
			case 53:
				setValueLegacyIntIndexes(53, 0); // TODO (Food Exhaustion Level)
				break;
			
			case 54:
				setValueLegacyIntIndexes(54, (int) (player.experience * 1000));
				break;
			
			case 55:
				setValueLegacyIntIndexes(55, player.experienceLevel);
				break;
			
			case 56:
				setValueLegacyIntIndexes(56, player.experienceTotal);
				break;
			
			case 57:
				setValueLegacyIntIndexes(57, player.isOnLadder() ? 1 : 0);
				break;
			
			case 58:
				setValueLegacyIntIndexes(58, player.getItemInUseDuration());
				break;
			
			case 59:
				// ---- / --- // / / setValue( 59, player.inventory.func_35157_d(Item.arrow.shiftedIndex) ? 1 : 0);
				setValueLegacyIntIndexes(59, 0);
				break;
			
			case 60:
				setValueLegacyIntIndexes(60, player.isBlocking() ? 1 : 0);
				break;
			
			case 61:
				setValueLegacyIntIndexes(61, 72000 - player.getItemInUseDuration());
				break;
			
			case 62:
				setValueLegacyIntIndexes(62, player.inventory.getCurrentItem() == null ? -1 : player.inventory
					.getCurrentItem().getItemDamage());
				break;
			
			case 63:
				setValueLegacyIntIndexes(63, player.isSprinting() ? 1 : 0);
				break;
			
			case 64:
				setValueLegacyIntIndexes(64, player.isSneaking() ? 1 : 0);
				break;
			
			case 65:
				setValueLegacyIntIndexes(65, player.isAirBorne ? 1 : 0);
				break;
			
			case 66:
				setValueLegacyIntIndexes(66, player.isUsingItem() ? 1 : 0);
				break;
			
			case 67:
				setValueLegacyIntIndexes(67, player.isRiding() ? 1 : 0);
				break;
			
			case 68:
				setValueLegacyIntIndexes(68, player.ridingEntity != null
					&& player.ridingEntity.getClass() == EntityMinecartEmpty.class ? 1 : 0);
				break;
			
			case 69:
				setValueLegacyIntIndexes(69, player.ridingEntity != null
					&& player.ridingEntity.getClass() == EntityBoat.class ? 1 : 0);
				break;
			
			case 70:
				setValueLegacyIntIndexes(70, mc.playerController != null && mc.playerController.isInCreativeMode()
					? 1 : 0);
				break;
			
			case 71:
				int rmx = player.ridingEntity != null ? (int) Math.round(player.ridingEntity.motionX * 1000) : 0;
				setValueLegacyIntIndexes(71, rmx);
				break;
			
			case 72:
				int rmy = player.ridingEntity != null ? (int) Math.round(player.ridingEntity.motionY * 1000) : 0;
				setValueLegacyIntIndexes(72, rmy);
				break;
			
			case 73:
				int rmz = player.ridingEntity != null ? (int) Math.round(player.ridingEntity.motionZ * 1000) : 0;
				setValueLegacyIntIndexes(73, rmz);
				break;
			
			case 74:
				int rmxx = player.ridingEntity != null ? (int) Math.round(player.ridingEntity.motionX * 1000) : 0;
				int rmzz = player.ridingEntity != null ? (int) Math.round(player.ridingEntity.motionZ * 1000) : 0;
				setValueLegacyIntIndexes(
					74, player.ridingEntity != null ? (int) Math.floor(Math.sqrt(rmxx * rmxx + rmzz * rmzz)) : 0);
				break;
			
			// 75-85 relaxed server
			
			case 86:
				setValueLegacyIntIndexes(
					86,
					mouseOverATile ? Version170Helper.getNameAt(
						mc.objectMouseOver.blockX, mc.objectMouseOver.blockY, mc.objectMouseOver.blockZ,
						MAT_HARD_LIMITS.NO_BLOCK_IN_THIS_CONTEXT) : MAT_HARD_LIMITS.NO_BLOCK_IN_THIS_CONTEXT);
				break;
			
			case 87:
				setValueLegacyIntIndexes(
					87,
					mouseOverATile ? w.getBlockMetadata(
						mc.objectMouseOver.blockX, mc.objectMouseOver.blockY, mc.objectMouseOver.blockZ) : 0);
				break;
			
			// 88 - moon phase
			
			case 89:
				setValueLegacyIntIndexes(
					89,
					player.inventory.armorInventory[0] != null ? Version170Helper
						.nameOf(player.inventory.armorInventory[0]) : MAT_HARD_LIMITS.NO_ITEM);
				break;
			
			case 90:
				setValueLegacyIntIndexes(
					90,
					player.inventory.armorInventory[1] != null ? Version170Helper
						.nameOf(player.inventory.armorInventory[1]) : MAT_HARD_LIMITS.NO_ITEM);
				break;
			
			case 91:
				setValueLegacyIntIndexes(
					91,
					player.inventory.armorInventory[2] != null ? Version170Helper
						.nameOf(player.inventory.armorInventory[2]) : MAT_HARD_LIMITS.NO_ITEM);
				break;
			
			case 92:
				setValueLegacyIntIndexes(
					92,
					player.inventory.armorInventory[3] != null ? Version170Helper
						.nameOf(player.inventory.armorInventory[3]) : MAT_HARD_LIMITS.NO_ITEM);
				break;
			
			// 93 - ME BiomeID
			case 94:
				///setValueLegacyIntIndexes(
				///	94, y >= 0 && y < MAT_HARD_LIMITS.USE_MAX_HEIGHT_LIMIT
				///		? Version170Helper.getNameAt(x, y, z) : MAT_HARD_LIMITS.NO_BLOCK_OUT_OF_BOUNDS); 
				setValueLegacyIntIndexes(
					94, Version170Helper.getNameAt(x, y, z, MAT_HARD_LIMITS.NO_BLOCK_OUT_OF_BOUNDS));
				break;
			
			case 95:
				///setValueLegacyIntIndexes(
				///	95, y >= 0 && y < mod().util().getWorldHeight() - 1
				///		? Version170Helper.getNameAt(x, y + 1, z) : MAT_HARD_LIMITS.NO_BLOCK_OUT_OF_BOUNDS);
				setValueLegacyIntIndexes(
					95, Version170Helper.getNameAt(x, y + 1, z, MAT_HARD_LIMITS.NO_BLOCK_OUT_OF_BOUNDS));
				break;
			
			case 96:
				ItemStack currentItem = player.inventory.getCurrentItem();
				setValueLegacyIntIndexes(96, currentItem != null
					? Version170Helper.nameOf(currentItem) : MAT_HARD_LIMITS.NO_ITEM);
				break;
			
			case 97:
				setValueLegacyIntIndexes(97, mc.currentScreen != null && mc.currentScreen instanceof GuiContainer
					? 1 : 0);
				break;
			
			// 98 99 UNUSED
			
			case 100:
				setValueLegacyIntIndexes(100, player.ridingEntity != null && player.ridingEntity instanceof EntityHorse
					? 1 : 0);
				break;
			
			default:
				break;
			}
			
		}
		
	}
	
}
