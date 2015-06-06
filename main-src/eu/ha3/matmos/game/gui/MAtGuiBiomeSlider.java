package eu.ha3.matmos.game.gui;

import eu.ha3.matmos.game.system.MAtMod;
import eu.ha3.mc.gui.HDisplayStringProvider;
import eu.ha3.mc.gui.HGuiSliderControl;
import eu.ha3.mc.gui.HSliderListener;
import net.minecraft.world.biome.BiomeGenBase;

import java.util.ArrayList;
import java.util.List;

/* x-placeholder */

public class MAtGuiBiomeSlider implements HDisplayStringProvider, HSliderListener
{
	final protected MAtMod mod;
	
	final protected int maxBiomes = calculateMaxBiomes();
	protected int definedBiomeID;
	
	private List<Integer> validBiomes = new ArrayList<Integer>();
	
	public MAtGuiBiomeSlider(MAtMod mod, int define)
	{
		this.mod = mod;
		this.definedBiomeID = define;
		
		computeBiomes();
	}
	
	private void computeBiomes()
	{
		BiomeGenBase[] biomes = BiomeGenBase.getBiomeGenArray();
		
		for (int i = 0; i < biomes.length; i++)
		{
			if (biomes[i] != null)
			{
				this.validBiomes.add(i);
			}
		}
	}
	
	@Override
	public void sliderValueChanged(HGuiSliderControl slider, float value)
	{
		int biomeID = (int) (Math.floor(value * this.validBiomes.size()) - 1);
		this.definedBiomeID =
			biomeID >= 0 && biomeID < this.validBiomes.size() ? this.validBiomes.get(biomeID) : biomeID;
		
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
		// biomeList
		if (this.definedBiomeID >= 0 && this.definedBiomeID < BiomeGenBase.getBiomeGenArray().length)
		{
			BiomeGenBase biome = BiomeGenBase.getBiomeGenArray()[this.definedBiomeID];
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
		if (this.validBiomes.contains(biomeID))
			return (this.validBiomes.indexOf(biomeID) + 1f) / this.validBiomes.size();
		else if (biomeID == -1)
			return 0;
		else
			return 1f;
	}
	
	private int calculateMaxBiomes()
	{
		// biomeList
		BiomeGenBase[] biomes = BiomeGenBase.getBiomeGenArray();
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
