package eu.ha3.matmos.game.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import eu.ha3.matmos.expansions.Expansion;
import eu.ha3.matmos.game.system.MAtMod;
import eu.ha3.mc.quick.chat.ChatColorsSimple;

/*
--filenotes-placeholder
*/

public class MAtGuiExpansionInfo extends GuiScreen
{
	private final MAtGuiMenu parentScreen;
	private final MAtMod mod;
	private final Expansion expansion;
	
	private final String[] info;
	
	public MAtGuiExpansionInfo(MAtGuiMenu menu, MAtMod mod, Expansion expansion)
	{
		this.parentScreen = menu;
		this.mod = mod;
		this.expansion = expansion;
		
		this.info =
			expansion.hasMoreInfo()
				? expansion.getInfo().replace("\r", "").replace("§", "\u00A7").split("\n")
				: new String[] { "No info.txt available." };
	}
	
	@Override
	public void drawScreen(int par1, int par2, float par3)
	{
		drawGradientRect(0, 0, this.width, this.height, 0xF0000000, 0x90000000);
		
		drawCenteredString(this.fontRendererObj, "About "
			+ ChatColorsSimple.COLOR_YELLOW + ChatColorsSimple.THEN_ITALIC + this.expansion.getFriendlyName()
			+ ChatColorsSimple.THEN_RESET + "...", this.width / 2, 4, 0xffffff);
		
		int lc = 0;
		for (String line : this.info)
		{
			this.fontRendererObj.drawString(line, this.width / 2 - 200, 16 + 8 * lc, 0xFFFFFF);
			lc++;
		}
		
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
		
		int h =
			new ScaledResolution(this.mc.gameSettings, this.mc.displayWidth, this.mc.displayHeight).getScaledHeight();
		h = h - _UNIT - _GAP;
		
		this.buttonList.add(new GuiButton(200, _GAP, h, 70, _UNIT, "Close"));
	}
	
	@Override
	protected void actionPerformed(GuiButton par1GuiButton)
	{
		Minecraft mc = Minecraft.getMinecraft();
		
		if (par1GuiButton.id == 200)
		{
			// This triggers onGuiClosed
			mc.displayGuiScreen(this.parentScreen);
		}
	}
}