package eu.ha3.matmos.game.data;

import java.util.Set;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Minecraft;
import net.minecraft.src.PotionEffect;
import eu.ha3.matmos.conv.MAtmosConvLogger;
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

public abstract class MAtProcessorPotionQuality extends MAtProcessorModel
{
	public MAtProcessorPotionQuality(MAtMod modIn, IntegerData dataIn, String normalNameIn, String deltaNameIn)
	{
		super(modIn, dataIn, normalNameIn, deltaNameIn);
	}
	
	@Override
	protected void doProcess()
	{
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		
		Set<Integer> required = getRequired();
		
		for (Integer i : required)
		{
			setValue(i, 0);
		}
		
		for (Object oeffect : player.getActivePotionEffects())
		{
			PotionEffect effect = (PotionEffect) oeffect;
			int id = effect.getPotionID();
			if (id < 32 && id >= 0)
			{
				if (required.contains(id))
				{
					setValue(id, getQuality(effect));
				}
			}
			else
			{
				MAtmosConvLogger.warning("Found potion effect which ID is " + id + "!!!");
			}
		}
		
	}
	
	abstract protected int getQuality(PotionEffect effect);
	
}
