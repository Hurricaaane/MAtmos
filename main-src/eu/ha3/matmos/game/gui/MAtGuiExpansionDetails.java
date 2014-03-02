package eu.ha3.matmos.game.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import eu.ha3.matmos.editor.EditorMaster;
import eu.ha3.matmos.editor.PluggableIntoMinecraft;
import eu.ha3.matmos.engine0.core.implem.abstractions.ProviderCollection;
import eu.ha3.matmos.engine0.core.interfaces.Data;
import eu.ha3.matmos.expansions.Expansion;
import eu.ha3.matmos.expansions.ExpansionDebugUnit;
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
		
		this.buttonList.add(new GuiButton(200, _GAP, _GAP, 70, _UNIT, "Close"));
		this.buttonList.add(new GuiButton(201, _GAP * 2 + 70, _GAP, 70, _UNIT, "Keep open"));
		this.buttonList.add(new GuiButton(202, _GAP * 3 + 70 * 2, _GAP, 110, _UNIT, "Reload file"));
		this.buttonList.add(new GuiButton(203, _GAP * 4 + 70 * 3, _GAP, 110, _UNIT, "Edit..."));
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
		else if (par1GuiButton.id == 203)
		{
			final ExpansionDebugUnit k = this.expansion.obtainDebugUnit();
			if (k != null)
			{
				new EditorMaster(new PluggableIntoMinecraft() {
					
					@Override
					public void reloadFromDisk()
					{
					}
					
					@Override
					public void pushJason(String jason)
					{
					}
					
					@Override
					public void overrideMachine(String machineName, boolean overrideOnStatus)
					{
					}
					
					@Override
					public void liftOverrides()
					{
					}
					
					@Override
					public ProviderCollection getProviders()
					{
						return k.obtainKnowledge().obtainProviders();
					}
					
					@Override
					public Data getData()
					{
						return null;
					}
				}, k.getExpansionFile()).run();
				
			}
		}
	}
}
