package eu.ha3.matmos.game.data.modules;

import net.minecraft.potion.PotionEffect;
import eu.ha3.matmos.engine.core.interfaces.Data;
import eu.ha3.matmos.game.data.abstractions.module.AbstractPotionQualityModule;

/*
--filenotes-placeholder
*/

public class S__potion_duration extends AbstractPotionQualityModule
{
	public S__potion_duration(Data data)
	{
		super(data, "potion_duration");
	}
	
	@Override
	protected String getQuality(PotionEffect effect)
	{
		return Integer.toString(effect.getDuration());
	}
}