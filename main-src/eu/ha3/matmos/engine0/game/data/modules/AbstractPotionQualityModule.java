package eu.ha3.matmos.engine0.game.data.modules;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import eu.ha3.matmos.engine0.core.interfaces.Data;
import eu.ha3.matmos.engine0.game.data.MAtDataGatherer;
import eu.ha3.matmos.engine0.game.data.abstractions.processor.ProcessorModel;

/* x-placeholder */

public abstract class AbstractPotionQualityModule extends ProcessorModel implements Module
{
	private final String NAME;
	
	public AbstractPotionQualityModule(Data data, String name)
	{
		super(data, name, name + MAtDataGatherer.DELTA_SUFFIX);
		this.NAME = name;
	}
	
	@Override
	protected void doProcess()
	{
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		
		for (String i : this.sheet.keySet())
		{
			setValue(i, 0);
		}
		
		for (Object oeffect : player.getActivePotionEffects())
		{
			PotionEffect effect = (PotionEffect) oeffect;
			int id = effect.getPotionID();
			setValueLegacyIntIndexes(id, getQuality(effect));
		}
	}
	
	@Override
	public String getModuleName()
	{
		return this.NAME;
	}
	
	abstract protected int getQuality(PotionEffect effect);
}
