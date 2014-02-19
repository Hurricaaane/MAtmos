package eu.ha3.matmos.engine0.datacustom;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import eu.ha3.matmos.engine0.core.interfaces.Data;
import eu.ha3.matmos.engine0.game.data.abstractions.module.ModuleProcessor;
import eu.ha3.matmos.v170helper.Version170Helper;

/*
--filenotes-placeholder
*/

public class X__x_seasons_mod extends ModuleProcessor
{
	private static final Map<Season, Integer> seasonsHash;
	static
	{
		X__x_seasons_mod.seasonsHash = new HashMap<Season, Integer>();
		X__x_seasons_mod.seasonsHash.put(Season.NotInstalled, -1);
		X__x_seasons_mod.seasonsHash.put(Season.NoSeason, 0);
		X__x_seasons_mod.seasonsHash.put(Season.Spring, 1);
		X__x_seasons_mod.seasonsHash.put(Season.Summer, 2);
		X__x_seasons_mod.seasonsHash.put(Season.Autumn, 3);
		X__x_seasons_mod.seasonsHash.put(Season.Winter, 4);
	}
	
	public X__x_seasons_mod(Data data)
	{
		super(data, "x_seasons_mod");
	}
	
	@Override
	protected void doProcess()
	{
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		int x = Version170Helper.getPlayerX();
		int y = Version170Helper.clampToBounds(Version170Helper.getPlayerY());
		int z = Version170Helper.getPlayerZ();
		
		Integer ic = X__x_seasons_mod.seasonsHash.get(Season.getSeason_Client(x, y, z));
		if (ic == null)
		{
			ic = -2;
		}
		
		setValueLegacyIntIndexes(0, 1);
		setValueLegacyIntIndexes(1, ic);
	}
}