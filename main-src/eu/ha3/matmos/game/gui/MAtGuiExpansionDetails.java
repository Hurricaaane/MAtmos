package eu.ha3.matmos.game.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import eu.ha3.matmos.expansions.Expansion;
import eu.ha3.matmos.game.debug.ExpansionDebug;
import eu.ha3.matmos.game.system.MAtMod;

/*
--filenotes-placeholder
*/

public class MAtGuiExpansionDetails extends GuiScreen
{
	private final MAtGuiMenu__Debug parentScreen;
	private final MAtMod mod;
	private final Expansion expansion;
	private final ExpansionDebug debug;
	
	public MAtGuiExpansionDetails(MAtGuiMenu__Debug menu, MAtMod mod, Expansion expansion)
	{
		this.parentScreen = menu;
		this.mod = mod;
		this.expansion = expansion;
		this.debug = new ExpansionDebug(this.mod, expansion.getName());
	}
	
	@Override
	public void drawScreen(int par1, int par2, float par3)
	{
		drawGradientRect(0, 0, this.width, this.height, 0xF0000000, 0xC0000000);
		
		drawCenteredString(
			this.fontRenderer, this.expansion.getFriendlyName() + "(" + this.expansion.getName() + ")", this.width / 2,
			8, 0xffffff);
		
		this.debug.onFrame(0f);
		
		super.drawScreen(par1, par2, par3);
	}
	
	@Override
	public boolean doesGuiPauseGame()
	{
		return false;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void initGui()
	{
		final int _GAP = 2;
		final int _UNIT = 20;
		
		this.buttonList.add(new GuiButton(200, _GAP, _GAP, 100, _UNIT, "Close"));
		this.buttonList.add(new GuiButton(201, _GAP * 2 + 100, _GAP, 100, _UNIT, "Keep open"));
	}
	
	@Override
	protected void actionPerformed(GuiButton par1GuiButton)
	{
		Minecraft mc = Minecraft.getMinecraft();
		
		if (par1GuiButton.id == 200)
		{
			this.mod.setDebugExpansion(null);
			
			// This triggers onGuiClosed
			mc.displayGuiScreen(this.parentScreen);
		}
		else if (par1GuiButton.id == 201)
		{
			this.mod.setDebugExpansion(this.debug);
			
			// This triggers onGuiClosed
			mc.displayGuiScreen(this.parentScreen);
		}
	}
}
