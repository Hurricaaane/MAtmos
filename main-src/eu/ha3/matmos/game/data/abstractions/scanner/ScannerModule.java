package eu.ha3.matmos.game.data.abstractions.scanner;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.client.Minecraft;
import eu.ha3.matmos.engine0.core.interfaces.Data;
import eu.ha3.matmos.game.data.abstractions.module.ExternalStringCountModule;
import eu.ha3.matmos.game.data.abstractions.module.PassOnceModule;
import eu.ha3.matmos.game.data.abstractions.module.ThousandStringCountModule;
import eu.ha3.matmos.game.system.MAtmosUtility;

/*
--filenotes-placeholder
*/

public class ScannerModule implements PassOnceModule, MAtScanCoordsOps, Progress
{
	public static final String THOUSAND_SUFFIX = "_p1k";
	
	private static final int LET_MINECRAFT_BOOT_FIRST = 100;
	
	private final String passOnceName;
	private final boolean requireThousand;
	private final int movement;
	private final int pulse;
	
	private final int xS;
	private final int yS;
	private final int zS;
	private final int blocksPerCall;
	
	private final ExternalStringCountModule base;
	private final ThousandStringCountModule thousand;
	
	private final Set<String> subModules = new HashSet<String>();
	
	//
	
	private int ticksPassed;
	private boolean progressInProgress;
	
	private int dimension = Integer.MIN_VALUE;
	private int xx = Integer.MIN_VALUE;
	private int yy = Integer.MIN_VALUE;
	private int zz = Integer.MIN_VALUE;
	
	//
	
	private final MAtScanVolumetricModel scanner = new MAtScanVolumetricModel();
	
	/**
	 * Movement: Requires the player to move to another block to trigger a new
	 * scan. If movement is zero, no scan until the player moves. If movement is
	 * negative, always scan even if the player hasn't moved.
	 * 
	 * @param data
	 * @param passOnceName
	 * @param baseName
	 * @param requireThousand
	 * @param movement
	 * @param pulse
	 * @param xS
	 * @param yS
	 * @param zS
	 * @param blocksPerCall
	 */
	public ScannerModule(
		Data data, String passOnceName, String baseName, boolean requireThousand, int movement, int pulse, int xS,
		int yS, int zS, int blocksPerCall)
	{
		this.passOnceName = passOnceName;
		this.requireThousand = requireThousand;
		this.movement = movement;
		this.pulse = pulse;
		
		this.xS = xS;
		this.yS = yS;
		this.zS = zS;
		this.blocksPerCall = blocksPerCall;
		
		//
		
		this.base = new ExternalStringCountModule(data, baseName, true);
		this.subModules.add(baseName);
		data.getSheet(baseName).setDefaultValue("0");
		if (requireThousand)
		{
			String thousandName = baseName + THOUSAND_SUFFIX;
			this.thousand = new ThousandStringCountModule(data, thousandName, true);
			this.subModules.add(thousandName);
			data.getSheet(thousandName).setDefaultValue("0");
		}
		else
		{
			this.thousand = null;
		}
		
		// 
		
		this.scanner.setPipeline(this);
	}
	
	@Override
	public String getModuleName()
	{
		return this.passOnceName;
	}
	
	@Override
	public Set<String> getSubModules()
	{
		return this.subModules;
	}
	
	@Override
	public void process()
	{
		if (this.ticksPassed < LET_MINECRAFT_BOOT_FIRST)
		{
			this.ticksPassed = this.ticksPassed + 1;
			return;
		}
		
		tryToBoot();
		
		if (this.progressInProgress)
		{
			this.scanner.routine();
		}
		this.ticksPassed = this.ticksPassed + 1;
	}
	
	private void tryToBoot()
	{
		if (this.progressInProgress)
			return;
		
		if (this.ticksPassed % this.pulse == 0)
		{
			boolean go = false;
			if (this.movement >= 0)
			{
				if (Minecraft.getMinecraft().thePlayer.dimension == this.dimension)
				{
					int x = MAtmosUtility.getPlayerX();
					int y = MAtmosUtility.clampToBounds(MAtmosUtility.getPlayerY());
					int z = MAtmosUtility.getPlayerZ();
					
					int max = Math.max(Math.abs(this.xx - x), Math.abs(this.yy - y));
					max = Math.max(max, Math.abs(this.zz - z));
					go = max > this.movement;
				}
				else
				{
					go = true;
				}
			}
			else
			{
				go = true;
			}
			
			if (go)
			{
				this.progressInProgress = true;
				
				this.dimension = Minecraft.getMinecraft().thePlayer.dimension;
				this.xx = MAtmosUtility.getPlayerX();
				this.yy = MAtmosUtility.clampToBounds(MAtmosUtility.getPlayerY());
				this.zz = MAtmosUtility.getPlayerZ();
				
				this.scanner.startScan(this.xx, this.yy, this.zz, this.xS, this.yS, this.zS, this.blocksPerCall);
			}
		}
	}
	
	@Override
	public void input(int x, int y, int z)
	{
		if (Minecraft.getMinecraft().thePlayer.dimension != this.dimension)
		{
			this.base.increment("minecraft:air");
			this.base.increment("minecraft:air^0");
			this.thousand.increment("minecraft:air");
			return;
		}
		
		this.base.increment(MAtmosUtility.getNameAt(x, y, z, ""));
		this.base.increment(MAtmosUtility.getPowerMetaAt(x, y, z, ""));
		this.thousand.increment(MAtmosUtility.getNameAt(x, y, z, ""));
	}
	
	@Override
	public void begin()
	{
	}
	
	@Override
	public void finish()
	{
		this.base.apply();
		if (this.requireThousand)
		{
			this.thousand.apply();
		}
		this.progressInProgress = false;
	}
	
	@Override
	public int getProgress_Current()
	{
		return this.scanner.getProgress_Current();
	}
	
	@Override
	public int getProgress_Total()
	{
		return this.scanner.getProgress_Total();
	}
	
}
