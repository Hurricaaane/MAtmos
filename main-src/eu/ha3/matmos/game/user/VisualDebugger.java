package eu.ha3.matmos.game.user;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.EntityList;

import org.apache.commons.lang3.StringUtils;
import org.lwjgl.opengl.GL11;

import eu.ha3.matmos.engine.core.implem.LongFloatSimplificator;
import eu.ha3.matmos.engine.core.interfaces.Sheet;
import eu.ha3.matmos.game.data.ModularDataGatherer;
import eu.ha3.matmos.game.data.abstractions.scanner.Progress;
import eu.ha3.matmos.game.data.abstractions.scanner.ScannerModule;
import eu.ha3.matmos.game.system.MAtMod;
import eu.ha3.mc.haddon.supporting.SupportsFrameEvents;
import eu.ha3.mc.quick.chat.ChatColorsSimple;

/*
--filenotes-placeholder
*/

public class VisualDebugger implements SupportsFrameEvents
{
	private final MAtMod mod;
	private final ModularDataGatherer dataGatherer;
	
	private DebugMode mode = DebugMode.NONE;
	private VisualExpansionDebugging ed;
	private String scanDebug;
	
	public VisualDebugger(MAtMod mod, ModularDataGatherer dataGatherer)
	{
		this.mod = mod;
		this.dataGatherer = dataGatherer;
	}
	
	public List<String> obtainSheetNamesCopy()
	{
		List<String> sheetNames = new ArrayList<String>(this.dataGatherer.getData().getSheetNames());
		Collections.sort(sheetNames);
		return sheetNames;
	}
	
	public void debugModeExpansion(VisualExpansionDebugging ed)
	{
		this.ed = ed;
		this.mode = DebugMode.EXPANSION;
	}
	
	public void debugModeScan(String name)
	{
		this.scanDebug = name;
		this.mode = DebugMode.SCAN;
	}
	
	public void noDebug()
	{
		this.mode = DebugMode.NONE;
	}
	
	@Override
	public void onFrame(float semi)
	{
		if (this.mode == DebugMode.NONE)
			return;
		
		if (this.mode == DebugMode.EXPANSION
			&& semi >= 0f && this.mod.util().getCurrentScreen() != null
			&& !(this.mod.util().getCurrentScreen() instanceof GuiChat))
			return;
		
		switch (this.mode)
		{
		case SCAN:
			debugScan();
			break;
		case EXPANSION:
			this.ed.onFrame(semi);
			break;
		default:
			break;
		}
		
	}
	
	private void debugScan()
	{
		Minecraft mc = Minecraft.getMinecraft();
		int fac = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight).getScaleFactor();
		
		float scale = 1f / fac;
		GL11.glPushMatrix();
		GL11.glScalef(scale, scale, 1.0F);
		
		final Sheet sheet = this.dataGatherer.getData().getSheet(this.scanDebug);
		final int ALL = 50;
		
		List<String> sort = new ArrayList<String>(sheet.keySet());
		
		if (this.scanDebug.startsWith("scan_"))
		{
			try
			{
				Collections.sort(sort, new Comparator<String>() {
					@Override
					public int compare(String o1, String o2)
					{
						Long l1 = LongFloatSimplificator.longOf(sheet.get(o1));
						Long l2 = LongFloatSimplificator.longOf(sheet.get(o2));
						
						if (l1 == null && l2 == null)
							return o1.compareTo(o2);
						else if (l1 == null)
							return -1;
						else if (l2 == null)
							return 1;
						
						if (l1 > l2)
							return 1;
						else if (l1 < l2)
							return -1;
						else
							return o1.compareTo(o2);
					}
				});
				Collections.reverse(sort);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
		int total = 0;
		for (String index : sort)
		{
			if (!index.contains("^"))
			{
				Long l = LongFloatSimplificator.longOf(sheet.get(index));
				if (l != null)
				{
					total = total + (int) (long) l;
				}
			}
		}
		
		FontRenderer fontRenderer = mc.fontRenderer;
		
		int lineNumber = 0;
		
		if (this.scanDebug.startsWith("scan_large"))
		{
			Progress progressObject = this.dataGatherer.getLargeScanProgress();
			float progress = (float) progressObject.getProgress_Current() / progressObject.getProgress_Total();
			
			fontRenderer.drawStringWithShadow(
				"Scan ["
					+ mc.theWorld.getHeight() + "]: " + StringUtils.repeat("|", (int) (100 * progress)) + " ("
					+ (int) (progress * 100) + "%)", 20, 2 + 9 * lineNumber, 0xFFFFCC);
		}
		
		lineNumber = lineNumber + 1;
		
		for (String index : sort)
		{
			if (lineNumber <= 100 && !index.contains("^"))
			{
				if (this.scanDebug.startsWith("scan_") || this.scanDebug.equals("block_contact"))
				{
					Long count = LongFloatSimplificator.longOf(sheet.get(index));
					if (count != null)
					{
						if (count > 0)
						{
							float scalar = (float) count / total;
							String percentage =
								!this.scanDebug.endsWith(ScannerModule.THOUSAND_SUFFIX) ? Float.toString(Math
									.round(scalar * 1000f) / 10f) : Integer.toString(Math.round(scalar * 100f));
							if (percentage.equals("0.0"))
							{
								percentage = "0";
							}
							
							int fill = Math.round(scalar * ALL * 2 /* * 2*/);
							int superFill = 0;
							if (fill > ALL * 2)
							{
								fill = ALL * 2;
							}
							if (fill > ALL)
							{
								superFill = fill - ALL;
							}
							
							String bars = "";
							if (superFill > 0)
							{
								bars = bars + ChatColorsSimple.COLOR_YELLOW + StringUtils.repeat("|", superFill);
							}
							bars =
								bars
									+ eu.ha3.mc.quick.chat.ChatColorsSimple.THEN_RESET
									+ StringUtils.repeat("|", fill - superFill * 2);
							
							if (index.startsWith("minecraft:"))
							{
								index = index.substring(10);
							}
							
							fontRenderer.drawStringWithShadow(bars
								+ (fill == ALL * 2
									? ChatColorsSimple.COLOR_YELLOW + "++" + ChatColorsSimple.THEN_RESET : "") + " ("
								+ count + ", " + percentage + "%) " + index, 2, 2 + 9 * lineNumber, 0xFFFFFF);
							lineNumber = lineNumber + 1;
						}
					}
				}
				else if (this.scanDebug.startsWith("detect_"))
				{
					String val = sheet.get(index);
					if (!val.equals("0") && !val.equals(Integer.toString(Integer.MAX_VALUE)))
					{
						if (!index.equals("0"))
						{
							fontRenderer.drawStringWithShadow(
								index + " (" + EntityList.getStringFromID(Integer.parseInt(index)) + "): " + val, 2,
								2 + 9 * lineNumber, 0xFFFFFF);
						}
						else
						{
							fontRenderer.drawStringWithShadow(
								index + " (Player): " + val, 2, 2 + 9 * lineNumber, 0xFFFFFF);
						}
						
						lineNumber = lineNumber + 1;
					}
				}
				else
				{
					String val = sheet.get(index);
					int color = 0xFFFFFF;
					if (val.equals("0"))
					{
						color = 0xFF0000;
					}
					else if (val.equals("1"))
					{
						color = 0x0099FF;
					}
					fontRenderer.drawStringWithShadow(index + ": " + val, 2, 2 + 9 * lineNumber, color);
					lineNumber = lineNumber + 1;
				}
			}
		}
		GL11.glPopMatrix();
	}
	
	private enum DebugMode
	{
		NONE, SCAN, EXPANSION;
	}
}
