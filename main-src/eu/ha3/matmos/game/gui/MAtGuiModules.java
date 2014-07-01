package eu.ha3.matmos.game.gui;

import java.util.Iterator;
import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import eu.ha3.matmos.game.data.abstractions.module.ModuleProcessor;
import eu.ha3.matmos.game.system.MAtMod;
import eu.ha3.mc.quick.chat.ChatColorsSimple;

/*
--filenotes-placeholder
*/

public class MAtGuiModules extends GuiScreen
{
	private final MAtMod mod;
	private GuiScreen parentScreen;
	
	private int buttonId;
	private List<String> val;
	
	public MAtGuiModules(GuiScreen par1GuiScreen, MAtMod mod)
	{
		this.mod = mod;
		this.buttonId = -1;
		this.parentScreen = par1GuiScreen;
		
		this.val = mod.getVisualDebugger().obtainSheetNamesCopy();
		Iterator<String> iter = this.val.iterator();
		while (iter.hasNext())
		{
			if (iter.next().endsWith(ModuleProcessor.DELTA_SUFFIX))
			{
				iter.remove();
			}
		}
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
		
		this.buttonList.add(new GuiButton(
			201, _LEFT + _MIX + _WIDTH - _MIX * 2 - _GAP - _TURNOFFWIDTH + _GAP, _SEPARATOR + _MIX * (5 + 4),
			_TURNOFFWIDTH, _UNIT, "Discard"));
		
		this.buttonList
			.add(new GuiButton(
				202, _LEFT + _MIX + _WIDTH - _MIX * 2 + _GAP, _SEPARATOR + _MIX * (5 + 4), _TURNOFFWIDTH, _UNIT,
				"Deltas?"));
		
		for (int id = 0; id < this.val.size(); id++)
		{
			int flid = id / 18;
			this.buttonList.add(new GuiButton(
				id, _LEFT + flid * _WIDTH / 3, _SEPARATOR + _MIX / 2 * (id % 18), _WIDTH / 3, _UNIT / 2, this.val
					.get(id)));
		}
		
		this.buttonList.add(new GuiButton(200, _LEFT + _MIX, _SEPARATOR + _MIX * (5 + 4), _WIDTH
			- _MIX * 2 - _GAP - _TURNOFFWIDTH, _UNIT, "Done"));
	}
	
	@Override
	protected void actionPerformed(GuiButton par1GuiButton)
	{
		if (par1GuiButton.id == 200)
		{
			this.mc.displayGuiScreen(this.parentScreen);
		}
		else if (par1GuiButton.id == 201)
		{
			this.mod.getVisualDebugger().noDebug();
		}
		else if (par1GuiButton.id == 202)
		{
			this.mod.getVisualDebugger().toggleDeltas();
		}
		else if (par1GuiButton.id < this.val.size())
		{
			this.mod.getVisualDebugger().debugModeScan(this.val.get(par1GuiButton.id));
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
		drawGradientRect(0, 0, this.width, this.height, 0xC0C06000, 0x60C06000);
		drawCenteredString(
			this.fontRendererObj, ChatColorsSimple.COLOR_GOLD + "Dev mode: On-screen Display", this.width / 2, 1,
			0xffffff);
		
		this.mod.getVisualDebugger().onFrame(-1f);
		
		super.drawScreen(par1, par2, par3);
		
	}
	
	@Override
	public boolean doesGuiPauseGame()
	{
		return false;
	}
}
