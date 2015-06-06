package eu.ha3.matmos.game.data.abstractions.module;

import eu.ha3.matmos.engine.core.interfaces.Data;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;

import java.util.LinkedHashSet;
import java.util.Set;

/* x-placeholder */

/**
 * An abstract module that extracts a certain quality of all potion effects
 * (such as time, strength...) that is currently affecting the player. The
 * quality is defined by the implementing class.
 * 
 * @author Hurry
 */
public abstract class AbstractPotionQualityModule extends ModuleProcessor implements RegistryBasedModule
{
	private Set<String> oldThings = new LinkedHashSet<String>();
	
	public AbstractPotionQualityModule(Data data, String name)
	{
		super(data, name);
		data.getSheet(name).setDefaultValue("0");
		data.getSheet(name + ModuleProcessor.DELTA_SUFFIX).setDefaultValue("0");
	}
	
	@Override
	public String getRegistryName()
	{
		return "potion";
	}
	
	@Override
	protected void doProcess()
	{
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		
		for (String i : this.oldThings)
		{
			setValue(i, 0);
		}
		this.oldThings.clear();
		
		for (Object oeffect : player.getActivePotionEffects())
		{
			PotionEffect effect = (PotionEffect) oeffect;
			
			int id = effect.getPotionID();
			setValue(Integer.toString(id), getQuality(effect));
			this.oldThings.add(Integer.toString(id));
		}
	}
	
	abstract protected String getQuality(PotionEffect effect);
}
