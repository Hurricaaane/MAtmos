package eu.ha3.matmos.engine0.game.gui;

import net.minecraft.world.biome.BiomeGenBase;
import eu.ha3.matmos.engine0.game.system.MAtMod;
import eu.ha3.mc.gui.HDisplayStringProvider;
import eu.ha3.mc.gui.HGuiSliderControl;
import eu.ha3.mc.gui.HSliderListener;

/* x-placeholder */

public class MAtGuiBiomeSlider implements HDisplayStringProvider, HSliderListener
{
	final protected MAtMod mod;
	
	final protected int maxBiomes = calculateMaxBiomes();
	protected int definedBiomeID;
	
	public MAtGuiBiomeSlider(MAtMod mod, int define)
	{
		this.mod = mod;
		this.definedBiomeID = define;
	}
	
	@Override
	public void sliderValueChanged(HGuiSliderControl slider, float value)
	{
		int biomeID = (int) (Math.floor(value * this.maxBiomes) - 1);
		this.definedBiomeID = biomeID;
		
		slider.updateDisplayString();
	}
	
	@Override
	public void sliderPressed(HGuiSliderControl hGuiSliderControl)
	{
	}
	
	@Override
	public void sliderReleased(HGuiSliderControl hGuiSliderControl)
	{
		this.mod.getConfig().setProperty("useroptions.biome.override", this.definedBiomeID);
		this.mod.saveConfig();
	}
	
	@Override
	public String provideDisplayString()
	{
		final String base = "Override biome detection: ";
		if (this.definedBiomeID >= 0 && this.definedBiomeID < BiomeGenBase.biomeList.length)
		{
			BiomeGenBase biome = BiomeGenBase.biomeList[this.definedBiomeID];
			if (biome == null)
				return base + "Undefined biome (" + this.definedBiomeID + ")";
			else if (biome.biomeName.equals(""))
				return base + "Unnamed biome (" + this.definedBiomeID + ")";
			else
				return base + "Only " + biome.biomeName + " (" + this.definedBiomeID + ")";
		}
		else if (this.definedBiomeID == -1)
			return base + "Disabled (use current biome)";
		
		return "";
	}
	
	public float calculateSliderLocation(int biomeID)
	{
		return (biomeID + 1f) / this.maxBiomes;
	}
	
	private int calculateMaxBiomes()
	{
		BiomeGenBase[] biomes = BiomeGenBase.biomeList;
		int max = 0;
		
		for (int i = 0; i < biomes.length; i++)
		{
			if (biomes[i] != null)
			{
				max = i + 1;
			}
		}
		
		return max;
	}
	
}
