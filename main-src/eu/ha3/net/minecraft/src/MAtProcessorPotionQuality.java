package net.minecraft.src;

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

public abstract class MAtProcessorPotionQuality extends MAtProcessorModel
{
	MAtProcessorPotionQuality(MAtMod modIn, Data dataIn, String normalNameIn, String deltaNameIn)
	{
		super(modIn, dataIn, normalNameIn, deltaNameIn);
	}
	
	@Override
	void doProcess()
	{
		EntityPlayer player = mod().manager().getMinecraft().thePlayer;
		
		for (int i = 0; i < 32; i++)
		{
			setValue(i, 0);
		}
		
		for (Object oeffect : player.getActivePotionEffects())
		{
			PotionEffect effect = (PotionEffect) oeffect;
			int id = effect.getPotionID();
			if (id < 32 && id >= 0)
			{
				setValue(id, getQuality(effect));
			}
		}
		
	}
	
	abstract protected int getQuality(PotionEffect effect);
	
}
