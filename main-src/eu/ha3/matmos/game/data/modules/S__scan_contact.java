package eu.ha3.matmos.game.data.modules;

import eu.ha3.matmos.engine0.core.interfaces.Data;
import eu.ha3.matmos.game.data.abstractions.module.AbstractStringCountModule;
import eu.ha3.matmos.game.system.MAtmosUtility;

/* x-placeholder */

public class S__scan_contact extends AbstractStringCountModule
{
	private final BlockTriplet[] blocks = { new BlockTriplet(0, -1, 0), // bottom
		new BlockTriplet(0, 0, 0), new BlockTriplet(0, 1, 0), // body
		new BlockTriplet(0, 2, 0), // column
		new BlockTriplet(-1, 0, 0), new BlockTriplet(1, 0, 0), // x -- 0
		new BlockTriplet(0, 0, -1), new BlockTriplet(0, 0, 1), // z -- 0
		new BlockTriplet(-1, 1, 0), new BlockTriplet(1, 1, 0), // x -- 1
		new BlockTriplet(0, 1, -1), new BlockTriplet(0, 1, 1), // z -- 1 
	};
	
	public S__scan_contact(Data dataIn)
	{
		super(dataIn, "block_contact", true);
		dataIn.getSheet(getModuleName()).setDefaultValue("0");
	}
	
	@Override
	protected void count()
	{
		int x = MAtmosUtility.getPlayerX();
		int y = MAtmosUtility.getPlayerY() - 1; // FIXME: Altitude
		int z = MAtmosUtility.getPlayerZ();
		
		//int worldHeight = mc.theWorld.getHeight();
		
		for (BlockTriplet triplet : this.blocks)
		{
			increment(triplet.getBlockRelative(x, y, z, ""));
			increment(triplet.getBMetaRelative(x, y, z, ""));
		}
		
		// TODO 2014-02-12 clean this up with hard coded triplets
		/*int nx;
		int ny;
		int nz;
		
		for (int k = 0; k < 12; k++)
		{
			ny = y + (k > 7 ? k - 9 : k % 2);
			if (ny >= 0 && ny < worldHeight)
			{
				nx = x + (k < 4 ? k < 2 ? -1 : 1 : 0);
				nz = z + (k > 3 && k < 8 ? k < 6 ? -1 : 1 : 0);
				
				String blockName = getBlockRelative(nx, ny, nz, "");
				increment(blockName);
			}
		}*/
		
	}
}
