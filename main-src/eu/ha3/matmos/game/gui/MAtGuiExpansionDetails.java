package eu.ha3.matmos.game.gui;

import eu.ha3.matmos.expansions.Expansion;
import eu.ha3.matmos.expansions.debugunit.ExpansionDebugUnit;
import eu.ha3.matmos.expansions.debugunit.FolderResourcePackEditableEDU;
import eu.ha3.matmos.expansions.debugunit.ReadOnlyJasonStringEDU;
import eu.ha3.matmos.game.debug.PluggableIntoMAtmos;
import eu.ha3.matmos.game.debug.SoundsJsonGenerator;
import eu.ha3.matmos.game.system.IDontKnowHowToCode;
import eu.ha3.matmos.game.system.MAtMod;
import eu.ha3.matmos.game.user.VisualExpansionDebugging;
import java.io.File;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.EnumChatFormatting;

/*
--filenotes-placeholder
*/

public class MAtGuiExpansionDetails extends GuiScreen
{
	private final MAtGuiMenu parentScreen;
	private final MAtMod mod;
	private final Expansion expansion;
	private final VisualExpansionDebugging debug;
	
	public MAtGuiExpansionDetails(MAtGuiMenu menu, MAtMod mod, Expansion expansion)
	{
		this.parentScreen = menu;
		this.mod = mod;
		this.expansion = expansion;
		this.debug = new VisualExpansionDebugging(this.mod, expansion.getName());
	}
	
	@Override
	public void drawScreen(int par1, int par2, float par3)
	{
		drawGradientRect(0, 0, this.width, this.height, 0xF0000000, 0x90000000);
		
		drawCenteredString(this.fontRendererObj, EnumChatFormatting.GOLD + "Dev mode: Viewing " + EnumChatFormatting.YELLOW
                + EnumChatFormatting.ITALIC + this.expansion.getFriendlyName() + " (" + this.expansion.getName() + ")",
                this.width / 2, 4, 0xffffff);
		
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
		
		int h = new ScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight).getScaledHeight();
		h = h - _UNIT - _GAP;
		
		this.buttonList.add(new GuiButton(200, _GAP, h, 70, _UNIT, "Close"));
		this.buttonList
			.add(new GuiButton(201, _GAP * 2 + 70, h, 70, _UNIT, EnumChatFormatting.GOLD + "Use in OSD"));
		this.buttonList.add(new GuiButton(202, _GAP * 3 + 70 * 2, h, 70, _UNIT, "Reload file"));
		if (this.mod.isEditorAvailable())
		{
			this.buttonList.add(new GuiButton(203, _GAP * 4 + 70 * 3, h, 110, _UNIT, "Open in Editor..."));
		}
		else
		{
			this.buttonList.add(new GuiButton(203, _GAP * 4 + 70 * 3 + 110, h, 220, _UNIT, "Editor Unavailable"));
		}
		this.buttonList.add(new GuiButton(204, _GAP * 5 + 70 * 3 + 110, h, 96, _UNIT, "Make sounds.json"));
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
		else if (par1GuiButton.id == 201)
		{
			this.mod.getVisualDebugger().debugModeExpansion(this.debug);
			
			// This triggers onGuiClosed
			mc.displayGuiScreen(null);
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
				PluggableIntoMAtmos plug = new PluggableIntoMAtmos(this.mod, this.expansion);
				
				Runnable editor = this.mod.instantiateRunnableEditor(plug);
				if (editor != null)
				{
					new Thread(editor, "EditorWindow_for_" + this.expansion.getName()).start();
					
					if (debugUnit instanceof ReadOnlyJasonStringEDU)
					{
						// XXX Read only mode
						this.mod.getChatter().printChat(EnumChatFormatting.RED,
                                "Expansions inside ZIP files are not supported in this version.");
						this.mod.getChatter().printChatShort(EnumChatFormatting.RED,
                                "Please unzip the resource packs to be able to view them.");
					}
				}
				else
				{
					this.mod.getChatter().printChat(EnumChatFormatting.RED, "Could not start editor for an unknown reason.");
				}
			}
		}
		else if (par1GuiButton.id == 204)
		{
			final ExpansionDebugUnit debugUnit = this.expansion.obtainDebugUnit();
			if (debugUnit instanceof FolderResourcePackEditableEDU)
			{
				File expFolder = ((FolderResourcePackEditableEDU) debugUnit).obtainExpansionFolder();
				File minecraftFolder = new File(expFolder, "assets/minecraft/");
				if (minecraftFolder.exists())
				{
					File soundsFolder = new File(minecraftFolder, "sounds/");
					File jsonFile = new File(minecraftFolder, "sounds.json");
					if (soundsFolder.exists())
					{
						try
						{
							new SoundsJsonGenerator(soundsFolder, jsonFile).run();
							this.mod.getChatter().printChat("File generated in " + jsonFile.getAbsolutePath());
							this.mod.getChatter().printChatShort("Changes will be applied next time Resource Packs are reloaded.");
						}
						catch (Exception e)
						{
							e.printStackTrace();
							IDontKnowHowToCode.whoops__printExceptionToChat(this.mod.getChatter(), e, this);
						}
					}
					else
					{
						this.mod.getChatter().printChat(EnumChatFormatting.RED, "Create the sounds/ folder first.");
					}
				}
				else
				{
					this.mod.getChatter().printChat(EnumChatFormatting.RED, "Create the minecraft/ folder first.");
				}
			}
		}
	}
}
