package eu.ha3.matmos.game.tobedeprecated;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import eu.ha3.matmos.expansions.Expansion;
import eu.ha3.matmos.expansions.volume.VolumeUpdatable;
import eu.ha3.matmos.game.gui.MAtGuiMore;
import eu.ha3.matmos.game.system.MAtMod;
import eu.ha3.mc.gui.HDisplayStringProvider;
import eu.ha3.mc.gui.HGuiSliderControl;
import eu.ha3.mc.gui.HSliderListener;

/* x-placeholder */

@Deprecated
public class MAtGuiMenu extends GuiScreen
{
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
	
	private int pageFromZero;
	private final int IDS_PER_PAGE = 5;
	
	private List<Expansion> expansionList;
	
	// Keep the active page in memory. Globally... (herpderp)
	private static int in_memory_page = 0;
	
	public MAtGuiMenu(GuiScreen par1GuiScreen, MAtMod matmos)
	{
		this(par1GuiScreen, matmos, in_memory_page);
	}
	
	public MAtGuiMenu(GuiScreen par1GuiScreen, MAtMod matmos, int pageFromZero)
	{
		this.screenTitle = "MAtmos Expansions";
		this.buttonId = -1;
		this.parentScreen = par1GuiScreen;
		this.mod = matmos;
		this.pageFromZero = pageFromZero;
		
		this.expansionList = new ArrayList<Expansion>();
		
		in_memory_page = this.pageFromZero;
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
		final int _RIGHT = this.width / 2 + _WIDTH / 2;
		
		Map<String, Expansion> expansions = this.mod.getExpansionList();
		int id = 0;
		
		{
			final VolumeUpdatable globalVolumeControl = this.mod.getGlobalVolumeControl();
			
			HGuiSliderControl sliderControl =
				new HGuiSliderControl(id, _LEFT, _MIX, _WIDTH, _UNIT, "", globalVolumeControl.getVolume() * 0.5f);
			sliderControl.setListener(new HSliderListener() {
				@Override
				public void sliderValueChanged(HGuiSliderControl slider, float value)
				{
					globalVolumeControl.setVolumeAndUpdate(value * 2);
					slider.updateDisplayString();
					MAtGuiMenu.this.mod.getConfig().setProperty("globalvolume.scale", globalVolumeControl.getVolume());
				}
				
				@Override
				public void sliderPressed(HGuiSliderControl hGuiSliderControl)
				{
				}
				
				@Override
				public void sliderReleased(HGuiSliderControl hGuiSliderControl)
				{
				}
			});
			sliderControl.setDisplayStringProvider(new HDisplayStringProvider() {
				@Override
				public String provideDisplayString()
				{
					return "Global Volume Control: " + (int) Math.floor(globalVolumeControl.getVolume() * 100) + "%";
				}
			});
			sliderControl.updateDisplayString();
			
			this.buttonList.add(sliderControl);
			id++;
			
		}
		
		List<String> sortedNames = new ArrayList<String>(expansions.keySet());
		Collections.sort(sortedNames);
		
		for (int expansionIndex = this.pageFromZero * this.IDS_PER_PAGE; expansionIndex < this.pageFromZero
			* this.IDS_PER_PAGE + this.IDS_PER_PAGE
			&& expansionIndex < sortedNames.size(); expansionIndex++)
		{
			final String uniqueIdentifier = sortedNames.get(expansionIndex);
			final Expansion expansion = expansions.get(uniqueIdentifier);
			this.expansionList.add(expansion);
			
			HGuiSliderControl sliderControl =
				new HGuiSliderControl(
					id, _LEFT + _MIX, _MIX * (id + 1), _WIDTH - _MIX * 2, _UNIT, "", expansion.getVolume() * 0.5f);
			sliderControl.setListener(new HSliderListener() {
				@Override
				public void sliderValueChanged(HGuiSliderControl slider, float value)
				{
					expansion.setVolumeAndUpdate(value * 2);
					if (value > 0f && !expansion.isActivated())
					{
						expansion.activate();
					}
					slider.updateDisplayString();
				}
				
				@Override
				public void sliderPressed(HGuiSliderControl hGuiSliderControl)
				{
				}
				
				@Override
				public void sliderReleased(HGuiSliderControl hGuiSliderControl)
				{
					if (MAtGuiMenu.this.mod.getConfig().getBoolean("sound.autopreview"))
					{
						expansion.playSample();
					}
				}
			});
			
			sliderControl.setDisplayStringProvider(new HDisplayStringProvider() {
				@Override
				public String provideDisplayString()
				{
					String display = expansion.getName() + ": ";
					if (expansion.getVolume() == 0f)
					{
						if (expansion.isActivated())
						{
							display = display + "Will be disabled";
						}
						else
						{
							display = display + "Disabled";
						}
					}
					else
					{
						display = display + (int) Math.floor(expansion.getVolume() * 100) + "%";
					}
					
					return display;
				}
			});
			sliderControl.updateDisplayString();
			
			this.buttonList.add(sliderControl);
			
			this.buttonList.add(new GuiButton(400 + id - 1, _RIGHT - _UNIT, _MIX * (id + 1), _UNIT, _UNIT, "?"));
			
			id++;
			
		}
		
		this.buttonList.add(new GuiButton(220, _RIGHT - _UNIT, _MIX * (this.IDS_PER_PAGE + 2), _UNIT, _UNIT, this.mod
			.getConfig().getBoolean("sound.autopreview") ? "^o^" : "^_^"));
		
		final int _PREVNEWTWIDTH = _WIDTH / 3;
		
		if (this.pageFromZero != 0)
		{
			this.buttonList.add(new GuiButton(
				201, _LEFT + _MIX, _MIX * (this.IDS_PER_PAGE + 2), _PREVNEWTWIDTH, _UNIT, "Previous"));
		}
		if (this.pageFromZero * this.IDS_PER_PAGE + this.IDS_PER_PAGE < sortedNames.size())
		{
			this.buttonList.add(new GuiButton(
				202, _RIGHT - _MIX - _PREVNEWTWIDTH, _MIX * (this.IDS_PER_PAGE + 2), _PREVNEWTWIDTH, _UNIT, "Next"));
		}
		
		final int _ASPLIT = 2;
		final int _AWID = _WIDTH / _ASPLIT - _GAP * (_ASPLIT - 1) / 2;
		
		final int _SEPARATOR = 10;
		
		this.buttonList.add(new GuiButton(
			210, _LEFT, _SEPARATOR + _MIX * (this.IDS_PER_PAGE + 3), _AWID, _UNIT, this.mod.getConfig().getBoolean(
				"start.enabled") ? "Start Enabled: ON" : "Start Enabled: OFF"));
		
		this.buttonList
			.add(new GuiButton(
				211, _LEFT + _AWID + _GAP, _SEPARATOR + _MIX * (this.IDS_PER_PAGE + 3), _AWID, _UNIT,
				"Advanced options..."));
		
		final int _TURNOFFWIDTH = _WIDTH / 5;
		
		this.buttonList.add(new GuiButton(200, _LEFT + _MIX, _SEPARATOR + _MIX * (this.IDS_PER_PAGE + 4), _WIDTH
			- _MIX * 2 - _GAP - _TURNOFFWIDTH, _UNIT, "Done"));
		
		this.buttonList.add(new GuiButton(212, _RIGHT - _TURNOFFWIDTH - _MIX, _SEPARATOR
			+ _MIX * (this.IDS_PER_PAGE + 4), _TURNOFFWIDTH, _UNIT, "Turn Off"));
		
		//this.screenTitle = stringtranslate.translateKey("controls.title");
	}
	
	/**
	 * Fired when a control is clicked. This is the equivalent of
	 * ActionListener.actionPerformed(ActionEvent e).
	 */
	@Override
	protected void actionPerformed(GuiButton par1GuiButton)
	{
		Minecraft mc = Minecraft.getMinecraft();
		
		if (par1GuiButton.id == 200)
		{
			// This triggers onGuiClosed
			mc.displayGuiScreen(this.parentScreen);
		}
		else if (par1GuiButton.id == 201)
		{
			mc.displayGuiScreen(new MAtGuiMenu(this.parentScreen, this.mod, this.pageFromZero - 1));
		}
		else if (par1GuiButton.id == 202)
		{
			mc.displayGuiScreen(new MAtGuiMenu(this.parentScreen, this.mod, this.pageFromZero + 1));
		}
		else if (par1GuiButton.id == 210)
		{
			boolean newEnabledState = !this.mod.getConfig().getBoolean("start.enabled");
			this.mod.getConfig().setProperty("start.enabled", newEnabledState);
			par1GuiButton.displayString = newEnabledState ? "Start Enabled: ON" : "Start Enabled: OFF";
			this.mod.saveConfig();
		}
		else if (par1GuiButton.id == 211)
		{
			mc.displayGuiScreen(new MAtGuiMore(this, this.mod));
		}
		else if (par1GuiButton.id == 212)
		{
			mc.displayGuiScreen(this.parentScreen);
			this.mod.deactivate();
		}
		else if (par1GuiButton.id == 220)
		{
			this.mod
				.getConfig().setProperty("sound.autopreview", !this.mod.getConfig().getBoolean("sound.autopreview"));
			par1GuiButton.displayString = this.mod.getConfig().getBoolean("sound.autopreview") ? "^o^" : "^_^";
			this.mod.saveConfig();
		}
		else if (par1GuiButton.id >= 400)
		{
			int id = par1GuiButton.id - 400;
			Expansion expansion = this.expansionList.get(id);
			
			if (expansion.isActivated())
			{
				expansion.playSample();
				
			}
			
		}
		
	}
	
	private void aboutToClose()
	{
		this.mod.synchronize();
		this.mod.saveExpansions();
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
		// XXX 2014-01-04 unsure
		//drawDefaultBackground();
		drawCenteredString(this.fontRenderer, this.screenTitle, this.width / 2, 8, 0xffffff);
		
		super.drawScreen(par1, par2, par3);
		
	}
	
}
