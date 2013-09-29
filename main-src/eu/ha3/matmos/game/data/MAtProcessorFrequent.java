package eu.ha3.matmos.game.data;

import net.minecraft.src.EntityBoat;
import net.minecraft.src.EntityHorse;
import net.minecraft.src.EntityMinecartEmpty;
import net.minecraft.src.EntityPlayerSP;
import net.minecraft.src.EnumMovingObjectType;
import net.minecraft.src.EnumSkyBlock;
import net.minecraft.src.GuiContainer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.MAtAccessors;
import net.minecraft.src.Minecraft;
import net.minecraft.src.World;
import net.minecraft.src.WorldInfo;
import eu.ha3.matmos.engine.implem.IntegerData;
import eu.ha3.matmos.game.system.MAtMod;

/*
            DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE 
                    Version 2, December 2004 

 Copyright (C) 2004 Sam Hocevar <sam@hocevar.net> 

 Everyone is permitted to copy and distribute verbatim or modified 
 copies of this license document, and changing it is allowed as long 
 as the name is changed. 

            DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE 
   TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION 

  0. You just DO WHAT THE FUCK YOU WANT TO. 
*/

public class MAtProcessorFrequent extends MAtProcessorModel
{
	public MAtProcessorFrequent(MAtMod modIn, IntegerData dataIn, String normalNameIn, String deltaNameIn)
	{
		super(modIn, dataIn, normalNameIn, deltaNameIn);
	}
	
	@Override
	protected void doProcess()
	{
		Minecraft mc = Minecraft.getMinecraft();
		World w = mc.theWorld;
		WorldInfo worldinfo = MAtAccessors.getWorldInfoOf(w);
		EntityPlayerSP player = mc.thePlayer;
		
		int x = (int) Math.floor(player.posX);
		int y = (int) Math.floor(player.posY);
		int z = (int) Math.floor(player.posZ);
		
		boolean mouseOverATile =
			mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == EnumMovingObjectType.TILE;
		
		for (Integer index : getRequired())
		{
			switch (index)
			{
			case 0:
				// Get the Sky Light value
				setValue(0, w.getSavedLightValue(EnumSkyBlock.Sky, x, y, z));
				break;
			
			case 1:
				// Get the Artificial Light value
				setValue(1, w.getSavedLightValue(EnumSkyBlock.Block, x, y, z));
				break;
			
			case 2:
				// Get the Block Light value
				setValue(2, w.getBlockLightValue(x, y, z));
				break;
			
			case 3:
				// Get the World Time modulo 24KL
				setValue(3, (int) (worldinfo.getWorldTime() % 24000L));
				break;
			
			case 4:
				// Get the Altitude (Y)
				setValue(4, y);
				break;
			
			// 5 : (RELAXED) 
			
			case 6:
				// Get if Is in Water
				setValue(6, player.isInWater() ? 1 : 0);
				break;
			
			case 7:
				// Get if It's Raining
				setValue(7, worldinfo.isRaining() ? 1 : 0);
				break;
			
			case 8:
				// Get if It's Thundering (regardless of rain)
				setValue(8, worldinfo.isThundering() ? 1 : 0);
				break;
			
			case 9:
				// Get if the Current block player is on is exposed to the sky
				setValue(9, w.canBlockSeeTheSky(x, y, z) ? 1 : 0);
				break;
			
			case 10:
				// Get if Player is un the Nether
				setValue(10, player.dimension == -1 ? 1 : 0);
				break;
			
			case 11:
				// Get the Skylight level subtracted (the amount of light stripped from Skylight)
				setValue(11, w.skylightSubtracted);
				break;
			
			// [12,18] (RELAXED)
			
			case 19:
				// Get if Player is inside of Water material
				setValue(19, player.isWet() ? 1 : 0);
				break;
			
			case 20:
				// Get if Player X (Floored (Double) Player X, casted into to Integer)
				setValue(20, x);
				break;
			
			case 21:
				// Get if Player Z (Floored (Double) Player Z, casted into to Integer)
				setValue(21, z);
				break;
			
			case 22:
				// Get if Player is on Ground
				setValue(22, player.onGround ? 1 : 0);
				break;
			
			case 23:
				// Get Player oxygen amount (Integer)
				setValue(23, player.getAir());
				break;
			
			case 24:
				// Get Player health amount (Integer)
				setValue(24, (int) Math.ceil(player.getHealth())); // HEALTH is now a float.
				break;
			
			case 25:
				// Get Player dimension (Integer)
				setValue(25, player.dimension);
				break;
			
			case 26:
				setValue(26, w.canBlockSeeTheSky(x, y, z) && !(w.getTopSolidOrLiquidBlock(x, z) > y) ? 1 : 0);
				break;
			
			case 27:
				setValue(27, w.getTopSolidOrLiquidBlock(x, z));
				break;
			
			case 28:
				setValue(28, w.getTopSolidOrLiquidBlock(x, z) - y);
				break;
			
			// [29,31] : (RELAXED)
			
			case 32:
				setValue(32, player.inventory.getCurrentItem() != null ? player.inventory.getCurrentItem().itemID : -1);
				//setValue( 32, player.getHeldItem() != null ? player.getHeldItem().itemID : -1 );
				break;
			
			case 33:
				setValue(33, (int) Math.round(player.motionX * 1000));
				break;
			
			case 34:
				setValue(34, (int) Math.round(player.motionY * 1000));
				break;
			
			case 35:
				setValue(35, (int) Math.round(player.motionZ * 1000));
				break;
			
			case 36:
				setValue(
					36,
					y >= 1 && y < mod().util().getWorldHeight() ? getTranslatedBlockId(mc.theWorld.getBlockId(
						x, y - 1, z)) : -1); //FIXME difference in Altitude notion
				break;
			
			case 37:
				setValue(
					37,
					y >= 2 && y < mod().util().getWorldHeight() ? getTranslatedBlockId(mc.theWorld.getBlockId(
						x, y - 2, z)) : -1); //FIXME difference in Altitude notion
				break;
			
			case 38:
				setValue(38, (int) mod().util().getClientTick());
				break;
			
			case 39:
				setValue(39, player.isBurning() ? 1 : 0); // XXX ERROR NOW IS A PRIVATE VALUE
				break;
			
			case 40:
				setValue(40, (int) Math.floor(player.swingProgress * 16));
				break;
			
			case 41:
				setValue(41, player.swingProgress != 0 ? 1 : 0); // is swinging
				break;
			
			case 42:
				setValue(42, MAtAccessors.getIsJumpingOf(player) ? 1 : 0);
				break;
			
			case 43:
				setValue(43, (int) (player.fallDistance * 1000));
				break;
			
			case 44:
				setValue(44, MAtAccessors.getIsInWebOf(player) ? 1 : 0);
				break;
			
			case 45:
				int mxx = (int) Math.round(player.motionX * 1000);
				int mzz = (int) Math.round(player.motionZ * 1000);
				
				setValue(45, (int) Math.floor(Math.sqrt(mxx * mxx + mzz * mzz)));
				break;
			
			case 46:
				setValue(46, player.inventory.currentItem);
				break;
			
			case 47:
				setValue(47, mc.objectMouseOver != null ? 1 : 0);
				break;
			
			case 48:
				setValue(48, mc.objectMouseOver != null ? mc.objectMouseOver.typeOfHit.ordinal() : -1);
				break;
			
			case 49:
				setValue(49, player.isBurning() ? 1 : 0);
				break;
			
			case 50:
				setValue(50, player.getTotalArmorValue());
				break;
			
			case 51:
				setValue(51, MAtAccessors.getFoodStatsOf(player).getFoodLevel()); //(getFoodStats())
				break;
			
			case 52:
				setValue(52, (int) (MAtAccessors.getFoodStatsOf(player).getSaturationLevel() * 1000)); //(getFoodStats())
				break;
			
			case 53:
				setValue(53, 0); // TODO (Food Exhaustion Level)
				break;
			
			case 54:
				setValue(54, (int) (player.experience * 1000));
				break;
			
			case 55:
				setValue(55, player.experienceLevel);
				break;
			
			case 56:
				setValue(56, player.experienceTotal);
				break;
			
			case 57:
				setValue(57, player.isOnLadder() ? 1 : 0);
				break;
			
			case 58:
				setValue(58, player.getItemInUseDuration());
				break;
			
			case 59:
				// ---- / --- // / / setValue( 59, player.inventory.func_35157_d(Item.arrow.shiftedIndex) ? 1 : 0);
				setValue(59, 0);
				break;
			
			case 60:
				setValue(60, player.isBlocking() ? 1 : 0);
				break;
			
			case 61:
				setValue(61, 72000 - player.getItemInUseDuration());
				break;
			
			case 62:
				setValue(62, player.inventory.getCurrentItem() == null ? -1 : player.inventory
					.getCurrentItem().getItemDamage());
				break;
			
			case 63:
				setValue(63, player.isSprinting() ? 1 : 0);
				break;
			
			case 64:
				setValue(64, player.isSneaking() ? 1 : 0);
				break;
			
			case 65:
				setValue(65, player.isAirBorne ? 1 : 0);
				break;
			
			case 66:
				setValue(66, player.isUsingItem() ? 1 : 0);
				break;
			
			case 67:
				setValue(67, player.isRiding() ? 1 : 0);
				break;
			
			case 68:
				setValue(68, player.ridingEntity != null && player.ridingEntity.getClass() == EntityMinecartEmpty.class
					? 1 : 0);
				break;
			
			case 69:
				setValue(69, player.ridingEntity != null && player.ridingEntity.getClass() == EntityBoat.class ? 1 : 0);
				break;
			
			case 70:
				setValue(70, mc.playerController != null && mc.playerController.isInCreativeMode() ? 1 : 0);
				break;
			
			case 71:
				int rmx = player.ridingEntity != null ? (int) Math.round(player.ridingEntity.motionX * 1000) : 0;
				setValue(71, rmx);
				break;
			
			case 72:
				int rmy = player.ridingEntity != null ? (int) Math.round(player.ridingEntity.motionY * 1000) : 0;
				setValue(72, rmy);
				break;
			
			case 73:
				int rmz = player.ridingEntity != null ? (int) Math.round(player.ridingEntity.motionZ * 1000) : 0;
				setValue(73, rmz);
				break;
			
			case 74:
				int rmxx = player.ridingEntity != null ? (int) Math.round(player.ridingEntity.motionX * 1000) : 0;
				int rmzz = player.ridingEntity != null ? (int) Math.round(player.ridingEntity.motionZ * 1000) : 0;
				setValue(74, player.ridingEntity != null ? (int) Math.floor(Math.sqrt(rmxx * rmxx + rmzz * rmzz)) : 0);
				break;
			
			// 75-85 relaxed server
			
			case 86:
				setValue(
					86,
					mouseOverATile ? w.getBlockId(
						mc.objectMouseOver.blockX, mc.objectMouseOver.blockY, mc.objectMouseOver.blockZ) : 0);
				break;
			
			case 87:
				setValue(
					87,
					mouseOverATile ? w.getBlockMetadata(
						mc.objectMouseOver.blockX, mc.objectMouseOver.blockY, mc.objectMouseOver.blockZ) : 0);
				break;
			
			// 88 - moon phase
			
			case 89:
				setValue(89, player.inventory.armorInventory[0] != null
					? player.inventory.armorInventory[0].itemID : -1);
				break;
			
			case 90:
				setValue(90, player.inventory.armorInventory[1] != null
					? player.inventory.armorInventory[1].itemID : -1);
				break;
			
			case 91:
				setValue(91, player.inventory.armorInventory[2] != null
					? player.inventory.armorInventory[2].itemID : -1);
				break;
			
			case 92:
				setValue(92, player.inventory.armorInventory[3] != null
					? player.inventory.armorInventory[3].itemID : -1);
				break;
			
			// 93 - ME BiomeID
			
			case 94:
				setValue(
					94,
					y >= 0 && y < mod().util().getWorldHeight()
						? getTranslatedBlockId(mc.theWorld.getBlockId(x, y, z)) : -1); //FIXME difference in Altitude notion
				break;
			
			case 95:
				setValue(
					95,
					y >= 0 && y < mod().util().getWorldHeight() - 1 ? getTranslatedBlockId(mc.theWorld.getBlockId(
						x, y + 1, z)) : -1);
				break;
			
			case 96:
				ItemStack currentItem = player.inventory.getCurrentItem();
				setValue(96, currentItem != null ? currentItem.itemID : -1);
				break;
			
			case 97:
				setValue(97, mc.currentScreen != null && mc.currentScreen instanceof GuiContainer ? 1 : 0);
				break;
			
			// 98 99 UNUSED
			
			case 100:
				setValue(100, player.ridingEntity != null && player.ridingEntity instanceof EntityHorse ? 1 : 0);
				break;
			
			default:
				break;
			}
			
		}
		
	}
	
	private int getTranslatedBlockId(int dataValue)
	{
		// XXX Crash prevention in case of data value system hack
		if (dataValue < 0)
			return 0;
		
		if (dataValue >= MAtDataGatherer.COUNT_WORLD_BLOCKS)
			return 0;
		
		return dataValue;
	}
	
}
