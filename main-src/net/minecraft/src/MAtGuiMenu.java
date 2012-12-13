package net.minecraft.src;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import eu.ha3.mc.gui.HDisplayStringProvider;
import eu.ha3.mc.gui.HGuiSliderControl;
import eu.ha3.mc.gui.HSliderListener;

/*
            DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE 
                    Version 2, December 2004 

 Copyright (C) 2004 Sam Hocevar <sam@hocevar.net> 

 Everyone is permitted to copy and distribute verbatim or modified 
 copies of this license document, and changing it is allowed as long 
 as the name is changed. 

            DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE 
   TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION 

  0. You just DO WHAT THE FUCK YOU WANT TO. 
*/

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
	
	private List<MAtExpansion> expansionList;
	
	// Keep the active page in memory. Globally... (herpderp)
	private static int in_memory_page = 0;
	
	public MAtGuiMenu(GuiScreen par1GuiScreen, MAtMod matmos)
	{
		this(par1GuiScreen, matmos, in_memory_page);
	}
	
	public MAtGuiMenu(GuiScreen par1GuiScreen, MAtMod matmos, int pageFromZero)
	{
		this.screenTitle = "Expansions";
		this.buttonId = -1;
		this.parentScreen = par1GuiScreen;
		this.mod = matmos;
		this.pageFromZero = pageFromZero;
		
		this.expansionList = new ArrayList<MAtExpansion>();
		
		in_memory_page = this.pageFromZero;
	}
	
	/**
	 * Adds the buttons (and other controls) to the screen in question.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void initGui()
	{
		StringTranslate stringtranslate = StringTranslate.getInstance();
		
		final int _GAP = 2;
		final int _UNIT = 20;
		final int _WIDTH = 155 * 2;
		
		final int _MIX = _GAP + _UNIT;
		
		final int _LEFT = this.width / 2 - _WIDTH / 2;
		final int _RIGHT = this.width / 2 + _WIDTH / 2;
		
		Map<String, MAtExpansion> expansions = this.mod.getExpansionManager().getExpansions();
		int id = 0;
		
		{
			final MAtSoundManagerMaster central = this.mod.getSoundManagerMaster();
			
			HGuiSliderControl sliderControl =
				new HGuiSliderControl(id, _LEFT, _MIX, _WIDTH, _UNIT, "", central.getVolume() * 0.5f);
			sliderControl.setListener(new HSliderListener() {
				@Override
				public void sliderValueChanged(HGuiSliderControl slider, float value)
				{
					central.setVolume(value * 2);
					slider.updateDisplayString();
					MAtGuiMenu.this.mod.getConfig().setProperty("globalvolume.scale", central.getVolume());
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
					return "Global Volume Control: " + (int) Math.floor(central.getVolume() * 100) + "%";
				}
			});
			sliderControl.updateDisplayString();
			
			this.controlList.add(sliderControl);
			id++;
			
		}
		
		List<String> sortedNames = new ArrayList<String>(expansions.keySet());
		Collections.sort(sortedNames);
		
		for (int expansionIndex = this.pageFromZero * this.IDS_PER_PAGE; expansionIndex < this.pageFromZero
			* this.IDS_PER_PAGE + this.IDS_PER_PAGE
			&& expansionIndex < sortedNames.size(); expansionIndex++)
		{
			final String uniqueIdentifier = sortedNames.get(expansionIndex);
			final MAtExpansion expansion = expansions.get(uniqueIdentifier);
			this.expansionList.add(expansion);
			
			HGuiSliderControl sliderControl =
				new HGuiSliderControl(
					id, _LEFT + _MIX, _MIX * (id + 1), _WIDTH - _MIX * 2, _UNIT, "", expansion.getVolume() * 0.5f);
			sliderControl.setListener(new HSliderListener() {
				@Override
				public void sliderValueChanged(HGuiSliderControl slider, float value)
				{
					expansion.setVolume(value * 2);
					if (value != 0f && !expansion.isRunning())
					{
						expansion.turnOn();
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
					String display = expansion.getFriendlyName() + ": ";
					if (expansion.getVolume() == 0f)
					{
						if (expansion.isRunning())
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
			
			this.controlList.add(sliderControl);
			
			this.controlList.add(new GuiButton(400 + id - 1, _RIGHT - _UNIT, _MIX * (id + 1), _UNIT, _UNIT, "?"));
			
			id++;
			
		}
		
		this.controlList.add(new GuiButton(220, _RIGHT - _UNIT, _MIX * (this.IDS_PER_PAGE + 2), _UNIT, _UNIT, this.mod
			.getConfig().getBoolean("sound.autopreview") ? "^o^" : "^_^"));
		
		final int _PREVNEWTWIDTH = _WIDTH / 3;
		
		if (this.pageFromZero != 0)
		{
			this.controlList.add(new GuiButton(
				201, _LEFT + _MIX, _MIX * (this.IDS_PER_PAGE + 2), _PREVNEWTWIDTH, _UNIT, stringtranslate
					.translateKey("Previous")));
		}
		if (this.pageFromZero * this.IDS_PER_PAGE + this.IDS_PER_PAGE < sortedNames.size())
		{
			this.controlList.add(new GuiButton(
				202, _RIGHT - _MIX - _PREVNEWTWIDTH, _MIX * (this.IDS_PER_PAGE + 2), _PREVNEWTWIDTH, _UNIT,
				stringtranslate.translateKey("Next")));
		}
		
		final int _ASPLIT = 2;
		final int _AWID = _WIDTH / _ASPLIT - _GAP * (_ASPLIT - 1) / 2;
		
		final int _SEPARATOR = 10;
		
		this.controlList.add(new GuiButton(
			210, _LEFT, _SEPARATOR + _MIX * (this.IDS_PER_PAGE + 3), _AWID, _UNIT, this.mod.getConfig().getBoolean(
				"start.enabled") ? "Start Enabled: ON" : "Start Enabled: OFF"));
		
		this.controlList.add(new GuiButton(
			211, _LEFT + _AWID + _GAP, _SEPARATOR + _MIX * (this.IDS_PER_PAGE + 3), _AWID, _UNIT, this.mod
				.getConfig().getBoolean("reversed.controls") ? "Menu: Hold Down Key" : "Menu: Press Key"));
		
		final int _TURNOFFWIDTH = _WIDTH / 5;
		
		this.controlList.add(new GuiButton(200, _LEFT + _MIX, _SEPARATOR + _MIX * (this.IDS_PER_PAGE + 4), _WIDTH
			- _MIX * 2 - _GAP - _TURNOFFWIDTH, _UNIT, "Done"));
		
		this.controlList.add(new GuiButton(212, _RIGHT - _TURNOFFWIDTH - _MIX, _SEPARATOR
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
		if (par1GuiButton.id == 200)
		{
			// This triggers onGuiClosed
			this.mc.displayGuiScreen(this.parentScreen);
		}
		else if (par1GuiButton.id == 201)
		{
			this.mc.displayGuiScreen(new MAtGuiMenu(this.parentScreen, this.mod, this.pageFromZero - 1));
		}
		else if (par1GuiButton.id == 202)
		{
			this.mc.displayGuiScreen(new MAtGuiMenu(this.parentScreen, this.mod, this.pageFromZero + 1));
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
			this.mod
				.getConfig().setProperty("reversed.controls", !this.mod.getConfig().getBoolean("reversed.controls"));
			par1GuiButton.displayString =
				this.mod.getConfig().getBoolean("reversed.controls") ? "Menu: Hold Down Key" : "Menu: Press Key";
			this.mod.saveConfig();
		}
		else if (par1GuiButton.id == 212)
		{
			this.mc.displayGuiScreen(this.parentScreen);
			this.mod.stopRunning();
			
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
			MAtExpansion expansion = this.expansionList.get(id);
			
			if (expansion.isRunning())
			{
				expansion.playSample();
				
			}
			
		}
		
	}
	
	private void aboutToClose()
	{
		Map<String, MAtExpansion> expansions = this.mod.getExpansionManager().getExpansions();
		for (MAtExpansion expansion : expansions.values())
		{
			if (expansion.getVolume() == 0f && expansion.isRunning())
			{
				expansion.turnOff();
				
			}
			
		}
		
		this.mod.saveConfig();
		for (MAtExpansion expansion : expansions.values())
		{
			expansion.saveConfig();
			
		}
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
