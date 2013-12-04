package eu.ha3.matmos.engine0.game.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import eu.ha3.matmos.engine0.game.system.MAtMod;
import eu.ha3.mc.gui.HGuiSliderControl;

/* x-placeholder */

public class MAtGuiMore extends GuiScreen
{
	private final int IDS_PER_PAGE = 5;
	
	/**
	 * A reference to the screen object that created this. Used for navigating
	 * between screens.
	 */
	private GuiScreen parentScreen;
	
	/** The title string that is displayed in the top-center of the screen. */
	protected String screenTitle;
	
	private MAtMod mod;
	
	/** The ID of the button that has been pressed. */
	private int buttonId;
	
	public MAtGuiMore(GuiScreen par1GuiScreen, MAtMod matmos)
	{
		this.screenTitle = "MAtmos Advanced options";
		this.buttonId = -1;
		this.parentScreen = par1GuiScreen;
		this.mod = matmos;
	}
	
	/**
	 * Adds the buttons (and other controls) to the screen in question.
	 */
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
		
		MAtGuiSetSlider setSlider =
			new MAtGuiSetSlider(this.mod, this.mod.getConfig().getString("totalconversion.name"));
		if (setSlider.getMaxPossibilities() > 1)
		{
			HGuiSliderControl setControl =
				new HGuiSliderControl(
					214, _LEFT, _MIX * (4 + 1), _WIDTH, _UNIT, "", setSlider.calculateSliderLocation(this.mod
						.getConfig().getString("totalconversion.name")));
			setControl.setListener(setSlider);
			setControl.setDisplayStringProvider(setSlider);
			setControl.updateDisplayString();
			this.buttonList.add(setControl);
		}
		
		this.buttonList.add(new GuiButton(220, _LEFT + _MIX, _MIX * (5 + 1), _WIDTH - _MIX * 2, _UNIT, this.mod
			.getConfig().getBoolean("dump.sheets.enabled")
			? "Data dump: "
				+ (this.mod.isDumpReady()
					? "Enabled (this will slow down Minecraft)" : "Enabled when Minecraft restarts")
			: "Data dump: Disabled"));
		
		if (this.mod.isDumpReady())
		{
			this.buttonList.add(new GuiButton(
				215, _LEFT + _MIX, _MIX * (6 + 1), _WIDTH - _MIX * 2, _UNIT, "Generate data dump now"));
		}
		else
		{
			this.buttonList.add(new GuiButton(
				215, _LEFT + _MIX, _MIX * (6 + 1), _WIDTH - _MIX * 2, _UNIT, "Generate PARTIAL data dump now"));
		}
		
		this.buttonList.add(new GuiButton(200, _LEFT + _MIX, _SEPARATOR + _MIX * (this.IDS_PER_PAGE + 4), _WIDTH
			- _MIX * 2 - _GAP - _TURNOFFWIDTH, _UNIT, "Done"));
	}
	
	/**
	 * Fired when a control is clicked. This is the equivalent of
	 * ActionListener.actionPerformed(ActionEvent e).
	 */
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
			if (this.mod.isDumpReady())
			{
				this.mod.createDataDump(false);
			}
			else
			{
				this.mod.createDataDump(true);
			}
		}
		else if (par1GuiButton.id == 220)
		{
			this.mod.getConfig().setProperty(
				"dump.sheets.enabled", !this.mod.getConfig().getBoolean("dump.sheets.enabled"));
			par1GuiButton.displayString =
				this.mod.getConfig().getBoolean("dump.sheets.enabled")
					? "Data dump: "
						+ (this.mod.isDumpReady()
							? "Enabled (this will slow down Minecraft)" : "Enabled when Minecraft restarts")
					: "Data dump: Disabled";
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
	
	/**
	 * Called when the mouse is clicked.
	 */
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
	
	/**
	 * Draws the screen and all the components in it.
	 */
	@Override
	public void drawScreen(int par1, int par2, float par3)
	{
		drawDefaultBackground();
		drawCenteredString(this.fontRenderer, this.screenTitle, this.width / 2, 8, 0xffffff);
		
		super.drawScreen(par1, par2, par3);
		
	}
	
}
