package net.minecraft.src;

import eu.ha3.matmos.engine0.game.system.MAtMod;

/* x-placeholder */

public class mod_MAtmos extends HaddonBridgeModLoader
{
	public mod_MAtmos()
	{
		super(new MAtMod());
		
	}
	
	@Override
	public String getVersion()
	{
		return "r" + MAtMod.VERSION + " for " + MAtMod.FOR; // Remember to change the thing on MAtMod
		
	}
	
}