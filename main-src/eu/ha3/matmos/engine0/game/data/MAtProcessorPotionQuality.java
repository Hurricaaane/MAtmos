package eu.ha3.matmos.engine0.game.data;

import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import eu.ha3.matmos.engine0.conv.MAtmosConvLogger;
import eu.ha3.matmos.engine0.core.implem.SelfGeneratingData;
import eu.ha3.matmos.engine0.game.system.MAtMod;

/* x-placeholder */

public abstract class MAtProcessorPotionQuality extends MAtProcessorModel
{
	public MAtProcessorPotionQuality(MAtMod modIn, SelfGeneratingData dataIn, String normalNameIn, String deltaNameIn)
	{
		super(modIn, dataIn, normalNameIn, deltaNameIn);
	}
	
	@Override
	protected void doProcess()
	{
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		
		Set<String> required = getRequired();
		
		for (String i : required)
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
					setValueLegacyIntIndexes(id, getQuality(effect));
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
