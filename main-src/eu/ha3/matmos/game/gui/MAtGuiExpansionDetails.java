package eu.ha3.matmos.game.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import eu.ha3.matmos.expansions.Expansion;
import eu.ha3.matmos.expansions.debugunit.ExpansionDebugUnit;
import eu.ha3.matmos.expansions.debugunit.ReadOnlyJasonStringEDU;
import eu.ha3.matmos.game.debug.PluggableImpl;
import eu.ha3.matmos.game.debug.VisualExpansionDebugging;
import eu.ha3.matmos.game.system.MAtMod;
import eu.ha3.mc.quick.chat.ChatColorsSimple;

/*
--filenotes-placeholder
*/

public class MAtGuiExpansionDetails extends GuiScreen
{
	private final MAtGuiMenu__Debug parentScreen;
	private final MAtMod mod;
	private final Expansion expansion;
	private final VisualExpansionDebugging debug;
	
	public MAtGuiExpansionDetails(MAtGuiMenu__Debug menu, MAtMod mod, Expansion expansion)
	{
		this.parentScreen = menu;
		this.mod = mod;
		this.expansion = expansion;
		this.debug = new VisualExpansionDebugging(this.mod, expansion.getName());
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
		
		this.buttonList.add(new GuiButton(200, _GAP, _GAP, 70, _UNIT, "Close"));
		this.buttonList.add(new GuiButton(201, _GAP * 2 + 70, _GAP, 70, _UNIT, "Keep open"));
		this.buttonList.add(new GuiButton(202, _GAP * 3 + 70 * 2, _GAP, 110, _UNIT, "Reload file"));
		if (this.mod.isEditorAvailable())
		{
			this.buttonList.add(new GuiButton(203, _GAP * 4 + 70 * 2 + 110, _GAP, 110, _UNIT, "Edit..."));
		}
		else
		{
			this.buttonList.add(new GuiButton(203, _GAP * 4 + 70 * 2 + 110, _GAP, 220, _UNIT, "Editor Unavailable"));
		}
	}
	
	@Override
	protected void actionPerformed(GuiButton par1GuiButton)
	{
		Minecraft mc = Minecraft.getMinecraft();
		
		if (par1GuiButton.id == 200)
		{
			this.mod.getVisualDebugger().debugModeScan("scan_large");
			
			// This triggers onGuiClosed
			mc.displayGuiScreen(this.parentScreen);
		}
		else if (par1GuiButton.id == 201)
		{
			this.mod.getVisualDebugger().debugModeExpansion(this.debug);
			
			// This triggers onGuiClosed
			mc.displayGuiScreen(this.parentScreen);
		}
		else if (par1GuiButton.id == 202)
		{
			this.expansion.refreshKnowledge();
		}
		else if (par1GuiButton.id == 203 && this.mod.isEditorAvailable())
		{
			final ExpansionDebugUnit debugUnit = this.expansion.obtainDebugUnit();
			if (debugUnit != null)
			{
				PluggableImpl plug = new PluggableImpl(this.mod, this.expansion);
				this.expansion.addPluggable(plug);
				
				Runnable editor = this.mod.instantiateRunnableEditor(plug);
				if (editor != null)
				{
					new Thread(editor).start();
					
					if (debugUnit instanceof ReadOnlyJasonStringEDU)
					{
						// XXX Read only mode
						this.mod.getChatter().printChat(
							ChatColorsSimple.COLOR_RED
								+ "Expansions inside ZIP files are not supported in this version.");
						this.mod.getChatter().printChatShort(
							ChatColorsSimple.COLOR_RED + "Please unzip the resource packs to be able to view them.");
					}
				}
				else
				{
					this.mod.getChatter().printChat(
						ChatColorsSimple.COLOR_RED + "Could not start editor for an unknown reason.");
				}
			}
		}
	}
}
