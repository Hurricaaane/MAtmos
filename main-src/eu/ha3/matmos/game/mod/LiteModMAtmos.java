package eu.ha3.matmos.game.mod;

import eu.ha3.matmos.game.system.MAtMod;
import eu.ha3.mc.haddon.litemod.LiteBase;

/* x-placeholder */

public class LiteModMAtmos extends LiteBase
{
	public LiteModMAtmos()
	{
		super(new MAtMod());
		
	}
	
	@Override
	public String getVersion()
	{
		return "r" + MAtMod.VERSION + " for " + MAtMod.FOR; // Remember to change the thing on MAtMod
		
	}
	
}