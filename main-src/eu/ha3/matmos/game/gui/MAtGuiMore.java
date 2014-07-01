package eu.ha3.matmos.game.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import eu.ha3.matmos.game.system.MAtMod;
import eu.ha3.mc.gui.HDisplayStringProvider;
import eu.ha3.mc.gui.HGuiSliderControl;
import eu.ha3.mc.gui.HSliderListener;
import eu.ha3.mc.quick.chat.ChatColorsSimple;

/* x-placeholder */

public class MAtGuiMore extends GuiScreen
{
	private final int IDS_PER_PAGE = 5;
	
	private GuiScreen parentScreen;
	
	protected String screenTitle;
	
	private MAtMod mod;
	
	private int buttonId;
	
	public MAtGuiMore(GuiScreen par1GuiScreen, MAtMod matmos)
	{
		this.screenTitle = "MAtmos Advanced options";
		this.buttonId = -1;
		this.parentScreen = par1GuiScreen;
		this.mod = matmos;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void initGui()
	{
		final int _GAP = 2;
		final int _UNIT = 20;
		final int _WIDTH = 155 * 2;
		
		final int _MIX = _GAP + _UNIT;
		
		final int _LEFT = this.width / 2 - _WIDTH / 2;
		
		final int _SEPARATOR = 10;
		final int _TURNOFFWIDTH = _WIDTH / 5;
		
		this.buttonList.add(new GuiButton(211, _LEFT + _MIX, _MIX * (0 + 1), _WIDTH - _MIX * 2, _UNIT, this.mod
			.getConfig().getBoolean("reversed.controls") ? "Menu: Hold Down Key to open" : "Menu: Press Key to open"));
		
		this.buttonList.add(new GuiButton(212, _LEFT + _MIX, _MIX * (1 + 1), _WIDTH - _MIX * 2, _UNIT, this.mod
			.getConfig().getBoolean("useroptions.altitudes.low")
			? "Low-altitude ambiences: ON" : "Low-altitude ambiences: OFF"));
		
		this.buttonList.add(new GuiButton(213, _LEFT + _MIX, _MIX * (2 + 1), _WIDTH - _MIX * 2, _UNIT, this.mod
			.getConfig().getBoolean("useroptions.altitudes.high")
			? "High-altitude ambiences: ON" : "High-altitude ambiences: OFF"));
		
		/*this.buttonList.add(new GuiButton(
			214, _LEFT + _MIX, _MIX * (3 + 1), _WIDTH - _MIX * 2, _UNIT, "Use custom world height: "
				+ this.mod.getConfig().getInteger("world.height")));*/
		
		MAtGuiBiomeSlider biomeSlider =
			new MAtGuiBiomeSlider(this.mod, this.mod.getConfig().getInteger("useroptions.biome.override"));
		HGuiSliderControl biomeControl =
			new HGuiSliderControl(
				214, _LEFT, _MIX * (3 + 1), _WIDTH, _UNIT, "", biomeSlider.calculateSliderLocation(this.mod
					.getConfig().getInteger("useroptions.biome.override")));
		
		biomeControl.setListener(biomeSlider);
		biomeControl.setDisplayStringProvider(biomeSlider);
		biomeControl.updateDisplayString();
		this.buttonList.add(biomeControl);
		
		HGuiSliderControl ambienceVolume =
			new HGuiSliderControl(216, _LEFT, _MIX * (4 + 1), _WIDTH, _UNIT, "", this.mod.getConfig().getFloat(
				"minecraftsound.ambient.volume"));
		ambienceVolume.setListener(new HSliderListener() {
			
			@Override
			public void sliderValueChanged(HGuiSliderControl slider, float value)
			{
				Minecraft.getMinecraft().gameSettings.setSoundLevel(SoundCategory.AMBIENT, value);
				MAtGuiMore.this.mod.getConfig().setProperty("minecraftsound.ambient.volume", value);
				slider.updateDisplayString();
			}
			
			@Override
			public void sliderReleased(HGuiSliderControl hGuiSliderControl)
			{
				MAtGuiMore.this.mod.saveConfig();
				Minecraft.getMinecraft().gameSettings.saveOptions();
			}
			
			@Override
			public void sliderPressed(HGuiSliderControl hGuiSliderControl)
			{
			}
		});
		ambienceVolume.setDisplayStringProvider(new HDisplayStringProvider() {
			@Override
			public String provideDisplayString()
			{
				return "Minecraft base Ambient/Environment volume: "
					+ (int) Math.floor(MAtGuiMore.this.mod.getConfig().getFloat("minecraftsound.ambient.volume") * 100)
					+ "%";
			}
		});
		ambienceVolume.updateDisplayString();
		this.buttonList.add(ambienceVolume);
		
		this.buttonList.add(new GuiButton(215, _LEFT + _MIX, _MIX * (6 + 1), _WIDTH - _MIX * 2, _UNIT, this.mod
			.getConfig().getInteger("debug.mode") == 1
			? ChatColorsSimple.COLOR_GOLD + "Dev/Editor mode: ON" : "Dev/Editor mode: OFF"));
		
		this.buttonList.add(new GuiButton(200, _LEFT + _MIX, _SEPARATOR + _MIX * (this.IDS_PER_PAGE + 4), _WIDTH
			- _MIX * 2 - _GAP - _TURNOFFWIDTH, _UNIT, "Done"));
	}
	
	@Override
	protected void actionPerformed(GuiButton par1GuiButton)
	{
		if (par1GuiButton.id == 200)
		{
			this.mc.displayGuiScreen(this.parentScreen);
		}
		else if (par1GuiButton.id == 211)
		{
			this.mod
				.getConfig().setProperty("reversed.controls", !this.mod.getConfig().getBoolean("reversed.controls"));
			par1GuiButton.displayString =
				this.mod.getConfig().getBoolean("reversed.controls")
					? "Menu: Hold Down Key to open" : "Menu: Press Key to open";
			this.mod.saveConfig();
		}
		else if (par1GuiButton.id == 212)
		{
			this.mod.getConfig().setProperty(
				"useroptions.altitudes.low", !this.mod.getConfig().getBoolean("useroptions.altitudes.low"));
			par1GuiButton.displayString =
				this.mod.getConfig().getBoolean("useroptions.altitudes.low")
					? "Low-altitude ambiences: ON" : "Low-altitude ambiences: OFF";
			this.mod.saveConfig();
		}
		else if (par1GuiButton.id == 213)
		{
			this.mod.getConfig().setProperty(
				"useroptions.altitudes.high", !this.mod.getConfig().getBoolean("useroptions.altitudes.high"));
			par1GuiButton.displayString =
				this.mod.getConfig().getBoolean("useroptions.altitudes.high")
					? "High-altitude ambiences: ON" : "High-altitude ambiences: OFF";
			this.mod.saveConfig();
		}
		/*else if (par1GuiButton.id == 214)
		{
			this.mod.getConfig().setProperty(
				"world.height", 256 + (this.mod.getConfig().getInteger("world.height") - 128) % 896);
			par1GuiButton.displayString = "Use custom world height: " + this.mod.getConfig().getInteger("world.height");
			this.mod.saveConfig();
		}*/
		else if (par1GuiButton.id == 215)
		{
			this.mod.getConfig().setProperty("debug.mode", this.mod.getConfig().getInteger("debug.mode") == 0 ? 1 : 0);
			par1GuiButton.displayString =
				this.mod.getConfig().getInteger("debug.mode") == 1 ? ChatColorsSimple.COLOR_GOLD
					+ "Dev/Editor mode: ON" : "Dev/Editor mode: OFF";
			this.mod.changedDebugMode();
			this.mod.saveConfig();
		}
		
	}
	
	private void aboutToClose()
	{
		this.mod.saveConfig();
	}
	
	@Override
	public void onGuiClosed()
	{
		aboutToClose();
		
	}
	
	@Override
	protected void mouseClicked(int par1, int par2, int par3)
	{
		if (this.buttonId >= 0)
		{
		}
		else
		{
			super.mouseClicked(par1, par2, par3);
		}
	}
	
	@Override
	public void drawScreen(int par1, int par2, float par3)
	{
		final int _GAP = 2;
		final int _UNIT = 20;
		final int _MIX = _GAP + _UNIT;
		final int _SEPARATOR = 10;
		
		if (!this.mod.isDebugMode())
		{
			drawGradientRect(0, 0, this.width, this.height, 0xC0000000, 0x60000000);
			drawCenteredString(this.fontRendererObj, "MAtmos Advanced options", this.width / 2, 8, 0xffffff);
		}
		else
		{
			
			drawGradientRect(0, 0, this.width, this.height, 0xC0C06000, 0x60C06000);
			drawCenteredString(this.fontRendererObj, "MAtmos Advanced options "
				+ ChatColorsSimple.COLOR_GOLD + "(Dev mode)", this.width / 2, 8, 0xffffff);
			
			drawCenteredString(this.fontRendererObj, ChatColorsSimple.COLOR_YELLOW
				+ "Dev mode is enabled. This may cause Minecraft to run slower.", this.width / 2, _SEPARATOR
				+ _MIX * (this.IDS_PER_PAGE + 3) - 9, 0xffffff);
		}
		
		this.mod.util().prepareDrawString();
		this.mod.util().drawString(
			ChatColorsSimple.COLOR_GRAY + this.mod.getLag().getMilliseconds() + "ms", 1f, 1f, 0, 0, '3', 0, 0, 0, 0,
			true);
		
		super.drawScreen(par1, par2, par3);
		
	}
	
	@Override
	public boolean doesGuiPauseGame()
	{
		return false;
	}
	
}
