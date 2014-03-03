package eu.ha3.matmos.game.user;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiChat;

import org.apache.commons.lang3.StringUtils;

import eu.ha3.matmos.engine.core.implem.LongFloatSimplificator;
import eu.ha3.matmos.engine.core.interfaces.Sheet;
import eu.ha3.matmos.game.data.ModularDataGatherer;
import eu.ha3.matmos.game.data.abstractions.scanner.Progress;
import eu.ha3.matmos.game.data.abstractions.scanner.ScannerModule;
import eu.ha3.matmos.game.debug.VisualExpansionDebugging;
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
		
		if (this.mod.util().getCurrentScreen() != null && !(this.mod.util().getCurrentScreen() instanceof GuiChat))
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
		final Sheet sheet = this.dataGatherer.getData().getSheet(this.scanDebug);
		final int ALL = 50;
		
		List<String> sort = new ArrayList<String>(sheet.keySet());
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
		
		Minecraft mc = Minecraft.getMinecraft();
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
		}
	}
	
	private enum DebugMode
	{
		NONE, SCAN, EXPANSION;
	}
}
