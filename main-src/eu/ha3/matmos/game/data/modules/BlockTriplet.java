package eu.ha3.matmos.game.data.modules;

import eu.ha3.matmos.game.system.MAtmosUtility;

/*
--filenotes-placeholder
*/

public final class BlockTriplet
{
	private final int nx;
	private final int ny;
	private final int nz;
	
	public BlockTriplet(int nx, int ny, int nz)
	{
		this.nx = nx;
		this.ny = ny;
		this.nz = nz;
	}
	
	public String getBlockRelative(int xx, int yy, int zz, String defaultIfFail)
	{
		return MAtmosUtility.getNameAt(xx + this.nx, yy + this.ny, zz + this.nz, defaultIfFail);
	}
	
	public String getBMetaRelative(int xx, int yy, int zz, String defaultIfFail)
	{
		return MAtmosUtility.getPowerMetaAt(xx + this.nx, yy + this.ny, zz + this.nz, defaultIfFail);
	}
}
