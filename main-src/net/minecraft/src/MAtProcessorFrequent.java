package net.minecraft.src;

import net.minecraft.client.Minecraft;
import eu.ha3.matmos.engine.Data;

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
	
	MAtProcessorFrequent(MAtMod modIn, Data dataIn, String normalNameIn, String deltaNameIn)
	{
		super(modIn, dataIn, normalNameIn, deltaNameIn);
		
	}
	
	@Override
	void doProcess()
	{
		Minecraft mc = mod().manager().getMinecraft();
		World w = mc.theWorld;
		WorldInfo worldinfo = w.worldInfo;
		EntityPlayerSP player = mc.thePlayer;
		
		int x = (int) Math.floor(player.posX);
		int y = (int) Math.floor(player.posY);
		int z = (int) Math.floor(player.posZ);
		
		int mx = (int) Math.round(player.motionX * 1000);
		int my = (int) Math.round(player.motionY * 1000);
		int mz = (int) Math.round(player.motionZ * 1000);
		
		// Get the Sky Light value
		setValue(0, w.getSavedLightValue(EnumSkyBlock.Sky, x, y, z));
		
		// Get the Artificial Light value
		setValue(1, w.getSavedLightValue(EnumSkyBlock.Block, x, y, z));
		
		// Get the Block Light value
		setValue(2, w.getBlockLightValue(x, y, z));
		
		// Get the World Time modulo 24KL
		setValue(3, (int) (worldinfo.getWorldTime() % 24000L));
		
		// Get the Altitude (Y)
		setValue(4, y);
		
		// 5 : (RELAXED)
		
		// Get if Is in Water
		setValue(6, player.isInWater() ? 1 : 0);
		
		// Get if It's Raining
		setValue(7, worldinfo.isRaining() ? 1 : 0);
		
		// Get if It's Thundering (regardless of rain)
		setValue(8, worldinfo.isThundering() ? 1 : 0);
		
		// Get if the Current block player is on is exposed to the sky
		setValue(9, w.canBlockSeeTheSky(x, y, z) ? 1 : 0);
		
		// Get if Player is un the Nether
		setValue(10, player.dimension == -1 ? 1 : 0);
		
		// Get the Skylight level subtracted (the amount of light stripped from Skylight)
		setValue(11, w.skylightSubtracted);
		
		// [12,18] (RELAXED)
		
		// Get if Player is inside of Water material
		setValue(19, player.isWet() ? 1 : 0);
		
		// Get if Player X (Floored (Double) Player X, casted into to Integer)
		setValue(20, x);
		
		// Get if Player Z (Floored (Double) Player Z, casted into to Integer)
		setValue(21, z);
		
		// Get if Player is on Ground
		setValue(22, player.onGround ? 1 : 0);
		
		// Get Player oxygen amount (Integer)
		setValue(23, player.getAir());
		
		// Get Player health amount (Integer)
		setValue(24, player.health);
		
		// Get Player dimension (Integer)
		setValue(25, player.dimension);
		
		setValue(26, w.canBlockSeeTheSky(x, y, z) && !(w.getTopSolidOrLiquidBlock(x, z) > y) ? 1 : 0);
		setValue(27, w.getTopSolidOrLiquidBlock(x, z));
		setValue(28, w.getTopSolidOrLiquidBlock(x, z) - y);
		
		// [29,31] : (RELAXED)
		
		setValue(32, player.inventory.getCurrentItem() != null ? player.inventory.getCurrentItem().itemID : -1);
		//setValue( 32, player.getHeldItem() != null ? player.getHeldItem().itemID : -1 );
		setValue(33, mx);
		setValue(34, my);
		setValue(35, mz);
		setValue(
			36, y >= 1 && y < mod().util().getWorldHeight()
				? getTranslatedBlockId(mc.theWorld.getBlockId(x, y - 1, z)) : -1); //FIXME difference in Altitude notion
		setValue(
			37, y >= 2 && y < mod().util().getWorldHeight()
				? getTranslatedBlockId(mc.theWorld.getBlockId(x, y - 2, z)) : -1); //FIXME difference in Altitude notion
		setValue(38, (int) mod().util().getClientTick());
		setValue(39, player.isBurning() ? 1 : 0); // XXX ERROR NOW IS A PRIVATE VALUE
		setValue(40, (int) Math.floor(player.swingProgress * 16));
		setValue(41, player.swingProgress != 0 ? 1 : 0); // is swinging
		setValue(42, player.isJumping ? 1 : 0);
		setValue(43, (int) (player.fallDistance * 1000));
		setValue(44, player.isInWeb ? 1 : 0);
		setValue(45, (int) Math.floor(Math.sqrt(mx * mx + mz * mz)));
		setValue(46, player.inventory.currentItem);
		setValue(47, mc.objectMouseOver != null ? 1 : 0);
		setValue(48, mc.objectMouseOver != null ? mc.objectMouseOver.typeOfHit.ordinal() : -1);
		setValue(49, player.isBurning() ? 1 : 0);
		setValue(50, player.getTotalArmorValue());
		setValue(51, player.foodStats.getFoodLevel()); //(getFoodStats())
		setValue(52, (int) (player.foodStats.getSaturationLevel() * 1000)); //(getFoodStats())
		setValue(53, 0); // TODO (Food Exhaustion Level)
		setValue(54, player.experienceValue * 1000);
		setValue(55, player.experienceLevel);
		setValue(56, player.experienceTotal);
		setValue(57, player.isOnLadder() ? 1 : 0);
		setValue(58, player.getItemInUseDuration());
		// ---- / --- // / / setValue( 59, player.inventory.func_35157_d(Item.arrow.shiftedIndex) ? 1 : 0);
		setValue(59, 0);
		setValue(60, player.isBlocking() ? 1 : 0);
		setValue(61, 72000 - player.getItemInUseDuration());
		setValue(62, player.inventory.getCurrentItem() == null ? -1 : player.inventory.getCurrentItem().getItemDamage());
		setValue(63, player.isSprinting() ? 1 : 0);
		setValue(64, player.isSneaking() ? 1 : 0);
		setValue(65, player.isAirBorne ? 1 : 0);
		setValue(66, player.isUsingItem() ? 1 : 0);
		setValue(67, player.isRiding() ? 1 : 0);
		setValue(68, player.ridingEntity != null && player.ridingEntity.getClass() == EntityMinecart.class ? 1 : 0);
		setValue(69, player.ridingEntity != null && player.ridingEntity.getClass() == EntityBoat.class ? 1 : 0);
		setValue(70, mc.playerController != null && mc.playerController.isInCreativeMode() ? 1 : 0);
		
		int rmx = player.ridingEntity != null ? (int) Math.round(player.ridingEntity.motionX * 1000) : 0;
		int rmy = player.ridingEntity != null ? (int) Math.round(player.ridingEntity.motionY * 1000) : 0;
		int rmz = player.ridingEntity != null ? (int) Math.round(player.ridingEntity.motionZ * 1000) : 0;
		
		setValue(71, rmx);
		setValue(72, rmy);
		setValue(73, rmz);
		setValue(74, player.ridingEntity != null ? (int) Math.floor(Math.sqrt(rmx * rmx + rmz * rmz)) : 0);
		
		// 75-85 relaxed server
		
		if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == EnumMovingObjectType.TILE)
		{
			setValue(86, w.getBlockId(mc.objectMouseOver.blockX, mc.objectMouseOver.blockY, mc.objectMouseOver.blockZ));
			setValue(
				87, w.getBlockMetadata(mc.objectMouseOver.blockX, mc.objectMouseOver.blockY, mc.objectMouseOver.blockZ));
		}
		else
		{
			setValue(86, 0);
			setValue(87, 0);
			
		}
		
		// 88 - moon phase
		
		setValue(89, player.inventory.armorInventory[0] != null ? player.inventory.armorInventory[0].itemID : -1);
		setValue(90, player.inventory.armorInventory[1] != null ? player.inventory.armorInventory[1].itemID : -1);
		setValue(91, player.inventory.armorInventory[2] != null ? player.inventory.armorInventory[2].itemID : -1);
		setValue(92, player.inventory.armorInventory[3] != null ? player.inventory.armorInventory[3].itemID : -1);
		
		//
		/*
		for (int i = 0; i < 64; i++)
		{
			setValue(100 + i, 0);
		}
		
		if (player.inventory.getCurrentItem() != null
			&& player.inventory.getCurrentItem().getEnchantmentTagList() != null
			&& player.inventory.getCurrentItem().getEnchantmentTagList().tagCount() > 0)
		{
			int total = player.inventory.getCurrentItem().getEnchantmentTagList().tagCount();
			
			NBTTagList enchantments = player.inventory.getCurrentItem().getEnchantmentTagList();
			for (int i = 0; i < total; i++)
			{
				short id = ((NBTTagCompound) enchantments.tagAt(i)).getShort("id");
				short lvl = ((NBTTagCompound) enchantments.tagAt(i)).getShort("lvl");
				
				if (id < 64 && i >= 0)
				{
					setValue(100 + id, lvl);
				}
			}
		}*/
		/*
		for (int i = 0; i < 32; i++)
		{
			setValue(200 + i, 0);
		}
		
		for (Object oeffect : player.getActivePotionEffects())
		{
			PotionEffect effect = (PotionEffect) oeffect;
			int id = effect.getPotionID();
			if (id < 32 && id >= 0)
			{
				setValue(200 + id, 1 + effect.getAmplifier());
			}
		}*/
		// Remember to increase the data size.
		
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
