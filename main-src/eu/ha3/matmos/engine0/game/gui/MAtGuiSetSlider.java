package eu.ha3.matmos.engine0.game.gui;

import java.util.List;

import eu.ha3.matmos.engine0.game.system.MAtMod;
import eu.ha3.mc.gui.HDisplayStringProvider;
import eu.ha3.mc.gui.HGuiSliderControl;
import eu.ha3.mc.gui.HSliderListener;

/* x-placeholder */

@Deprecated
public class MAtGuiSetSlider implements HDisplayStringProvider, HSliderListener
{
	final protected MAtMod mod;
	private List<String> possibilities;
	private int currentIndex;
	
	public MAtGuiSetSlider(MAtMod mod, String fromConfig)
	{
		this.mod = mod;
		
		this.possibilities = mod.getTotalConversionsSpecialSort();
		this.currentIndex = this.possibilities.indexOf(fromConfig);
	}
	
	@Override
	public void sliderValueChanged(HGuiSliderControl slider, float value)
	{
		int id = Math.round(value * (getMaxPossibilities() - 1));
		this.currentIndex = id;
		slider.updateDisplayString();
	}
	
	@Override
	public void sliderPressed(HGuiSliderControl hGuiSliderControl)
	{
	}
	
	@Override
	public void sliderReleased(HGuiSliderControl hGuiSliderControl)
	{
		String conversion = this.possibilities.get(this.currentIndex);
		this.mod.getConfig().setProperty("totalconversion.name", conversion);
		this.mod.saveConfig();
	}
	
	@Override
	public String provideDisplayString()
	{
		String base = "";
		
		String conversion = this.possibilities.get(this.currentIndex);
		if (conversion.equals("default"))
		{
			base = "MAtmos default expansion set";
		}
		else
		{
			base = "\"" + conversion + "\" custom Expansion Set";
		}
		
		if (this.currentIndex != 0)
		{
			base = "Will use " + base + " (Requires Minecraft restart)";
		}
		else
		{
			base = "Using " + base;
		}
		
		return base;
	}
	
	public int getMaxPossibilities()
	{
		return this.possibilities.size();
	}
	
	public float calculateSliderLocation(String current)
	{
		return this.possibilities.indexOf(current) / (getMaxPossibilities() - 1f);
	}
}
